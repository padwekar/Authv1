package com.example.savi.auth.operation.manager;

import com.example.savi.auth.model.NotificationRequest;
import com.example.savi.auth.operation.SendFriendRequestOperation;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Savi on 23-10-2016.
 */

public class SocialManager {

    private OnSendFriendRequest onSendFriendRequest ;


    public interface OnSendFriendRequest{
       void OnSendFriendRequestSuccess(Object o);
       void OnSendFriendRequestFailure(Exception e);


    }


    public void sendFriendRequest(NotificationRequest request , OnSendFriendRequest listener){
        String body = new Gson().toJson(request) ;
        onSendFriendRequest = listener ;
        Map<String,String> map = new HashMap<>();
        map.put("Authorization","key=AIzaSyCTsuGE-TlKCWuqM8ucE6joMhVASaj1H60");
        SendFriendRequestOperation operation = new SendFriendRequestOperation("https://fcm.googleapis.com/fcm/send", map, body,new SendFriendRequestOperation.OnSendFriendRequestOperation() {
            @Override
            public void SendFriendRequestOperationSuccess(Object o) {
                if(onSendFriendRequest!=null){
                    onSendFriendRequest.OnSendFriendRequestSuccess(o);
                }
            }

            @Override
            public void SendFriendRequestOperationFailure(Exception e) {
                if(onSendFriendRequest!=null){
                    onSendFriendRequest.OnSendFriendRequestFailure(e);
                }
            }
        });
        operation.addToRequestQueue();
    }


}
