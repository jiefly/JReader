package com.gao.jiefly.jieflysbooks.Model.download;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;

import java.net.URL;

/**
 * Created by jiefly on 2016/7/8.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class VolleyClient implements HttpURLClient {

    public static VolleyClient instance = null;
    RequestQueue mRequestQueue = null;
    StringRequest mStringRequest;

    public VolleyClient(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static VolleyClient build(Context context) {
        if (instance == null) {
            synchronized (VolleyClient.class) {
                if (instance == null) {
                    instance = new VolleyClient(context);
                }
            }
        }
        return instance;
    }

    @Override
    public String getWebResourse(URL url) {
        return new BaseHttpURLClient().getWebResourse(url);
    }

    @Override
    public void getWebResource(String url, final OnDataStateListener onDataStateListener) {
        mStringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onDataStateListener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onDataStateListener.onFailed(error);
            }
        });
        mRequestQueue.add(mStringRequest);
    }

}
