package com.example.savi.auth.modules.dashboard.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseAuthFragment;
import com.example.savi.auth.modules.dashboard.adapter.DrawerAdapter;
import com.example.savi.auth.modules.profile.ProfileActivity;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


public class DrawerFragmentAuth extends BaseAuthFragment implements GoogleApiClient.OnConnectionFailedListener{

    public static DrawerFragmentAuth newInstance() {
        return new DrawerFragmentAuth();
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

        TextView textViewDisplayName = (TextView)view.findViewById(R.id.textview_display_name);
        textViewDisplayName.setText(user.getDisplayName());

        TextView textViewUserName = (TextView)view.findViewById(R.id.textview_username);
        textViewUserName.setText("@"+user.getUserName());


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
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                }
            }
        });

        return view;
    }


 /*   */


}
