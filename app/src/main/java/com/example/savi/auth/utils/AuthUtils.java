package com.example.savi.auth.utils;

import android.util.Log;

/**
 * Created by Savi on 23-10-2016.
 */

public class AuthUtils {

    private static AuthUtils instance;
    private boolean mLogEnabled = true;//Config.DEBUG;

    /**
     * Constructor is  private, so no body can create a object of this class outside of this class.
     */
    private AuthUtils() {}

    public static AuthUtils getInstance()
    {
        if (instance == null)
        {
            synchronized (AuthUtils.class)
            {
                if (instance == null)
                {
                    instance = new AuthUtils();
                }
            }
        }
        return instance;
    }

    public void debug(Object tag, Object log)
    {
        if (mLogEnabled)
            Log.d(tag.getClass().getName(), log.toString());
    }

    public void debugLong(Object tag, Object log)
    {
        if (mLogEnabled) {
            String message = log.toString();
            int maxLogSize = 2000;
            for (int i = 0; i <= message.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > message.length() ? message.length() : end;
                Log.d(tag.getClass().getName(), message.substring(start, end));
            }
        }
    }

    public void error(Object tag, Object log)
    {
        if (mLogEnabled)
            Log.e(tag.getClass().getName(), log.toString());
    }

    public void warning(Object tag, Object log)
    {
        if (mLogEnabled)
            Log.w(tag.getClass().getName(), log.toString());
    }
}
