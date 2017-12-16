package com.sanilk.chatCliBackend;

import java.util.ArrayList;
import java.util.HashMap;

public class Client {
	public String nick;
	private String password;
	
	//frankly, toSend doesn't make a lot of sense
	//and received would be better as a Queue
	private ArrayList<Message> received;
	private ArrayList<Message> toSend;
	
	private Client(){}
	
	public Client(String nick, String password){
		this.nick=nick;
		this.password=password;
		
		received=new ArrayList();
		toSend=new ArrayList();
	}
	
	public String getPassword() {
		return password;
	}
	
	//receivers denotes the arrayList of the people the client is connected to
	public HashMap<String, Integer> getAllInfo(String senderNick, String[] senders){
		HashMap<String, Integer> info=new HashMap();
		for(String string:senders){
			for(Message message:received){
				if(message.from.nick.equals(string)){
					if(!info.containsKey(string)){
						info.put(string, 0);
					}
					info.put(string, info.get(string)+1);
				}
			}
		}
		return info;
	}
	
	public void sendMessage(String text, Client receiver,  int encryptDuration){
		Message msg=new Message(text, encryptDuration);
		msg.from=this;
		msg.to=receiver;
		receiver.receiveMessage(msg);
	}
	
	public void receiveMessage(Message message){
		received.add(message);
	}
	
	//Looking for messages sent by nick of sender
	public ArrayList<Message> checkMessages(String sender){
		ArrayList<Message> forThisClient=new ArrayList();
		for(int i=0;i<received.size();i++){
			Message message=received.get(i);
			if(message.from.nick.equals(sender)){
				forThisClient.add(message);
				received.remove(i);
				i--;
			}
		}
		return forThisClient;
	}
	
	public void clearMessages(String senderNick){
		received.clear();
	}
	
	public static class Message{
		public int encryptDuration;
		public boolean delivered=false;
		public String msg=null;
		public Client to;
		public Client from;
		
		private Message(){}
		
		public Message(String msg, int encryptDuration){
			this.msg=msg;
			this.encryptDuration=encryptDuration;
		}
		
		public void delivered(){
			this.delivered=true;
		}
		
		@Override
		public String toString() {
			return msg;
		}
	}

}
