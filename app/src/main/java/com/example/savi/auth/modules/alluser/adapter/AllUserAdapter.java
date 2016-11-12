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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.AllUserViewHolder> {

    private List<User> mUserList;
    private LinkedHashMap<String, User> mKeyUserMap;
    private boolean isInboxList;
    private Context mContext;
    private List<User> keyList = new ArrayList<>();
    private TypedArray mTypedArray;
    private LinkedHashMap<User, MessageItem> userMessageItemLinkedHashMap;
    private String userUid = AuthPreferences.getInstance().getUserUid();
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

    public AllUserAdapter(Context mContext, boolean isInboxList) {
        mUserList = new ArrayList<>();
        mKeyUserMap = new LinkedHashMap<>();
        this.mContext = mContext;
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.avatars);
        this.isInboxList = isInboxList;
        userMessageItemLinkedHashMap = new LinkedHashMap<>();
    }


    public void addUser(User user) {

        String uid = user.getUid();
        if (mKeyUserMap.containsKey(uid)) {
            int keyposition = mUserList.indexOf(mKeyUserMap.get(uid));
            User userItem = mKeyUserMap.get(uid);
            mKeyUserMap.remove(uid);
            mUserList.remove(userItem);
            notifyItemRemoved(keyposition);
        }

        mKeyUserMap.put(uid, user);
        mUserList.add(0, user);
        notifyItemInserted(0);
    }

    public void addTotheMap(User key, MessageItem messageItem) {

        if (this.userMessageItemLinkedHashMap.containsValue(messageItem))
            return;

        if (this.userMessageItemLinkedHashMap.containsKey(key)) {
            int keyposition = keyList.indexOf(key);
            userMessageItemLinkedHashMap.remove(key);
            keyList.remove(keyposition);
            notifyItemRemoved(keyposition);
        }
        this.userMessageItemLinkedHashMap.put(key, messageItem);
        keyList.add(0, key);
        notifyItemInserted(0);
    }


    @Override
    public AllUserAdapter.AllUserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_userlist, viewGroup, false);
        return new AllUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllUserAdapter.AllUserViewHolder holder, int position) {
        if (!isInboxList) {

            User user = mUserList.get(position);

            //Picture Setting
            if (user.getPicPosition() >= 0) {
                Picasso.with(mContext).load(mTypedArray.getResourceId(user.getPicPosition(), 0)).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.mImageView);
            } else {
                Picasso.with(mContext).load(Uri.parse(user.getProfileDownloadUri())).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.mImageView);
            }

            holder.mImageViewFriendshipStatus.setImageResource(R.drawable.ic_add_friend);
            user.setFriendShipStatus(User.NOT_FRIENDS);
            if (user.getContactedPersonsMap() != null) {
                forLoop:
                for (Map.Entry<String, Integer> entry : user.getContactedPersonsMap().entrySet()) {
                    if (userUid.equals(entry.getKey())) {
                        switch (entry.getValue()) {
                            case User.REQUEST_SENT:
                                holder.mImageViewFriendshipStatus.setImageResource(R.drawable.ic_request_sent);
                                user.setFriendShipStatus(User.REQUEST_SENT);
                                break forLoop;
                            case User.FRIENDS:
                                holder.mImageViewFriendshipStatus.setImageResource(R.drawable.ic_friends);
                                user.setFriendShipStatus(User.FRIENDS);
                                break forLoop;
                            case User.FRIEND_REQUEST:
                                holder.mImageViewFriendshipStatus.setImageResource(R.drawable.ic_request_pending);
                                user.setFriendShipStatus(User.FRIEND_REQUEST);
                                break forLoop;
                        }
                    }
                }
            }

            holder.mTextviewName.setText(mUserList.get(position).getDisplayName());
            holder.mTextviewStatus.setText(mUserList.get(position).getStatus());

        } else {

            User sender = keyList.get(position);

            MessageItem messageItem = userMessageItemLinkedHashMap.get(sender);
            if (sender != null) {
                if (sender.getPicPosition() >= -0)
                    Picasso.with(mContext).load(mTypedArray.getResourceId(sender.getPicPosition(), 0)).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.mImageView);
                else
                    Picasso.with(mContext).load(Uri.parse(sender.getProfileDownloadUri())).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.mImageView);

                holder.mTextviewName.setText(sender.getDisplayName());

                if (messageItem.getSelf())
                    holder.mTextviewStatus.setText("You : " + messageItem.getMessage());
                else
                    holder.mTextviewStatus.setText(sender.getEmail().substring(0, sender.getEmail().indexOf('@')) + " : " + messageItem.getMessage());
            }
        }

    }


    public void changeItemStatus(User user, Integer status) {
        int position = mUserList.indexOf(user);
        User item = mUserList.get(position);
        item.setFriendShipStatus(status);
        notifyItemChanged(position);
    }


    @Override
    public int getItemCount() {
        if (isInboxList)
            return userMessageItemLinkedHashMap.size();
        else
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
                    if (!isInboxList) {
                        if (IOnItemClickListener != null) {
                            IOnItemClickListener.onItemClick(mUserList.get(getAdapterPosition()).getUid());
                        }
                    } else {
                        IOnItemClickListener.onItemClick(keyList.get(getAdapterPosition()).getUid());

                    }
                }
            });
        }
    }
}
