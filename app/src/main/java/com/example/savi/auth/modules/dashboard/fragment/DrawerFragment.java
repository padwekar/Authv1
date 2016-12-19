package com.example.savi.auth.modules.dashboard.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
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
import android.widget.ImageView;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseFragment;
import com.example.savi.auth.modules.account.activity.LoginActivity;
import com.example.savi.auth.modules.account.fragment.LoginFragment;
import com.example.savi.auth.modules.dashboard.adapter.DrawerAdapter;
import com.example.savi.auth.modules.profile.fragment.ProfileFragment;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


public class DrawerFragment extends BaseFragment implements GoogleApiClient.OnConnectionFailedListener{

    public static DrawerFragment newInstance() {
        return new DrawerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer,container,false);
        mAuth = FirebaseAuth.getInstance();

        User user = AuthPreferences.getInstance().getUser();
        TypedArray typedArray = getContext().getResources().obtainTypedArray(R.array.avatars);

        ImageView imageViewProfilePic = (ImageView)view.findViewById(R.id.imageview_profilepic);
        if(user.getProfileDownloadUri()==null){
            Picasso.with(getContext()).load(typedArray.getResourceId(user.getPicPosition(),0)).into(imageViewProfilePic);
        }else {
            Picasso.with(getContext()).load(user.getProfileDownloadUri()).fit().into(imageViewProfilePic);
        }

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


}
