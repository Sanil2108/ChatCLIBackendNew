package com.sanilk.chatCliBackend.Requests.receive;

import com.sanilk.chatCliBackend.Requests.MyRequest;

/**
 * Created by root on 16/11/17.
 */
public class ReceiveRequest extends MyRequest{
    //constants
    public final static String TYPE ="RECEIVE";
    public final static String SENDER_NICK="sender_nick";
    public final static String RECEIVER_NICK="receiver_nick";
    public final static String SENDER_PASSWORD="sender_password";

    public String senderNick;
    public String receiverNick;
    public String senderPass;

    public ReceiveRequest(String senderNick, String receiverNick, String senderPass){
        this.senderNick=senderNick;
        this.receiverNick=receiverNick;
        this.senderPass=senderPass;
    }
}
