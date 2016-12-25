package com.example.savi.auth.modules.poll.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseViewPagerFragment;
import com.example.savi.auth.modules.poll.adapter.GroupListAdapter;
import com.example.savi.auth.pojo.Group;


public class GroupListFragment extends BaseViewPagerFragment {

    private GroupListAdapter mGroupListAdapter;

    public static GroupListFragment newInstance() {
         return new GroupListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poll_list, container, false);

        mGroupListAdapter = new GroupListAdapter(getContext());
        RecyclerView recyclerViewGroupList = (RecyclerView) view.findViewById(R.id.recycler_view_group_list);
        recyclerViewGroupList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewGroupList.setAdapter(mGroupListAdapter);

        FloatingActionButton fabAddGroup = (FloatingActionButton) view.findViewById(R.id.floatingbutton_add_group);
        fabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CreatePollFragment fragment = CreatePollFragment.newInstance();
                fragment.setOnSubmitClickListener(new CreatePollFragment.OnSubmitClickListener() {
                    @Override
                    public void onSubmitClick(Group group) {
                        mGroupListAdapter.addGroup(group);
                        fragment.dismiss();
                    }

                    @Override
                    public void onCancelClick() {
                        fragment.dismiss();
                    }
                });

            //
                    fragment.show(getFragmentManager(),"dialog");
              //  setFragment(fragment,R.id.layout_middle_container);
            }
        });
        return view;
    }

    @Override
    public CharSequence getTitle() {
        return "Poll (New) ";
    }
}
