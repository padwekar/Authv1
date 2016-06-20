package com.example.savi.auth.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.model.ToDoItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    int clickedPosition = -2 ;
    List<ToDoItem> mTaskList ;
    boolean isVisible = false ;
    public TodoAdapter() {
        mTaskList = new ArrayList<>();
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_listview,parent,false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        holder.mTextViewTaskName.setText(mTaskList.get(position).getTask());
       /* if(position==clickedPosition){
            if(holder.mRelativeLayout.getVisibility()==View.VISIBLE){
                holder.mRelativeLayout.setVisibility(View.VISIBLE);
                holder.mLinearLayout.setVisibility(View.GONE);
            }else {
                holder.mRelativeLayout.setVisibility(View.VISIBLE);
                holder.mLinearLayout.setVisibility(View.GONE);
            }
        }else {
                holder.mRelativeLayout.setVisibility(View.VISIBLE);
                holder.mLinearLayout.setVisibility(View.GONE);
        }*/
    }

    public void addList(List<ToDoItem> taskList){
        mTaskList.clear();
        mTaskList = taskList ;
        clickedPosition = -2 ;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public void swap(int firstPosition, int secondPosition){
        Collections.swap(mTaskList, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }
    public void remove(int position) {
        mTaskList.remove(position);
        notifyItemRemoved(position);
    }


    public class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mTextViewTaskName ;
        CardView mCardView ;
        RelativeLayout mRelativeLayout ;
        LinearLayout mLinearLayout ;
        public TodoViewHolder(View itemView) {
            super(itemView);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativelayout);
            mLinearLayout = (LinearLayout)itemView.findViewById(R.id.linearlayout);
            mTextViewTaskName = (TextView)itemView.findViewById(R.id.textview_task);
            mCardView = (CardView)itemView.findViewById(R.id.cardview2);
            View.OnClickListener layoutClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isVisible){
                        mCardView.animate().translationY(-50).setDuration(700);
                        isVisible = true ;
                      /*  mLinearLayout.animate()
                                .translationY(-50)
                                .alpha(1.0f)
                                .setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mLinearLayout.setVisibility(View.VISIBLE);
                                    }
                                });*/
                    }else{
                        mCardView.animate().translationY(70).setDuration(700);
                        isVisible =false ;
                       /* mLinearLayout.animate()
                                .translationY(+50)
                                .alpha(0.0f)
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mLinearLayout.setVisibility(View.GONE);
                                    }
                                });*/
                    }
                }
            };
            mRelativeLayout.setOnClickListener(layoutClick);
            mLinearLayout.setOnClickListener(layoutClick);


        }

        @Override
        public void onClick(View v) {

        }
    }




}
