package com.example.savi.auth.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.model.ToDoItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    List<ToDoItem> mTaskList ;

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
    }

    public void addList(List<ToDoItem> taskList){
        mTaskList.clear();
        mTaskList = taskList ;
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


    public class TodoViewHolder extends RecyclerView.ViewHolder{
        TextView mTextViewTaskName ;
        public TodoViewHolder(View itemView) {
            super(itemView);
            mTextViewTaskName = (TextView)itemView.findViewById(R.id.textview_task);
        }
    }


}
