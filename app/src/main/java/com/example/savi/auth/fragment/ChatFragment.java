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
import com.example.savi.auth.utils.Constants;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.savi.auth.utils.Constants.MESSAGE_CENTER;
import static com.example.savi.auth.utils.Constants.TODOCLOUND_ROOT_FIREBASE_URL;

public class ChatFragment extends Fragment {
    
    private ChatAdapter chatAdapter;
    private User receiver ;
    private String uid ;
    private Firebase mRefUser , mFireBaseRef;
    private FirebaseStorage mFirebaseStorage ;
    private StorageReference mStorageReference ;
    private Map<String ,User> allUserMap ;
    private RecyclerView recyclerViewMessages ;
    private EditText mEditTextMessageInput ;
    private RelativeLayout mRelativeLayoutButtonSend ;
    private TextView mTextviewReceiver ;
    private  User sender ;
    List<MessageItem> messageItemList ;

    public static ChatFragment newInstance(User receiver) {
        ChatFragment fragment = new ChatFragment();
        fragment.receiver = receiver ;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed_chat, container, false);

        mTextviewReceiver = (TextView)view.findViewById(R.id.textview_receiver);
        messageItemList = new ArrayList<>();

        uid = getActivity().getIntent().getStringExtra("uid"); ;

        mRefUser = new Firebase(Constants.TODOCLOUND_ROOT_FIREBASE_URL+ Constants.MESSAGE_CENTER);
        mFireBaseRef = new Firebase(TODOCLOUND_ROOT_FIREBASE_URL);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://todocloudsavi.appspot.com/");

        mRelativeLayoutButtonSend = (RelativeLayout)view.findViewById(R.id.rlayout_msg_send);
        mRelativeLayoutButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessageInput.getText().toString() ;
                if(!message.equals("")){
                    sendMessageto(receiver,message,true);
                    mEditTextMessageInput.setText("");
                }

            }
        });

        mEditTextMessageInput = (EditText)view.findViewById(R.id.edittext_inputmsg);
        

        mTextviewReceiver.setText(receiver.getDisplayName());

        chatAdapter = new ChatAdapter(getContext());

        recyclerViewMessages = (RecyclerView) view.findViewById(R.id.list_view_messages);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
      //  mLayoutManager.setReverseLayout(true);
      //  mLayoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(mLayoutManager);
        recyclerViewMessages.setAdapter(chatAdapter);

        mRefUser.child(uid).child(receiver.getUid()).limitToLast(10).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().toString() != null) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        MessageItem messageItem = postSnapshot.getValue(MessageItem.class);
                        if (!messageItemList.contains(messageItem))
                            messageItemList.add(messageItem);
                    }
                    chatAdapter.addMessageList(messageItemList);
                    addChildEventListener();
                    recyclerViewMessages.scrollToPosition(messageItemList.size()-1);


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        return  view ;
    }

    private void addChildEventListener() {
        mRefUser.child(uid).child(receiver.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot!=null){
                    MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);
                    if (!messageItemList.contains(messageItem))
                        messageItemList.add(messageItem);

                    chatAdapter.addMessage(messageItem);
                    recyclerViewMessages.scrollToPosition(messageItemList.size()-1);
                }
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


    private void sendMessageto(final User receiver, final String message , boolean isNew) {

        final String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "" ;

        mRefUser.child(receiver.getUid()).child(uid).push().setValue(new MessageItem(uid, message, timeStamp, MessageItem.NEW), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getContext(), "Message has been sent - 1", Toast.LENGTH_SHORT).show();
                mFireBaseRef.child(MESSAGE_CENTER).child(receiver.getUid()).child(uid).setPriority(timeStamp);
            }
        });

        mRefUser.child(uid).child(receiver.getUid()).push().setValue(new MessageItem(uid, message, timeStamp, MessageItem.NEW, true), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getContext(), "Message has been sent - 2", Toast.LENGTH_SHORT).show();
                mFireBaseRef.child(MESSAGE_CENTER).child(uid).child(receiver.getUid()).setPriority(timeStamp);
            }
        });
    }
}
