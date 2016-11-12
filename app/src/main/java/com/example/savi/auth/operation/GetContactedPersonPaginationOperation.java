package com.example.savi.auth.operation;

import com.example.savi.auth.constant.ErrorConstants;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.savi.auth.constant.Constants.TODOCLOUD_ROOT_FIREBASE_URL;
import static com.example.savi.auth.constant.Constants.USER_DETAIL;

/**
 * Created by Savi on 12-11-2016.
 */

public class GetContactedPersonPaginationOperation {
    private GetContactedPersonOperation.OnGetContactedPersonOperation onGetContactedPersonOperation ;

    public GetContactedPersonPaginationOperation(String uid, String map , int status , int limit ,String endKey, GetContactedPersonOperation.OnGetContactedPersonOperation listener){
        this.onGetContactedPersonOperation = listener ;
        Firebase fireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL + USER_DETAIL);
        fireBaseRef.child(uid).child(map).orderByValue().equalTo(status).endAt(null, endKey).limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
        fireBaseRef.child(uid).child(map).orderByValue().equalTo(status).limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(onGetContactedPersonOperation!=null){
                    if(dataSnapshot==null || dataSnapshot.getChildren()==null){ onCancelled(new FirebaseError(-400, ErrorConstants.NO_DATA_FOUND)); return;}
                    List<String> uidList = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        uidList.add(snapshot.getKey());
                    }
                    if(uidList.size()==0)onCancelled(new FirebaseError(-400,ErrorConstants.NO_DATA_FOUND));
                  //  onGetContactedPersonOperation.onGetContactedPersonOperationSuccess(uidList);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                onGetContactedPersonOperation.onGetContactedPersonOperationFailure(firebaseError);
            }
        });
    }


    public interface OnGetContactedPersonOperation {
        void onGetContactedPersonOperationSuccess(List<String> uidList);
        void onGetContactedPersonOperationFailure(FirebaseError error);
    }
}
