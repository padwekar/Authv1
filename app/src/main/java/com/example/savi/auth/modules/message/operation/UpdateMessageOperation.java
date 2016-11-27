package com.example.savi.auth.modules.message.operation;

import com.example.savi.auth.operation.BaseOperation;
import com.example.savi.auth.pojo.MessageItem;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import static com.example.savi.auth.constant.Constants.MESSAGE_CENTER;
import static com.example.savi.auth.constant.Constants.TODOCLOUD_ROOT_FIREBASE_URL;


public class UpdateMessageOperation extends BaseOperation {


    public interface OnUpdateMessageListener{
        void onMessageAdded(MessageItem messageItem , String key);
        void onMessageUpdated(MessageItem messageItem, String key);
        void onMessageDeleted(MessageItem messageItem);
        void onMessageCancelled(FirebaseError firebaseError);
    }

    public UpdateMessageOperation(String receiverUid , int limit , final OnUpdateMessageListener listener){
        Firebase fireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL + MESSAGE_CENTER);
        fireBaseRef.child(fireBaseRef.getAuth().getUid()).child(receiverUid).limitToLast(limit).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(isEmpty(dataSnapshot)||listener==null)return;
                listener.onMessageAdded(dataSnapshot.getValue(MessageItem.class),s);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(isEmpty(dataSnapshot)||listener==null)return;
                listener.onMessageUpdated(dataSnapshot.getValue(MessageItem.class),s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(isEmpty(dataSnapshot)||listener==null)return;
                listener.onMessageDeleted(dataSnapshot.getValue(MessageItem.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(listener==null)return;
                listener.onMessageCancelled(firebaseError);
            }
        });
    }
}
