package com.example.savi.auth.fragment;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
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
import com.example.savi.auth.adapter.AllUserAdapter;
import com.example.savi.auth.adapter.ChatAdapter;
import com.example.savi.auth.model.MessageItem;
import com.example.savi.auth.model.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MessageFragment extends Fragment {

    private  List<String> mUIDList ;
    private List<String> keyList ;
    private List<User> mUserList ;
    private Map<String,User> allUserMap ;
    private Map<String,User> userHashMap ;


    private User sender ;
    private AllUserAdapter mAllUserAdapter  ;
    private ChatAdapter mChatAdapter ;
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
        Toast.makeText(getContext(),"In MessageFragment",Toast.LENGTH_SHORT).show();
        Firebase.setAndroidContext(getContext());
        keyList = new ArrayList<>();
        userHashMap = new HashMap<>();
        mChatAdapter = new ChatAdapter(getContext());
        mAllUserAdapter = new AllUserAdapter(getContext(),true);
        mFireBaseRef = new Firebase("https://todocloudsavi.firebaseio.com/");
        allUserMap = new HashMap<>();
        final String uid = getActivity().getIntent().getStringExtra("uid");
        mRecyclerViewMessageList = (RecyclerView)view.findViewById(R.id.recycler_view_messages);
        mRecyclerViewMessageList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewMessageList.setAdapter(mAllUserAdapter);

        final LinkedHashMap<User,MessageItem> itemLinkedHashMap = new LinkedHashMap<>();
        final Firebase mFireBaseRefnew = mFireBaseRef.child("message_center").child(uid) ;

        //List of friend chat for that user
        mFireBaseRefnew.orderByPriority().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.w("Now in","onChildAdded");
                final String key = dataSnapshot.getKey() ;

                mFireBaseRefnew.child(key).limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            final MessageItem item = post.getValue(MessageItem.class) ;
                            if(userHashMap.containsKey(dataSnapshot.getKey())){
                                mAllUserAdapter.addTotheMap(userHashMap.get(dataSnapshot.getKey()), item);
                            }else{
                                mFireBaseRef.child("detaileduser_v1").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        userHashMap.put(dataSnapshot.getKey(),user);
                                        mAllUserAdapter.addTotheMap(userHashMap.get(dataSnapshot.getKey()), item);
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
            public void onChildChanged(DataSnapshot dataSnapshot, String s){
                Log.w("Now in","onChildChange");
                final String key = dataSnapshot.getKey() ;;
                mFireBaseRefnew.removeEventListener(this);
                mFireBaseRefnew.child(key).limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            final MessageItem item = post.getValue(MessageItem.class) ;
                            if(userHashMap.containsKey(dataSnapshot.getKey())){
                                mAllUserAdapter.addTotheMap(userHashMap.get(dataSnapshot.getKey()), item);
                                break;
                            }else{
                                mFireBaseRef.child("detaileduser_v1").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        userHashMap.put(dataSnapshot.getKey(),user);
                                        mAllUserAdapter.addTotheMap(userHashMap.get(dataSnapshot.getKey()), item);
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
                        Log.w("Now in","onChildChange");
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


        mFireBaseRef.child("detaileduser_v1").addValueEventListener(new ValueEventListener() {
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
                    mAllUserAdapter.addUserList(mUserList);
                    mAllUserAdapter.addKeyUserMapp(allUserMap);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mAllUserAdapter.setUserItemClickListener(new AllUserAdapter.OnUserItemClickListener() {
            @Override
            public void onItemClick(String receiverUID) {
                setFragment(ChatFragment.newInstance(receiverUID,allUserMap));
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
}
