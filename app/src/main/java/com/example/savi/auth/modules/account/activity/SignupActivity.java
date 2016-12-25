package com.example.savi.auth.modules.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.URLConstants;
import com.example.savi.auth.pojo.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private Firebase firebaseRef;
    private EditText mEditTextEmail , mEditTextPassword ,mEditTextConfirmPassword ;

    private TextInputLayout mTextInputLayoutEmail,mTextInputLayoutPassword ,mTextInputLayoutConfirmPassword ;

    private Button mButtonSignUp ;
    private ProgressBar mProgressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_sign_up);
        firebaseRef = new Firebase("https://todocloudsavi.firebaseio.com/");

        mEditTextPassword = (EditText)findViewById(R.id.edittext_password);
        mEditTextConfirmPassword = (EditText)findViewById(R.id.edittext_confirm_password);
        mEditTextEmail = (EditText)findViewById(R.id.edittext_email);

        mTextInputLayoutEmail = (TextInputLayout)findViewById(R.id.textinputlayout_email);
        mTextInputLayoutPassword = (TextInputLayout)findViewById(R.id.textinputlayout_password);
        mTextInputLayoutConfirmPassword = (TextInputLayout)findViewById(R.id.textinputlayout_confirm_password);

        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);

        mButtonSignUp = (Button)findViewById(R.id.button_signup_main);

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    mProgressBar.setVisibility(View.VISIBLE);
                    final String emailId = mEditTextEmail.getText().toString() ;
                    String password = mEditTextPassword.getText().toString() ;
                    firebaseRef.createUser(emailId, password, new Firebase.ValueResultHandler<Map<String,Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> stringObjectMap) {

                            User user = new User();
                            user.setUid(stringObjectMap.get("uid").toString());
                            user.setEmail(emailId);
                            user.setProfileStatus(User.NEW);
                            firebaseRef.child(URLConstants.USER_DETAIL).child(user.getUid()).setValue(user);
                            mProgressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                            intent.putExtra("email",mEditTextEmail.getText().toString());
                            startActivity(intent);

                            Toast.makeText(getBaseContext(),"You are Successfully Registered in",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getBaseContext(),"Login to continue..",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(),firebaseError.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    boolean isValid(){
        boolean isValid = true ;
        if(mEditTextEmail.getText().toString().equals("")){
            isValid = false ;
            mTextInputLayoutEmail.setErrorEnabled(true);
            mTextInputLayoutEmail.setError(getResources().getString(R.string.error_msg_required));
        }else if(mEditTextPassword.getText().toString().equals("")){
            isValid = false ;
            mTextInputLayoutPassword.setErrorEnabled(true);
            mTextInputLayoutPassword.setError(getResources().getString(R.string.error_msg_required));
        }else if(mEditTextConfirmPassword.getText().toString().equals("")){
            isValid = false ;
            mTextInputLayoutConfirmPassword.setErrorEnabled(true);
            mTextInputLayoutConfirmPassword.setError(getResources().getString(R.string.error_msg_required));
        }else if(!mEditTextConfirmPassword.getText().toString().equals(mEditTextPassword.getText().toString())){
            isValid = false ;
            mTextInputLayoutConfirmPassword.setErrorEnabled(true);
            mTextInputLayoutConfirmPassword.setError(getResources().getString(R.string.error_msg_password_mismatch));
            mTextInputLayoutPassword.setError(getResources().getString(R.string.error_msg_password_mismatch));
        }

        return isValid ;
    }


}
