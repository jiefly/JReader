package com.gao.jiefly.jieflysbooks.View;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.loader.ChapterLoader;
import com.gao.jiefly.jieflysbooks.R;
import com.gao.jiefly.jieflysbooks.Utils.ApplicationLoader;
import com.gao.jiefly.jieflysbooks.Utils.Utils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by jiefly on 2016/8/9.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class SettingActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.id_scan_tool_bar_title_tv)
    TextView mIdScanToolBarTitleTv;
    @InjectView(R.id.id_scan_tool_bar)
    Toolbar mIdScanToolBar;
    @InjectView(R.id.textView)
    TextView mTextView;
    @InjectView(R.id.id_fragment_setting_manage_book_btn)
    Button mIdFragmentSettingManageBookBtn;
    @InjectView(R.id.id_fragment_setting_clear_cache_btn)
    Button mIdFragmentSettingClearCacheBtn;
    @InjectView(R.id.id_fragment_setting_feed_back_btn)
    Button mIdFragmentSettingFeedBackBtn;
    @InjectView(R.id.id_fragment_setting_about_btn)
    Button mIdFragmentSettingAboutBtn;
    @InjectView(R.id.id_fragment_setting_rg_one)
    RadioGroup mIdFragmentSettingRgOne;
    @InjectView(R.id.id_fragment_setting_rg_two)
    RadioGroup mIdFragmentSettingRgTwo;
    @InjectView(R.id.id_fragment_setting_update_frequence_btn)
    Button mIdFragmentSettingUpdateFrequenceBtn;
    @InjectView(R.id.id_fragment_rb_1)
    RadioButton mIdFragmentRb1;
    @InjectView(R.id.id_fragment_rb_2)
    RadioButton mIdFragmentRb2;
    @InjectView(R.id.id_fragment_rb_3)
    RadioButton mIdFragmentRb3;
    @InjectView(R.id.id_fragment_rb_4)
    RadioButton mIdFragmentRb4;
    @InjectView(R.id.id_fragment_rb_5)
    RadioButton mIdFragmentRb5;
    @InjectView(R.id.id_fragment_rb_6)
    RadioButton mIdFragmentRb6;
    @InjectView(R.id.id_fragment_setting_frequence_ll)
    LinearLayout mIdFragmentSettingFrequenceLl;
    private int currentChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_setting1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        //设置沉浸的颜色
        tintManager.setStatusBarTintResource(R.color.theme_green);
        ButterKnife.inject(this);
        StatusBarCompat.compat(this);
        setSupportActionBar(mIdScanToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mIdScanToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        currentChecked = ApplicationLoader.getIntValue(ApplicationLoader.UPDATE_FREQUENCE);
        switch (currentChecked) {
            case 1:
                mIdFragmentSettingRgOne.check(R.id.id_fragment_rb_1);
                break;
            case 2:
                mIdFragmentSettingRgOne.check(R.id.id_fragment_rb_2);
                break;
            case 3:
                mIdFragmentSettingRgOne.check(R.id.id_fragment_rb_3);
                break;
            case 4:
                mIdFragmentSettingRgTwo.check(R.id.id_fragment_rb_4);
                break;
            case 5:
                mIdFragmentSettingRgTwo.check(R.id.id_fragment_rb_5);
                break;
            case 6:
                mIdFragmentSettingRgTwo.check(R.id.id_fragment_rb_6);
                break;
        }
        mIdFragmentSettingRgOne.setOnCheckedChangeListener(this);
        mIdFragmentSettingRgTwo.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        ApplicationLoader.save(ApplicationLoader.UPDATE_FREQUENCE, currentChecked);
        if (currentChecked == 1)
            ApplicationLoader.save(ApplicationLoader.IS_NEED_UPDATE_BG, false);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationLoader.save(ApplicationLoader.UPDATE_FREQUENCE, currentChecked);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @OnClick({R.id.id_fragment_setting_manage_book_btn, R.id.id_fragment_setting_update_frequence_btn, R.id.id_fragment_setting_clear_cache_btn, R.id.id_fragment_setting_feed_back_btn, R.id.id_fragment_setting_about_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_fragment_setting_manage_book_btn:
                Intent addLocalBookIntent = new Intent(SettingActivity.this, ScanTxtView.class);
                addLocalBookIntent.putExtra("type", ScanTxtView.TYPE_MANAGER_BOOK);
                startActivityForResult(addLocalBookIntent, 1);
                break;
            case R.id.id_fragment_setting_clear_cache_btn:
                long size = ChapterLoader.build(getApplicationContext()).getDiskCacheSize();
                new AlertDialog.Builder(this)
                        .setTitle("确定清除所有阅读缓存")
                        .setMessage("共:" + Utils.formatFileSize(size) + "\n删除后您的本地图书请重新添加")
                        .setPositiveButton("取消", null)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ChapterLoader.build(getApplicationContext()).clearAllCache();
//                                删除本地书籍
                                /*BookLoader bookLoader = BookLoader.build(getApplicationContext());
                                List<Book> books = bookLoader.getLocalBooks();
                                for (Book b:books) {
                                    bookLoader.removeBook(b);
                                }*/
                            }
                        })
                        .show();
                break;
            case R.id.id_fragment_setting_feed_back_btn:
                new AlertDialog.Builder(this)
                        .setMessage("有任何意见或者建议请联系作者，jiefly1993@gmail.com。")
                        .setNegativeButton("确定", null)
                        .show();
                break;
            case R.id.id_fragment_setting_about_btn:
                new AlertDialog.Builder(this)
                        .setMessage("本app网络小说仅提供转码服务\nps:本app仅用于学习交流。")
                        .setNegativeButton("确定", null).show();
                break;
            case R.id.id_fragment_setting_update_frequence_btn:
                mIdFragmentSettingFrequenceLl.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.id_fragment_rb_1:
                if (currentChecked > 3)
                    mIdFragmentSettingRgTwo.clearCheck();
                currentChecked = 1;
                break;
            case R.id.id_fragment_rb_2:
                if (currentChecked > 3)
                    mIdFragmentSettingRgTwo.clearCheck();
                currentChecked = 2;
                break;
            case R.id.id_fragment_rb_3:
                if (currentChecked > 3)
                    mIdFragmentSettingRgTwo.clearCheck();
                currentChecked = 3;
                break;
            case R.id.id_fragment_rb_4:
                if (currentChecked < 4)
                    mIdFragmentSettingRgOne.clearCheck();
                currentChecked = 4;
                break;
            case R.id.id_fragment_rb_5:
                if (currentChecked < 4)
                    mIdFragmentSettingRgOne.clearCheck();
                currentChecked = 5;
                break;
            case R.id.id_fragment_rb_6:
                if (currentChecked < 4)
                    mIdFragmentSettingRgOne.clearCheck();
                currentChecked = 6;
                break;
        }
    }

}
