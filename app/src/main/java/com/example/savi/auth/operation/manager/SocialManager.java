package com.example.savi.auth.operation.manager;

import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.operation.GetUserCircleIdsMapOperation;
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

public class SocialManager {

    public interface OnSendFriendRequest{
       void onSendFriendRequestSuccess(Object o);
       void onSendFriendRequestFailure(Exception e);
    }

    public interface OnGetContactedPersons{
        void onGetContactedPersonsSuccess(List<User> userList);
        void onGetContactedPersonsFailure(FirebaseError e);
    }

    public interface OnGetUserDetail{
        void onGetUserDetailSuccess(User user);
        void onGetUserDetailFailure(FirebaseError e);
    }

    public interface OnGetUserCircleMap{
        void onGetUserCircleMapSuccess(HashMap<String,Integer> circleMap);
        void onGetUserCircleMapFailure(FirebaseError error);
    }


    public void getUserCircleMap(String uid, int type , final OnGetUserCircleMap listener){
        new GetUserCircleIdsMapOperation(uid, type, new GetUserCircleIdsMapOperation.GetUserCircleMapOperationListener() {
            @Override
            public void onGetUserCircleMapOperationListenerSuccess(HashMap<String, Integer> circleMap) {
                if(listener==null)return;
                listener.onGetUserCircleMapSuccess(circleMap);
            }

            @Override
            public void onGetUserCircleMapOperationListenerFailure(FirebaseError error) {
                if(listener==null)return;
                listener.onGetUserCircleMapFailure(error);
            }
        });
    }

    public void getUserCircleMap(String uid , final  OnGetUserCircleMap listener){
        getUserCircleMap(uid,Constants.VALUE_EVENT_LISTENER,listener);
    }

    public void sendFriendRequest(NotificationRequest request ,final OnSendFriendRequest listener){
        String body = new Gson().toJson(request) ;
        Map<String,String> map = new HashMap<>();
        map.put("Authorization","key=AIzaSyCTsuGE-TlKCWuqM8ucE6joMhVASaj1H60");
        SendFriendRequestOperation operation = new SendFriendRequestOperation("https://fcm.googleapis.com/fcm/send", map, body,new SendFriendRequestOperation.OnSendFriendRequestOperation() {
            @Override
            public void SendFriendRequestOperationSuccess(Object o) {
                if(listener!=null){
                    listener.onSendFriendRequestSuccess(o);
                }
            }

            @Override
            public void SendFriendRequestOperationFailure(Exception e) {
                if(listener!=null){
                    listener.onSendFriendRequestFailure(e);
                }
            }
        });
        operation.addToRequestQueue();
    }


    public void getContactedPerson(String uid , int status , final int limit , final OnGetContactedPersons listener){
      new GetContactedPersonOperation(uid,status, limit, new GetContactedPersonOperation.OnGetContactedPersonOperation() {
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

    public void getUserDetails(String uid, int type, final OnGetUserDetail listener){
        new GetUserOperation(uid, type, new GetUserOperation.OnGetUserOperationListener() {
            @Override
            public void OnGetUserOperationSuccess(User user) {
                if(listener!=null){
                    listener.onGetUserDetailSuccess(user);
                }
            }

            @Override
            public void OnGetUserOperationError(FirebaseError error) {
                if(listener!=null){
                    listener.onGetUserDetailFailure(error);
                }
            }
        });
    }

    public void getUserDetails(String uid ,final OnGetUserDetail listener){
        getUserDetails(uid, Constants.VALUE_EVENT_LISTENER,listener);
    }


}
