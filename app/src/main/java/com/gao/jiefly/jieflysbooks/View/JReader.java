package com.gao.jiefly.jieflysbooks.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.gao.jiefly.jieflysbooks.Animation.DepthPageTransformer;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnItemClickListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnMoveNextChapterListener;
import com.gao.jiefly.jieflysbooks.Present.PresentReader;
import com.gao.jiefly.jieflysbooks.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class JReader extends AppCompatActivity {

    @InjectView(R.id.id_jie_reader_content_vp)
    CustomViewPager mIdJieReaderContentVp;
    @InjectView(R.id.id_include_context_btn)
    ImageButton mIdIncludeContextBtn;
    @InjectView(R.id.id_include_night_btn)
    CheckBox mIdIncludeNightBtn;
    @InjectView(R.id.id_include_mode_tv)
    TextView mIdIncludeModeTv;
    @InjectView(R.id.id_include_setting_btn)
    ImageButton mIdIncludeSettingBtn;
    @InjectView(R.id.id_reader_bottom_bar)
    LinearLayout mIdReaderBottomBar;
    @InjectView(R.id.id_reader_left_menu_book_name)
    TextView mIdReaderLeftMenuBookName;
    @InjectView(R.id.id_reader_left_menu_bottom_btn)
    ImageButton mIdReaderLeftMenuBottomBtn;
    @InjectView(R.id.id_jie_reader_left_menu_rv)
    RecyclerView mIdJieReaderLeftMenuRv;
    @InjectView(R.id.id_reader_left_menu_cache_ll)
    LinearLayout mIdReaderLeftMenuCacheLl;
    @InjectView(R.id.id_reader_left_menu_progress_bar)
    NumberProgressBar mIdReaderLeftMenuProgressBar;
    @InjectView(R.id.id_reader_left_menu_info_cached)
    TextView mIdReaderLeftMenuInfoCached;
    @InjectView(R.id.id_jie_reader_drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.id_tool_bar)
    Toolbar mIdJreaderToolBar;

    private static final String TAG = "JReader";
    private List<FragmentReader> mFragmentReaders = new ArrayList<>();
    //    从main中选择的要阅读的书
    private Book mBook;
    //    书的章节title列表
    private List<String> titleList;
    //    书的章节url列表
    private List<String> urlList;
    //    当前所所读的章节索引
    private int chapterIndex;
    //    开始阅读时间
    private long startTime;
    //    屏幕宽度
    private int mScreenWidth;
    //    屏幕高度
    private int mScreenHeight;

    private int textColor;

    LinearLayoutManager manager;

    private ProgressDialog progressDialog;
    private PresentReader mPresentReader;
    //    bottom的显示与消失动画
    Animation animationShow;
    Animation animationDismiss;

    //    翻页动画效果
    private DepthPageTransformer mViewPageTransformer;
    //    设置页面的pop
    private PopupWindow setPopupWindow;
    private SeekBar lightSeekBar;
    private CheckBox followSystemCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jjjreader);
        ButterKnife.inject(this);
//        设置沉浸式状态栏
        StatusBarCompat.compat(this);
        initToolBar();
        initProgressDialog();
        initData();
        initViewPager();
        initRecycleView();
        initLeftMenuContent();
        initAnimation();
        initpop();
    }

    public void vpToNextPage() {
        Logger.i("toNextPage");
        Observable.just(getCurrentFragmentIndex() + 1)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        if (integer <= mFragmentReaders.size() - 1)
                            return true;
                        showSnackbar("已经是最后一章了");
                        return false;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                mIdJieReaderContentVp.setCurrentItem(getCurrentFragmentIndex() + 1, true);
            }
        });

    }

    public int getCurrentFragmentIndex() {
        return mIdJieReaderContentVp.getCurrentItem();
    }

    public void scrollDownToNextPage(OnMoveNextChapterListener listener) {
        mFragmentReaders.get(getCurrentFragmentIndex()).scrollDownToNextPage(listener);
    }

    private void initpop() {
        View popupWindowView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.set_reader_config_popup, null, false);
        setPopupWindow = new PopupWindow(popupWindowView
                , WindowManager.LayoutParams.MATCH_PARENT
                , WindowManager.LayoutParams.WRAP_CONTENT);
        setPopupWindow.setFocusable(true);
        setPopupWindow.setBackgroundDrawable(new BitmapDrawable());


        //                    设置字体大小
//                    加大字体
        (popupWindowView.findViewById(R.id.id_add_text_size_ibtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresentReader.saveReaderTextSize(mPresentReader.getReaderTextSize() + 1);
                for (FragmentReader reader : mFragmentReaders)
                    reader.setTextSize(mPresentReader.getReaderTextSize());
            }
        });
//                    减小字体
        (popupWindowView.findViewById(R.id.id_reduce_text_size_ibtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresentReader.saveReaderTextSize(mPresentReader.getReaderTextSize() - 1);

                for (FragmentReader reader : mFragmentReaders)
                    reader.setTextSize(mPresentReader.getReaderTextSize());
            }
        });

        //                    设置亮度
        if (lightSeekBar == null)
            lightSeekBar = (SeekBar) popupWindowView.findViewById(R.id.id_set_popup_sb);
        //        设置进度条为当前亮度
        lightSeekBar.setProgress((Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                255)));
        lightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (followSystemCheckBox.isChecked())
                    followSystemCheckBox.setChecked(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                setScreenBrightness(progress);
                Log.e("seekBar", progress + "");
            }
        });

//                    降低亮度
        popupWindowView.findViewById(R.id.id_set_popup_lightdown_ibtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followSystemCheckBox.isChecked())
                    followSystemCheckBox.setChecked(false);
                lightSeekBar.setProgress(lightSeekBar.getProgress() - 5);
                setScreenBrightness(lightSeekBar.getProgress());
            }
        });
//                    增加亮度
        popupWindowView.findViewById(R.id.id_set_popup_lightup_ibtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followSystemCheckBox.isChecked())
                    followSystemCheckBox.setChecked(false);
                lightSeekBar.setProgress(lightSeekBar.getProgress() + 5);
                setScreenBrightness(lightSeekBar.getProgress());
            }
        });
        if (followSystemCheckBox == null) {
            followSystemCheckBox = (CheckBox) popupWindowView.findViewById(R.id.id_set_popup_light_follow_sys_cb);
        }
        //设置跟随系统亮度
        followSystemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("cb", isChecked + "");
                if (isChecked) {
                    try {
                        int systemLightLevel = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                        lightSeekBar.setProgress(systemLightLevel);
                        setScreenBrightness(systemLightLevel);
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
//                    设置背景
        ((RadioGroup) popupWindowView.findViewById(R.id.id_set_popup_radio_group))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int viewPagerBackgroundId = 0;
                        int textColorId = 0;
                        switch (checkedId) {
                            case R.id.id_set_reader_config_rbtn_01:
                                viewPagerBackgroundId = R.drawable.read_default_background;
                                textColorId = R.color.colorDefaultBackgroundText;
                                break;
                            case R.id.id_set_reader_config_rbtn_02:
                                viewPagerBackgroundId = R.color.colorNovelReadBackgroundBlue;
                                textColorId = R.color.colorNovelReadBackgroundBlueText;
                                break;
                            case R.id.id_set_reader_config_rbtn_03:
                                viewPagerBackgroundId = R.color.colorNovelReadBackgroundgray;
                                textColorId = R.color.colorNovelReadBackgroundgrayText;
                                break;
                            case R.id.id_set_reader_config_rbtn_04:
                                viewPagerBackgroundId = R.color.colorNovelReadBackgroundGraygreen;
                                textColorId = R.color.colorNovelReadBackgroundGraygreenText;
                                break;
                            case R.id.id_set_reader_config_rbtn_05:
                                viewPagerBackgroundId = R.color.colorNovelReadBackgroundgreen1;
                                textColorId = R.color.colorNovelReadBackgroundgreen1Text;
                                break;
                        }
                        if (viewPagerBackgroundId != 0) {
                            setTextAndBackgroundColor(viewPagerBackgroundId, textColorId);
                        }
                    }
                });
    }

    public void setTextAndBackgroundColor(int viewPagerBackgroundId, int textColorId) {
        mPresentReader.saveTextAndBackgroundColor(textColorId, viewPagerBackgroundId);
        mIdJieReaderContentVp
                .setBackgroundResource(viewPagerBackgroundId);
        setTextColor(getResources().getColor(textColorId));
    }

    //    设置text大小
    private void setTextColor(int color) {
        for (FragmentReader fragmentReader : mFragmentReaders)
            fragmentReader.setTextColor(color);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("加载中请稍后");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void initToolBar() {
        mIdJreaderToolBar.setTitle("");
        setSupportActionBar(mIdJreaderToolBar);
        if (getSupportActionBar() != null) {
//            设置返回小箭头
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setShowHideAnimationEnabled(true);
        }
        mIdJreaderToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void initData() {
        getScreenSize();
        Bundle bundle = this.getIntent().getBundleExtra("bookbundle");
        mBook = (Book) bundle.getSerializable("book");
        mPresentReader = new PresentReader(mBook, this);
//        检查传过来的book的合法性
        if (mPresentReader.checkBook(mBook)) {
            titleList = mBook.getChapterList().getChapterTitleList();
            urlList = mBook.getChapterList().getChapterUrlList();
            chapterIndex = mBook.getReadChapterIndex();
        } else {
            Log.e(TAG, "book in JReader is error");
        }
        textColor = mPresentReader.getTextColor();
    }

    private void initViewPager() {
//        新建一本书章节的数大小数量的fragment
        int chapterNum = urlList.size();

        for (int i = 0; i < titleList.size(); i++) {
            Chapter chapter = new Chapter(urlList.get(i));
            chapter.setTitle(titleList.get(i));
            FragmentReader fragmentReader = new JReaderFragment(chapter, mPresentReader, textColor, 100.0f * (i + 1) / chapterNum);
            mFragmentReaders.add(fragmentReader);
        }
        FragmentPagerAdapter pagerAdapter = new JReaderFragmentPagerAdapter(getSupportFragmentManager());
        mIdJieReaderContentVp.setAdapter(pagerAdapter);
        mIdJieReaderContentVp.setOffscreenPageLimit(3);
        mIdJieReaderContentVp.setPageTransformer(true,new DepthPageTransformer());
        if (mPresentReader.getBackgroundColor() != 0)
            mIdJieReaderContentVp.setBackgroundResource(mPresentReader.getBackgroundColor());

        mIdJieReaderContentVp.setCurrentItem(chapterIndex);
    }

    private void initRecycleView() {

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        if (mIdJieReaderLeftMenuRv != null) {
            mIdJieReaderLeftMenuRv.setLayoutManager(manager);
            mIdJieReaderLeftMenuRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mIdJieReaderLeftMenuRv.setHasFixedSize(true);
            CustomRecycleAdapter recycleAdapter = new CustomRecycleAdapter();
            recycleAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mPresentReader.chooseItem(position);
                }

                @Override
                public void onItemLongClick(int position) {

                }
            });
            mIdJieReaderLeftMenuRv.setAdapter(recycleAdapter);
        }
    }

    private void initLeftMenuContent() {
//        设置书名
        if (mBook.getBookName() != null) {
            String title = "《" + mBook.getBookName() + "》";
            mIdReaderLeftMenuBookName.setText(title);
        }
//        设置底部显示文字
        if (mBook.isCached()) {
            //        判断是否需要显示缓存所有章节
            if (mIdReaderLeftMenuCacheLl != null)
                mIdReaderLeftMenuCacheLl.setVisibility(View.GONE);
            if (mIdReaderLeftMenuInfoCached != null)
                mIdReaderLeftMenuInfoCached.setVisibility(View.VISIBLE);
        } else {
            //        判断是否需要显示缓存所有章节
            if (mIdReaderLeftMenuCacheLl != null)
                mIdReaderLeftMenuCacheLl.setVisibility(View.VISIBLE);
            if (mIdReaderLeftMenuInfoCached != null)
                mIdReaderLeftMenuInfoCached.setVisibility(View.GONE);
        }
    }

    private void initAnimation() {
        animationShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_bar_show);
        animationDismiss = AnimationUtils.loadAnimation(this, R.anim.bottom_bar_dismiss);
        animationShow.setDuration(500);
        animationDismiss.setDuration(500);
    }

    //    切换页面
    public void setCurrentFragment(int position) {
        Observable.just(position).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mIdJieReaderContentVp.setCurrentItem(integer);
                    }
                });

    }

    //    切换全屏模式
    public boolean toogleScreenState() {
        final boolean flag = mIdReaderBottomBar.isShown();
        if (flag) {
            mIdReaderBottomBar.startAnimation(animationDismiss);
        } else {
            mIdReaderBottomBar.setTranslationY(0);
            mIdReaderBottomBar.startAnimation(animationShow);
        }

        Observable.timer(600, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mIdReaderBottomBar.setVisibility(flag ? View.INVISIBLE : View.VISIBLE);
                        if (flag) {
                            mIdReaderBottomBar.setTranslationY(300);
                        }
                    }
                });
        if (flag) {
            setFullScreen();
        } else {
            cancelFullScreen();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        // 音量减小
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mPresentReader.scrollDownToNextPage();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getScreenSize() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }

    //    显示侧边栏
    public void showLeftMenu() {
        if (mDrawerLayout != null)
            mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void dismissLeftMenu() {
        if (mDrawerLayout != null && mDrawerLayout.isShown())
            mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    //    设置全屏阅读模式
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
    }

    private void cancelFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null)
            getSupportActionBar().show();
    }

    public void backToMain() {
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                如果手指点击屏幕中心区域，则表示用户想要唤醒底部bar
                if (ev.getX() < mScreenWidth * 3 / 5
                        && ev.getX() > mScreenWidth * 2 / 5
                        && ev.getY() > mScreenHeight * 2 / 5
                        && ev.getY() < mScreenHeight * 3 / 5) {
//                    当侧边栏打开的时候不对点击事件进行拦截
                    if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
                        return super.dispatchTouchEvent(ev);
                    return toogleScreenState();
                } else if (ev.getX() > mScreenWidth * 4 / 5 && ev.getY() > mScreenHeight * 4 / 5) {
                    if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT) && !mIdReaderBottomBar.isShown()) {
                        mPresentReader.scrollDownToNextPage();
                        return false;
                    }

                }

        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        更新阅读时间
        mPresentReader.updateReadTime(startTime);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        更新阅读进度
        mBook.setHasUpdate(false);
        chapterIndex = mIdJieReaderContentVp.getCurrentItem();
        mPresentReader.updateBookReadChapterIndex(mBook, chapterIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    /**********************************************************************************************/
    public void showToast(final String message) {
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showSnackbar(String value) {
        Observable.just(value)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Snackbar.make(mIdReaderBottomBar, s, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                });
    }

    public void showProgressDialog() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });

    }

    public void showProgressDialog(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message != null && !message.equals(""))
                    progressDialog.setMessage(message);
                showProgressDialog();
            }
        });
    }

    public void dismissProgressDialog() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    @OnClick({R.id.id_include_night_btn, R.id.id_reader_left_menu_cache_ll, R.id.id_include_context_btn, R.id.id_include_setting_btn, R.id.id_reader_left_menu_bottom_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_include_night_btn:
                if (mIdIncludeNightBtn.isChecked()) {
                    mIdIncludeModeTv.setText("日间");
                    setTextAndBackgroundColor(R.color.colorNovelReadBackgroundgray, R.color.colorNovelReadBackgroundgrayText);
                } else {
                    mIdIncludeModeTv.setText("夜间");
                    setTextAndBackgroundColor(R.drawable.read_default_background, R.color.colorDefaultBackgroundText);
                }
                break;
            case R.id.id_include_setting_btn:
                if (setPopupWindow != null && JReader.this.findViewById(android.R.id.content) != null) {
                    toogleScreenState();
                    setPopupWindow.showAtLocation((
                            (ViewGroup) JReader.this.findViewById(android.R.id.content))
                            .getChildAt(0), Gravity.BOTTOM, 0, 0);
                }
                break;
            case R.id.id_include_context_btn:
                mPresentReader.showLeftMenu();
                break;
            case R.id.id_reader_left_menu_bottom_btn:
                recycleViewGoDestination();
                break;
            case R.id.id_reader_left_menu_cache_ll:
                mIdReaderLeftMenuCacheLl.setVisibility(View.GONE);
                mIdReaderLeftMenuProgressBar.setVisibility(View.VISIBLE);
                mPresentReader.downloadAllChapters(mBook);
                break;
        }
    }

    //设置屏幕亮度的函数
    private void setScreenBrightness(float tmpInt) {
        if (tmpInt < 80) {
            tmpInt = 80;
        }

        // 根据当前进度改变亮度
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, (int) tmpInt);
        tmpInt = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, -1);
        WindowManager.LayoutParams wl = getWindow().getAttributes();

        float tmpFloat = tmpInt / 255;
        if (tmpFloat > 0 && tmpFloat <= 1) {
            wl.screenBrightness = tmpFloat;
        }
        getWindow().setAttributes(wl);
    }

    public void setProgressMaxValue(int max) {
        if (mIdReaderLeftMenuProgressBar != null)
            mIdReaderLeftMenuProgressBar.setMax(max);
    }

    //    更新侧边栏下载进度
    public void updateProgressBar(int progress) {

        Observable.just(progress).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (mIdReaderLeftMenuProgressBar != null)
                    mIdReaderLeftMenuProgressBar.setProgress(integer);
            }
        });
    }

    //   侧边栏直达底部或顶部
    protected void recycleViewGoDestination() {
        Log.e("bottomBtn", "clicked:" + (manager).findFirstVisibleItemPosition());
//              直接用scrollToPositon不能够滑动到底部，可以通过滑动到接近底部的时候调用smoothScrollToPosition来滑动剩余部分
//                如果直接用smoothScrollToPosition，由于数据量过大，滑动的时间非常非常非常长。。。

//                如果当前最后一个可见item大于一半的数据量，则向上滑动到底，否则滑动到底部
        if ((manager).findLastVisibleItemPosition() > titleList.size() / 2) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_up_anim);
            mIdReaderLeftMenuBottomBtn.startAnimation(animation);
            mIdJieReaderLeftMenuRv.scrollToPosition(20);
            mIdJieReaderLeftMenuRv.smoothScrollToPosition(0);
        } else {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_down_anim);
            mIdReaderLeftMenuBottomBtn.startAnimation(animation);
            mIdJieReaderLeftMenuRv.scrollToPosition(titleList.size() - 20);
            mIdJieReaderLeftMenuRv.smoothScrollToPosition(titleList.size() + 1);
        }
    }

    /**********************************************************************************************/


    class JReaderFragmentPagerAdapter extends FragmentPagerAdapter {
        public JReaderFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return (Fragment) mFragmentReaders.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentReaders.size();
        }
    }

    /**********************************************************************************************/

    class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.ViewHolder> {
        OnItemClickListener mListener = null;

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(JReader.this).inflate(R.layout.reader_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position);
                }
            });
            holder.mTextView.setText(titleList.get(position));
        }

        @Override
        public int getItemCount() {
            return titleList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.reader_item_tv);
            }
        }
    }
    /**********************************************************************************************/


}
