package com.gao.jiefly.jieflysbooks.Model.downloader;

import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;

import java.net.URL;

/**
 * Created by jiefly on 2016/6/28.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface HttpURLClient {
    String getWebResourse(URL url);
    void getWebResource(String url, OnDataStateListener onDataStateListener);
    void getWebResource(String url, OnDataStateListener onDataStateListener,String chatset);
    String getWebResourse(String url);
    String getWebResourse(String url,String chatset);
}
