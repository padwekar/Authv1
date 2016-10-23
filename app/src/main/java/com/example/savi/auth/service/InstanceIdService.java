package com.example.savi.auth.service;

import android.util.Log;
import android.widget.Toast;

import com.example.savi.auth.utils.AuthApplication;
import com.example.savi.auth.utils.AuthPreferences;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;


public class InstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("refreshToken",refreshToken);

        Firebase mFireBaseUserRef = new Firebase("https://todocloudsavi.firebaseio.com/detaileduser_v1");
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("token",refreshToken);

        Firebase mRef= new Firebase("https://todocloudsavi.firebaseio.com/");
        if(mRef.getAuth()!=null){
            String uid = AuthPreferences.getInstance().getUserUid();
            mFireBaseUserRef.child(uid).updateChildren(hashMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    Toast.makeText(AuthApplication.getInstance(),"token Updated",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
