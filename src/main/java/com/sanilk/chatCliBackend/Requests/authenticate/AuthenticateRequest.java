package com.sanilk.chatCliBackend.Requests.authenticate;

import com.sanilk.chatCliBackend.Requests.MyRequest;

/**
 * Created by root on 15/11/17.
 */
public class AuthenticateRequest extends MyRequest {
    //constants
    public final static String TYPE="AUTHENTICATE";
    public final static String SENDER_NICK="sender_nick";
    public final static String SENDER_PASSWORD="sender_password";

    public String senderNick;
    public String senderPass;

    public AuthenticateRequest(String senderNick, String senderPass){
        this.senderNick=senderNick;
        this.senderPass=senderPass;
    }
}
