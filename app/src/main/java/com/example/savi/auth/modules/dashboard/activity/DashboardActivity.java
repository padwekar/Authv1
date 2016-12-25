package com.example.savi.auth.modules.dashboard.activity;

import android.os.Bundle;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseActivity;
import com.example.savi.auth.modules.dashboard.fragment.DashboardViewPagerFragment;
import com.example.savi.auth.modules.dashboard.fragment.DrawerFragmentAuth;

public class DashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        setFragment(DashboardViewPagerFragment.newInstance(), R.id.layout_middle_container);
        setFragment(DrawerFragmentAuth.newInstance(), R.id.layout_drawer_container);
    }


    @Override
    public void onBackPressed() {
    }
}
