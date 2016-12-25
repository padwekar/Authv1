package com.example.savi.auth.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.example.savi.auth.constant.URLConstants;
import com.firebase.client.Firebase;


public class BaseFragment extends DialogFragment {

    private ProgressDialog mProgressDialog ;
    protected Firebase fireBaseRef;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(getContext());
        fireBaseRef = new Firebase(URLConstants.TODOCLOUD_FIREBASE_ROOT_URL);
        mProgressDialog = new ProgressDialog(getContext());
    }

    public void setFragment(Fragment fragment, int resId) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //fragmentManager.popBackStackImmediate();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    protected void showProgressDialog(String s) {
        mProgressDialog.setMessage(s);
        mProgressDialog.show();
    }

    protected void showProgressDialog() {
        showProgressDialog("Loading");
    }

    protected void dismissProgressDialog() {
        mProgressDialog.dismiss();
    }
}
