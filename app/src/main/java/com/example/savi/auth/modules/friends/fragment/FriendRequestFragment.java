package com.example.savi.auth.modules.friends.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.modules.friends.adapter.FriendAdapter;
import com.example.savi.auth.pojo.User;

/**
 * Created by Savi on 12-11-2016.
 */

public class FriendRequestFragment extends BaseFriendFragment {

    public static FriendRequestFragment newInstance() {
       return new FriendRequestFragment();
    }

    @Override
    protected int getStatus() {
        return User.FRIEND_REQUEST;
    }

    @Override
    protected int getLimit() {
        return 5;
    }

    @Override
    protected FriendAdapter getAdapter() {
        FriendAdapter friendAdapter = new FriendAdapter(getContext(),User.FRIEND_REQUEST);
        friendAdapter.setOnActionClickListener(new FriendAdapter.OnActionClickListener() {
            @Override
            public void onActionClick(User user, int action) {
                if(action==User.ACTION_ACCEPT_REQUEST){
                    Toast.makeText(getContext(),"accepted",Toast.LENGTH_SHORT).show();
                }else if(action==User.ACTION_REJECT_REQUEST){
                    Toast.makeText(getContext(),"rejected",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return friendAdapter;
    }

    @Override
    String getHeader() {
        return getString(R.string.lbl_friend_requests);
    }
}
