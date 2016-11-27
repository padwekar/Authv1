package com.example.savi.auth.modules.message.adapter;

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
import com.example.savi.auth.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.AllUserViewHolder> {


    private Context mContext;
    private List<User> keyList = new ArrayList<>();
    private TypedArray mTypedArray;
    private LinkedHashMap<User, MessageItem> userMessageItemLinkedHashMap;
    private OnUserItemClickListener IOnItemClickListener;

    public interface OnUserItemClickListener {
        void onItemClick(String receiverUID);
    }

    public void setUserItemClickListener(OnUserItemClickListener IOnItemClickListener) {
        this.IOnItemClickListener = IOnItemClickListener;
    }

    public InboxAdapter(Context mContext) {
        this.mContext = mContext;
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.avatars);
        userMessageItemLinkedHashMap = new LinkedHashMap<>();
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
    public InboxAdapter.AllUserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_userlist, viewGroup, false);
        return new AllUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InboxAdapter.AllUserViewHolder holder, int position) {
            User sender = keyList.get(position);

            MessageItem messageItem = userMessageItemLinkedHashMap.get(sender);
            if (sender != null) {
                if (sender.getPicPosition() >= -0)
                    Picasso.with(mContext).load(mTypedArray.getResourceId(sender.getPicPosition(), 0)).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.mImageView);
                else
                    Picasso.with(mContext).load(Uri.parse(sender.getProfileDownloadUri())).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.mImageView);

                holder.mTextViewName.setText(sender.getDisplayName());

                if (messageItem.getSelf())
                    holder.mTextViewStatus.setText("You : " + messageItem.getMessage());
                else
                    holder.mTextViewStatus.setText(sender.getEmail().substring(0, sender.getEmail().indexOf('@')) + " : " + messageItem.getMessage());
            }
    }


    @Override
    public int getItemCount() {
        return userMessageItemLinkedHashMap.size();
    }

    class AllUserViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewName, mTextViewStatus;
        ImageView mImageView, mImageViewFriendshipStatus, mImageViewMessage;

        AllUserViewHolder(View itemView) {
            super(itemView);
            mTextViewName = (TextView) itemView.findViewById(R.id.textview_display_name);
            mTextViewStatus = (TextView) itemView.findViewById(R.id.textview_status);
            mImageView = (ImageView) itemView.findViewById(R.id.imageview_profilepic);
            mImageViewFriendshipStatus = (ImageView) itemView.findViewById(R.id.imageview_friendship_status);
            mImageViewMessage = (ImageView)itemView.findViewById(R.id.imageview_message);
            mImageViewFriendshipStatus.setVisibility(View.GONE);
            mImageViewMessage.setVisibility(View.GONE);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        IOnItemClickListener.onItemClick(keyList.get(getAdapterPosition()).getUid());
                }
            });
        }
    }
}
