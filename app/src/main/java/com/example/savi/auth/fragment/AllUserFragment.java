package com.example.savi.auth.fragment;

import android.content.Context;
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
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.AllUserAdapter;
import com.example.savi.auth.model.Message;
import com.example.savi.auth.model.User;
import com.example.savi.auth.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AllUserFragment extends Fragment {

    public static AllUserFragment newInstance() {
        AllUserFragment fragment = new AllUserFragment();
        return fragment;
    }
    AllUserAdapter mAllUserAdapter ;
    List<String> mUIDList ;
    List<User>  mUserList ;
    Firebase mFireBaseRef ;
    AllUserAdapter.OnUserItemClickListener IOnItemClickListener ;
    String uid ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alluser,container,false);
        mFireBaseRef = new Firebase("https://todocloudsavi.firebaseio.com/");
        mAllUserAdapter = new AllUserAdapter(getContext(),false);
        uid = getActivity().getIntent().getStringExtra("uid");
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_alluser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAllUserAdapter);
        mFireBaseRef.child("alluser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    String data = dataSnapshot.getValue().toString();
                    mUIDList = new Gson().fromJson(data, new TypeToken<List<String>>() {
                    }.getType());
                    mUIDList.remove(uid);
                    mUserList = new ArrayList<User>();
                    for (int i = 0; i < mUIDList.size(); i++) {

                        mFireBaseRef.child("user").child(mUIDList.get(i)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                    String userData = dataSnapshot.getValue().toString();
                                    User user = new Gson().fromJson(userData, User.class);
                                    int keyIndex = mUIDList.indexOf(dataSnapshot.getKey());
                                    if (mUIDList.size() != mUserList.size())
                                        mUserList.add(user);
                                    else
                                        mUserList.set(keyIndex, user);

                                    mAllUserAdapter.addUserList(mUserList);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        Toast.makeText(getContext(), "In AllUserFragment", Toast.LENGTH_SHORT).show();
        mAllUserAdapter.setUserItemClickListener(new AllUserAdapter.OnUserItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String people = mUserList.get(position).getDisplayName();
                builder.setTitle(people + " ")
                        .setItems(R.array.user_actions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (which == 0) {
                                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                                    final EditText mEditText = new EditText(getContext());
                                    alert.setTitle("Send Message to :" + people);
                                    alert.setView(mEditText);
                                    alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!mEditText.getText().toString().equals("")) {
                                                Toast.makeText(getContext(), "Sending", Toast.LENGTH_SHORT).show();
                                                sendMessageto(position,mEditText.getText().toString());
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
        return view ;
    }

    private void sendMessageto(int position, final String message) {
        final String receiverUid = mUIDList.get(position);
        mFireBaseRef.child("user").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
                    String data = dataSnapshot.getValue().toString();
                    String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
                    User receiverInfo = new Gson().fromJson(data, User.class);
                    Map<String,List<Message>> messageMap = receiverInfo.getMessageMap() ;
                    messageMap = messageMap==null? new HashMap<String, List<Message>>() : messageMap ;
                    List<Message> messageList = messageMap.get(receiverUid);
                    messageList = messageList==null ? new ArrayList<Message>(): messageList ;
                    messageList.add(new Message(uid, message, timeStamp, Message.NEW));
                    messageMap.put(uid, messageList);
                    receiverInfo.setMessageMap(messageMap);
                    String receiverfinal = new Gson().toJson(receiverInfo);
                    mFireBaseRef.child("user").child(receiverUid).setValue(receiverfinal);
                    Toast.makeText(getContext(), "Send Successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
