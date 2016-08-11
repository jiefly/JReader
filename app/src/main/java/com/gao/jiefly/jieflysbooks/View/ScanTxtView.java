package com.gao.jiefly.jieflysbooks.View;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jiefly on 2016/8/9.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class ScanTxtView extends AppCompatActivity {

    @InjectView(R.id.id_sacn_txt_btn)
    Button mIdSacnTxtBtn;
    @InjectView(R.id.id_scan_tool_bar)
    Toolbar mIdScanToolBar;
    @InjectView(R.id.id_scan_dir_path_tv)
    TextView mIdScanDirPathTv;
    @InjectView(R.id.id_scan_tool_bar_scan_tv)
    TextView mIdScanToolBarScanTv;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private DirectoryFragment mDirectoryFragment;
    private FragmentScan mFragmentScan;

    public static final int REQUEST_CODE = 0x1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_txt_view);
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
        mIdScanToolBarScanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdScanToolBarScanTv.getText().toString().contains("重新")) {
                    mFragmentScan.startScan(Environment.getExternalStorageDirectory(), ".txt");
                } else {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, mFragmentScan, mFragmentScan.toString());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mFragmentScan = new FragmentScan();
        mDirectoryFragment = new DirectoryFragment();
        mDirectoryFragment.setDelegate(new DirectoryFragment.DocumentSelectActivityDelegate() {

            @Override
            public void startDocumentSelectActivity() {

            }

            @Override
            public void didSelectFiles(DirectoryFragment activity,
                                       ArrayList<File> files) {
                mDirectoryFragment.showSureAddBookBox(files);
            }

            @Override
            public void updateToolBarName(String name) {
                mIdScanDirPathTv.setText(name);
            }
        });
        fragmentTransaction.add(R.id.fragment_container, mDirectoryFragment, "" + mDirectoryFragment.toString());
        fragmentTransaction.commit();
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         MenuInflater menuInflater = getMenuInflater();
         menuInflater.inflate(R.menu.activity_scan_txt, menu);
         return true;
     }*/
    public void hideScanTv(boolean isHide) {
        Observable.just(isHide)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        mIdScanToolBarScanTv.setText(!aBoolean ? "扫描" : "重新扫描");
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDirectoryFragment.isVisible())
//            到最上层的文件夹时才退出当前activity
                return mDirectoryFragment.onBackPressed_() && super.onKeyDown(keyCode, event);
            else if (mFragmentScan.isVisible())
                return mFragmentScan.onBackPressed_() && super.onKeyDown(keyCode, event);
        }
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

    public void chooseFilesComplete(String[] filesPath){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("chooseBooks",filesPath);
        setResult(REQUEST_CODE, resultIntent);
        finish();
    }
    @OnClick(R.id.id_sacn_txt_btn)
    public void onClick() {
        final long time = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                walkdir(new File("/storage/emulated/0/91PandaReader/download"), ".txt");
                Log.e("scantxt", "扫描时间：" + (System.currentTimeMillis() - time));
            }
        }).start();
    }

    public void showText(String value) {
        Observable.just(value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mIdScanDirPathTv.setText(s);
                    }
                });
    }
    /*public void startScan(){
        final long time = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                new File("/storage/emulated/0/91PandaReader/download")
                walkdir(Environment.getExternalStorageDirectory(), ".txt");
                Log.e("scantxt", "扫描时间：" + (System.currentTimeMillis() - time));
                mFragmentScan.scanCompleted();
            }
        }).start();
    }
    public List<File> walkdir(File dir, String pattern) {
        String pdfPattern = pattern;
        List<File> result = new ArrayList<>();
        File[] listFile = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkdir(listFile[i], pattern);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
//                        只截取中文开头的文件
                        if (isChinese(listFile[i].getName().charAt(0))) {
                            //Do what ever u want
                            Log.e("scanTxt", "path:" + listFile[i].getAbsolutePath() + "name:" + listFile[i].getName());
                            result.add(listFile[i]);
                            mFragmentScan.addItem(listFile[i]);
                            String value = "扫描结果 " + mFragmentScan.mFiles.size() + " 本";

                        }
                    }
                }
            }
        }

        return result;
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }*/

}
