package com.example.savi.auth.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.model.Message;
import com.example.savi.auth.model.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Savi on 09-07-2016.
 */
public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.AllUserViewHolder> {
    List<String> mUIDKeyList ;
    List<User> mUserList ;
    Map<String,User> mKeyUserMap ;
    boolean isInboxList ;
    Context mContext ;
    TypedArray mTypedArray ;
    OnUserItemClickListener IOnItemClickListener ;
    Map<String,List<Message>> messageMap ;
    Firebase mFireBaseRef ;
    public interface OnUserItemClickListener{
        void onItemClick(int position);
    }

    public void setUserItemClickListener(OnUserItemClickListener IOnItemClickListener){
        this.IOnItemClickListener = IOnItemClickListener ;
    }

    public AllUserAdapter(Context mContext , boolean isInboxList) {
        mUIDKeyList = new ArrayList<>();
        mUserList = new ArrayList<>();
        mKeyUserMap = new HashMap<>();
        messageMap = new HashMap<>();
        this.mContext = mContext ;
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.avatars);
        this.isInboxList = isInboxList ;
        mFireBaseRef = new Firebase("https://todocloudsavi.firebaseio.com/");

    }


    public void addUserList(List<User> mUserList){
        this.mUserList.clear();
        this.mUserList.addAll(mUserList);
        notifyDataSetChanged();
    }

    public void addKeyUserMapp (Map<String,User> mKeyUserMap){
        this.mKeyUserMap.clear();
        this.mKeyUserMap.putAll(mKeyUserMap);
        notifyDataSetChanged();
    }
    public void addMessageMap( Map<String,List<Message>> messageMap){
        this.messageMap.clear();
        this.messageMap.putAll(messageMap);
        setKeySet(messageMap);
        notifyDataSetChanged();
    }


    private void setKeySet(Map<String,List<Message>> messageMap) {
        mUIDKeyList.clear();
        for ( String key : messageMap.keySet() ) {
            mUIDKeyList.add(key);
        }
    }

    public void addItem(User user){
        mUserList.add(user);
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
            holder.mImageView.setImageResource(mTypedArray.getResourceId(mUserList.get(position).getPicPosition(),0));
            holder.mTextviewName.setText(mUserList.get(position).getDisplayName());
            holder.mTextviewStatus.setText(mUserList.get(position).getStatus());
        }else if(messageMap.size()>0 && mKeyUserMap.size()>0) {
           //
            User  sender =  mKeyUserMap.get(mUIDKeyList.get(position));
            if(sender!=null){
                holder.mImageView.setImageResource(mTypedArray.getResourceId(sender.getPicPosition(),0));
                holder.mTextviewName.setText(sender.getDisplayName());
                holder.mTextviewStatus.setText(messageMap.get(sender.getUid()).get(0).getMessage());
            }

        }

    }

    @Override
    public int getItemCount() {
        if(isInboxList)
        return messageMap.size();
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
                    if(!isInboxList)
                    IOnItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }


    }
}
