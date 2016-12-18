package com.example.savi.auth.modules.friends.fragment;

import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.modules.friends.adapter.FriendAdapter;
import com.example.savi.auth.pojo.User;


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
    String getHeader() {
        return getString(R.string.lbl_friend_requests);
    }

    @Override
    protected FriendAdapter getAdapter() {
        final FriendAdapter friendAdapter = new FriendAdapter(getContext(),User.FRIEND_REQUEST);
        friendAdapter.setOnActionClickListener(new FriendAdapter.OnActionClickListener() {
            @Override
            public void onActionClick(User user, int action) {
                if(action==User.ACTION_ACCEPT_REQUEST){
                    mFireBaseRef.child(Constants.CIRCLE).child(userUid).child(user.getUid()).setValue(User.FRIENDS);
                    mFireBaseRef.child(Constants.CIRCLE).child(user.getUid()).child(userUid).setValue(User.FRIENDS);
                    Toast.makeText(getContext(),"Accepted",Toast.LENGTH_SHORT).show();
                }else if(action==User.ACTION_REJECT_REQUEST){
                    mFireBaseRef.child(Constants.CIRCLE).child(userUid).child(user.getUid()).removeValue();
                    mFireBaseRef.child(Constants.CIRCLE).child(user.getUid()).child(userUid).removeValue();
                    Toast.makeText(getContext(),"Rejected",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return friendAdapter;
    }

    @Override
    public CharSequence getTitle() {
        return getActivity().getResources().getString(R.string.lbl_friend_requests);
    }
}
