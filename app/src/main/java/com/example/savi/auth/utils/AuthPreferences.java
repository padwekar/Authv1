package com.example.savi.auth.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Savi on 23-10-2016.
 */

public class AuthPreferences  {

    //MAIN SHARED PREF
    public static String AUTH_SHARED_PREF = "AUTH_SHARED_PREF" ;

    //ELEMENTS
    public static String USER_UID = "USER_UID";
    public static String USER_TOKEN ="USER_TOKEN";
    public static String USER_NAME = "USER_NAME";
    public static String IS_LOGGED_IN = "IS_LOGGED_IN";



    public static Object object = new Object();
    public static SharedPreferences sharedPreferences ;
    public static AuthPreferences mInstance ;

    public static AuthPreferences getInstance() {
        synchronized (object) {
            if (mInstance == null) {
                mInstance = new AuthPreferences();
                sharedPreferences = AuthApplication.getInstance().getSharedPreferences(AUTH_SHARED_PREF, Context.MODE_PRIVATE);
            }
        }
    return mInstance;
    }

    public String getUserUid(){
        return sharedPreferences.getString(USER_UID,"");
    }

    public void setUserUid(String uid){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_UID,uid);
        editor.apply();
    }

    public String getUserToken(){
        return sharedPreferences.getString(USER_TOKEN,"");
    }

    public void setUserToken(String token){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_TOKEN,token);
        editor.apply();
    }

    public String getUserName(){
        return  sharedPreferences.getString(USER_NAME,"");
    }

    public void setUserName(String token){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME,token);
        editor.apply();
    }

    public void setLoginStatus(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putBoolean(IS_LOGGED_IN,status);
        editor.apply();
    }

    public boolean getLoginStatus(){
        return sharedPreferences.getBoolean(IS_LOGGED_IN,false);
    }

}

