package com.gao.jiefly.jieflysbooks.View;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.R;
import com.gao.jiefly.jieflysbooks.Utils.LocalBookSegmentation;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jiefly on 2016/8/9.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class ScanTxtView extends AppCompatActivity {

    @InjectView(R.id.id_scan_tool_bar)
    Toolbar mIdScanToolBar;
    @InjectView(R.id.id_scan_dir_path_tv)
    TextView mIdScanDirPathTv;
    @InjectView(R.id.id_scan_tool_bar_scan_tv)
    TextView mIdScanToolBarScanTv;
    @InjectView(R.id.id_scan_tool_bar_title_tv)
    TextView mIdScanToolBarTitleTv;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private DirectoryFragment mDirectoryFragment;
    private FragmentScan mFragmentScan;
    private FragmentManagerBooks mFragmentManagerBooks;
    private ProgressDialog progressDialog;
    private List<String> chooseFilesPath = new ArrayList<>();
    private boolean isCancle = false;
    public static final int REQUEST_CODE = 0x1001;
    public static final int TYPE_SCAN = 0x0010;
    public static final int TYPE_MANAGER_BOOK = 0x0011;
    private static final int TYPE_UNKNOW = 0x1111;
    private int type;

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
        type = getIntent().getIntExtra("type", TYPE_UNKNOW);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (type) {
            case TYPE_MANAGER_BOOK:
                someThingDoInManager();
                mIdScanDirPathTv.setVisibility(View.GONE);
                break;
            case TYPE_SCAN:
                someThingDoInScan();
                break;
            case TYPE_UNKNOW:
                break;
        }

        mIdScanToolBarScanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                扫描
                if (mIdScanToolBarScanTv.getText().toString().equals("重新扫描")) {
                    mFragmentScan.startScan(Environment.getExternalStorageDirectory(), ".txt");
//                    管理书籍
                } else if (mIdScanToolBarScanTv.getText().toString().equals("全选")) {
                    mIdScanToolBarScanTv.setText("取消全选");
                    mFragmentManagerBooks.toogleChooseAll();
                } else if (mIdScanToolBarScanTv.getText().toString().equals("取消全选")) {
                    mIdScanToolBarScanTv.setText("全选");
                    mFragmentManagerBooks.toogleChooseAll();
                } else {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, mFragmentScan, mFragmentScan.toString());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });


    }

    private void someThingDoInManager() {
        mIdScanToolBarTitleTv.setText("管理书籍");
        mIdScanToolBarScanTv.setText("全选");
        mFragmentManagerBooks = new FragmentManagerBooks();
        fragmentTransaction.add(R.id.fragment_container, mFragmentManagerBooks, mFragmentManagerBooks.toString());
        fragmentTransaction.commit();
    }

    private void someThingDoInScan() {
        initProgressDialog();
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

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("添加书籍中...请稍后");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isCancle = true;
                progressDialog.dismiss();
            }
        });
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "后台运行", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

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
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (type) {
                case TYPE_MANAGER_BOOK:
                    break;
                case TYPE_SCAN:
                    if (mDirectoryFragment.isVisible())
//            到最上层的文件夹时才退出当前activity
                        return mDirectoryFragment.onBackPressed_() && super.onKeyDown(keyCode, event);
                    else if (mFragmentScan.isVisible())
                        return mFragmentScan.onBackPressed_() && super.onKeyDown(keyCode, event);
                    break;
                case TYPE_UNKNOW:
                    break;
            }
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

    private void setProgressDialogMessage(String message) {
        Observable.just(message)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        progressDialog.setMessage(s);
                    }
                });
    }

    private void setProgressDialogStatue(boolean isShow) {
        Observable.just(isShow)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean && !progressDialog.isShowing()) {
                            progressDialog.show();
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                finish();
                            }
                        }
                    }

                });
    }

    public void chooseFilesComplete(String[] filesPath) {
        if (filesPath == null) {
            finish();
            return;
        }
        Collections.addAll(chooseFilesPath, filesPath);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("chooseBooks", filesPath);
        setResult(REQUEST_CODE, resultIntent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setProgressDialogStatue(true);
                for (String path : chooseFilesPath) {
                    if (isCancle)
                        break;
                    File file = new File(path);
                    setProgressDialogMessage("添加" + file.getName() + "中");
                    Log.e("scanTxtView", "转换小说中" + file.getName());
                    LocalBookSegmentation.build().LocalBook2CachedBook(file);
                    Log.e("scanTxtView", "转换小说完成" + file.getName());
                }
                if (!isCancle)
                    finish();
            }
        }).start();
    }

/*    @OnClick(R.id.id_sacn_txt_btn)
    public void onClick() {
        final long time = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                walkdir(new File("/storage/emulated/0/91PandaReader/download"), ".txt");
                Log.e("scantxt", "扫描时间：" + (System.currentTimeMillis() - time));
            }
        }).start();
    }*/

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
}
