package com.example.savi.auth.modules.alluser.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.pojo.MessageItem;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.example.savi.auth.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.AllUserViewHolder> {

    private List<User> mUserList;
    private Context mContext;
    private TypedArray mTypedArray;
    private HashMap<String, Integer> mCircleMap;

    private OnUserItemClickListener IOnItemClickListener;
    private OnFriendShipStatusClickListener mOnFriendShipStatusClickListener;
    private OnInstantMessageClickListener mOnInstantMessageClickListener;


    public interface OnUserItemClickListener {
        void onItemClick(String receiverUID);
    }

    public interface OnFriendShipStatusClickListener {
        void onFriedShipStatusClick(User user);
    }

    public interface OnInstantMessageClickListener {
        void onInstantMessagesClick(User User);
    }

    public void setUserItemClickListener(OnUserItemClickListener IOnItemClickListener) {
        this.IOnItemClickListener = IOnItemClickListener;
    }

    public void setOnInstantMessageClickListener(OnInstantMessageClickListener listener) {
        mOnInstantMessageClickListener = listener;
    }

    public void setOnFriendShipStatusClickListener(OnFriendShipStatusClickListener listener) {
        mOnFriendShipStatusClickListener = listener;
    }

    public AllUserAdapter(Context mContext) {
        mUserList = new ArrayList<>();
        this.mContext = mContext;
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.avatars);
        mCircleMap = new HashMap<>();
    }


    public void addUser(User user) {
        if (mUserList.contains(user)) {
            int keyposition = mUserList.indexOf(user);
            mUserList.remove(keyposition);
            notifyItemRemoved(keyposition);
        }

        mUserList.add(0, user);
        notifyItemInserted(0);
    }


    public void setCircleMap(HashMap<String, Integer> circleMap) {
        mCircleMap.clear();
        mCircleMap = circleMap;
        notifyDataSetChanged();
    }

    @Override
    public AllUserAdapter.AllUserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_user, viewGroup, false);
        return new AllUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllUserAdapter.AllUserViewHolder holder, int position) {
        User user = mUserList.get(position);

        //Picture Setting
        //transform(new CircleTransform(Color.WHITE, 5))
        Picasso.with(mContext).load(mTypedArray.getResourceId(user.getPicPosition(), 0)).fit().into(holder.mImageView);
        if (user.getProfileDownloadUri() != null) {
            Picasso.with(mContext).load(user.getProfileDownloadUri()).fit().into(holder.mImageView);
        }
        holder.mImageViewFriendshipStatus.setImageResource(R.drawable.ic_add_friend);
        user.setFriendShipStatus(User.NOT_FRIENDS);

        if (mCircleMap.get(user.getUid()) != null)
            switch (mCircleMap.get(user.getUid())) {
                case User.REQUEST_SENT:
                    holder.mImageViewFriendshipStatus.setImageResource(R.drawable.ic_request_sent);
                    user.setFriendShipStatus(User.REQUEST_SENT);
                    break;
                case User.FRIENDS:
                    holder.mImageViewFriendshipStatus.setImageResource(R.drawable.ic_friends);
                    user.setFriendShipStatus(User.FRIENDS);
                    break;
                case User.FRIEND_REQUEST:
                    holder.mImageViewFriendshipStatus.setImageResource(R.drawable.ic_request_pending);
                    user.setFriendShipStatus(User.FRIEND_REQUEST);
                    break;
            }

        holder.mTextviewName.setText(mUserList.get(position).getDisplayName());
        holder.mTextviewStatus.setText(mUserList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class AllUserViewHolder extends RecyclerView.ViewHolder {
        TextView mTextviewName, mTextviewStatus, mTextViewEmail;
        ImageView mImageView, mImageViewFriendshipStatus, mImageViewMessage;

        AllUserViewHolder(View itemView) {
            super(itemView);
            mTextviewName = (TextView) itemView.findViewById(R.id.textview_display_name);
            mTextviewStatus = (TextView) itemView.findViewById(R.id.textview_status);
            mImageView = (ImageView) itemView.findViewById(R.id.imageview_profilepic);
            mImageViewFriendshipStatus = (ImageView) itemView.findViewById(R.id.imageview_friendship_status);
            mImageViewFriendshipStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnFriendShipStatusClickListener != null) {
                        mOnFriendShipStatusClickListener.onFriedShipStatusClick(mUserList.get(getAdapterPosition()));
                    }
                }
            });

            mImageViewMessage = (ImageView) itemView.findViewById(R.id.imageview_message);
            mImageViewMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnInstantMessageClickListener != null) {
                        mOnInstantMessageClickListener.onInstantMessagesClick(mUserList.get(getAdapterPosition()));
                    }
                }
            });

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (IOnItemClickListener != null) {
                        IOnItemClickListener.onItemClick(mUserList.get(getAdapterPosition()).getUid());
                    }
                }
            });
        }
    }
}
