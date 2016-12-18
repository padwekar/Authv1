package com.example.savi.auth.modules.dashboard.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.savi.auth.base.BaseViewPagerFragment;
import com.example.savi.auth.constant.Constants;

public class DashboardViewPagerAdapter extends FragmentStatePagerAdapter {

    private BaseViewPagerFragment[] fragments ;

    public DashboardViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragment(BaseViewPagerFragment[] fragments){
        this.fragments = fragments ;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {return fragments.length;}

    @Override
    public CharSequence getPageTitle(int position) {
       return Constants.viewPagerFragmentTitle[position];
    }
}
