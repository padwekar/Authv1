package com.example.savi.auth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.ChatAdapter;
import com.example.savi.auth.model.MessageItem;
import com.example.savi.auth.model.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChatFragment extends Fragment {
    
    private ChatAdapter chatAdapter;
    private String receiverId ;
    private String uid ;
    private Firebase mRefUser;
    private FirebaseStorage mFirebaseStorage ;
    private StorageReference mStorageReference ;
    private Map<String ,User> allUserMap ;
    private RecyclerView recyclerViewMessages ;
    private EditText mEditTextMessageInput ;
    private RelativeLayout mRelativeLayoutButtonSend ;
    private TextView mTextviewReceiver ;

    public static ChatFragment newInstance(String receiverId , Map<String ,User> allUserMap ) {
        ChatFragment fragment = new ChatFragment();
        fragment.receiverId = receiverId ;
        fragment.allUserMap = allUserMap ;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_chat, container, false);


        mTextviewReceiver = (TextView)view.findViewById(R.id.textview_receiver);

        mRefUser = new Firebase("https://todocloudsavi.firebaseio.com/user");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://todocloudsavi.appspot.com/");

        mRelativeLayoutButtonSend = (RelativeLayout)view.findViewById(R.id.rlayout_msg_send);
        mRelativeLayoutButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessageInput.getText().toString() ;
                if(!message.equals("")){
                    sendMessageto(receiverId,message);
                    mEditTextMessageInput.setText("");
                }
            }
        });
        mEditTextMessageInput = (EditText)view.findViewById(R.id.edittext_inputmsg);
        
        uid = getActivity().getIntent().getStringExtra("uid"); ;

        mTextviewReceiver.setText(allUserMap.get(receiverId).getDisplayName());

        chatAdapter = new ChatAdapter(getContext());
        recyclerViewMessages = (RecyclerView) view.findViewById(R.id.list_view_messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMessages.setAdapter(chatAdapter);

        mRefUser.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue().toString() != null) {
                    String data = dataSnapshot.getValue().toString();
                    User sender = new Gson().fromJson(data, User.class);
                    LinkedHashMap<String, List<MessageItem>> messageMap = sender.getMessageMap();
                    messageMap = messageMap == null ? new LinkedHashMap<String, List<MessageItem>>() : sender.getMessageMap();
                    List<MessageItem> messageItemList = messageMap.get(receiverId);
                    messageItemList = messageItemList == null ? new ArrayList<MessageItem>() : messageMap.get(receiverId);
                    chatAdapter.addMessageList(messageItemList);
                    recyclerViewMessages.scrollToPosition(messageItemList.size()-1);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //User sender = allUserMap.get(uid) ;

        return  view ;
    }

    private void sendMessageto(final String receiverUid, final String message) {
        //  final String receiverUid = mUIDList.get(position);

        //receiver change listener
        mRefUser.child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot!=null && dataSnapshot.getValue()!=null){

                        //Get the receiver object as a String
                        String data = dataSnapshot.getValue().toString();
                        String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";

                        //Convert it to receiver object
                        User receiverInfo = new Gson().fromJson(data, User.class);

                        //Get the MessageItem Map of Receiver
                        LinkedHashMap<String,List<MessageItem>> messageMap = receiverInfo.getMessageMap() ;
                        messageMap = messageMap==null? new LinkedHashMap<String, List<MessageItem>>() : messageMap ;

                        //Get the Sender Uid block
                        List<MessageItem> messageItemList = messageMap.get(uid);
                        messageItemList = messageItemList ==null ? new ArrayList<MessageItem>(): messageItemList;


                        //Get Receiver UID block  from sender messages
                        mRefUser.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String data = dataSnapshot.getValue().toString();
                                String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";

                                //Convert it to receiver object
                                User senderInfo = new Gson().fromJson(data, User.class);

                                LinkedHashMap<String,List<MessageItem>> messageMap = senderInfo.getMessageMap() ;
                                messageMap = messageMap==null? new LinkedHashMap<String, List<MessageItem>>() : messageMap ;
                                List<MessageItem> messageItemList = messageMap.get(receiverUid);

                                messageItemList = messageItemList ==null ? new ArrayList<MessageItem>(): messageItemList;
                                messageItemList.add(new MessageItem(uid, message, timeStamp, MessageItem.NEW,true));

                                //if Uid already exist delete it
                                if(messageMap.containsKey(uid))
                                    messageMap.remove(uid);

                                //set rhe message to it
                                messageMap.put(uid, messageItemList);
                                senderInfo.setMessageMap(messageMap);

                                String senderfinal = new Gson().toJson(senderInfo);

                                mRefUser.child(uid).setValue(senderfinal);
                                mRefUser.child(uid).removeEventListener(this);
                                Toast.makeText(getContext(), "Self Data Fill Success", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });



                        //Put the message in it
                        messageItemList.add(new MessageItem(uid, message, timeStamp, MessageItem.NEW));

                        //if Uid already exist delete it
                        if(messageMap.containsKey(uid))
                            messageMap.remove(uid);

                        //set rhe message to it
                        messageMap.put(uid, messageItemList);
                        receiverInfo.setMessageMap(messageMap);

                        String receiverfinal = new Gson().toJson(receiverInfo);

                        mRefUser.child(receiverUid).setValue(receiverfinal);
                        mRefUser.child(receiverUid).removeEventListener(this);

                        Toast.makeText(getContext(), "Send Successful", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
