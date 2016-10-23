package com.example.savi.auth.model;


import java.util.LinkedHashMap;
import java.util.List;

public class User  {

    final static int MSG_USER_DATA_SUCCESS_IN = 0 ;
    final static int MSG_USER_DATA_SUCCESS_OUT = MSG_USER_DATA_SUCCESS_IN+1 ;

    private String uid ;
    private String displayName ;
    private String status ;
    private boolean isVisible ;
    private String email ;
    private String token ;
    private int picPosition ;
    private LinkedHashMap<String,List<MessageItem>> messageMap ;
    private String profileDownloadUri ;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProfileDownloadUri() {
        return profileDownloadUri;
    }

    public void setProfileDownloadUri(String profileDownloadUri) {
        this.profileDownloadUri = profileDownloadUri;
    }

    public LinkedHashMap<String, List<MessageItem>> getMessageMap() {
        return messageMap;
    }


    public User() {
        super();
    }

    public void setMessageMap(LinkedHashMap<String, List<MessageItem>> messageMap) {
        this.messageMap = messageMap;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPicPosition() {
        return picPosition;
    }

    public void setPicPosition(int picPosition) {
        this.picPosition = picPosition;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
