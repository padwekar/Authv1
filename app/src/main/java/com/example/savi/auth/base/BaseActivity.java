package com.example.savi.auth.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public abstract class BaseActivity extends AppCompatActivity {

    public interface AuthOptionDialog{
        void onOptionSelected(int position);
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
