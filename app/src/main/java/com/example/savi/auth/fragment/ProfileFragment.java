package com.example.savi.auth.fragment;

import android.app.Dialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.ProfilePicSelectAdapter;
import com.example.savi.auth.model.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;


import java.util.logging.LogRecord;

public class ProfileFragment extends Fragment {
    EditText mEdittextDisplayName ;
    EditText mEdittextStatus ;
    Button mButtonSubmit ;
    Switch mSwitch ;
    String uid ;
    User user ;
    ProgressBar mProgressbar ;
    Firebase mFireBaseUserRef ;
    Handler handler ;
    ImageView mImgaeView ;
    int image_position ;
    TypedArray mTypedArray ;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        mFireBaseUserRef = new Firebase("https://todocloudsavi.firebaseio.com/user");
        mProgressbar = (ProgressBar)view.findViewById(R.id.progressbar);
        Toast.makeText(getContext(),"In ProfileFragment",Toast.LENGTH_SHORT).show();
        mEdittextDisplayName = (EditText)view.findViewById(R.id.edittext_displayname);
        mEdittextDisplayName.setText(mFireBaseUserRef.getAuth().getProviderData().get("email").toString());
        mEdittextStatus = (EditText)view.findViewById(R.id.edittext_status);
        mSwitch = (Switch)view.findViewById(R.id.switch_email_visibility);
        mEdittextStatus.setText("Hi everyone");
        mButtonSubmit = (Button)view.findViewById(R.id.button_submit);
        mImgaeView = (ImageView)view.findViewById(R.id.imageview_profile);
        mTypedArray = getContext().getResources().obtainTypedArray(R.array.avatars);
        mImgaeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_select_profile_pic);
                RecyclerView recyclerView = (RecyclerView)dialog.findViewById(R.id.recycler_view_profile_pic);
                ProfilePicSelectAdapter picSelectAdapter = new ProfilePicSelectAdapter(getContext());
                picSelectAdapter.setOnProfilePicSelectedListener(new ProfilePicSelectAdapter.OnProfilePicSelectedListener() {
                    @Override
                    public void onProfilePicSelected(int position) {
                        mImgaeView.setImageResource(mTypedArray.getResourceId(position,0));
                        image_position = position ;
                        dialog.dismiss();
                    }
                });
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                recyclerView.setAdapter(picSelectAdapter);
                dialog.show();
            }
        });
        uid = getActivity().getIntent().getStringExtra("uid");

        handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(getActivity()!=null){
                    switch (msg.arg1){
                        case 0 :  mProgressbar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"User Details Updated",Toast.LENGTH_SHORT).show(); break;
                        case 1 :  mProgressbar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show(); break;
                    }
                }

            }
        };
        setData();
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mProgressbar.setVisibility(View.VISIBLE);
              new Thread(new Runnable() {
                  @Override
                  public void run() {
                      String displayName =  mEdittextDisplayName.getText().toString();
                      String status = mEdittextStatus.getText().toString() ;
                      String email = mFireBaseUserRef.getAuth().getProviderData().get("email").toString();
                      user  = new User();
                      user.setDisplayName(displayName);
                      user.setStatus(status);
                      user.setVisible(false);
                      user.setEmail(email);
                      user.setPicPosition(image_position);
                      user.setUid(uid);
                      Message message = new Message();
                      message.arg1 = 1 ;
                      String data = new Gson().toJson(user);
                      mFireBaseUserRef.child(uid).setValue(data);
                      handler.sendMessage(message);
                  }
              }).start();

            }
        });


        return view;
    }

    private void setData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFireBaseUserRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
                                String data = dataSnapshot.getValue().toString() ;
                                user = new User();
                                user = new Gson().fromJson(data,User.class);
                                Message message = new Message();
                                message.arg1 = 0 ;
                                String displayName = user.getDisplayName() ;
                                String status = user.getStatus() ;
                                image_position = user.getPicPosition() ;
                                mEdittextDisplayName.setText(displayName);
                                mEdittextStatus.setText(status);
                                mImgaeView.setImageResource(mTypedArray.getResourceId(user.getPicPosition(),0));
                                handler.sendMessage(message);
                            }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }).start();
    }
}
