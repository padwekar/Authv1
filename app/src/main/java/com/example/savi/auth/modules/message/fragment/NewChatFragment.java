package com.example.savi.auth.modules.message.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.modules.message.adapter.ChatAdapter;
import com.example.savi.auth.pojo.MessageItem;
import com.example.savi.auth.pojo.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.savi.auth.constant.Constants.MESSAGE_CENTER;
import static com.example.savi.auth.constant.Constants.TODOCLOUD_ROOT_FIREBASE_URL;

public class NewChatFragment extends Fragment {

    private User receiver ;
    private ChatAdapter mChatAdapter ;
    private Firebase mRefUser, mFireBaseRef;
    private LinearLayoutManager mLayoutManager ;

    private List<MessageItem> messageItemList = new ArrayList<>();
    private String uid;
    private String mLastElementOffset = "";

    //Views
    private EditText mEditTextMessageInput;
    private Button mButtonShowMore ;
    private ImageView mImageViewUpArrow;
    private ImageView mImageViewDownArrow;
    private RecyclerView mRecyclerViewMessages ;


    public static NewChatFragment newInstance(User receiver) {
        NewChatFragment fragment = new NewChatFragment();
        fragment.receiver = receiver;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_chat, container, false);

        //References
        mRefUser = new Firebase(Constants.TODOCLOUD_ROOT_FIREBASE_URL + Constants.MESSAGE_CENTER);
        mFireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL);
        uid = mFireBaseRef.getAuth().getUid() ;

        mEditTextMessageInput = (EditText)view.findViewById(R.id.edittext_inputmsg);
        ((TextView) view.findViewById(R.id.textview_receiver)).setText(receiver.getDisplayName());

        RelativeLayout mRelativeLayoutButtonSend = (RelativeLayout) view.findViewById(R.id.rlayout_msg_send);
        mRelativeLayoutButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessageInput.getText().toString();
                if (message.equals(""))return;
                sendMessage(receiver, message, true);
                mEditTextMessageInput.setText("");

            }
        });


        mImageViewUpArrow = (ImageView) view.findViewById(R.id.imageview_uparrow);
        mImageViewUpArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutManager.smoothScrollToPosition(mRecyclerViewMessages, null, 0);
            }
        });

        mImageViewDownArrow = (ImageView) view.findViewById(R.id.imageview_down_arrow);
        mImageViewDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutManager.smoothScrollToPosition(mRecyclerViewMessages, null, mChatAdapter.getItemCount());
            }
        });

        mChatAdapter = new ChatAdapter(getContext());
        mRecyclerViewMessages = (RecyclerView)view.findViewById(R.id.recycler_view_messages);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewMessages.setLayoutManager(mLayoutManager);
        mRecyclerViewMessages.setAdapter(mChatAdapter);


        mButtonShowMore = (Button) view.findViewById(R.id.button_show_more);

        addChildEventListener();

        return view;
    }

    private void addChildEventListener() {
        mRefUser.child(uid).child(receiver.getUid()).limitToLast(20).endAt(null,mLastElementOffset).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("ChildAdded", "inside");
                if (dataSnapshot != null) {
                    if (mLastElementOffset == null) {
                        mLastElementOffset = dataSnapshot.getKey();
                        Log.i("firstElementKey", mLastElementOffset);
                    }

                    final MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);
                    if (!messageItemList.contains(messageItem)) {
                        messageItemList.add(messageItem);
                        mChatAdapter.addMessage(messageItem);
                        int count = mChatAdapter.getItemCount() - 1;

                        if (!messageItem.getSelf()) {
                            if (messageItem.getStatus() != MessageItem.DELIVERED) {
                                messageItem.setStatus(MessageItem.DELIVERED);
                                mRefUser.child(receiver.getUid()).child(uid).child(messageItem.getSenderBranchKey()).child("status").setValue(MessageItem.DELIVERED, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        Log.i(messageItem.getMessage(), "Delivered with " + firebaseError + ": Error");
                                    }
                                });
                            }

                            if (true)
                                mRecyclerViewMessages.scrollToPosition(count);
                        } else {
                            mRecyclerViewMessages.scrollToPosition(count);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);
                mChatAdapter.updateMessage(messageItem);
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


    private void sendMessage(final User receiver, final String message, boolean isNew) {

        Firebase mRefUserTemp = mRefUser.child(uid).child(receiver.getUid()).push();
        String key = mRefUserTemp.getKey();

        final String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
        mRefUser.child(receiver.getUid()).child(uid).push().setValue(new MessageItem(uid, message, timeStamp, MessageItem.SENT, key, false), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mFireBaseRef.child(MESSAGE_CENTER).child(receiver.getUid()).child(uid).setPriority(timeStamp);
            }
        });

        mRefUserTemp.setValue(new MessageItem(uid, message, timeStamp, MessageItem.SENT, key, true), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mFireBaseRef.child(MESSAGE_CENTER).child(uid).child(receiver.getUid()).setPriority(timeStamp);
            }
        });
    }
}
