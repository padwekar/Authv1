package com.example.savi.auth.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.example.savi.auth.R;

public class ProfilePicSelectAdapter extends RecyclerView.Adapter<ProfilePicSelectAdapter.ProfileViewHolder> {

    TypedArray mTypedArray ;
    Context mContext ;
    OnProfilePicSelectedListener onProfilePicSelectedListener ;



    public interface OnProfilePicSelectedListener{
        public void onProfilePicSelected(int position);
    }

    public  void setOnProfilePicSelectedListener(OnProfilePicSelectedListener onProfilePicSelectedListener){
        this.onProfilePicSelectedListener = onProfilePicSelectedListener ;
    }

    public ProfilePicSelectAdapter(Context mContext) {
        this.mContext = mContext;
        this.mTypedArray = mContext.getResources().obtainTypedArray(R.array.avatars);
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_profile_pic,viewGroup,false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        holder.imageView.setImageResource(mTypedArray.getResourceId(position,0));
    }

    @Override
    public int getItemCount() {
        return mTypedArray.length();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView ;
        public ProfileViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.imageview_profilepic);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProfilePicSelectedListener.onProfilePicSelected(getAdapterPosition());
        }
    }


}
