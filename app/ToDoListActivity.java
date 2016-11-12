package com.example.savi.auth.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.TodoAdapter;
import com.example.savi.auth.pojo.ToDoItem;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {
    FloatingActionButton mFloatingActionButtonDone ,mFloatingActionButtonAdd , mFloatingActionButtonLogOut;
    EditText mEditTextAddTask ;
    boolean mTaskAddVisibility = false;
    TodoAdapter mTodoAdapter ;
    List<ToDoItem> mTodoList ;
    RecyclerView mRecyclerView;
    Firebase mRef ;
    String uid ;
    String email = "";
    TextView mTextViewGreets , mTextViewChangePassword ;
    SpannableString mSpannableStringGreetUser;
    ProgressBar mProgressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_to_do_list);
        mTodoList = new ArrayList<>();
        mRef= new Firebase("https://todocloudsavi.firebaseio.com/users");
        uid= getIntent().getStringExtra("uid");
        if(uid!=null){
            mRef.child(uid);
            email = mRef.getAuth().getProviderData().get("email").toString();
            mSpannableStringGreetUser = new SpannableString("Hello ,"+email);
            mSpannableStringGreetUser.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(),android.R.color.holo_green_dark)),7,mSpannableStringGreetUser.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        mTextViewChangePassword = (TextView)findViewById(R.id.textview_change_password);
        mTextViewChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildChangePasswordDialog();
            }
        });
        mTextViewGreets = (TextView)findViewById(R.id.textview_user_greet);
        if(mSpannableStringGreetUser!=null)
            mTextViewGreets.setText(mSpannableStringGreetUser);


        mEditTextAddTask = (EditText)findViewById(R.id.edittext_add_task);
        mTodoAdapter = new TodoAdapter();
        try {
            getSupportActionBar().hide();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
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
        mFloatingActionButtonLogOut = (FloatingActionButton)findViewById(R.id.floatingbutton_logout);
        mFloatingActionButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef.unauth();
                finish();
            }
        });

        mRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
            {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
                                String list  = dataSnapshot.getValue().toString();
                                mTodoList.clear();
                                mTodoList = new Gson().fromJson(list,new TypeToken<List<ToDoItem>>(){}.getType());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTodoAdapter.addList(mTodoList);
                                        mProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(getBaseContext(),"Updated Successful",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    }).start();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mProgressBar.setVisibility(View.GONE);
            }
        });


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


    private void buildChangePasswordDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(ToDoListActivity.this);
        builder.setTitle("Change Password");
        builder.setMessage(email);

        final EditText editTextCurrentPassword = createNewEdittext("Current Password",12);
        final EditText editTextPassword = createNewEdittext("New Password",12);
        final EditText editTextConfirmPassword = createNewEdittext("Confirm Password",12);


        LinearLayout layout = new LinearLayout(ToDoListActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        editTextConfirmPassword.setLayoutParams(lp);
        editTextPassword.setLayoutParams(lp);
        editTextConfirmPassword.setLayoutParams(lp);

        layout.addView(editTextCurrentPassword);
        layout.addView(editTextPassword);
        layout.addView(editTextConfirmPassword);

        builder.setView(layout);
        builder.setIcon(R.drawable.ic_checkbox_marked_circle_outline_black_18dp);
        builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if(isValid(editTextCurrentPassword,editTextPassword,editTextConfirmPassword)){
                    mRef.changePassword(email,editTextCurrentPassword.getText().toString(), editTextPassword.getText().toString(), new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ToDoListActivity.this,"Password Changed Successfully",Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // error encountered
                            Toast.makeText(ToDoListActivity.this,firebaseError.toString(),Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private EditText createNewEdittext(String hint, int textSize) {
        final EditText edittext = new EditText(ToDoListActivity.this);
        edittext.setHint(hint);
        edittext.setTextSize(textSize);
        return edittext;
    }


    private boolean isValid(EditText currentpassword ,EditText newPassword , EditText confirmPassword) {
        boolean isValid = true ;
        if(currentpassword.getText().toString().equals("")){
            confirmPassword.setHint("Enter Current Password");
            isValid = false ;
        }else if(newPassword.getText().toString().equals("")){
            newPassword.setHint("Enter New Password");
            isValid = false ;
    }else  if(confirmPassword.getText().toString().equals("")){
            newPassword.setHint("Re-Enter Password");
            isValid = false ;
        }
        return  isValid ;
    }

    public void setTaskAddVisibility(int visibility){
       mEditTextAddTask.setVisibility(visibility);
       mFloatingActionButtonDone.setVisibility(visibility);
       visibility = visibility==View.VISIBLE?View.GONE:View.VISIBLE ;
       mFloatingActionButtonAdd.setVisibility(visibility);
   }

    public void saveData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = new Gson().toJson(mTodoList);
                mRef.child(uid).setValue(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(),"Sync Success",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
