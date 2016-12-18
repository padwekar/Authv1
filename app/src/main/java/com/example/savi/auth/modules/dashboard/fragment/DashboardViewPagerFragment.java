package com.example.savi.auth.modules.dashboard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.modules.dashboard.adapter.DashboardViewPagerAdapter;
import com.example.savi.auth.modules.dashboard.model.ViewPagerFragments;

public class DashboardViewPagerFragment extends Fragment {

    public static DashboardViewPagerFragment newInstance() {
        DashboardViewPagerFragment fragment = new DashboardViewPagerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_dashboard_view_pager,container,false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_dashboard);

        DashboardViewPagerAdapter pagerAdapter = new DashboardViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.setFragment(new ViewPagerFragments().getDashboardFragments());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().setTitle(Constants.viewPagerFragmentTitle[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view ;
    }
}
