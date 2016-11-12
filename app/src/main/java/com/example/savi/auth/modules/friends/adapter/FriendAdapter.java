package com.example.savi.auth.modules.friends.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private Context mContext ;
    private List<User> userList ;
    private OnActionClickListener mOnActionClickListener ;
    private TypedArray mTypedArray;

    private int itemType =  0 ;

    public FriendAdapter(Context context ,int itemType) {
        this.mContext = context;
        this.itemType = itemType ;
        userList = new ArrayList<>();
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.avatars);
    }

    public interface OnActionClickListener{
        void onActionClick(User user , int action);
    }

    public void setOnActionClickListener(OnActionClickListener listener){
        mOnActionClickListener = listener ;
    }

    public void setData(List<User> userList){
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
    }

    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_friend_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendAdapter.ViewHolder holder, int position) {
        final User user =  userList.get(position) ;
        String actionText = "" ;
        int action = User.ACTION_SEND_REQUEST ;
        holder.buttonAction.setVisibility(View.VISIBLE);
        holder.linearLayoutRequestResponse.setVisibility(View.INVISIBLE);

        if (user.getPicPosition() >= 0) {
            Picasso.with(mContext).load(mTypedArray.getResourceId(user.getPicPosition(), 0)).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.imageViewProfile);
        } else {
            Picasso.with(mContext).load(Uri.parse(user.getProfileDownloadUri())).transform(new CircleTransform(Color.WHITE, 5)).fit().into(holder.imageViewProfile);
        }

        holder.textViewName.setText(user.getDisplayName());
        holder.textViewEmail.setText(user.getEmail());

        if(itemType==User.FRIEND_REQUEST) holder.linearLayoutRequestResponse.setVisibility(View.VISIBLE);

        if(itemType==User.FRIENDS) { actionText = User.friendshipActions[User.FRIENDS]; action = User.ACTION_UNFRIEND ; }

        if(itemType==User.REQUEST_SENT) { actionText = User.friendshipActions[User.REQUEST_SENT]; action = User.ACTION_CANCEL_REQUEST ;}

        if(actionText.equals("")) holder.buttonAction.setVisibility(View.INVISIBLE);

        final int mainaction = action ;
        holder.buttonAction.setText(actionText);
        holder.buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnActionClickListener!=null) mOnActionClickListener.onActionClick(user,mainaction);
            }
        });

        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnActionClickListener!=null) mOnActionClickListener.onActionClick(user,User.ACTION_ACCEPT_REQUEST);
            }
        });

        holder.buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnActionClickListener!=null) mOnActionClickListener.onActionClick(user,User.ACTION_REJECT_REQUEST);
            }
        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

         TextView textViewName ;
         TextView textViewEmail ;
         ImageView imageViewProfile ;
         Button buttonAction ;
         Button buttonAccept ;
         Button buttonReject ;
         LinearLayout linearLayoutRequestResponse;

        public ViewHolder(View itemView) {
            super(itemView);
             textViewName = (TextView)itemView.findViewById(R.id.textview_name);
             textViewEmail = (TextView)itemView.findViewById(R.id.textview_email);
             imageViewProfile = (ImageView)itemView.findViewById(R.id.imageview_profile_image);
             buttonAction = (Button)itemView.findViewById(R.id.button_action);
             buttonAccept = (Button)itemView.findViewById(R.id.button_accept);
             buttonReject = (Button)itemView.findViewById(R.id.button_reject);
             linearLayoutRequestResponse = (LinearLayout)itemView.findViewById(R.id.ll_request_response);
        }
    }
}
