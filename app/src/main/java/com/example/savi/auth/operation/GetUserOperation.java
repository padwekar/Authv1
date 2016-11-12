package com.example.savi.auth.operation;

import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.constant.ErrorConstants;
import com.example.savi.auth.pojo.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class GetUserOperation {

    private Firebase fireBaseRef = new Firebase( Constants.TODOCLOUD_ROOT_FIREBASE_URL + Constants.USER_DETAIL);
    public OnGetUserOperationListener onGetUserOperationListener ;

    public GetUserOperation(String uid , int type ,OnGetUserOperationListener listener){
        onGetUserOperationListener = listener ;
        if(type==Constants.VALUE_EVENT_LISTENER)addValueEventListener(uid);else
        if(type==Constants.SINGLE_VALUE_EVENT_LISTENER) addSingleValueEventListener(uid);
    }

    private void addValueEventListener(String uid) {
        fireBaseRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user ;
                if(onGetUserOperationListener!=null){
                    if(dataSnapshot==null || (user =dataSnapshot.getValue(User.class))==null){onCancelled(new FirebaseError(-400 , ErrorConstants.USER_NOT_FOUND));return;}
                    onGetUserOperationListener.OnGetUserOperationSuccess(user);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(onGetUserOperationListener!=null) {
                    onGetUserOperationListener.OnGetUserOperationError(firebaseError);
                }

            }
        });
    }

    private void addSingleValueEventListener(String uid) {
        fireBaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user ;
                if(onGetUserOperationListener!=null){
                    if(dataSnapshot==null || (user =dataSnapshot.getValue(User.class))==null){onCancelled(new FirebaseError(-400 , ErrorConstants.USER_NOT_FOUND));return;}
                    onGetUserOperationListener.OnGetUserOperationSuccess(user);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(onGetUserOperationListener!=null) {
                    onGetUserOperationListener.OnGetUserOperationError(firebaseError);
                }
            }
        });

    }

    public interface OnGetUserOperationListener {
        void OnGetUserOperationSuccess(User user);
        void OnGetUserOperationError(FirebaseError error);
    }
}
