package com.example.savi.auth.modules.friends.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.modules.friends.adapter.FriendAdapter;
import com.example.savi.auth.pojo.User;

public class SentFriendRequestFragment extends BaseFriendFragment{

    public static SentFriendRequestFragment newInstance() {
        return new SentFriendRequestFragment();
    }

    @Override
    protected int getStatus() {
        return User.REQUEST_SENT;
    }

    @Override
    protected int getLimit() {
        return 5;
    }

    @Override
    protected FriendAdapter getAdapter() {
        FriendAdapter friendAdapter = new FriendAdapter(getContext(),User.REQUEST_SENT);
        friendAdapter.setOnActionClickListener(new FriendAdapter.OnActionClickListener() {
            @Override
            public void onActionClick(User user, int action) {
                if(action==User.ACTION_CANCEL_REQUEST){
                    mFireBaseRef.child(Constants.CIRCLE).child(userUid).child(user.getUid()).removeValue();
                    mFireBaseRef.child(Constants.CIRCLE).child(user.getUid()).child(userUid).removeValue();                }
            }
        });
        return friendAdapter;
    }

    @Override
    String getHeader() {
        return getString(R.string.lbl_sent_requests);
    }

    @Override
    public CharSequence getTitle() {
        return getActivity().getResources().getString(R.string.lbl_friend_requests);
    }
}
