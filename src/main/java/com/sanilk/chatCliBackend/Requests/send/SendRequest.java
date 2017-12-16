package com.sanilk.chatCliBackend.Requests.send;

import com.sanilk.chatCliBackend.Client;
import com.sanilk.chatCliBackend.Requests.MyRequest;

import java.util.ArrayList;

/**
 * Created by root on 15/11/17.
 */
public class SendRequest extends MyRequest{
    //constants
    public final static String TYPE ="SEND";
    public final static String SENDER_NICK="sender_nick";
    public final static String RECEIVER_NICK="receiver_nick";
    public final static String MESSAGE_ROOT="messages";
    public final static String MESSAGE="message";
    public final static String CONTENTS="contents";
    public final static String ENCRYPTION_DURATION="encrypt_duration";
    public final static String SENDER_PASSWORD="sender_password";

    public String senderNick;
    public String receiverNick;
    public String senderPass;
    public ArrayList<Client.Message> messages;



    public SendRequest(String senderNick, String receiverNick, String senderPass, ArrayList<Client.Message> messages){
        this.senderNick=senderNick;
        this.receiverNick=receiverNick;
        this.senderPass=senderPass;
        this.messages=messages;
    }
}
