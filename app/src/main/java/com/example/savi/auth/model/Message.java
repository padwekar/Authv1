package com.example.savi.auth.model;

/**
 * Created by Savi on 09-07-2016.
 */
public class Message {

    public final static int NEW = 0 ;
    public final static int UNREAD = 1 ;
    public final static int READ = 2 ;

    String senderUid ;
    String message ;
    String timeStamp ;
    int status ;

    public Message(String senderUid,String message, String timeStamp, int status) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.status = status;
        this.senderUid = senderUid ;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
