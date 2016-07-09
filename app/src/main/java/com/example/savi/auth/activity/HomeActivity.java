package com.example.savi.auth.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.fragment.AllUserFragment;
import com.example.savi.auth.fragment.FriendsFragment;
import com.example.savi.auth.fragment.MessageFragment;
import com.example.savi.auth.fragment.ProfileFragment;
import com.example.savi.auth.model.ToDoItem;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    Firebase mRef, mFirebaseRefAllUser;
    List<String> allUserList;
    String uid ;
    TextView textViewLogOut ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        uid = getIntent().getStringExtra("uid");
        RadioGroup radioGroupProfileOptions = (RadioGroup)findViewById(R.id.radiogroup_dashboard);
        radioGroupProfileOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radiobutton_alluser : setFragment(AllUserFragment.newInstance()); break;
                    case R.id.radiobutton_friends : setFragment(FriendsFragment.newInstance()); break;
                    case R.id.radiobutton_msg :     setFragment(MessageFragment.newInstance()); break;
                    case R.id.radiobutton_profile : setFragment(ProfileFragment.newInstance()); break;
                    case R.id.radiobutton_logout :  dologout(); break;
                }
            }
        });


        allUserList = new ArrayList<>();
        mRef = new Firebase("https://todocloudsavi.firebaseio.com/user");
        mFirebaseRefAllUser = new Firebase("https://todocloudsavi.firebaseio.com/alluser");
        mFirebaseRefAllUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null && dataSnapshot.getValue()!= null){
                    String data = dataSnapshot.getValue().toString() ;
                    allUserList = new Gson().fromJson(data,new TypeToken<List<String>>(){}.getType());
                    Toast.makeText(getBaseContext(), "User List Updated", Toast.LENGTH_SHORT).show();
                    if (!allUserList.contains(uid)) {
                        allUserList.add(uid);
                        String userUpdate = new Gson().toJson(allUserList);
                        mFirebaseRefAllUser.setValue(userUpdate);
                        Toast.makeText(getBaseContext(), "User Added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void dologout() {
        mRef.unauth();
        finish();
        Toast.makeText(getBaseContext(),"LogOut",Toast.LENGTH_SHORT).show();
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager() ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}