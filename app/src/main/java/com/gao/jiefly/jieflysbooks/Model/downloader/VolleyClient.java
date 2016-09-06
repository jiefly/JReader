package com.gao.jiefly.jieflysbooks.Model.downloader;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;

import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by jiefly on 2016/7/8.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class VolleyClient implements HttpURLClient {

    private static VolleyClient instance = null;
    RequestQueue mRequestQueue = null;
    StringRequest mStringRequest;

    private VolleyClient(Context context) {
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
//        Log.e("VolleyClient", url);
        mStringRequest = new StringRequestForGBK(url, new Response.Listener<String>() {
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

    @Override
    public void getWebResource(String url, final OnDataStateListener onDataStateListener, String chatset) {
        if (chatset.equals("UTF-8")) {
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
        } else if (chatset.equals("gbk"))
            getWebResource(url, onDataStateListener);
    }

    @Override
    public String getWebResourse(String url) {
        RequestFuture future = RequestFuture.newFuture();
        StringRequestForGBK requestForGBK = new StringRequestForGBK(url, future, future);
        mRequestQueue.add(requestForGBK);
        String result = null;
        try {
            result = (String) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getWebResourse(String url, String chatset) {
        String result = null;
        if (chatset.equals("UTF-8")) {
            RequestFuture future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(url,future,future);
            mRequestQueue.add(request);
            try {
                result = (String) future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return result;
        } else if (chatset.equals("gbk")) {
            result = getWebResourse(url);
        }
        return result;
    }

}
