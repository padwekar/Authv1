package com.example.savi.auth.operation.manager;

import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.pojo.NotificationRequest;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.operation.GetContactedPersonOperation;
import com.example.savi.auth.operation.GetUserOperation;
import com.example.savi.auth.operation.SendFriendRequestOperation;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Savi on 23-10-2016.
 */

public class SocialManager {

    private OnSendFriendRequest onSendFriendRequest ;
    private OnGetContactedPersons onGetContactedPersons ;

    public interface OnSendFriendRequest{
       void onSendFriendRequestSuccess(Object o);
       void onSendFriendRequestFailure(Exception e);
    }

    public interface OnGetContactedPersons{
        void onGetContactedPersonsSuccess(List<User> userList);
        void onGetContactedPersonsFailure(FirebaseError e);
    }

    public interface OnGetUserDetail{
        void onGetUserDetailSuccess(Object o);
        void onGetUserDetailFailure(Exception e);
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
                    onSendFriendRequest.onSendFriendRequestSuccess(o);
                }
            }

            @Override
            public void SendFriendRequestOperationFailure(Exception e) {
                if(onSendFriendRequest!=null){
                    onSendFriendRequest.onSendFriendRequestFailure(e);
                }
            }
        });
        operation.addToRequestQueue();
    }


    public void getContactedPerson(String uid , String map , int status , final int limit , final OnGetContactedPersons listener){
      new GetContactedPersonOperation(uid, map, status, limit, new GetContactedPersonOperation.OnGetContactedPersonOperation() {
            @Override
            public void onGetContactedPersonOperationSuccess(List<User> userList) {
                if(listener!=null){
                    listener.onGetContactedPersonsSuccess(userList);
                }
            }

            @Override
            public void onGetContactedPersonOperationFailure(FirebaseError error) {
                if(listener!=null){
                    listener.onGetContactedPersonsFailure(error);
                }
            }
        });
    }

    public void getUserDetails(String uid, int type, final GetUserOperation.OnGetUserOperationListener listener){
        new GetUserOperation(uid, type, new GetUserOperation.OnGetUserOperationListener() {
            @Override
            public void OnGetUserOperationSuccess(User user) {
                if(listener!=null){
                    listener.OnGetUserOperationSuccess(user);
                }
            }

            @Override
            public void OnGetUserOperationError(FirebaseError error) {
                if(listener!=null){
                    listener.OnGetUserOperationError(error);
                }
            }
        });
    }

    public void getUserDetails(String uid ,final GetUserOperation.OnGetUserOperationListener listener){
        getUserDetails(uid, Constants.VALUE_EVENT_LISTENER,listener);
    }


}
