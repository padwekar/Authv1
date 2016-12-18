package com.example.savi.auth.modules.friends.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseViewPagerFragment;
import com.example.savi.auth.modules.friends.adapter.FriendAdapter;
import com.example.savi.auth.operation.manager.SocialManager;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.List;

import static com.example.savi.auth.constant.Constants.TODOCLOUD_ROOT_FIREBASE_URL;

public abstract class BaseFriendFragment extends BaseViewPagerFragment{

    private FriendAdapter mFriendAdapter ;
    protected Firebase mFireBaseRef ;
    protected String userUid = AuthPreferences.getInstance().getUserUid();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_friend,container,false);

        TextView textViewHeader = (TextView)view.findViewById(R.id.textview_header);
        textViewHeader.setText(getHeader());

        RecyclerView recyclerViewPersons = (RecyclerView)view.findViewById(R.id.recycler_view_person_list);
        recyclerViewPersons.setLayoutManager(new LinearLayoutManager(getContext()));
        mFriendAdapter = getAdapter() ;
        recyclerViewPersons.setAdapter(mFriendAdapter);

        mFireBaseRef = new Firebase(TODOCLOUD_ROOT_FIREBASE_URL);
        getPersonList(userUid,getStatus(),getLimit());

        return view;
    }

    protected abstract int getStatus();

    protected abstract int getLimit();

    protected abstract FriendAdapter getAdapter();

    protected void getPersonList(String uid,int status,int limit){
        SocialManager manager = new SocialManager();
        manager.getContactedPerson(uid, status, limit, new SocialManager.OnGetContactedPersons() {
            @Override
            public void onGetContactedPersonsSuccess(List<User> userList) {
                if(isAdded()){
                    mFriendAdapter.setData(userList);
                    Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGetContactedPersonsFailure(FirebaseError e) {
                if(isAdded()){
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    abstract String getHeader();
}
