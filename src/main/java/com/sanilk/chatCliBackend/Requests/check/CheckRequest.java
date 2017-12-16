package com.sanilk.chatCliBackend.Requests.check;

import com.sanilk.chatCliBackend.Requests.MyRequest;

import java.util.ArrayList;

/**
 * Created by root on 25/11/17.
 */
public class CheckRequest extends MyRequest {
    public final static String TYPE ="CHECK";
    public final static String RECEIVERS="receivers";
    public final static String SENDER_NICK="sender_nick";
    public final static String SENDER_PASSWORD="sender_password";

    public String senderNick;
    public String senderPassword;
    public Receivers receivers;

    public CheckRequest(String senderNick, Receivers receivers, String senderPassword){
        this.senderNick=senderNick;
        this.senderPassword=senderPassword;
        this.receivers=receivers;
    }

    public static class Receivers{
        public final static ArrayList<Receiver> RECEIVERS=new ArrayList<>();
        public final static String RECEIVERS_ROOT="receivers";
        public final static String RECEIVERS_COUNT="receivers_count";

        public int receiversCount;

        public Receivers(int receiversCount){
            this.receiversCount=receiversCount;
        }

        public static class Receiver{
            public final static String RECEIVER_NAME="receiver_name";

            public String receiverName;

            Receiver(String receiverName){
                this.receiverName=receiverName;
            }
        }
    }
}

