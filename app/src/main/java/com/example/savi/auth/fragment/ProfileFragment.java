package com.example.savi.auth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.model.User;
import com.firebase.client.Firebase;
import com.google.gson.Gson;

public class ProfileFragment extends Fragment {
    EditText mEdittextDisplayName ;
    EditText mEdittextStatus ;
    Button mButtonSubmit ;
    String uid ;
    User user ;
    Firebase mFireBaseUserRef ;
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        mFireBaseUserRef = new Firebase("https://todocloudsavi.firebaseio.com/user");
        Toast.makeText(getContext(),"In ProfileFragment",Toast.LENGTH_SHORT).show();
        mEdittextDisplayName = (EditText)view.findViewById(R.id.edittext_displayname);
        mEdittextStatus = (EditText)view.findViewById(R.id.edittext_status);
        mButtonSubmit = (Button)view.findViewById(R.id.button_submit);
        uid = getActivity().getIntent().getStringExtra("uid");
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String displayName =  mEdittextDisplayName.getText().toString();
              String status = mEdittextStatus.getText().toString() ;
              user  = new User();
              user.setDisplayName(displayName);
              user.setStatus(status);
              user.setVisible(false);
              String data = new Gson().toJson(user);
              mFireBaseUserRef.child(uid).setValue(data);
            }
        });
        return view;
    }
}
