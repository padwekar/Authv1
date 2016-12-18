package com.example.savi.auth.modules.friends.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.modules.friends.adapter.FriendAdapter;
import com.example.savi.auth.pojo.User;


public class FriendsFragment extends BaseFriendFragment{

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }


    @Override
    protected int getStatus() {
        return User.FRIENDS;
    }

    @Override
    protected int getLimit() {
        return 5;
    }

    @Override
    protected FriendAdapter getAdapter() {
        FriendAdapter friendAdapter = new FriendAdapter(getContext(),User.FRIENDS);
        friendAdapter.setOnActionClickListener(new FriendAdapter.OnActionClickListener() {
            @Override
            public void onActionClick(User user, int action) {
                if(action==User.ACTION_UNFRIEND){
                    mFireBaseRef.child(Constants.CIRCLE).child(userUid).child(user.getUid()).removeValue();
                    mFireBaseRef.child(Constants.CIRCLE).child(user.getUid()).child(userUid).removeValue();                }
            }
        });
        return friendAdapter;
    }

    @Override
    String getHeader() {
        return getString(R.string.lbl_friends);
    }

    @Override
    public CharSequence getTitle() {
        return getActivity().getResources().getString(R.string.lbl_friend_requests);
    }
}
