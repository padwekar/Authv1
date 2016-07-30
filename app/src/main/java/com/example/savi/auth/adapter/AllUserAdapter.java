package com.example.savi.auth.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.model.MessageItem;
import com.example.savi.auth.model.User;
import com.example.savi.auth.utils.CircleTransform;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Savi on 09-07-2016.
 */
public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.AllUserViewHolder> {
    List<String> mUIDKeyList ;
    List<User> mUserList ;
    LinkedHashMap<String,User> mKeyUserMap ;
    boolean isInboxList ;
    Context mContext ;
    List<User> keyList = new ArrayList<>();
    TypedArray mTypedArray ;
    OnUserItemClickListener IOnItemClickListener ;
    LinkedHashMap<String,List<MessageItem>> messageMap ;
    Firebase mFireBaseRef ;
    LinkedHashMap<User,MessageItem> userMessageItemLinkedHashMap ;

    public interface OnUserItemClickListener{
        void onItemClick(String receiverUID);
    }

    public void setUserItemClickListener(OnUserItemClickListener IOnItemClickListener){
        this.IOnItemClickListener = IOnItemClickListener ;
    }


    public AllUserAdapter(Context mContext , boolean isInboxList) {
        mUIDKeyList = new ArrayList<>();
        mUserList = new ArrayList<>();
        mKeyUserMap = new LinkedHashMap<>();
        messageMap = new LinkedHashMap<>();
        this.mContext = mContext ;
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.avatars);
        this.isInboxList = isInboxList ;
        mFireBaseRef = new Firebase("https://todocloudsavi.firebaseio.com/");
        userMessageItemLinkedHashMap = new LinkedHashMap<>();

    }



   /* public void addKeyUserMapp (Map<String,User> mKeyUserMap){
        this.mKeyUserMap.clear();
        this.mKeyUserMap.putAll(mKeyUserMap);
        notifyDataSetChanged();
    }
   */
/*
    public void addMessageMap( Map<String,List<MessageItem>> messageMap){
        this.messageMap.clear();
     //   this.messageMap.putAll(messageMap);
        ArrayList<String> arrayList =  new ArrayList<>(messageMap.keySet());
        for(int i = arrayList.size()-1 ; i >= 0 ; i -- ){
            this.messageMap.put(arrayList.get(i),messageMap.get(arrayList.get(i)));
        }
        setKeySet(this.messageMap);
        notifyDataSetChanged();
    }
*/

/*

    private void setKeySet(Map<String,List<MessageItem>> messageMap) {
        mUIDKeyList.clear();
        for ( String key : messageMap.keySet() ) {
            mUIDKeyList.add(key);
        }
    }
*/

    public void addUser(User user){

        String uid = user.getUid() ;

        if(mKeyUserMap.containsKey(uid)){
            int keyposition  = mUserList.indexOf(mKeyUserMap.get(uid));
            User userItem =  mKeyUserMap.get(uid);
            mKeyUserMap.remove(uid);
            mUserList.remove(userItem);
            notifyItemRemoved(keyposition);
        }

        mKeyUserMap.put(uid,user);
        mUserList.add(0,user);
        notifyItemInserted(0);

    }

    public void addTotheMap(User key , MessageItem messageItem){

        if(this.userMessageItemLinkedHashMap.containsValue(messageItem))
            return;

        if(this.userMessageItemLinkedHashMap.containsKey(key)){
            int keyposition  = keyList.indexOf(key);
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
        LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_userlist,viewGroup,false);
        return new AllUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllUserAdapter.AllUserViewHolder holder, int position) {
        if(!isInboxList){
            if(mUserList.get(position).getPicPosition()>=0)
                Picasso.with(mContext).load(mTypedArray.getResourceId(mUserList.get(position).getPicPosition(),0)).transform(new CircleTransform(Color.WHITE,5)).fit().into(holder.mImageView);
            else
                Picasso.with(mContext).load(Uri.parse(mUserList.get(position).getProfileDownloadUri())).transform(new CircleTransform(Color.WHITE,5)).fit().into(holder.mImageView);
            holder.mTextviewName.setText(mUserList.get(position).getDisplayName());
            holder.mTextviewStatus.setText(mUserList.get(position).getStatus());
        }else {

            User sender =  keyList.get(position);

            MessageItem messageItem = userMessageItemLinkedHashMap.get(sender);
            if(sender!=null) {
                if (sender.getPicPosition() >= -0)
                    Picasso.with(mContext).load(mTypedArray.getResourceId(sender.getPicPosition(), 0)).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.mImageView);
                else
                    Picasso.with(mContext).load(Uri.parse(sender.getProfileDownloadUri())).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.mImageView);

                holder.mTextviewName.setText(sender.getDisplayName());

                if(messageItem.getSelf())
                    holder.mTextviewStatus.setText("You : " + messageItem.getMessage());
                else
                    holder.mTextviewStatus.setText( sender.getEmail().substring(0,sender.getEmail().indexOf('@')) +" : " + messageItem.getMessage());
            }
        }

    }


    @Override
    public int getItemCount() {
        if(isInboxList)
        return userMessageItemLinkedHashMap.size();
        else
        return mUserList.size();
    }

    class AllUserViewHolder extends RecyclerView.ViewHolder{
        TextView mTextviewName , mTextviewStatus , mTextViewEmail ;
        ImageView mImageView ;
        public AllUserViewHolder(View itemView) {
            super(itemView);
            mTextviewName = (TextView)itemView.findViewById(R.id.textview_display);
            mTextViewEmail = (TextView)itemView.findViewById(R.id.textview_email);
            mTextviewStatus = (TextView)itemView.findViewById(R.id.textview_status);
            mImageView = (ImageView)itemView.findViewById(R.id.imageview_profilepic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isInboxList){
                        IOnItemClickListener.onItemClick(mUserList.get(getAdapterPosition()).getUid());
                    }
                    else{
                        IOnItemClickListener.onItemClick(keyList.get(getAdapterPosition()).getUid());

                    }
                }
            });
        }


    }
}
