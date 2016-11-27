package com.example.savi.auth.modules.message.operation;

import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.constant.OperationConstants;
import com.example.savi.auth.operation.BaseOperation;
import com.example.savi.auth.pojo.MessageItem;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.concurrent.TimeUnit;


public class SendMessageOperation extends BaseOperation {

        public interface OnMessageSentListener{
            void OnMessageSentSuccess(String message);
            void OnMessageSentFailure(String message);
        }

    public SendMessageOperation(final String receiverUid , String message , final OnMessageSentListener listener){

        final Firebase fireBaseRef = new Firebase(Constants.TODOCLOUD_ROOT_FIREBASE_URL + Constants.MESSAGE_CENTER);
        final String senderUid =fireBaseRef.getAuth().getUid() ;
        final String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";

        Firebase sendRef = fireBaseRef.child(fireBaseRef.getAuth().getUid()).child(receiverUid).push() ;

        fireBaseRef.child(receiverUid).child(senderUid).push().setValue(new MessageItem(senderUid, message, timeStamp, MessageItem.SENT,sendRef.getKey(), false), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError!=null){listener.OnMessageSentFailure(firebaseError.getMessage()); return;}
                fireBaseRef.child(receiverUid).child(senderUid).setPriority(timeStamp);
                listener.OnMessageSentSuccess(OperationConstants.MESSAGE_SENT_SUCCESS);
            }
        });

        sendRef.setValue(new MessageItem(senderUid, message, timeStamp, MessageItem.SENT,sendRef.getKey(), true), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                fireBaseRef.child(senderUid).child(receiverUid).setPriority(timeStamp);
            }
        });
    }
}
