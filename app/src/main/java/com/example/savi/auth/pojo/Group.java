package com.example.savi.auth.pojo;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public static final int OPEN_GROUP = 0 ;
    public static final int ACTIVE_GROUP = 1 + OPEN_GROUP ;
    public static final int CLOSED_GROUP = 1 + ACTIVE_GROUP ;
    public static final String[] headers = {"Open" , "Active" , "Closed"};

    public static final int TYPE_FREE = 0 ;
    public static final int TYPE_PROTECTED = 1 + TYPE_FREE ;


    private int id ;
    private String name ;
    private int maxMembers ;
    private int type ;
    private int status ;
    private String password ;
    private List<User> activeUsers = new ArrayList<>();
    private String headerName ;
    private boolean isHeader = false;
    public transient boolean isOpen = false ;

    public boolean isHeader() {
        return isHeader;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public Group(){
    }

    public Group(String headerName){
        isHeader = true ;
        this.headerName = headerName ;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<User> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<User> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
