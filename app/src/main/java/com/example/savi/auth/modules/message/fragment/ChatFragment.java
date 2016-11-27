package com.example.savi.auth.modules.message.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.modules.dashboard.activity.HomeActivity;
import com.example.savi.auth.modules.message.adapter.ChatAdapter;
import com.example.savi.auth.modules.message.operation.manager.MessageManager;
import com.example.savi.auth.pojo.MessageItem;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.example.savi.auth.constant.Constants;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.savi.auth.constant.Constants.MESSAGE_CENTER;
import static com.example.savi.auth.constant.Constants.TODOCLOUD_ROOT_FIREBASE_URL;

public class ChatFragment extends Fragment {

    private ChatAdapter chatAdapter;
    private User receiver;
    private String uid;
    private Firebase mRefUser, mFireBaseRef;
    private RecyclerView recyclerViewMessages;
    private EditText mEditTextMessageInput;
    private List<MessageItem> messageItemList;
    private String firstElementKey;
    private int position = 0;
    private Button mButtonShowMore;
    private boolean isDataAvailable = true;
    private ImageView imageViewUpArrow;
    private ImageView imageViewDownArrow;
    private TextView textviewNewMessages;
    private LinearLayoutManager mLayoutManager;
    private boolean scrollToBottom = true;
    private ProgressBar progressBar ;
    public static ChatFragment newInstance(User receiver) {
        ChatFragment fragment = new ChatFragment();
        fragment.receiver = receiver;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed_chat, container, false);
        uid = AuthPreferences.getInstance().getUserUid();


        mRefUser = new Firebase(Constants.TODOCLOUD_ROOT_FIREBASE_URL + Constants.MESSAGE_CENTER);
        mFireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL);
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.TODOCLOUD_FIREBASE_STORAGE_URL);

        RelativeLayout mRelativeLayoutButtonSend = (RelativeLayout) view.findViewById(R.id.rlayout_msg_send);
        mRelativeLayoutButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessageInput.getText().toString();
                if (!message.equals("")) {
                    new MessageManager().sendMessage(receiver.getUid(), message);
                    mEditTextMessageInput.setText("");
                }

            }
        });

        messageItemList = new ArrayList<>();
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);

        TextView mTextViewReceiver = (TextView) view.findViewById(R.id.textview_receiver);
        mTextViewReceiver.setText(receiver.getDisplayName());

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        mButtonShowMore = (Button) view.findViewById(R.id.button_show_more);
        mEditTextMessageInput = (EditText) view.findViewById(R.id.edittext_inputmsg);
        chatAdapter = new ChatAdapter(getContext());

        imageViewUpArrow = (ImageView) view.findViewById(R.id.imageview_uparrow);
        imageViewUpArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "UP ARROW", Toast.LENGTH_SHORT).show();
                // recyclerViewMessages.scrollToPosition(0);
                mLayoutManager.smoothScrollToPosition(recyclerViewMessages, null, 0);

            }
        });

        imageViewDownArrow = (ImageView) view.findViewById(R.id.imageview_down_arrow);
        imageViewDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "DOWN ARROW " + chatAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
                // recyclerViewMessages.scrollToPosition(chatAdapter.getItemCount());
                mLayoutManager.smoothScrollToPosition(recyclerViewMessages, null, chatAdapter.getItemCount());
            }
        });


        recyclerViewMessages = (RecyclerView) view.findViewById(R.id.list_view_messages);
        recyclerViewMessages.setLayoutManager(mLayoutManager);
        recyclerViewMessages.setAdapter(chatAdapter);
        recyclerViewMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = mLayoutManager.findFirstVisibleItemPosition();
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {


                    if (position == 0 && isDataAvailable) {
                        mButtonShowMore.performClick();
                    } else {
                        progressBar.setVisibility(View.GONE);

                    }

                    new CountDownTimer(500,3000){
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            imageViewUpArrow.setVisibility(View.GONE);
                            imageViewDownArrow.setVisibility(View.GONE);
                        }
                    };

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    imageViewUpArrow.setVisibility(View.GONE);
                    imageViewDownArrow.setVisibility(View.VISIBLE);
                    setScrollToBottom();
                } else {
                    // Scrolling down
                    Log.i("Scroll to Bottom-l", "false");
                    imageViewUpArrow.setVisibility(View.VISIBLE);
                    imageViewDownArrow.setVisibility(View.GONE);
                    setScrollToBottom();
                }
            }
        });
        addChildEventListener();

        mButtonShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                position = -1;
                mButtonShowMore.setVisibility(View.GONE);
                mRefUser.child(uid).child(receiver.getUid()).endAt(null, firstElementKey).limitToLast(20).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {

                        if (dataSnapshot != null) {
                            MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);

                            if (position == -1) {
                                if (firstElementKey.equals(dataSnapshot.getKey()) && !messageItemList.contains(messageItem)) {
                                    chatAdapter.addMessage(messageItem, 0);
                                    messageItemList.add(0, messageItem);
                                    isDataAvailable = false;
                                    return;
                                }
                                firstElementKey = dataSnapshot.getKey();
                                Log.i("firstElementKey - new", firstElementKey);
                                position++;
                            }

                            if (!messageItemList.contains(messageItem)) {
                                chatAdapter.addMessage(messageItem, position);
                                messageItemList.add(position, messageItem);
                                position++;
                            }

                        }
                        progressBar.setVisibility(View.GONE);
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
        return view;
    }

    private void setScrollToBottom() {
        int lastPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = mLayoutManager.findLastVisibleItemPosition();
        int visibleCount = lastVisible - firstVisible ;
        Log.i(messageItemList.size() - 1 + "", messageItemList.size() - 1 + "");
        Log.i("last position", lastPosition + "");

        if (lastPosition == messageItemList.size() - 1) {
            scrollToBottom = true;
                imageViewUpArrow.setVisibility(View.VISIBLE);
                imageViewDownArrow.setVisibility(View.GONE);

            Log.i("Scroll to Bottom", "true");
        } else {
            scrollToBottom = false;
            Log.i("Scroll to Bottom", "false");
        }

        if(visibleCount==messageItemList.size() - 1){
            imageViewUpArrow.setVisibility(View.GONE);
            imageViewDownArrow.setVisibility(View.GONE);
        }
    }
// S-uid R-uid limitToLast 20 Value Event Listener // Will Listen to all the Latest Messages

    private void addChildEventListener() {
        new MessageManager().getMessages(receiver.getUid(), 20, new MessageManager.OnUpdateMessage() {
            @Override
            public void onMessageAdded(MessageItem messageItem, String key) {
                if (firstElementKey == null) firstElementKey = key ;

                chatAdapter.addMessage(messageItem);
                int count = chatAdapter.getItemCount() - 1;

                if (!messageItem.getSelf()&& messageItem.getStatus() != MessageItem.DELIVERED) {
                        messageItem.setStatus(MessageItem.DELIVERED);
                        mRefUser.child(receiver.getUid()).child(uid).child(messageItem.getSenderBranchKey()).child("status").setValue(MessageItem.DELIVERED);
                        if (scrollToBottom) recyclerViewMessages.scrollToPosition(count);
                } else {
                       recyclerViewMessages.scrollToPosition(count);
                }
            }

            @Override
            public void onMessageUpdated(MessageItem messageItem, String key) {
                chatAdapter.updateMessage(messageItem);
            }

            @Override
            public void onMessageDeleted(MessageItem messageItem) {

            }
        });
    }

}
