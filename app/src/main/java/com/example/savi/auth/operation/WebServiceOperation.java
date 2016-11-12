package com.example.savi.auth.operation;

import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.example.savi.auth.pojo.WebServiceRequest;
import com.example.savi.auth.utils.AuthApplication;
import com.example.savi.auth.utils.AuthException;
import com.example.savi.auth.utils.AuthUtils;
import com.example.savi.auth.utils.ConnectionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Map;


public abstract class WebServiceOperation implements Response.Listener, Response.ErrorListener {

    public static final String TAG = WebServiceOperation.class.getSimpleName();

    protected final int TIME_OUT_CONNECTION = 10000;//

    protected String mTag;
    protected Class mClazz;
    protected Type mClazzType;

    protected WebServiceRequest mWebServiceRequest;

    protected WebServiceOperation(String uri, int method, Map<String, String> header, Class clazz, String tag) {
        this(uri, method, header, null, clazz, null, tag);
    }

    protected WebServiceOperation(String uri, int method, Map<String, String> header, Type type, String tag) {
        this(uri, method, header, null, null, type, tag);
    }

    protected WebServiceOperation(String uri, int method, Map<String, String> header, String body, Class clazz, String tag) {
        this(uri, method, header, body, clazz, null, tag);
    }
    
    
    /*protected WebServiceOperation(String uri,  int method, Map<String, String> header, String body, Type type, String tag) {
        this(uri,  method, header, body, null, type, tag);
    }

    protected WebServiceOperation(String uri, int method, Map<String, String> header, Class clazz, String tag) {
        this(uri, URLConstant.CONQUEST_BASE_URL_INDEX, method, header, null, clazz, null, tag);
    }

    protected WebServiceOperation(String uri, int method, Map<String, String> header, Type type, String tag) {
        this(uri, URLConstant.CONQUEST_BASE_URL_INDEX, method, header, null, null, type, tag);
    }

    protected WebServiceOperation(String uri, int method, Map<String, String> header, String body, Class clazz, String tag) {
        this(uri, URLConstant.CONQUEST_BASE_URL_INDEX, method, header, body, clazz, null, tag);
    }

    protected WebServiceOperation(String uri, int method, Map<String, String> header, String body, Type type, String tag) {
        this(uri, URLConstant.CONQUEST_BASE_URL_INDEX, method, header, body, null, type, tag);
    }*/

    protected WebServiceOperation(String uri, int method, Map<String, String> header, String body, Class clazz, Type type, String tag) {
        mTag = tag;
        mClazz = clazz;
        mClazzType = type;
        setRequest(uri, method, header, body);
    }

/*    protected void setRequest(String uri, int method, Map<String, String> header, String body) {
        setRequest(uri, URLConstant.CONQUEST_BASE_URL_INDEX, method, header, body);
    }*/

    protected void setRequest(String uri, int method, Map<String, String> header, String body) {
        mWebServiceRequest = new WebServiceRequest(uri, method, header, body, this, this);
        RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_CONNECTION, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        mWebServiceRequest.setRetryPolicy(policy);
    }

    protected void addToRequestQueue() {
        if (ConnectionUtils.isOnline(AuthApplication.getInstance())) {
            AuthUtils.getInstance().debug(this, "Network Online");
            mWebServiceRequest.setTag(TextUtils.isEmpty(mTag) ? WebServiceOperation.TAG : mTag);
            AuthApplication.getInstance().getRequestQueue().add(mWebServiceRequest);
        } else {
            onError(new AuthException("Network is not available"));
            AuthUtils.getInstance().debug(this, "Network offline");
        }
    }



    /**
     * This method will parse the json data based on class
     */
    @Override
    public void onResponse(Object response) {
        Object object = null;
        try {
            AuthUtils.getInstance().debug(mTag, "Response:= " + ((String) response));
            if (mClazzType != null) {
                object = new Gson().fromJson(((String) response), mClazzType);
            } else {
                object = new Gson().fromJson((String) response, mClazz);
            }
            if (object != null) {
                onSuccess(object);
            } else {
                onError(new AuthException(401));
            }
        } catch (JsonSyntaxException e) {
            AuthUtils.getInstance().debug(this, "Network error:=" + e.getMessage());
            onError(new AuthException(401));
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AuthUtils.getInstance().debug(this, "Network error:=" + error != null ? "error" : error.getMessage());
        onError(new AuthException(error.networkResponse != null ? error.networkResponse.statusCode : 401));
    }
    /**
     * Abstract method for handling success response
     */
    public abstract void onSuccess(Object response);
    public abstract void onError(AuthException exception);
}

