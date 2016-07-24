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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    ProgressBar mProgressBar ;
    AllUserAdapter.OnUserItemClickListener IOnItemClickListener ;

    String uid ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alluser,container,false);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressbar);
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
                                        else{
                                            mUserList.set(keyIndex, user);
                                        }
                                        mProgressBar.setVisibility(View.GONE);
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
            public void onItemClick(final String receiverUID) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String people = mUserList.get(mUIDList.indexOf(receiverUID)).getDisplayName();
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
                                                sendMessageto(receiverUID,mEditText.getText().toString());
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

    private void sendMessageto(final String receiverUid, final String message) {
      //  final String receiverUid = mUIDList.get(position);

        //receiver change listener
        mFireBaseRef.child("user").child(receiverUid).addValueEventListener(new ValueEventListener() {
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
                       mFireBaseRef.child("user").child(uid).addValueEventListener(new ValueEventListener() {
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

                                mFireBaseRef.child("user").child(uid).setValue(senderfinal);
                                mFireBaseRef.child("user").child(uid).removeEventListener(this);
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

                        mFireBaseRef.child("user").child(receiverUid).setValue(receiverfinal);
                        mFireBaseRef.child("user").child(receiverUid).removeEventListener(this);

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
