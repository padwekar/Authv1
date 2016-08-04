package com.example.savi.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Savi on 09-07-2016.
 */


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageItem {


    public final static int SELF_MESSAGE = 0 ;
    public final static int RESPONSE_MESSAGE = 1 ;

    public final static int NEW = 0 ;
    public final static int UNREAD = 1 ;
    public final static int READ = 2 ;

    String senderUid ;
    String senderBranchKey ;
    String message ;
    String timeStamp ;


    boolean self ;
    int status ;

    public MessageItem(){

    }

    public MessageItem(String senderUid, String message, String timeStamp, int status) {
       this(senderUid,message,timeStamp,status,false);
    }


    public MessageItem(String senderUid, String message, String timeStamp, int status, boolean isSelf) {
        this(senderUid,message,timeStamp,status,"",isSelf);

    }

    public MessageItem(String senderUid, String message, String timeStamp, int status,String senderBranchKey, boolean isSelf) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.status = status;
        this.senderUid = senderUid ;
        this.self  = isSelf ;
        this.senderBranchKey = senderBranchKey;
    }

    public String getSenderBranchKey() {
        return senderBranchKey;
    }

    public void setSenderBranchKey(String senderBranchKey) {
        this.senderBranchKey = senderBranchKey;
    }

    public boolean getSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
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
