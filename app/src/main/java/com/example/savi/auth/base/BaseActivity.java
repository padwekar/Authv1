package com.example.savi.auth.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.savi.auth.constant.URLConstants;
import com.firebase.client.Firebase;

public abstract class BaseActivity extends AppCompatActivity {

    protected Firebase fireBaseRef;

    public interface AuthOptionDialog{
        void onOptionSelected(int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        fireBaseRef = new Firebase(URLConstants.TODOCLOUD_FIREBASE_ROOT_URL);
    }

    public void setFragment(Fragment fragment, int resId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.popBackStackImmediate();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void showSingleChoiceAlertDialog(String title , String[] options , String positiveButton , String negativeButton, final AuthOptionDialog listener ){
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(title);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onOptionSelected(i);
            }
        });
        builder.show();
    }
}
