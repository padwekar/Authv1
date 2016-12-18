package com.example.savi.auth.modules.profile.operation;

import com.example.savi.auth.constant.URLConstants;
import com.example.savi.auth.operation.BaseOperation;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class VerifyUserNameOperation extends BaseOperation {

    public VerifyUserNameOperation(String username , final OnVerifyUserNameOperation listener) {
        Firebase fireBaseRef = new Firebase(URLConstants.TODOCLOUD_FIREBASE_ROOT_URL + URLConstants.USER_ID);
        fireBaseRef.orderByKey().equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(listener!=null){
                 if(isEmpty(dataSnapshot) || !dataSnapshot.getChildren().iterator().hasNext()){
                     listener.OnVerifyUserNameOperationSuccess(true);
                 }else {
                     listener.OnVerifyUserNameOperationSuccess(false);
                 }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(listener!=null){
                    listener.OnVerifyUserNameOperationError(firebaseError);
                }
            }
        });
    }

    public interface OnVerifyUserNameOperation{
        void OnVerifyUserNameOperationSuccess(boolean isAvailable);
        void OnVerifyUserNameOperationError(FirebaseError error);
    }
}
