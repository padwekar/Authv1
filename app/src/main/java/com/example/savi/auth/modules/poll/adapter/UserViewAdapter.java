package com.example.savi.auth.modules.poll.adapter;

import android.support.v7.widget.RecyclerView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.pojo.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.AllUserViewHolder> implements Animator.AnimatorListener {

    private List<User> mUserList;
    private Context mContext;
    private TypedArray mTypedArray;
    private HashMap<String, Integer> mCircleMap;

    private UserViewAdapter.OnUserItemClickListener IOnItemClickListener;
    private UserViewAdapter.OnFriendShipStatusClickListener mOnFriendShipStatusClickListener;
    private UserViewAdapter.OnInstantMessageClickListener mOnInstantMessageClickListener;

    private List<View> mListViews;
    private int position;

    private int startPosition = 0;
    private int endPosition = 0;

    private int startPoint = 0;
    private int endPoint = 150;


    public interface OnUserItemClickListener {
        void onItemClick(String receiverUID);
    }

    public interface OnFriendShipStatusClickListener {
        void onFriedShipStatusClick(User user);
    }

    public interface OnInstantMessageClickListener {
        void onInstantMessagesClick(User User);
    }


    public void setUserItemClickListener(UserViewAdapter.OnUserItemClickListener IOnItemClickListener) {
        this.IOnItemClickListener = IOnItemClickListener;
    }

    public void setOnInstantMessageClickListener(UserViewAdapter.OnInstantMessageClickListener listener) {
        mOnInstantMessageClickListener = listener;
    }

    public void setOnFriendShipStatusClickListener(UserViewAdapter.OnFriendShipStatusClickListener listener) {
        mOnFriendShipStatusClickListener = listener;
    }

    public UserViewAdapter(Context mContext) {
        mUserList = new ArrayList<>();
        this.mContext = mContext;
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.avatars);
        mCircleMap = new HashMap<>();
        mListViews = new ArrayList<View>();
    }


    public void addUser(User user) {
        if (mUserList.contains(user)) {
            int keyposition = mUserList.indexOf(user);
            mUserList.remove(keyposition);
            notifyItemRemoved(keyposition);
        }

        mUserList.add(user);
        notifyItemInserted(mUserList.size());
    }


    public void setCircleMap(HashMap<String, Integer> circleMap) {
        mCircleMap.clear();
        mCircleMap = circleMap;
        notifyDataSetChanged();
    }

    @Override
    public AllUserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_user_small, viewGroup, false);
        return new AllUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllUserViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.mCardViewUser.setVisibility(View.GONE);
        //Picture Setting
        //transform(new CircleTransform(Color.WHITE, 5))
        Picasso.with(mContext).load(mTypedArray.getResourceId(user.getPicPosition(), 0)).fit().into(holder.mImageViewUser);
        if (user.getProfileDownloadUri() != null) {
            Picasso.with(mContext).load(user.getProfileDownloadUri()).fit().into(holder.mImageViewUser);
        }

        if (mListViews.size() != getItemCount()) {
            holder.mCardViewUser.setTag(position);
            mListViews.add(holder.mCardViewUser);
            move(holder.mCardViewUser, "translationY", 1000, 0, position * 100);
        }

    }


    private void move(View view, String transaction, int start, int end) {
        move(view, transaction, start, end, 0);
    }

    private void move(View view, String transaction, int start, int end, long delay) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, transaction, start, end);
        animator.addListener(this);
        animator.setDuration(1000);
        animator.setStartDelay(delay);
        animator.start();
    }


    @Override
    public void onAnimationStart(Animator animator) {
        mListViews.get(position++).setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animator animator) {
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void startCollection() {
        //isAnimation = true ;
        move(mListViews.get(0), "translationY", startPoint, endPoint);
        notifyDataSetChanged();
    }

    public void performBottomToMidAnimation() {
        //isAnimation = false ;
        position = 0;
        startPoint = 150;
        endPoint = 0;
        move(mListViews.get(position), "translationY", startPoint, endPoint);
    }

    public void performTopToMidAnimation() {
        //isAnimation = false ;
        position = 0;
        startPoint = -150;
        endPoint = 0;
        move(mListViews.get(position), "translationY", startPoint, endPoint);
    }

    public void performMidToTopAnimation() {
        // isAnimation = true ;
        position = 0;
        startPoint = 0;
        endPoint = -150;
        move(mListViews.get(position++), "translationY", startPoint, endPoint);
    }

    @Override
    public void onAnimationCancel(Animator animator) {
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
    }


    public class AllUserViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageViewUser;
        CardView mCardViewUser;

        AllUserViewHolder(View itemView) {
            super(itemView);
            mImageViewUser = (ImageView) itemView.findViewById(R.id.imageview_user);
            mCardViewUser = (CardView) itemView.findViewById(R.id.cardview_user);
        }
    }
}
