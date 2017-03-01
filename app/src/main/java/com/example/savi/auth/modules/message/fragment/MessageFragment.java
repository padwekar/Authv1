package com.example.savi.auth.modules.message.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseViewPagerFragment;
import com.example.savi.auth.modules.alluser.adapter.AllUserAdapter;
import com.example.savi.auth.modules.message.adapter.InboxAdapter;
import com.example.savi.auth.pojo.MessageItem;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageFragment extends BaseViewPagerFragment {

    private  List<String> mUIDList ;
    private List<User> mUserList ;
    private Map<String,User> userHashMap ;


    private User sender ;
    private InboxAdapter mInboxAdapter  ;
    private Firebase mFireBaseRef ;
    private RecyclerView mRecyclerViewMessageList ;

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view  = inflater.inflate(R.layout.fragment_messages,container,false);


        Toast.makeText(getContext(), "Name : "+getActivity().getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();

        Firebase.setAndroidContext(getContext());
        userHashMap = new HashMap<>();
        mInboxAdapter = new InboxAdapter(getContext());
        mFireBaseRef = new Firebase("https://todocloudsavi.firebaseio.com/");

        final String uid = AuthPreferences.getInstance().getUserUid();

        mRecyclerViewMessageList = (RecyclerView)view.findViewById(R.id.recycler_view_messages);
        mRecyclerViewMessageList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewMessageList.setAdapter(mInboxAdapter);

        final LinkedHashMap<User,MessageItem> itemLinkedHashMap = new LinkedHashMap<>();
        final Firebase mFireBaseRefnew = mFireBaseRef.child("message_center").child(uid) ;


        mFireBaseRefnew.orderByPriority().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //List of friend chat for that user
        mFireBaseRefnew.orderByPriority().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.w("Now in", "onChildAdded");
                final String key = dataSnapshot.getKey();

                mFireBaseRefnew.child(key).limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            final MessageItem item = post.getValue(MessageItem.class);
                            if (userHashMap.containsKey(dataSnapshot.getKey())) {
                                mInboxAdapter.addTotheMap(userHashMap.get(dataSnapshot.getKey()), item);
                            } else {
                                mFireBaseRef.child("detaileduser_v1").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        userHashMap.put(dataSnapshot.getKey(), user);
                                        mInboxAdapter.addTotheMap(userHashMap.get(dataSnapshot.getKey()), item);
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

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.w("Now in", "onChildChange");
                final String key = dataSnapshot.getKey();
                mFireBaseRefnew.removeEventListener(this);
                mFireBaseRefnew.child(key).limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            final MessageItem item = post.getValue(MessageItem.class);
                            if (userHashMap.containsKey(dataSnapshot.getKey())) {
                                mInboxAdapter.addTotheMap(userHashMap.get(dataSnapshot.getKey()), item);
                                break;
                            } else {
                                mFireBaseRef.child("detaileduser_v1").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        userHashMap.put(dataSnapshot.getKey(), user);
                                        mInboxAdapter.addTotheMap(userHashMap.get(dataSnapshot.getKey()), item);
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
                        Log.w("Now in", "onChildChange");
                    }
                });

            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                itemLinkedHashMap.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


   /*     mFireBaseRef.child("detaileduser_v1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    mUserList = new ArrayList<User>();
                    mUIDList = new ArrayList<String>();
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        User user =  postSnapshot.getValue(User.class) ;
                        if(!user.getUid().equals(uid)){
                            mUserList.add(user);
                            mUIDList.add(user.getUid());
                        }else  if(user.getUid().equals(uid)){
                            sender = user ;
                        }
                        allUserMap.put(user.getUid(),user);

                    }
                    mInboxAdapter.addUserList(mUserList);
                    mInboxAdapter.addKeyUserMapp(allUserMap);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
        mInboxAdapter.setUserItemClickListener(new InboxAdapter.OnUserItemClickListener() {
            @Override
            public void onItemClick(String receiverUID) {
                setFragment(ChatFragment.newInstance(userHashMap.get(receiverUID)));
            }
        });

        return view ;
    }


    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager() ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    public CharSequence getTitle(){
        return getActivity().getResources().getString(R.string.lbl_friends);
    }
}
