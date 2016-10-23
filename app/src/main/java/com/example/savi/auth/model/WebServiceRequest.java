package com.example.savi.auth.model;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.savi.auth.operation.WebServiceOperation;
import com.example.savi.auth.utils.AuthUtils;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Savi on 23-10-2016.
 */

public class WebServiceRequest extends Request {

    private final Gson gson = new Gson();
    private Map<String, String> mHeaders;
    private String mBody;
    private Response.Listener mListener;
    private Class mClazz;


    /**
     * Make a GET, POST, PUT request
     *
     * @param method           Type of request GET/POST/PUT
     * @param url              URL of the request to make
     * @param headers          headers adviseInfo for request headers
     * @param body             body of the request if method is POST/PUT
     * @param responseListener
     * @param errorListener
     */
    public WebServiceRequest(String url, int method, Map<String, String> headers,
                             String body, Response.Listener responseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        AuthUtils.getInstance().debug(WebServiceOperation.class.getSimpleName(),"url:=" + url);
        this.mHeaders = headers;
        this.mBody = body;
        this.mListener = responseListener;

    }

    /**
     * Return headers adviseInfo for request headers
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        mHeaders = mHeaders != null ? mHeaders : super.getHeaders();
        //mHeaders.put("Accept", "application/json");
        mHeaders.put("Content-Type", "application/json");
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }


    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() throws AuthFailureError {
        AuthUtils.getInstance().debug(this, "body:=" + mBody);

        if (!TextUtils.isEmpty(mBody)) {
            return mBody.getBytes();
        }
        return null;

    }

    @Override
    protected void deliverResponse(Object response) {
        if (mListener != null)
            mListener.onResponse(response);
    }

    /**
     * Return the response data as json string on main thread, if response code is ok i.e. 200/201.
     *
     * @param response
     * @return
     */
    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        AuthUtils.getInstance().debug(this, "Network Response:=" + response.data.length);
        try {
            String json = new String(
                    response.data,"UTF-8");
            AuthUtils.getInstance().debug(this, "Network Response:=" + json);
            return Response.success(json,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
