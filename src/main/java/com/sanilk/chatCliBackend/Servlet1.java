package com.sanilk.chatCliBackend;

//import com.google.appengine.api.blobstore.BlobstoreService;
//import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
//import com.google.appengine.repackaged.org.joda.time.DateTime;
//import com.google.appengine.repackaged.org.joda.time.DateTimeZone;
//import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
//import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;
import com.sanilk.chatCliBackend.Client.Message;
import com.sanilk.chatCliBackend.Requests.MyRequest;
import com.sanilk.chatCliBackend.Requests.authenticate.AuthenticateRequest;
import com.sanilk.chatCliBackend.Requests.receive.ReceiveRequest;
import com.sanilk.chatCliBackend.Requests.send.SendRequest;
import com.sanilk.chatCliBackend.Requests.sign_up.SignUpRequest;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;



@WebServlet("/Servlet1")
public class Servlet1 extends HttpServlet {

	public static void main(String[] args) throws Exception{
		Server server = new Server(
//                5000
				Integer.valueOf(System.getenv("PORT"))
		);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new Servlet1()),"/*");
		server.start();
		server.join();
	}

	private static final long serialVersionUID = 1L;
	ClientHandler clientHandler;
	JDBCController controller;

	private static final String[] REQUEST_TYPES={
		//Send is for sending a new message, receive checks if there is a new message or not, Check sends the number of messages from each sender
			"SEND",
			"RECEIVE",
			"CHECK",
			"SIGN_UP",
			"AUTHENTICATE",
			"SEND_LOG",
			"GET_LOG"
	};

	File logFile;
	String appLogs;

	static {
//		storage = StorageOptions.getDefaultInstance().getService();
	}

	private final static String ADMIN_USER_NAME="sanilk21";
	private final static String ADMIN_PASSWORD="root";
       
    public Servlet1() {
        super();
    }
    

	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
    
    @Override
    public void init() throws ServletException {
    	logFile =new File("log");
    	clientHandler=ClientHandler.getInstance();
    	clientHandler.registerServlet(this);
    	
    	getServletContext().setAttribute("clientHandler", clientHandler);
    	
    	try{
			Class.forName(JDBC_DRIVER);
		}catch(Exception e){
			e.printStackTrace();
		}

		appLogs="";
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataOutputStream dos=new DataOutputStream(response.getOutputStream());
		dos.write("in doGet()".getBytes());
		dos.flush();
		dos.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		controller=new JDBCController(this);
		
		DataInputStream din=new DataInputStream(request.getInputStream());
		String contents = din.readUTF();


		
		String senderNick="";
		String receiverNick="";
		String message="";
		String request_type="";
		String password="";


		//New XML Reading
		MyRequest myRequest=XMLParser.parseXML(contents);
		if(myRequest instanceof AuthenticateRequest) {
			AuthenticateRequest authenticateRequest = (AuthenticateRequest) myRequest;
			System.out.println("Authenticate request");
			DataOutputStream dos = new DataOutputStream(response.getOutputStream());
			if (clientHandler.isClientAuthentic(authenticateRequest.senderNick, authenticateRequest.senderPass)) {
				String finalXml="<response>\n" +
						"    <type>AUTHENTICATE</type>\n" +
						"    <successful>true</successful>\n" +
						"    <authentic>true</authentic>\n" +
						"    <error_code>0</error_code>\n" +
						"    <error_details></error_details>\n" +
						"</response>";
				System.out.println("Client is authentic");
				dos.writeUTF(finalXml);
			} else {
				String finalXml="<response>\n" +
						"    <type>AUTHENTICATE</type>\n" +
						"    <successful>true</successful>\n" +
						"    <authentic>false</authentic>\n" +
						"    <error_code>100</error_code>\n" +
						"    <error_details>Client is not authentic</error_details>\n" +
						"</response>";
				System.out.println("Client is not authentic");
				dos.writeUTF(finalXml);
			}
			dos.flush();
			dos.close();
			return;
		}else if(myRequest instanceof SignUpRequest){
			SignUpRequest signUpRequest=(SignUpRequest)myRequest;
			DataOutputStream dos=new DataOutputStream(response.getOutputStream());
			if(clientHandler.clientExists(signUpRequest.senderNick)){
				String output="<response>\n" +
						"    <type>SIGN_UP</type>\n" +
						"    <succesful>false</succesful>\n" +
						"    <error_code>100</error_code>\n" +
						"    <error_details>Client name already exists</error_details>\n" +
						"</response>";
				dos.writeUTF(output);
			}else{
				System.out.println("Registering new client by username - "+signUpRequest.senderNick+" and password - "+
					signUpRequest.senderPassword);
				clientHandler.registerClient(signUpRequest.senderNick, signUpRequest.senderPassword, controller);

				String output="<response>\n" +
						"    <type>SIGN_UP</type>\n" +
						"    <succesful>true</succesful>\n" +
						"    <error_code>0</error_code>\n" +
						"    <error_details>none</error_details>\n" +
						"</response>";
				dos.writeUTF(output);
			}
			dos.flush();
			dos.close();
			return;
		}else if(myRequest instanceof SendRequest){
			SendRequest sendRequest=(SendRequest)myRequest;
			for(Message clientMessage:sendRequest.messages){
				sendMessage(clientMessage.msg, sendRequest.senderNick, sendRequest.receiverNick, controller, clientMessage.encryptDuration);
			}
			DataOutputStream dos=new DataOutputStream(response.getOutputStream());
			String finalXML="<response>\n" +
					"    <type>SEND</type>\n" +
					"    <succesful>true</succesful>\n" +
					"    <error_code>0</error_code>\n" +
					"    <error_details></error_details>\n" +
					"</response>";
			dos.writeUTF(finalXML);
			return;
		}else if(myRequest instanceof ReceiveRequest){
			ReceiveRequest receiveRequest=(ReceiveRequest)myRequest;
			DataOutputStream dos = new DataOutputStream(response.getOutputStream());
//			dos.writeUTF(String.format("\nSender : %s\nRequest type : %s\nMessage : %s\n\nOld messages : %s\n\n", senderNick, request_type, message, checkForNewMessages(senderNick)));
			ArrayList<Message> messages=checkForNewMessages(receiveRequest.senderNick, receiveRequest.receiverNick, controller);
			String finalXML="<response>\n" +
					"    <type>RECEIVE</type>\n" +
					"    <succesful>true</succesful>\n" +
					"    <error_code>0</error_code>\n" +
					"    <error_details></error_details>\n" +
					"    <messages>\n";
			for(Message i:messages){
				finalXML+="<message>\n" +
						"	<contents>"+i.msg+"</contents>\n" +
						"	<encryption_duration>"+i.encryptDuration+"</encryption_duration>\n" +
						"</message>\n";
			}

			finalXML+=	"    </messages>\n" +
						"    <time_of_sending></time_of_sending>\n" +
						"</response>";
				dos.writeUTF(finalXML);
//			}
			return;
		}

		
		//They mark the locations of ':'
		int k1=-1;
		int k2=-1;
		int k3=-1;
		int k4=-1;
		
		char[] requestContents=contents.toCharArray();
		for(int i=0;i<requestContents.length;i++){
			if(requestContents[i] == ':' && (k1==-1 || k2==-1 || k3==-1 || k4==-1)){
				
				
				//Now that i think about it I could have just used the split() of String
				//If k1 == -1 then that means that this is the first ':' encountered
				//silly way to do it but if it looks stupid but it works, it ain't stupid
				if(k1==-1){
					k1=i;
				}else if(k2==-1){
					k2=i;
				}else if(k3==-1){
					k3=i;
				}else if(k4==-1){
					k4=i;
				}

				
				continue;
			}

			if(k1==-1){
				senderNick+=requestContents[i];
			}else if(k2==-1){
				request_type+=requestContents[i];
			}else if(k3==-1){
				receiverNick+=requestContents[i];
			}else if(k4==-1){
				password+=requestContents[i];
			}else{
				message+=requestContents[i];
			}
		}
		
		System.out.println("HIT");
		
		//At this point, appropriate value must be set in senderNick, message, request_type and receiverNick

		//All formats are from the point of view of sender.
		//Formats
		//SEND - senderNick:SEND:receiverNick:password:message
		//RECEIVE - senderNick:RECEIVE:receiverNick:password:
		//CHECK - senderNick:CHECK::password:message (message contains string of usernames seperated by ';')
		//SIGN_UP - senderNick:SIGN_UP::password:
		//AUTHENTICATE - senderNick:AUTHENTICATE::password:
		//SEND_LOG - senderNick:SEND_LOG:::message
		//GET_LOG - senderNick:GET_LOG::password::
		if(senderNick == "" || request_type=="" || (request_type.equals(REQUEST_TYPES[0]) && (message=="" || receiverNick=="")) || (request_type.equals(REQUEST_TYPES[1]) && receiverNick.equals("")) || 
				(request_type.equals(REQUEST_TYPES[2]) && message.equals("")) || (request_type.equals(REQUEST_TYPES[3]) && (password.equals("") || password==null)) || (request_type.equals(REQUEST_TYPES[4]) && password.equals(""))){
			throw new RuntimeException("Something is set null");
		}
		
		ClientHandler clientHandler=(ClientHandler)getServletContext().getAttribute("clientHandler");

		System.out.println(String.format("\nSender : %s\nRequest type : %s\nReceiver Nick : %s\nMessage : %s	\nPassword : %s", senderNick, request_type, receiverNick, message, password));
		
		if(!request_type.equals(REQUEST_TYPES[3]) && !request_type.equals(REQUEST_TYPES[4]) &&
				!request_type.equals(REQUEST_TYPES[5]) && !request_type.equals(REQUEST_TYPES[6])){
			if(!clientHandler.isClientAuthentic(senderNick, password)){
				DataOutputStream dos=new DataOutputStream(response.getOutputStream());
				dos.writeUTF("Authentication failed. The client is either not registered or the password is wrong. ");
				return;
			}
		}
		
		if(request_type.equals(REQUEST_TYPES[0])){
//			sendMessage(message, senderNick, receiverNick, controller);
		}else if(request_type.equals(REQUEST_TYPES[1])){
			DataOutputStream dos = new DataOutputStream(response.getOutputStream());
//			dos.writeUTF(String.format("\nSender : %s\nRequest type : %s\nMessage : %s\n\nOld messages : %s\n\n", senderNick, request_type, message, checkForNewMessages(senderNick)));
//			String newMessages
			ArrayList<Message> messages
					=checkForNewMessages(senderNick, receiverNick, controller);
//			if(newMessages!=null && newMessages!=""){
//				dos.writeUTF(newMessages);
//			}
			if(messages!=null){
				dos.writeUTF("RECEIVING MESSAGES USING THIS CODE IS NOW INCOMPATIBLE");
			}
		}else if(request_type.equals(REQUEST_TYPES[2])){
			DataOutputStream dos=new DataOutputStream(response.getOutputStream());
			HashMap<String, Integer> info=getAllMessagesInfo(senderNick, message);
			String finalMessage="";
			Set<String> keys=info.keySet();
			for(String string:keys){
				finalMessage+=string+":"+info.get(string)+";";
			}
			System.out.println(finalMessage);
			if(finalMessage!= null && finalMessage!=""){
				dos.writeUTF(finalMessage);
			}
		}else if(request_type.equals(REQUEST_TYPES[3])){
			DataOutputStream dos=new DataOutputStream(response.getOutputStream());
			if(clientHandler.clientExists(senderNick)){
				dos.writeUTF("Client name already exists");
			}else{
				System.out.println("Registering new client by username - "+senderNick+" and password - "+password);
				clientHandler.registerClient(senderNick, password, controller);
			}
		}else if(request_type.equals(REQUEST_TYPES[4])){
			DataOutputStream dos=new DataOutputStream(response.getOutputStream());
			if(clientHandler.isClientAuthentic(senderNick, password)){
				System.out.println("Client is authentic");
				dos.writeUTF("T");
			}else{
				System.out.println("Client is not authentic");
				dos.writeUTF("F");
			}
			dos.flush();
			dos.close();
			
		}else if(request_type.equals(REQUEST_TYPES[5])){
			appLogs=appLogs+message;
//			BlobstoreService blobstoreService= BlobstoreServiceFactory.getBlobstoreService();

		}else if(request_type.equals(REQUEST_TYPES[6])){
			if(senderNick.equals(ADMIN_USER_NAME) && password.equals(ADMIN_PASSWORD)){
				DataOutputStream dos=new DataOutputStream(response.getOutputStream());
				dos.writeUTF(appLogs);
				dos.flush();
				dos.close();
			}
		}

	}
	
	public HashMap<String, Integer> getAllMessagesInfo(String senderNick, String message){
		String[] senders=message.split(";");
		ClientHandler clientHandler=(ClientHandler)getServletContext().getAttribute("clientHandler");
		return clientHandler.getAllInfo(senderNick, senders);
	}
	
	public void sendMessage(String message, String senderNick, String receiverNick, JDBCController controller, int encryptDuration){
		((ClientHandler)getServletContext().getAttribute("clientHandler")).sendToClient(message, senderNick, receiverNick, controller, encryptDuration);
	}
	
	public ArrayList<Message> checkForNewMessages(String nick, String senderNick, JDBCController controller){
		ClientHandler clientHandler=((ClientHandler)getServletContext().getAttribute("clientHandler"));
		String newMessages="";
		ArrayList<Message> newMessagesList=clientHandler.checkClient(nick, senderNick, controller);
//		if(newMessagesList!=null && newMessagesList.size()>0){
//			for(int i=0;i<newMessagesList.size();i++){
//				newMessages+=newMessagesList.get(i);
//			}
//			clientHandler.clearMessages(nick, senderNick);
//			return newMessages;
//		}
		return newMessagesList;
	}
	
}