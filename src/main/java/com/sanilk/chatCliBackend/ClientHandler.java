package com.sanilk.chatCliBackend;

import com.sanilk.chatCliBackend.Client.Message;

import java.util.ArrayList;
import java.util.HashMap;

//The point of this class is to enable communication between client and the servlet
//Singleton
public class ClientHandler {
	
	private static ClientHandler clientHandler=new ClientHandler();
	
	public HashMap<String, Client> allClients;
	public Servlet1 servlet;
	
	private final static String DEFAULT_PASSWORD="raymon11";
	
	private ClientHandler(){
		allClients=new HashMap();
	}
	
	public static ClientHandler getInstance(){
		return clientHandler;
	}
	
	public void registerServlet(Servlet1 servlet){
		this.servlet=servlet;
	}
	
	public boolean isClientAuthentic(String nick, String password){
		if(allClients.containsKey(nick)){
			if(allClients.get(nick).getPassword().equals(password)){
				return true;
			}
		}
		return false;
	}
	
	public void registerClient(String nick, String password, JDBCController controller){
		Client client=new Client(nick, password);
		allClients.put(nick, client);
//		System.out.println("Registering new client by name "+nick);
//		controller.newEntry(new Client(nick));
	}
	
	public ArrayList<Message> checkClient(String nick, String senderNick, JDBCController controller){
		if(allClients.containsKey(nick)){
			Client client=allClients.get(nick);
			ArrayList<Message> received=client.checkMessages(senderNick);
			return received;
		}else{
//			registerClient(nick, DEFAULT_PASSWORD, controller);
			return null;
		}
	}
	
	public void clearMessages(String nick, String senderNick){
		if(allClients.containsKey(nick)){
			Client client=allClients.get(nick);
			client.clearMessages(senderNick);
		}
	}
	
	public HashMap<String, Integer> getAllInfo(String sender, String[] allSenders){
		if(allClients.containsKey(sender)){
			Client client=allClients.get(sender);
			return client.getAllInfo(sender, allSenders);
		}
		return null;
	}
	
	public void sendToClient(String message, String nick, String receiverNick, JDBCController controller, int encryptDuration){
		if(allClients.containsKey(nick)){
			if(allClients.containsKey(receiverNick)){
				Client client=allClients.get(nick);
				Client rcvr=allClients.get(receiverNick);
				client.sendMessage(message, rcvr, encryptDuration);
			}
		}else{
			//Commenting this part, not sure what to do
//			registerClient(nick, DEFAULT_PASSWORD, controller);
		}
	}
	
	public boolean clientExists(String nick){
		if(allClients.containsKey(nick)){
			return true;
		}
		return false;
	}

}
