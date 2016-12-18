package com.example.savi.auth.modules.dashboard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.savi.auth.R;
import com.example.savi.auth.modules.account.activity.LoginActivity;
import com.example.savi.auth.modules.account.fragment.LoginFragment;
import com.example.savi.auth.modules.dashboard.adapter.DrawerAdapter;
import com.example.savi.auth.modules.profile.fragment.ProfileFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;


public class DrawerFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    public static DrawerFragment newInstance() {
        return new DrawerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer,container,false);
        mAuth = FirebaseAuth.getInstance();
        RecyclerView recyclerViewOptions = (RecyclerView)view.findViewById(R.id.recycler_view_drawer_options);
        DrawerAdapter drawerAdapter = new DrawerAdapter(getContext());
        recyclerViewOptions.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewOptions.setAdapter(drawerAdapter);
        drawerAdapter.setOnOptionSelectedListener(new DrawerAdapter.OnOptionSelectedListener() {
            @Override
            public void onOptionSelected(int position) {
                if(position==3){
                    signOut();
                }if(position==0){
                    setFragment(ProfileFragment.newInstance(),R.id.layout_middle_container);
                }
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity()/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return view;
    }

    public void setFragment(Fragment fragment ,int resId) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager() ;
        //fragmentManager.popBackStackImmediate();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(resId,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}
