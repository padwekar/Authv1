package com.example.savi.auth.operation;

import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.constant.OperationConstants;
import com.example.savi.auth.operation.manager.SocialManager;
import com.example.savi.auth.pojo.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.savi.auth.constant.Constants.CIRCLE;
import static com.example.savi.auth.constant.Constants.TODOCLOUD_ROOT_FIREBASE_URL;

public class GetContactedPersonOperation {

    private OnGetContactedPersonOperation onGetContactedPersonOperation ;
    private SocialManager manager = new SocialManager();
    private int size = 0 ;

    public GetContactedPersonOperation(String uid,int status , int limit , OnGetContactedPersonOperation listener){
        this.onGetContactedPersonOperation = listener ;
        Firebase fireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL + CIRCLE);
        fireBaseRef.child(uid).orderByValue().equalTo(status).limitToLast(limit).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(onGetContactedPersonOperation!=null){
                    if(dataSnapshot==null || dataSnapshot.getChildren()==null){ onCancelled(new FirebaseError(-400, OperationConstants.NO_DATA_FOUND)); return;}
                    final List<User> userList = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        manager.getUserDetails(snapshot.getKey(), Constants.VALUE_EVENT_LISTENER, new SocialManager.OnGetUserDetail() {
                            @Override
                            public void onGetUserDetailSuccess(User user) {
                                userList.add(user);
                                if(userList.size()==size){
                                    onGetContactedPersonOperation.onGetContactedPersonOperationSuccess(userList);
                                    size=0;
                                }
                            }

                            @Override
                            public void onGetUserDetailFailure(FirebaseError e) {
                                onCancelled(e);
                            }
                        });
                        size++;
                    }
               if(size==0)onGetContactedPersonOperation.onGetContactedPersonOperationSuccess(userList);
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


