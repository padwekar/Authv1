package com.example.savi.auth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.savi.auth.utils.Constants.MESSAGE_CENTER;
import static com.example.savi.auth.utils.Constants.TODOCLOUD_ROOT_FIREBASE_URL;

public class ChatFragment extends Fragment {
    
    private ChatAdapter chatAdapter;
    private User receiver ;
    private String uid ;
    private Firebase mRefUser , mFireBaseRef ;
    private RecyclerView recyclerViewMessages ;
    private EditText mEditTextMessageInput ;
    private List<MessageItem> messageItemList ;
    private String firstElementKey ;
    private int position = 0;
    private Button mButtonShowMore ;
    private boolean isDataAvailable = true;
    private ImageView imageViewUpArrow ;
    private ImageView imageViewDownArrow ;
    private TextView textviewNewMessages ;
    private  LinearLayoutManager mLayoutManager ;

    public static ChatFragment newInstance(User receiver) {
        ChatFragment fragment = new ChatFragment();
        fragment.receiver = receiver ;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed_chat, container, false);
        uid = getActivity().getIntent().getStringExtra(Constants.UID);

        mRefUser = new Firebase(Constants.TODOCLOUD_ROOT_FIREBASE_URL + Constants.MESSAGE_CENTER);
        mFireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL);
        StorageReference mStorageReference  = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.TODOCLOUD_FIREBASE_STORAGE_URL);

        RelativeLayout mRelativeLayoutButtonSend = (RelativeLayout)view.findViewById(R.id.rlayout_msg_send);
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

        messageItemList = new ArrayList<>();

        TextView mTextviewReceiver = (TextView)view.findViewById(R.id.textview_receiver);
        mTextviewReceiver.setText(receiver.getDisplayName());

        mLayoutManager = new LinearLayoutManager(getActivity());

        mButtonShowMore = (Button)view.findViewById(R.id.button_show_more);
        mEditTextMessageInput = (EditText)view.findViewById(R.id.edittext_inputmsg);
        chatAdapter = new ChatAdapter(getContext());

        imageViewUpArrow = (ImageView)view.findViewById(R.id.imageview_uparrow);
        imageViewUpArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"UP ARROW",Toast.LENGTH_SHORT).show();
               // recyclerViewMessages.scrollToPosition(0);
                mLayoutManager.smoothScrollToPosition(recyclerViewMessages, null, 0);

            }
        });

        imageViewDownArrow = (ImageView)view.findViewById(R.id.imageview_down_arrow);
        imageViewDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"DOWN ARROW "+chatAdapter.getItemCount(),Toast.LENGTH_SHORT).show();
               // recyclerViewMessages.scrollToPosition(chatAdapter.getItemCount());
                mLayoutManager.smoothScrollToPosition(recyclerViewMessages,null,chatAdapter.getItemCount());
            }
        });


        recyclerViewMessages = (RecyclerView) view.findViewById(R.id.list_view_messages);
        recyclerViewMessages.setLayoutManager(mLayoutManager);
        recyclerViewMessages.setAdapter(chatAdapter);
        recyclerViewMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int position = mLayoutManager.findFirstVisibleItemPosition();


                    if (position == 0 && isDataAvailable) {
                        mButtonShowMore.setVisibility(View.VISIBLE);
                    } else {
                        mButtonShowMore.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    imageViewUpArrow.setVisibility(View.VISIBLE);
                } else {
                    // Scrolling down

                    imageViewUpArrow.setVisibility(View.GONE);
                }
            }
        });
        addChildEventListener();

        mButtonShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = -1;
                mButtonShowMore.setVisibility(View.GONE);
                mRefUser.child(uid).child(receiver.getUid()).endAt(null, firstElementKey).limitToLast(20).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if (dataSnapshot != null) {
                            MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);

                            if (position == -1) {
                                if(firstElementKey.equals(dataSnapshot.getKey())&&!messageItemList.contains(messageItem)){
                                    chatAdapter.addMessage(messageItem, 0);
                                    messageItemList.add(0, messageItem);
                                    isDataAvailable = false ;
                                    return;
                                }
                                firstElementKey = dataSnapshot.getKey();
                                Log.i("firstElementKey - new",firstElementKey);
                                position++;
                                return;

                            }

                            chatAdapter.addMessage(messageItem, position);
                            messageItemList.add(position, messageItem);
                            position++;
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
        });
        return  view ;
    }


    private void addChildEventListener() {
        mRefUser.child(uid).child(receiver.getUid()).limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    if (firstElementKey == null) {
                        firstElementKey = dataSnapshot.getKey();
                        Log.i("firstElementKey",firstElementKey);
                        return;
                    }

                    MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);
                    if (!messageItemList.contains(messageItem))
                        messageItemList.add(messageItem);

                    chatAdapter.addMessage(messageItem);
                    recyclerViewMessages.scrollToPosition(chatAdapter.getItemCount());
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
                Toast.makeText(getContext(), "Message has been Sent", Toast.LENGTH_SHORT).show();
                mFireBaseRef.child(MESSAGE_CENTER).child(receiver.getUid()).child(uid).setPriority(timeStamp);
            }
        });

        mRefUser.child(uid).child(receiver.getUid()).push().setValue(new MessageItem(uid, message, timeStamp, MessageItem.NEW, true), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getContext(), "Message has been Delivered", Toast.LENGTH_SHORT).show();
                mFireBaseRef.child(MESSAGE_CENTER).child(uid).child(receiver.getUid()).setPriority(timeStamp);
            }
        });
    }
}
