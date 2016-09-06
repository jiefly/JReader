package com.gao.jiefly.jieflysbooks;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.downloader.VolleyClient;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        VolleyClient.build(getContext()).getWebResource("www.baidu.com", new OnDataStateListener() {
            @Override
            public void onSuccess(String result) {
                Log.e("onsuccess",result);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}