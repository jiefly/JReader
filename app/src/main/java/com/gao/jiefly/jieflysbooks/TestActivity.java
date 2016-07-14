package com.gao.jiefly.jieflysbooks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.gao.jiefly.jieflysbooks.Model.download.VolleyClient;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test);

    }
}
