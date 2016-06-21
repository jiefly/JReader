package com.gao.jiefly.jieflysbooks.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class DataModelImpl implements DataModel {
    StringBuilder sb = new StringBuilder();
    onDataStateListener mOnDataStateListener = null;

    public DataModelImpl(onDataStateListener onDataStateListener) {
        mOnDataStateListener = onDataStateListener;
    }

    @Override
    public void getDate(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sb.length() > 0)
                        sb.delete(0, sb.length());
                    URL url = new URL("http://www.biquge.la/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
                    String lines;
                    while ((lines = bufferedReader.readLine()) != null)
                        sb.append(lines);
                        mOnDataStateListener.onSuccess(sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
