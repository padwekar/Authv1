package com.example.savi.auth.modules.dashboard.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.savi.auth.R;
import com.example.savi.auth.modules.dashboard.fragment.DashboardViewPagerFragment;
import com.example.savi.auth.modules.dashboard.fragment.DrawerFragment;
import com.example.savi.auth.modules.friends.fragment.FriendContainerFragment;
import com.example.savi.auth.modules.message.fragment.MessageFragment;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        setFragment(DashboardViewPagerFragment.newInstance(),R.id.layout_middle_container);
        setFragment(DrawerFragment.newInstance(),R.id.layout_drawer_container);
    }

    public void setFragment(Fragment fragment ,int resId) {
        FragmentManager fragmentManager = getSupportFragmentManager() ;
        //fragmentManager.popBackStackImmediate();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(resId,fragment);
       fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}
