package com.example.savi.auth.operation;

import com.android.volley.Request;
import com.example.savi.auth.utils.AuthException;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Savi on 23-10-2016.
 */

public class SendFriendRequestOperation extends WebServiceOperation{

    public OnSendFriendRequestOperation mOnSendFriendRequestOperation;

    public interface  OnSendFriendRequestOperation{
        public void SendFriendRequestOperationSuccess(Object o);
        public void SendFriendRequestOperationFailure(Exception e);
    }


    public SendFriendRequestOperation(String uri, Map<String, String> header,String body,OnSendFriendRequestOperation listener) {
        super(uri, Request.Method.POST, header,body, Object.class, SendFriendRequestOperation.class.getName());
        mOnSendFriendRequestOperation = listener ;
    }

    @Override
    public void onSuccess(Object response) {
        if(mOnSendFriendRequestOperation!=null){
            mOnSendFriendRequestOperation.SendFriendRequestOperationSuccess(response);
        }
    }

    @Override
    public void onError(AuthException exception) {
        if(mOnSendFriendRequestOperation!=null){
            mOnSendFriendRequestOperation.SendFriendRequestOperationFailure(exception);
        }
    }

    public void addToRequestQueue(){
        super.addToRequestQueue();
    }
}
