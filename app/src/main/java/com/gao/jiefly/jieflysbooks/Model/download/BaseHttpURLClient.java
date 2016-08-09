package com.gao.jiefly.jieflysbooks.Model.download;

import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jiefly on 2016/6/28.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BaseHttpURLClient implements HttpURLClient {
    URL mURL;
    HttpURLConnection mHttpURLConnection;
    BufferedReader mBufferedReader;
    StringBuilder sb = new StringBuilder();

    @Override
    public String getWebResourse(final URL url) {
        if (mURL == null)
            mURL = url;
        try {
            if (mHttpURLConnection == null)
                mHttpURLConnection = (HttpURLConnection) mURL.openConnection();
            String lines;
            mBufferedReader = new BufferedReader(new InputStreamReader(mHttpURLConnection.getInputStream(), "gbk"));
            if (sb.length() > 0)
                sb.delete(0, sb.length());
            while ((lines = mBufferedReader.readLine()) != null)
                sb.append(lines);
        } catch (EOFException eof) {
            try {
                Thread.sleep(10000);
                getWebResourse(url);
                Log.e("BaseHttpURLClient",eof.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            if (mHttpURLConnection != null && mHttpURLConnection.)
                mHttpURLConnection.disconnect();
        }*/

        return sb.toString();
    }

    @Override
    public void getWebResource(String url, OnDataStateListener onDataStateListener) {

    }

    @Override
    public String getWebResourse(String url) {
        return null;
    }
}
