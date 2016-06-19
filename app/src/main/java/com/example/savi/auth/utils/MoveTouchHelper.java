package com.example.savi.auth.utils;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.savi.auth.adapter.TodoAdapter;


public class MoveTouchHelper extends ItemTouchHelper.SimpleCallback {
    private TodoAdapter mMovieAdapter;

    public MoveTouchHelper(TodoAdapter movieAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mMovieAdapter = movieAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mMovieAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    //Remove item
        int tempPos = viewHolder.getAdapterPosition();
        mMovieAdapter.remove(tempPos);
        mMovieAdapter.notifyItemRemoved(tempPos);
    }


}