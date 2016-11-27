package com.example.savi.auth.modules.message.operation.manager;

import android.widget.Toast;

import com.example.savi.auth.modules.message.operation.SendMessageOperation;
import com.example.savi.auth.modules.message.operation.UpdateMessageOperation;
import com.example.savi.auth.pojo.MessageItem;
import com.example.savi.auth.utils.AuthApplication;
import com.firebase.client.FirebaseError;

public class MessageManager {

    public interface OnUpdateMessage{
        void onMessageAdded(MessageItem messageItem , String key);
        void onMessageUpdated(MessageItem messageItem, String key);
        void onMessageDeleted(MessageItem messageItem);
    }

    public void sendMessage(String receiverUid,String message){
        new SendMessageOperation(receiverUid, message, new SendMessageOperation.OnMessageSentListener() {
            @Override
            public void OnMessageSentSuccess(String message) {
                Toast.makeText(AuthApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnMessageSentFailure(String message) {
                Toast.makeText(AuthApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // S-uid R-uid limitToLast 20 Value Event Listener // Will Listen to all the Latest Messages
    public void getMessages(String receiverUid , final int limit , final OnUpdateMessage listener){
        new UpdateMessageOperation(receiverUid, limit, new UpdateMessageOperation.OnUpdateMessageListener() {
            @Override
            public void onMessageAdded(MessageItem messageItem, String key) {
                  if(listener==null)return;
                   listener.onMessageAdded(messageItem,key);
            }

            @Override
            public void onMessageUpdated(MessageItem messageItem, String key) {
                if(listener==null)return;
                    listener.onMessageUpdated(messageItem,key);
            }

            @Override
            public void onMessageDeleted(MessageItem messageItem) {
                if (listener==null)return;
                listener.onMessageDeleted(messageItem);
            }

            @Override
            public void onMessageCancelled(FirebaseError firebaseError) {
                if(listener==null)return;
                Toast.makeText(AuthApplication.getInstance(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
