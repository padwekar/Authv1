package com.example.savi.auth.modules.dashboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savi.auth.R;


public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private String[] mOptionArray ;
    private Context mContext ;
    private OnOptionSelectedListener mOnOptionSelectedListener ;

    public interface OnOptionSelectedListener{
        void onOptionSelected(int position);
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener listener){
        mOnOptionSelectedListener = listener ;
    }

    public DrawerAdapter(Context context) {
        mContext = context;
        mOptionArray = mContext.getResources().getStringArray(R.array.array_drawer_options);
    }

    @Override
    public DrawerAdapter.DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new DrawerViewHolder(inflater.inflate(R.layout.item_drawer,parent,false));
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        holder.textViewOption.setText(mOptionArray[position]);
    }


    @Override
    public int getItemCount() {
        return mOptionArray.length;
    }

    public void setDrawerOptions(String[] drawerOptions){
        mOptionArray = drawerOptions;
        notifyDataSetChanged();
    }

    public class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textViewOption ;
        ImageView imageViewIcon ;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            textViewOption = (TextView)itemView.findViewById(R.id.textview_option);
            imageViewIcon = (ImageView)itemView.findViewById(R.id.imageview_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mOnOptionSelectedListener!=null)
            mOnOptionSelectedListener.onOptionSelected(getAdapterPosition());
        }
    }
}
