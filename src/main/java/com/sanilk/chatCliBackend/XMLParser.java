package com.sanilk.chatCliBackend;

import com.sanilk.chatCliBackend.Requests.MyRequest;
import com.sanilk.chatCliBackend.Requests.authenticate.AuthenticateRequest;
import com.sanilk.chatCliBackend.Requests.receive.ReceiveRequest;
import com.sanilk.chatCliBackend.Requests.send.SendRequest;
import com.sanilk.chatCliBackend.Requests.sign_up.SignUpRequest;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by root on 15/11/17.
 */
public class XMLParser {
    private final static boolean DEBUG=true;

    public static MyRequest parseXML(String xmlText){
        try {
            StringReader reader = new StringReader(xmlText);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(reader);

            XMLEvent e;
            while(xmlEventReader.hasNext()){
                e=xmlEventReader.nextEvent();

                while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(MyRequest.TYPE))) {
                    e=xmlEventReader.nextEvent();
                }
                e=xmlEventReader.nextEvent();
                if(e.isCharacters()) {
                    switch (e.asCharacters().getData()) {
                        case SendRequest.TYPE:
                            return parseSendRequest(xmlEventReader);
                        case AuthenticateRequest.TYPE:
                            return parseAuthenticateRequest(xmlEventReader);
                        case SignUpRequest.TYPE:
                            return parseSignUpRequest(xmlEventReader);
                        case ReceiveRequest.TYPE:
                            return parseReceiveRequest(xmlEventReader);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static ReceiveRequest parseReceiveRequest(XMLEventReader eventReader) throws Exception{
        String senderNick="";
        String senderPassword="";
        String receiverNick="";
        XMLEvent e=eventReader.nextEvent();
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(ReceiveRequest.SENDER_NICK))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()){
            senderNick=e.asCharacters().getData();
            if(DEBUG){
                System.out.println("senderNick - "+senderNick);
            }
        }
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(ReceiveRequest.RECEIVER_NICK))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()){
            receiverNick=e.asCharacters().getData();
            if(DEBUG){
                System.out.println("receiverNick - "+receiverNick);
            }
        }
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(ReceiveRequest.SENDER_PASSWORD))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()){
            senderPassword=e.asCharacters().getData();
            if(DEBUG){
                System.out.println("senderPassword - "+senderPassword);
            }
        }

        return new ReceiveRequest(senderNick, receiverNick, senderPassword);
    }

    static SignUpRequest parseSignUpRequest(XMLEventReader eventReader) throws Exception{
        String nick="";
        String password="";
        XMLEvent e=eventReader.nextEvent();
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SignUpRequest.SENDER_NICK))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()){
            nick=e.asCharacters().getData();
            if(DEBUG){
                System.out.println("Nick - "+nick);
            }
        }
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SignUpRequest.SENDER_PASSWORD))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()){
            password=e.asCharacters().getData();
            if(DEBUG){
                System.out.println("Password - "+password);
            }
        }

        return new SignUpRequest(nick, password);
    }

    static SendRequest parseSendRequest(XMLEventReader eventReader) throws Exception{
        XMLEvent e=eventReader.nextEvent();
        String senderNick="";
        String senderPassword="";
        String receiverNick="";
        ArrayList<Client.Message> messages=new ArrayList<>();
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.SENDER_NICK))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()) {
            senderNick = e.asCharacters().getData();
            if(DEBUG){
                System.out.println("SenderNick - "+senderNick);
            }
        }
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.RECEIVER_NICK))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()) {
            receiverNick = e.asCharacters().getData();
            if(DEBUG){
                System.out.println("ReceiverNick - "+receiverNick);
            }
        }
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.SENDER_PASSWORD))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()) {
            senderPassword = e.asCharacters().getData();
            if(DEBUG){
                System.out.println("SenderPass - "+senderPassword);
            }
        }
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.MESSAGE_ROOT))){
            e=eventReader.nextEvent();
        }
        while(true) {
            String message="";
            int encryptDuration=-2;
            while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.MESSAGE))){
                e=eventReader.nextEvent();
            }
            e = eventReader.nextEvent();
            while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.CONTENTS))){
                e=eventReader.nextEvent();
            }
            e=eventReader.nextEvent();
            if (e.isCharacters()) {
                message = e.asCharacters().getData();
                if (DEBUG) {
                    System.out.println("Message - " + message);
                }
            }
            while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.ENCRYPTION_DURATION))){
                e=eventReader.nextEvent();
            }
            e = eventReader.nextEvent();
            if (e.isCharacters()) {
                encryptDuration=Integer.parseInt(e.asCharacters().getData());
                if (DEBUG) {
                    System.out.print("\tEncrypt duration - " + encryptDuration);
                }
            }

            if(!(encryptDuration==-2 || message=="")){
                messages.add(new Client.Message(message, encryptDuration));
            }

            while(!((e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.MESSAGE)) ||
                    (e.isEndElement() && e.asEndElement().getName().toString().equals(SendRequest.MESSAGE_ROOT)))){
                e=eventReader.nextEvent();
            }
            if(e.isStartElement() && e.asStartElement().getName().toString().equals(SendRequest.MESSAGE)){
                continue;
            }else{
                break;
            }
        }

        return new SendRequest(senderNick, receiverNick, senderPassword, messages);
    }

    static AuthenticateRequest parseAuthenticateRequest(XMLEventReader eventReader) throws Exception{
        XMLEvent e;
        String senderNick="";
        String senderPassword="";
        e=eventReader.nextEvent();
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(AuthenticateRequest.SENDER_NICK))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()) {
            senderNick = e.asCharacters().getData();
            if(DEBUG){
                System.out.println("SenderNick - "+senderNick);
            }
        }
        while(!(e.isStartElement() && e.asStartElement().getName().toString().equals(AuthenticateRequest.SENDER_PASSWORD))){
            e=eventReader.nextEvent();
        }
        e=eventReader.nextEvent();
        if(e.isCharacters()) {
            senderPassword = e.asCharacters().getData();
            if(DEBUG){
                System.out.println("SenderPass - "+senderPassword);
            }
        }

        return new AuthenticateRequest(senderNick, senderPassword);
    }
}
