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
import com.example.savi.auth.modules.alluser.operation.manager.UserManager;
import com.example.savi.auth.modules.message.operation.manager.MessageManager;
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

import java.util.HashMap;
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

        //Request Send Listener
        mAllUserAdapter.setOnFriendShipStatusClickListener(new AllUserAdapter.OnFriendShipStatusClickListener() {
            @Override
            public void onFriedShipStatusClick(final User user) {
                sendFriendRequest(user);
            }
        });

        getCircleMap(uid);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_alluser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAllUserAdapter);
        getAllUser();

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
                                                new MessageManager().sendMessage(receiverInfo.getUid(), mEditText.getText().toString());
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

    private void getAllUser() {
        new UserManager().getAllUsers(new UserManager.OnGetAllUserManager() {
            @Override
            public void onUserAdded(User user, String key) {
                if(mProgressBar.getVisibility()!=View.GONE) mProgressBar.setVisibility(View.GONE);
                addToList(user,key);
            }

            @Override
            public void onUserUpdated(User user, String key) {
                addToList(user,key);
            }

            @Override
            public void onUserRemoved(User user) {

            }

            @Override
            public void onCancelled(FirebaseError error) {

            }
        });
    }

    private void addToList(User user, String key) {
        if (uid.equals(key))
            return;

        mKeyUserMap.put(key, user);
        mAllUserAdapter.addUser(user);
    }

    private void getCircleMap(String uid) {
        SocialManager manager = new SocialManager();
        manager.getUserCircleMap(uid, new SocialManager.OnGetUserCircleMap() {
            @Override
            public void onGetUserCircleMapSuccess(HashMap<String, Integer> circleMap) {
                if(isAdded()) mAllUserAdapter.setCircleMap(circleMap);
            }

            @Override
            public void onGetUserCircleMapFailure(FirebaseError error) {
                if(isAdded()) Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendFriendRequest(User receiverInfo) {

        mFireBaseRef.child(Constants.CIRCLE).child(uid).child(receiverInfo.getUid()).setValue(User.REQUEST_SENT, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                String message = "Successful" ;
                if(firebaseError!=null)
                    message = firebaseError.getMessage() ;

            }
        });
        mFireBaseRef.child(Constants.CIRCLE).child(receiverInfo.getUid()).child(uid).setValue(User.FRIEND_REQUEST);

        NotificationRequest request = new NotificationRequest();
        request.data.put("body",AuthPreferences.getInstance().getUserName() + " wants to be your friend ! ");
        request.data.put("for",receiverInfo.getUid());
        request.to = receiverInfo.getToken();
        SocialManager manager = new SocialManager();
        manager.sendFriendRequest(request, new SocialManager.OnSendFriendRequest() {
            @Override
            public void onSendFriendRequestSuccess(Object o) {
                Toast.makeText(getContext(),"Notification Sent",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSendFriendRequestFailure(Exception e) {
                Toast.makeText(getContext(),"Notification Error",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
