package com.example.savi.auth.modules.account.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.modules.dashboard.activity.HomeActivity;
import com.example.savi.auth.utils.AuthPreferences;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class LoginActivity extends AppCompatActivity {
    Firebase mRef ;
    EditText mEditTextEmail , mEditTextPassWord ;
    String uid = "";
    ProgressBar mProgressBar ;
    TextView mTextViewForgotPassword ;
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
        mTextViewForgotPassword= (TextView)findViewById(R.id.textview_forgot_password);
        mTextViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Reset Password !");
                builder.setMessage("Enter Your Email");
                final EditText input = new EditText(LoginActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                builder.setIcon(R.drawable.ic_checkbox_marked_circle_outline_black_18dp);
                builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String email = input.getText().toString();
                        if(!email.equals("")){
                            mRef.resetPassword(email, new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getBaseContext(),"Reset email has been sent to your email",Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    Toast.makeText(getBaseContext(),firebaseError.toString(),Toast.LENGTH_SHORT).show();
                                }
                            });
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
        });
        Button buttonSignUp = (Button)findViewById(R.id.button_signup);
        Button buttonLogIn = (Button)findViewById(R.id.button_login);
        AuthData authData = mRef.getAuth();
        if(authData!=null){
            uid = authData.getUid() ;
            startHomeActivity(uid);
        }

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
                        startHomeActivity(uid);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(getBaseContext(), firebaseError.toString(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
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

    private void startHomeActivity(String uid) {
        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
        AuthPreferences.getInstance().setLoginStatus(true);
        AuthPreferences.getInstance().setUserUid(uid);
        intent.putExtra("uid",uid);
        startActivity(intent);
    }


}
