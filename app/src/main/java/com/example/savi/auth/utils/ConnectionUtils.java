package com.example.savi.auth.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectionUtils {

    static public int CONNECT_TYPE_3G = 0;
    static public int CONNECT_TYPE_WIFI = 1;

    /**
     * Check network connection is available or not
     * @return boolean value
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
