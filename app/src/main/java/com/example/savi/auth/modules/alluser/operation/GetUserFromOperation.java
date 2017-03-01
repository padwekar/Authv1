package com.example.savi.auth.modules.alluser.operation;

import com.example.savi.auth.constant.URLConstants;
import com.example.savi.auth.operation.BaseOperation;
import com.example.savi.auth.pojo.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class GetUserFromOperation extends BaseOperation {

    public interface OnUserListener{
        void onUserAdded(User user);
        void onUserUpdated(User user);
        void onUserRemoved(User user);
        void onCancelled(FirebaseError error);
    }

    int count = 0 ;
    private int limit = 3 ;
    public GetUserFromOperation(final String fromUid, final int limitLast, final OnUserListener listener) {
        limit = limitLast ;
        Firebase fireBaseRef = new Firebase(URLConstants.TODOCLOUD_FIREBASE_ROOT_URL + URLConstants.USER_DETAIL);
        fireBaseRef.startAt(null,fromUid).limitToFirst(limit).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(isEmpty(dataSnapshot)||listener==null || dataSnapshot.getKey().equals(fromUid))return;
                User user = dataSnapshot.getValue(User.class);
                if (count++ ==limit - 1) {
                    user.setLast(true);
                    count=0;
                }
                listener.onUserAdded(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
