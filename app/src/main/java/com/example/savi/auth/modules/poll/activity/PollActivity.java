package com.example.savi.auth.modules.poll.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseActivity;
import com.example.savi.auth.base.BaseFragment;
import com.example.savi.auth.modules.poll.fragment.GroupListFragment;
import com.example.savi.auth.modules.poll.fragment.PollSessionFragment;
import com.google.gson.Gson;

public class PollActivity extends BaseActivity{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        setFragment(PollSessionFragment.newInstance(),R.id.frame_layout);
    }
}
