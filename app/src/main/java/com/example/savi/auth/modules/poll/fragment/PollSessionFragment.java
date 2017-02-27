package com.example.savi.auth.modules.poll.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseFragment;
import com.example.savi.auth.modules.alluser.operation.manager.UserManager;
import com.example.savi.auth.modules.poll.adapter.UserViewAdapter;
import com.example.savi.auth.pojo.Group;
import com.example.savi.auth.pojo.User;
import com.firebase.client.FirebaseError;


public class PollSessionFragment extends BaseFragment {

    private Group mGroup;
    private UserViewAdapter mAdapterSmallView;

    public static PollSessionFragment newInstance(Group group) {
        PollSessionFragment fragment = new PollSessionFragment();
        fragment.mGroup = group;
        return fragment;
    }

    public static PollSessionFragment newInstance() {
        return newInstance(null);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poll, container, false);

        mAdapterSmallView = new UserViewAdapter(getContext());
        RecyclerView recyclerViewSmall = (RecyclerView) view.findViewById(R.id.recycler_view_small);
        recyclerViewSmall.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSmall.setAdapter(mAdapterSmallView);

        getAllUser();
        return view;
    }

    private void getAllUser() {
        UserManager manager = new UserManager();
        manager.getAllUsers(new UserManager.OnGetAllUserManager() {
            @Override
            public void onUserAdded(User user) {
                mAdapterSmallView.addUser(user);
           //     mAdapterLargeView.addUser(user);
            }

            @Override
            public void onUserUpdated(User user) {

            }

            @Override
            public void onUserRemoved(User user) {

            }

            @Override
            public void onCancelled(FirebaseError error) {

            }
        });
    }

}
