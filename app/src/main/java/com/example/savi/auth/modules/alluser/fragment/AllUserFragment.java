package com.example.savi.auth.modules.alluser.fragment;

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
import com.example.savi.auth.modules.alluser.adapter.AllUserAdapter;
import com.example.savi.auth.pojo.FriendShipStatus;
import com.example.savi.auth.pojo.MessageItem;
import com.example.savi.auth.pojo.NotificationRequest;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.operation.manager.SocialManager;
import com.example.savi.auth.utils.AuthPreferences;
import com.example.savi.auth.constant.Constants;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.savi.auth.constant.Constants.MESSAGE_CENTER;
import static com.example.savi.auth.constant.Constants.TODOCLOUD_ROOT_FIREBASE_URL;
import static com.example.savi.auth.constant.Constants.USER_DETAIL;

public class AllUserFragment extends Fragment {

    public static AllUserFragment newInstance() {
        return new AllUserFragment();
    }

    private AllUserAdapter mAllUserAdapter;
    private Firebase mFireBaseRef, mFireBaseMsgCenter;
    private Map<String, User> mKeyUserMap;
    private User sender;

    private ProgressBar mProgressBar;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alluser, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        mFireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL);
        mFireBaseMsgCenter = mFireBaseRef.child(MESSAGE_CENTER);
        mAllUserAdapter = new AllUserAdapter(getContext(), false);
        mKeyUserMap = new LinkedHashMap<>();

        uid = AuthPreferences.getInstance().getUserUid();

        mAllUserAdapter.setOnFriendShipStatusClickListener(new AllUserAdapter.OnFriendShipStatusClickListener() {
            @Override
            public void onFriedShipStatusClick(final User user) {
                switch (user.getFriendShipStatus()){
                case  FriendShipStatus.NOT_FRIENDS :

            mFireBaseRef.child(Constants.USER_DETAIL).child(uid).child("contactedPersonsMap").child(user.getUid()).setValue(User.REQUEST_SENT, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    String message = "Successful" ;
                    if(firebaseError!=null)
                        message = firebaseError.getMessage() ;

                    Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show() ; return;

                }
            });
            mFireBaseRef.child(Constants.USER_DETAIL).child(user.getUid()).child("contactedPersonsMap").child(uid).setValue(User.FRIEND_REQUEST);


                }
            }
        });


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

                        if (uid.equals(user.getUid()))
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

                        if (uid.equals(user.getUid()))
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
                                                sendMessageto(receiverInfo, mEditText.getText().toString(), false);
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
                                }if(which==1){
                                    sendFriendRequest(receiverInfo);
                                }
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        return view;
    }

    private void sendFriendRequest(User receiverInfo) {
        NotificationRequest request = new NotificationRequest();
        request.data.put("body",AuthPreferences.getInstance().getUserName() + " wants to be your friend ! ");
        request.data.put("for",receiverInfo.getUid());
        request.to = receiverInfo.getToken();
        SocialManager manager = new SocialManager();
        manager.sendFriendRequest(request, new SocialManager.OnSendFriendRequest() {
            @Override
            public void onSendFriendRequestSuccess(Object o) {
                Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSendFriendRequestFailure(Exception e) {
                Toast.makeText(getContext(),"Failure",Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void sendMessageto(final User receiver, final String message, boolean isNew) {


        Firebase mRefUserTemp = mFireBaseMsgCenter.child(uid).child(receiver.getUid()).push();
        String key = mRefUserTemp.getKey();

        final String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
        mFireBaseMsgCenter.child(receiver.getUid()).child(uid).push().setValue(new MessageItem(uid, message, timeStamp, MessageItem.SENT, key, false), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getContext(), "Message has been Sent", Toast.LENGTH_SHORT).show();
                mFireBaseRef.child(MESSAGE_CENTER).child(receiver.getUid()).child(uid).setPriority(timeStamp);
            }
        });

        mRefUserTemp.setValue(new MessageItem(uid, message, timeStamp, MessageItem.SENT, key, true), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getContext(), "Message has been Delivered", Toast.LENGTH_SHORT).show();
                mFireBaseRef.child(MESSAGE_CENTER).child(uid).child(receiver.getUid()).setPriority(timeStamp);
            }
        });
    }
}
