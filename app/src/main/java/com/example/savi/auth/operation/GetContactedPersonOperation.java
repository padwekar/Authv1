package com.example.savi.auth.operation;


import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.constant.ErrorConstants;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.operation.manager.SocialManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.savi.auth.constant.Constants.TODOCLOUD_ROOT_FIREBASE_URL;
import static com.example.savi.auth.constant.Constants.USER_DETAIL;

public class GetContactedPersonOperation {

    private OnGetContactedPersonOperation onGetContactedPersonOperation ;
    private SocialManager manager = new SocialManager();
    private int size = 0 ;

    public GetContactedPersonOperation(String uid,String map ,int status , int limit , OnGetContactedPersonOperation listener){
        this.onGetContactedPersonOperation = listener ;
        Firebase fireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL + USER_DETAIL);
        fireBaseRef.child(uid).child(map).orderByValue().equalTo(status).limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(onGetContactedPersonOperation!=null){
                    if(dataSnapshot==null || dataSnapshot.getChildren()==null){ onCancelled(new FirebaseError(-400, ErrorConstants.NO_DATA_FOUND)); return;}
                    final List<User> userList = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        manager.getUserDetails(snapshot.getKey(), Constants.SINGLE_VALUE_EVENT_LISTENER, new GetUserOperation.OnGetUserOperationListener() {
                            @Override
                            public void OnGetUserOperationSuccess(User user) {
                                userList.add(user);
                                if(userList.size()==size){
                                    onGetContactedPersonOperation.onGetContactedPersonOperationSuccess(userList);
                                    size=0;
                                }
                            }

                            @Override
                            public void OnGetUserOperationError(FirebaseError error) {
                                onCancelled(error);
                            }
                        });
                        size++;
                    }
                   if(size==0)onCancelled(new FirebaseError(-400,ErrorConstants.NO_DATA_FOUND));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                    onGetContactedPersonOperation.onGetContactedPersonOperationFailure(firebaseError);
            }
        });
    }


    public interface OnGetContactedPersonOperation {
        void onGetContactedPersonOperationSuccess(List<User> userList);
        void onGetContactedPersonOperationFailure(FirebaseError error);
    }
}


