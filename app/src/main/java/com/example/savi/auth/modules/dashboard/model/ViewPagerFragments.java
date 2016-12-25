package com.example.savi.auth.modules.dashboard.model;


import com.example.savi.auth.base.BaseViewPagerFragment;
import com.example.savi.auth.modules.alluser.fragment.AllUserFragment;
import com.example.savi.auth.modules.friends.fragment.FriendContainerFragment;
import com.example.savi.auth.modules.message.fragment.MessageFragment;
import com.example.savi.auth.modules.poll.fragment.GroupListFragment;

public class ViewPagerFragments {
    private BaseViewPagerFragment[] dashboardFragments;
    private String[] fragmentTitle ;


    public ViewPagerFragments() {
        this.dashboardFragments = new BaseViewPagerFragment[]{AllUserFragment.newInstance(),
                MessageFragment.newInstance(), GroupListFragment.newInstance(), FriendContainerFragment.newInstance()};
    }

    public BaseViewPagerFragment[] getDashboardFragments() {
        return dashboardFragments;
    }

    public void setDashboardFragments(BaseViewPagerFragment[] dashboardFragments) {
        this.dashboardFragments = dashboardFragments;
    }
}
