package com.example.savi.auth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.AllUserAdapter;
import com.example.savi.auth.adapter.ChatAdapter;
import com.example.savi.auth.model.MessageItem;
import com.example.savi.auth.model.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFragment extends Fragment {
    List<String> mUIDList ;
    List<User> mUserList ;
    Map<String,User> allUserMap ;

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }
    AllUserAdapter mAllUserAdapter  ;
    ChatAdapter mChatAdapter ;
    Firebase mFireBaseRef ;
    RecyclerView mRecyclerViewMessageList ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view  = inflater.inflate(R.layout.fragment_messages,container,false);
        Toast.makeText(getContext(),"In MessageFragment",Toast.LENGTH_SHORT).show();
        Firebase.setAndroidContext(getContext());

        mChatAdapter = new ChatAdapter(getContext());
        mAllUserAdapter = new AllUserAdapter(getContext(),true);
        mFireBaseRef = new Firebase("https://todocloudsavi.firebaseio.com/");
        allUserMap = new HashMap<>();
        final String uid = getActivity().getIntent().getStringExtra("uid");
        mRecyclerViewMessageList = (RecyclerView)view.findViewById(R.id.recycler_view_messages);
        mRecyclerViewMessageList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewMessageList.setAdapter(mAllUserAdapter);
        mFireBaseRef.child("user").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    String data = dataSnapshot.getValue().toString();
                    User user = new Gson().fromJson(data, User.class);
                    if (user != null && user.getMessageMap() != null) {
                        Map<String, List<MessageItem>> messageMap = user.getMessageMap();

                        if(messageMap.containsKey(uid))
                            messageMap.remove(uid);

                        mAllUserAdapter.addMessageMap(user.getMessageMap());
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mFireBaseRef.child("alluser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    String data = dataSnapshot.getValue().toString();
                    mUIDList = new Gson().fromJson(data, new TypeToken<List<String>>() {
                    }.getType());
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

                                    allUserMap.put(dataSnapshot.getKey(), user);
                                    mAllUserAdapter.addUserList(mUserList);
                                    mAllUserAdapter.addKeyUserMapp(allUserMap);
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
