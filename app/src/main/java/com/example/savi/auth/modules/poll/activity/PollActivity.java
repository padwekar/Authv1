package com.example.savi.auth.modules.poll.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseActivity;
import com.example.savi.auth.base.BaseFragment;
import com.example.savi.auth.modules.poll.fragment.GroupListFragment;

public class PollActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        setFragment(GroupListFragment.newInstance(),R.id.frame_layout);
    }
}
