package com.example.savi.auth.modules.account.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.constant.URLConstants;
import com.example.savi.auth.modules.account.activity.LoginActivity;
import com.example.savi.auth.modules.account.activity.SignupActivity;
import com.example.savi.auth.modules.dashboard.activity.DashboardActivity;
import com.example.savi.auth.modules.profile.fragment.ProfileFragment;
import com.example.savi.auth.operation.manager.SocialManager;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {

    private static final int RC_SIGN_IN = 9090;
    private static final String TAG = "INSIDE";


    private EditText mEditTextEmail, mEditTextPassWord;
    private ProgressBar mProgressBar;

    private Firebase firebaseRef;
    public  FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public GoogleApiClient mGoogleApiClient;
    private String uid = "";

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Firebase.setAndroidContext(getContext());

        mAuth = FirebaseAuth.getInstance();

        firebaseRef = new Firebase(URLConstants.TODOCLOUD_FIREBASE_ROOT_URL);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);

        mEditTextEmail = (EditText) view.findViewById(R.id.edittext_username);
        mEditTextPassWord = (EditText) view.findViewById(R.id.edittext_password);

        TextView mTextViewForgotPassword = (TextView) view.findViewById(R.id.textview_forgot_password);
        mTextViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Reset Password !");
                builder.setMessage("Enter Your Email");
                final EditText input = new EditText(getContext());
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
                        if (!email.equals("")) {
                            firebaseRef.resetPassword(email, new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getContext(), "Reset email has been sent to your email", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    Toast.makeText(getContext(), firebaseError.toString(), Toast.LENGTH_SHORT).show();
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

        mAuth = FirebaseAuth.getInstance();

        Button buttonSignUp = (Button) view.findViewById(R.id.button_signup);

       AuthData authData = firebaseRef.getAuth();
        if(authData!=null){
            uid = authData.getUid() ;
            Toast.makeText(getContext(), "-"+authData.getAuth().get("email"), Toast.LENGTH_SHORT).show();
        //    directUser(uid);
        }

        Button buttonGoogleSignIn = (Button) view.findViewById(R.id.button_google_sign_in);
        buttonGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                        .enableAutoManage(getActivity()/* FragmentActivity */, LoginFragment.this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mAuthListener = this;

        Button buttonLogIn = (Button) view.findViewById(R.id.button_login);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                String username = mEditTextEmail.getText().toString();
                final String password = mEditTextPassWord.getText().toString();
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    //  email or not using proper regular expression
                    performLogin(username, password); // perform sign in with email & password or ask user to enter the userName
                } else {
                    //get the emailId stored against username

                    firebaseRef.child("user_ids").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                String userId = dataSnapshot.getValue(String.class);
                                performLogin(userId, password);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SignupActivity.class));
            }
        });

        return view;
    }

    private void performLogin(String emailId, String password) {
        firebaseRef.authWithPassword(emailId, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                mProgressBar.setVisibility(View.GONE);
                directUser(uid);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(getContext(), firebaseError.toString(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void directUser(final String uid) {
        SocialManager manager = new SocialManager();
        manager.getUserDetails(uid, Constants.SINGLE_VALUE_EVENT_LISTENER, new SocialManager.OnGetUserDetail() {
            @Override
            public void onGetUserDetailSuccess(User user) {
                AuthPreferences.getInstance().setUser(user);

                if (user.getProfileStatus() == User.NEW)
                    setFragment(ProfileFragment.newInstance());
                else
                    startHomeActivity(user.getUid());
            }

            @Override
            public void onGetUserDetailFailure(FirebaseError e) {
                User user = new User();
                user.setUid(uid);
                user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                user.setProfileStatus(User.NEW);
                firebaseRef.child(URLConstants.USER_DETAIL).child(user.getUid()).setValue(user);
                setFragment(ProfileFragment.newInstance());
            }
        });
    }

    private void startHomeActivity(String uid) {
        Intent intent = new Intent(getContext(), DashboardActivity.class);
        AuthPreferences.getInstance().setLoginStatus(true);
        AuthPreferences.getInstance().setUserUid(uid);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }


                        directUser(task.getResult().getUser().getUid());

                        /*User user = new User();
                        user.setUid(task.getResult().getUser().getUid()) ;
                        user.setDisplayName(task.getResult().getUser().getDisplayName()) ;
                      //  user.setProfileDownloadUri((String) task.getResult().getUser().getPhotoUrl());
                        user.placesAdded = 0 ;

                        mTextViewUserName.setText(task.getResult().getUser().getDisplayName() + "" + task.getResult().getUser().getEmail());
*/

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Toast.makeText(getContext(), "PASS GOOGLE SIGN IN", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account);

            } else {
                Toast.makeText(getContext(), "FAIL GOOGLE SIGN IN", Toast.LENGTH_SHORT).show();
                //  updateUI(false);

                // Google Sign In failed, update UI appropriately
            }
        }
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            //   Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            Toast.makeText(getContext(), "onAuthStateChanged:signed_in:", Toast.LENGTH_SHORT).show();

        } else {
            // User is signed out
            //  Log.d(TAG, "onAuthStateChanged:signed_out");
            Toast.makeText(getContext(), "onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();

        }
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        // fragmentManager.popBackStackImmediate();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void signOut() {
        // Firebase sign out
        firebaseRef.unauth();
        // mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });

new Gson().toJson("");
     }
}
