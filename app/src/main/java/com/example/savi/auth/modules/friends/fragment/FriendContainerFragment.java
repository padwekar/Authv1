package com.example.savi.auth.modules.friends.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.modules.friends.adapter.FriendViewPagerAdapter;

import java.util.Arrays;
import java.util.List;

public class FriendContainerFragment extends Fragment {


    public static FriendContainerFragment newInstance() {
        FriendContainerFragment fragment = new FriendContainerFragment();
        return fragment;
    }

    private ViewPager viewPager ;
    private FriendViewPagerAdapter viewPagerAdapter ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        Toast.makeText(getContext(), "In Friends Fragment", Toast.LENGTH_SHORT).show();

        Fragment[] fragments = {FriendRequestFragment.newInstance(),FriendsFragment.newInstance(),SentFriendRequestFragment.newInstance()} ;
        List<Fragment> fragmentList = Arrays.asList(fragments) ;
        viewPager = (ViewPager)view.findViewById(R.id.viewpager_friends);
        viewPagerAdapter = new FriendViewPagerAdapter(getActivity().getSupportFragmentManager(),getContext());
        viewPagerAdapter.setFragmentList(fragmentList);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerAdapter);

        return view;
    }
}
