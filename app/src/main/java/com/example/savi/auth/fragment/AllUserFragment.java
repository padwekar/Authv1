package com.example.savi.auth.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.AllUserAdapter;
import com.example.savi.auth.model.MessageItem;
import com.example.savi.auth.model.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.savi.auth.utils.Constants.MESSAGE_CENTER;
import static com.example.savi.auth.utils.Constants.TODOCLOUND_ROOT_FIREBASE_URL;
import static com.example.savi.auth.utils.Constants.UID;
import static com.example.savi.auth.utils.Constants.USER_DETAIL;

public class AllUserFragment extends Fragment {

    public static AllUserFragment newInstance() {
        return new AllUserFragment();
    }

    private AllUserAdapter mAllUserAdapter;
    private Firebase mFireBaseRef;
    private Map<String, User> mKeyUserMap;
    private User sender;

    private ProgressBar mProgressBar;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alluser, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        mFireBaseRef = new Firebase(TODOCLOUND_ROOT_FIREBASE_URL);
        mFireBaseRef.child(MESSAGE_CENTER);
        mAllUserAdapter = new AllUserAdapter(getContext(), false);
        mKeyUserMap = new LinkedHashMap<>();
        uid = getActivity().getIntent().getStringExtra(UID);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_alluser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAllUserAdapter);

        mFireBaseRef.child(USER_DETAIL).orderByPriority().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mProgressBar.setVisibility(View.GONE);
                final String key = dataSnapshot.getKey();
                mFireBaseRef.child(USER_DETAIL).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if(uid.equals(user.getUid()))
                            return;

                        mKeyUserMap.put(user.getUid(), user);
                        mAllUserAdapter.addUser(user);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final String key = dataSnapshot.getKey();

                mFireBaseRef.child(USER_DETAIL).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if(uid.equals(user.getUid()))
                            return;

                        mKeyUserMap.put(user.getUid(), user);
                        mAllUserAdapter.addUser(user);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final String key = dataSnapshot.getKey();
                mFireBaseRef.child(USER_DETAIL).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        mAllUserAdapter.setUserItemClickListener(new AllUserAdapter.OnUserItemClickListener() {
            @Override
            public void onItemClick(final String receiverUID) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                User receiver = new User();
                receiver = mKeyUserMap.get(receiverUID);

                final String people = receiver.getDisplayName();
                final User receiverInfo = receiver;
                builder.setTitle(people + " ")
                        .setItems(R.array.user_actions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (which == 0) {
                                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                                    final EditText mEditText = new EditText(getContext());
                                    alert.setTitle("Send MessageItem to :" + people);
                                    alert.setView(mEditText);
                                    alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!mEditText.getText().toString().equals("")) {
                                                Toast.makeText(getContext(), "Sending", Toast.LENGTH_SHORT).show();
                                                sendMessageto(receiverInfo, mEditText.getText().toString());
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                    alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alert.create();
                                    alert.show();
                                }
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        return view;
    }


    private void sendMessageto(final User receiver, final String message) {

        final String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "" ;
        mFireBaseRef.child(MESSAGE_CENTER).child(receiver.getUid()).child(uid).push().setValue(new MessageItem(uid, message, timeStamp, MessageItem.NEW), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getContext(), "Message has been sent - 1", Toast.LENGTH_SHORT).show();
                mFireBaseRef.child(MESSAGE_CENTER).child(receiver.getUid()).child(uid).setPriority(timeStamp);
            }
        });
        mFireBaseRef.child(MESSAGE_CENTER).child(uid).child(receiver.getUid()).push().setValue(new MessageItem(uid, message, timeStamp, MessageItem.NEW, true), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getContext(), "Message has been sent - 2", Toast.LENGTH_SHORT).show();
                mFireBaseRef.child(MESSAGE_CENTER).child(uid).child(receiver.getUid()).setPriority(timeStamp);
            }
        });
    }
}
