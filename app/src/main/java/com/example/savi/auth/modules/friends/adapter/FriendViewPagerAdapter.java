package com.example.savi.auth.modules.friends.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.savi.auth.R;
import com.example.savi.auth.modules.friends.fragment.FriendRequestFragment;
import com.example.savi.auth.modules.friends.fragment.FriendsFragment;
import com.example.savi.auth.modules.friends.fragment.SentFriendRequestFragment;

import java.util.ArrayList;
import java.util.List;


public class FriendViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList = new ArrayList<>();;
    private String[] titles ;

    public FriendViewPagerAdapter(FragmentManager fm , Context context) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.friends_child_names);
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
