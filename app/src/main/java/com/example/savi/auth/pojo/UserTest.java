package com.example.savi.auth.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserTest  {

    private String uid ;
    private String displayName ;
    private String status ;

    private List<String> stringList ;
    private List<MessageItem> messageItemList;
    private transient String[] baate = {"hi","hello","maushi","Rikami","bomkhanli"};
    private transient MessageItem[] messagesArray = { new MessageItem("67","hi baby","12345",2),new MessageItem("67","hi baby","12345",2),new MessageItem("23","live life","2222",82),new MessageItem("70","get lost","54321",22)};

    public UserTest(){
        uid = "no_uid"; ;
        displayName = "no_name" ;
        status = "no_status" ;
        stringList = Arrays.asList(baate);
        messageItemList = new ArrayList<>();
        messageItemList.addAll(Arrays.asList(messagesArray));
        messageItemList.addAll(Arrays.asList(messagesArray));
        messageItemList.addAll(Arrays.asList(messagesArray));
        messageItemList.addAll(Arrays.asList(messagesArray));
    }

    public void addToMessageList(MessageItem messageItem){
        messageItemList.add(messageItem);
    }
    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<MessageItem> getMessageItemList() {
        return messageItemList;
    }


    public void setMessageItemList(List<MessageItem> messageItemList) {
        this.messageItemList = messageItemList;
    }

    public UserTest(String uid, String displayName, String status) {
        this.uid = uid;
        this.displayName = displayName;
        this.status = status;
        stringList = Arrays.asList(baate);
        messageItemList = new ArrayList<>();
        messageItemList.addAll(Arrays.asList(messagesArray));
        messageItemList.addAll(Arrays.asList(messagesArray));
        messageItemList.addAll(Arrays.asList(messagesArray));
        messageItemList.addAll(Arrays.asList(messagesArray));
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

