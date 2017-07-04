package com.example.savi.auth.modules.poll.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.pojo.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class GroupListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Group> mGroupList;
    private Context mContext;
    private SparseArray<List<Group>> mSparseArrayGroup;
    private TypedArray mColorTypedArray;
    private String[] mHeaderArray;

    public final static int HEADER = 0 ;
    public final static int CHILD = HEADER+1 ;

    public GroupListAdapter(Context mContext) {
        this.mContext = mContext;
        mSparseArrayGroup = new SparseArray<>(3);
        mColorTypedArray = mContext.getResources().obtainTypedArray(R.array.status_color_array);
        mHeaderArray = mContext.getResources().getStringArray(R.array.group_status_array);
        initGroupList();
    }

    private void initGroupList() {
        mGroupList = new ArrayList<>();
        int i = 0;
        for (String header : Group.headers) {
            mGroupList.add(new Group(header));
            mSparseArrayGroup.append(i++, new ArrayList<Group>());
        }
    }

    public void updateGroupList(SparseArray<List<Group>> groupMap) {
        mSparseArrayGroup = groupMap;
        mGroupList = new ArrayList<>();
        for (int i = 0; i < groupMap.size(); i++) {
            mGroupList.add(new Group(Group.headers[i]));
            mGroupList.addAll(groupMap.get(groupMap.keyAt(i)));
        }
        notifyDataSetChanged();
    }

    public void addGroup(Group group) {
        int position = getPosition(group.getStatus());
        if (position >= mGroupList.size()) {
            mGroupList.add(group);
            mSparseArrayGroup.get(group.getStatus()).add(group);
            notifyItemInserted(mGroupList.size());
            return;
        }
        mGroupList.add(position, group);
        mSparseArrayGroup.get(group.getStatus()).add(group);
        notifyItemInserted(position);
    }

    private int getPosition(int status) {
        if (status == 0) return status + 1;
        int position = 0;
        do {
            position += mSparseArrayGroup.get(--status).size() + 1;
        } while (status > 0);

        return position + 1;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        if (viewType == HEADER) {
            view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new GroupTitleViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_group_list, parent, false);
            return new GroupViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Group group = getItem(position);
        switch (getItemViewType(position)) {
            case HEADER:
                GroupTitleViewHolder groupTitleViewHolder = (GroupTitleViewHolder) holder;
                groupTitleViewHolder.textViewTitle.setText(group.getHeaderName());
                break;
            case CHILD:
                GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
                groupViewHolder.textViewGroupName.setText(group.getName());
                groupViewHolder.textViewMembers.setText(String.format(Locale.getDefault(),mContext.getString(R.string.lbl_members), group.getActiveUsers().size(), group.getMaxMembers()));
                groupViewHolder.textViewStatus.setText(mHeaderArray[group.getStatus()]);
                groupViewHolder.textViewStatus.setBackgroundColor(mColorTypedArray.getColor(group.getStatus(), 0));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mGroupList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isHeader() ? HEADER : CHILD;
    }

    private Group getItem(int position) {
        return mGroupList.get(position);
    }

    private class GroupViewHolder extends RecyclerView.ViewHolder implements Animator.AnimatorListener {
        TextView textViewGroupName;
        TextView textViewMembers;
        TextView textViewStatus;
        CardView cardViewGroupInfo ;
        LinearLayout linearLayoutOptions ;

        GroupViewHolder(final View itemView) {
            super(itemView);
            textViewGroupName = (TextView) itemView.findViewById(R.id.textview_group_name);
            textViewMembers = (TextView) itemView.findViewById(R.id.textview_total_members);
            textViewStatus = (TextView) itemView.findViewById(R.id.textview_status);
            cardViewGroupInfo = (CardView)itemView.findViewById(R.id.cardview_group_info);
            linearLayoutOptions = (LinearLayout)itemView.findViewById(R.id.linearlayout_options);
            cardViewGroupInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mGroupList.get(getAdapterPosition()).isOpen){
                        ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(cardViewGroupInfo, "translationX", 0, 750);
                        objectAnimator.addListener(GroupViewHolder.this);
                        objectAnimator.setDuration(1000);
                        ObjectAnimator objectAnimator1= ObjectAnimator.ofFloat(linearLayoutOptions, "translationX", 750, 0);
                        objectAnimator1.setDuration(800);
                        objectAnimator1.start();
                        objectAnimator.start();
                    }else {
                        ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(cardViewGroupInfo, "translationX", 750, 0);
                        objectAnimator.addListener(GroupViewHolder.this);
                        objectAnimator.setDuration(1000);
                        ObjectAnimator objectAnimator1= ObjectAnimator.ofFloat(linearLayoutOptions, "translationX", 0, 750);
                        objectAnimator1.setDuration(1200);
                        objectAnimator1.start();
                        objectAnimator.start();
                    }
                    }
            });
        }

        @Override
        public void onAnimationStart(Animator animator) {
            cardViewGroupInfo.setEnabled(false);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mGroupList.get(getAdapterPosition()).isOpen = !mGroupList.get(getAdapterPosition()).isOpen ;
            cardViewGroupInfo.setEnabled(true);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    private class GroupTitleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        GroupTitleViewHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(android.R.id.text1);
            textViewTitle.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        }
    }
}
