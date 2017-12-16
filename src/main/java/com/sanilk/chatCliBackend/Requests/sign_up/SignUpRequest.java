package com.sanilk.chatCliBackend.Requests.sign_up;

import com.sanilk.chatCliBackend.Requests.MyRequest;

/**
 * Created by root on 16/11/17.
 */
public class SignUpRequest extends MyRequest {
    //constants
    public final static String TYPE ="SIGN_UP";
    public final static String SENDER_NICK="sender_nick";
    public final static String SENDER_PASSWORD="sender_password";

    public String senderNick;
    public String senderPassword;

    public SignUpRequest(String senderNick, String senderPassword){
        this.senderNick=senderNick;
        this.senderPassword=senderPassword;
    }
}
