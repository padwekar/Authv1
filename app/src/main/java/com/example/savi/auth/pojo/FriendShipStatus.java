package com.example.savi.auth.pojo;

public class FriendShipStatus {

    //FRIENDSHIP STATUS INFORMATION
    public final static int NOT_FRIENDS = 0 ;
    public final static int REQUEST_PENDING = NOT_FRIENDS + 1 ;
    public final static int FRIENDS =  REQUEST_PENDING + 1;

    private String uid ;

    private int status ;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {

        if (getClass() != obj.getClass())
            return false;

        FriendShipStatus friendShipStatus = (FriendShipStatus)obj ;
        return uid.equals(friendShipStatus.uid);
    }
}
