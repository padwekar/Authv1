package com.example.savi.auth.modules.dashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.IntentConstant;
import com.example.savi.auth.modules.alluser.fragment.AllUserFragment;
import com.example.savi.auth.modules.friends.fragment.FriendContainerFragment;
import com.example.savi.auth.modules.message.fragment.MessageFragment;
import com.example.savi.auth.modules.profile.fragment.ProfileFragment;
import com.example.savi.auth.service.InstanceIdService;
import com.example.savi.auth.service.NotificationHandlerService;
import com.example.savi.auth.utils.AuthPreferences;
import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView textViewLogOut ;

    private Firebase mRef, mFirebaseRefAllUser;
    private List<String> allUserList = new ArrayList<>();;
    private String uid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(getBaseContext());
        setContentView(R.layout.activity_dashboard);

        mRef = new Firebase("https://todocloudsavi.firebaseio.com/user");
        uid = getIntent().getStringExtra("uid");

        startService(new Intent(this, NotificationHandlerService.class));
        startService(new Intent(this, InstanceIdService.class));

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
     //   Log.d("refreshTokenLogin",refreshToken);

        RadioGroup radioGroupProfileOptions = (RadioGroup)findViewById(R.id.radiogroup_dashboard);
        radioGroupProfileOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radiobutton_alluser : setFragment(AllUserFragment.newInstance()); break;
                    case R.id.radiobutton_friends : setFragment(FriendContainerFragment.newInstance()); break;
                    case R.id.radiobutton_msg :     setFragment(MessageFragment.newInstance()); break;
                    case R.id.radiobutton_profile : setFragment(ProfileFragment.newInstance()); break;
                    case R.id.radiobutton_logout :  dologout(); break;
                }
            }
        });

        //FireBase All user link

        String email = mRef.getAuth().getProviderData().get("email").toString() ;
        setTitle(email.substring(0,email.indexOf('@')));
        onNewIntent(getIntent());
    //    getAllUser();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra(IntentConstant.INTENT_KEY_FRIEND_REQUEST,false)){
            setFragment(FriendContainerFragment.newInstance());
        }
    }

    private void dologout() {
        AuthPreferences.getInstance().setLoginStatus(false);
        mRef.unauth();
        finish();
        Toast.makeText(getBaseContext(),"LogOut",Toast.LENGTH_SHORT).show();
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager() ;
        fragmentManager.popBackStackImmediate();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
    }


}