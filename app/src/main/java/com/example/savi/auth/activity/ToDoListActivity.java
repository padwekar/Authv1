package com.example.savi.auth.activity;

import android.opengl.Visibility;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.TodoAdapter;
import com.example.savi.auth.model.ToDoItem;
import com.example.savi.auth.utils.MoveTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {
    FloatingActionButton mFloatingActionButtonDone ,mFloatingActionButtonAdd ;
    EditText mEditTextAddTask ;
    boolean mTaskAddVisibility = false;
    TodoAdapter mTodoAdapter ;
    List<ToDoItem> mTodoList ;
    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        mEditTextAddTask = (EditText)findViewById(R.id.edittext_add_task);
        mTodoAdapter = new TodoAdapter();
        mTodoList = new ArrayList<>();
        try {
            getSupportActionBar().hide();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        mRecyclerView= (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerView.setAdapter(mTodoAdapter);
        mTodoAdapter.addList(mTodoList);
        mTodoAdapter.notifyDataSetChanged();
        ItemTouchHelper.Callback callback = new MoveTouchHelper(mTodoAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);
        mFloatingActionButtonAdd = (FloatingActionButton)findViewById(R.id.floatingbutton_addtask);
        mFloatingActionButtonDone = (FloatingActionButton)findViewById(R.id.floatingbutton_done);
        mFloatingActionButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = mEditTextAddTask.getText().toString();
                if(!task.equals("")){
                    mTodoList.add(new ToDoItem(task));
                    mTodoAdapter.notifyDataSetChanged();
                    mEditTextAddTask.setText("");
                    setTaskAddVisibility(View.GONE);
                }
            }
        });
        mFloatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTaskAddVisibility){
                    setTaskAddVisibility(View.VISIBLE);
                }
            }
        });


    }

   public void setTaskAddVisibility(int visibility){
       mEditTextAddTask.setVisibility(visibility);
       mFloatingActionButtonDone.setVisibility(visibility);
       visibility = visibility==View.VISIBLE?View.GONE:View.VISIBLE ;
       mFloatingActionButtonAdd.setVisibility(visibility);
   }

}
