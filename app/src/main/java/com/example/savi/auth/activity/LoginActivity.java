package com.example.savi.auth.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class LoginActivity extends AppCompatActivity {
    Firebase mRef ;
    EditText mEditTextEmail , mEditTextPassWord ;
    String uid = "";
    ProgressBar mProgressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);
        mRef= new Firebase("https://todocloudsavi.firebaseio.com/");
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mEditTextEmail = (EditText)findViewById(R.id.edittext_username);
        mEditTextPassWord = (EditText)findViewById(R.id.edittext_password);
        Button buttonSignUp = (Button)findViewById(R.id.button_signup);
        Button buttonLogIn = (Button)findViewById(R.id.button_login);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mRef.authWithPassword(mEditTextEmail.getText().toString(), mEditTextPassWord.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        mProgressBar.setVisibility(View.GONE);
                        uid = authData.getUid() ;
                        Toast.makeText(getBaseContext(), authData.toString(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(getBaseContext(), firebaseError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }


}
