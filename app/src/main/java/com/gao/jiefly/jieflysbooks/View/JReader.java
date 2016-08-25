package com.gao.jiefly.jieflysbooks.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnItemClickListener;
import com.gao.jiefly.jieflysbooks.Present.PresentReader;
import com.gao.jiefly.jieflysbooks.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    @InjectView(R.id.id_jreader_tool_bar)
    Toolbar mIdJreaderToolBar;

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
    private ProgressDialog progressDialog;
    private PresentReader mPresentReader;
    private FragmentPagerAdapter mPagerAdapter;
    //    bottom的显示与消失动画
    Animation animationShow;
    Animation animationDismiss;
    private LinearLayoutManager manager;
    private CustomRecycleAdapter mRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_jreader);
        ButterKnife.inject(this);
//        设置沉浸式状态栏
        StatusBarCompat.compat(this);
        initToolBar();
        initProgressDialog();
        initData();
        initViewPager();
        initRecycleView();
        initAnimation();
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
        mIdJreaderToolBar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
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
        mPresentReader.checkBook();
        titleList = mBook.getChapterList().getChapterTitleList();
        urlList = mBook.getChapterList().getChapterUrlList();
        chapterIndex = mBook.getReadChapterIndex();
    }

    private void getScreenSize() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }


    private void initViewPager() {
//        新建一本书章节的数的三分之一数量的fragment
        for (int i = 0; i < titleList.size(); i++) {
            Chapter chapter = new Chapter(urlList.get(i));
            chapter.setTitle(titleList.get(i));
            FragmentReader fragmentReader = new JReaderFragment(chapter, mPresentReader);
            mFragmentReaders.add(fragmentReader);
        }

        mPagerAdapter = new JReaderFragmentPagerAdapter(getSupportFragmentManager());
        mIdJieReaderContentVp.setAdapter(mPagerAdapter);
        mIdJieReaderContentVp.addOnPageChangeListener(mPresentReader);
        mIdJieReaderContentVp.setOffscreenPageLimit(3);
        mIdJieReaderContentVp.setCurrentItem(chapterIndex);
    }

    private void initRecycleView() {
        mIdReaderLeftMenuBookName.setText("《" + mBook.getBookName() + "》");
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        if (mIdJieReaderLeftMenuRv != null) {
            mIdJieReaderLeftMenuRv.setLayoutManager(manager);
            mIdJieReaderLeftMenuRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mIdJieReaderLeftMenuRv.setHasFixedSize(true);
            mRecycleAdapter = new CustomRecycleAdapter();
            mRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mIdJieReaderContentVp.setCurrentItem(position);
                }

                @Override
                public void onItemLongClick(int position) {

                }
            });
            mIdJieReaderLeftMenuRv.setAdapter(mRecycleAdapter);
        }
    }

    private void initAnimation() {
        animationShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_bar_show);
        animationDismiss = AnimationUtils.loadAnimation(this, R.anim.bottom_bar_dismiss);
        animationShow.setDuration(500);
        animationDismiss.setDuration(500);
    }

//    切换全屏模式
    private boolean toogleScreenState() {
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
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void backToMain() {
        finish();
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
                        Snackbar.make(mIdJieReaderLeftMenuRv, s, Snackbar.LENGTH_SHORT)
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

            holder.mTextView.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
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

}
