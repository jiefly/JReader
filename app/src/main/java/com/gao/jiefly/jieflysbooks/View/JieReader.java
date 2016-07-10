package com.gao.jiefly.jieflysbooks.View;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.gao.jiefly.jieflysbooks.Animation.DepthPageTransformer;
import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnChapterCacheListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataModelListener;
import com.gao.jiefly.jieflysbooks.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiefly on 2016/7/4.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class JieReader extends AppCompatActivity implements OnDataModelListener {
    @InjectView(R.id.id_jie_reader_content_vp)
    CustomViewPager mIdJieReaderContentVp;
    @InjectView(R.id.id_jie_reader_left_menu_rv)
    RecyclerView mIdJieReaderLeftMenuRv;
    @InjectView(R.id.id_jie_reader_drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.id_reader_bottom_bar)
    LinearLayout mIdReaderBottomBar;
    @InjectView(R.id.id_include_context_btn)
    ImageButton mIdIncludeContextBtn;
    @InjectView(R.id.id_include_night_btn)
    CheckBox mIdIncludeNightBtn;
    @InjectView(R.id.id_include_setting_btn)
    ImageButton mIdIncludeSettingBtn;
    @InjectView(R.id.id_reader_left_menu_book_name)
    TextView mIdReaderLeftMenuBookName;
    @InjectView(R.id.id_reader_left_menu_bottom_btn)
    ImageButton mIdReaderLeftMenuBottomBtn;
    @InjectView(R.id.id_reader_left_menu_cache_ll)
    LinearLayout mIdReaderLeftMenuCacheLl;
    @InjectView(R.id.id_reader_left_menu_progress_bar)
    NumberProgressBar mIdReaderLeftMenuProgressBar;
    @InjectView(R.id.id_reader_left_menu_info_cached)
    TextView mIdReaderLeftMenuInfoCached;
    @InjectView(R.id.id_include_mode_tv)
    TextView mIdIncludeModeTv;
    @InjectView(R.id.id_tool_bar)
    Toolbar mIdToolBar;
    private int mScreenWidth;
    private int mScreenHeight;
    private List<String> mChapterList;
    private Book mBook;
    private AdvanceDataModel mAdvanceDataModel;
    private CustomRecycleAdapter mRecycleAdapter;
    private List<FragmentReader> mFragmentReaderList;
    private int chapterIndex;
    private List<String> urlList;
    Animation animationShow;
    Animation animationDismiss;
    private DepthPageTransformer mViewPageTransformer;
    private PopupWindow setPopupWindow;
    private SeekBar lightSeekBar;
    private CheckBox followSystemCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reader);
        StatusBarCompat.compat(this);
        ButterKnife.inject(this);
        mIdToolBar.setTitle("");
        setSupportActionBar(mIdToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        mIdToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBeforeClose();
                finish();
            }
        });
        mIdToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    Log.e("d","click");
                    finish();
                }
                return false;
            }
        });
        initData();
        initViewPager();
        initRecycleView();
        initAnimation();
        if (mBook.isCached()) {
            mIdReaderLeftMenuCacheLl.setVisibility(View.GONE);
            mIdReaderLeftMenuProgressBar.setProgress(View.GONE);
        } else {
            mIdReaderLeftMenuInfoCached.setVisibility(View.GONE);
        }
        Log.e("chapterListSize", mChapterList.size() + "" + "isCached" + mBook.isCached());
    }

    private void initAnimation() {
        animationShow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_bar_show);
        animationDismiss = AnimationUtils.loadAnimation(this, R.anim.bottom_bar_dismiss);
        animationShow.setDuration(500);
        animationDismiss.setDuration(500);
    }

    private void initViewPager() {
        mFragmentReaderList = new LinkedList<>();
        final FragmentReaderImpl currentFragment = new FragmentReaderImpl();
        final FragmentReaderImpl prevFragment = new FragmentReaderImpl();
        final FragmentReaderImpl nextFragment = new FragmentReaderImpl();
        currentFragment.setChapterSize(urlList.size());
        prevFragment.setChapterSize(urlList.size());
        nextFragment.setChapterSize(urlList.size());
        mFragmentReaderList.add(prevFragment);
        mFragmentReaderList.add(currentFragment);
        mFragmentReaderList.add(nextFragment);
        mIdJieReaderContentVp.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager()));
//        mViewPageTransformer = new DepthPageTransformer();
//        mIdJieReaderContentVp.setPageTransformer(false,mViewPageTransformer);
        mIdJieReaderContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("onPPageScrolled", "position:" + position + "positionOffset:" + positionOffset);

                /*
                * 当由第一页切换至第二页或则第零页时，在切换成功的即页面滑动至完整的第二页或则第零页时，将当前页面强制设置为第一页（并且更新第一页的数据），给用户一种可以无限翻页的效果
                * 当处于不同的chapterIndex时有不同的翻页策略
                * chapterIndex：
                *                1： 向前翻页时，不强制改变翻页之后的viewpager所处的item，即翻页后viewpager处于第三页，不能够再向后翻页
                *                other: 当前viewPager处于第二页，第一页加载前一章内容，第三页加载后一章内容
                *                chapterSize-2: 向后翻页时，不强制改变翻页之后的viewpager所处的item，即翻页后viewpager处于第一页，不能够再向前翻页
                * */
                if (positionOffset == 0) {
                    Observable.just(position)
                            .observeOn(Schedulers.io())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .map(new Func1<Integer, Chapter>() {
                                @Override
                                public Chapter call(final Integer integer) {
                                    /*
                                    * 当前为第二章，viewpager处于第二页，向前翻页之后提示处于本书的第一章，不能够向前翻页了
                                    * */
                                    if (chapterIndex == 1 && integer == 0) {
                                        showSnackbar("当前是第一章");
                                        chapterIndex--;
                                        return null;
                                    }
                                    /*
                                    * 当前为倒数第二章，viewpager处于第二页，向前翻页之后提示处于本书的最后一章，不能够向后翻页了
                                    * */
                                    if (chapterIndex == mChapterList.size() - 2 && integer == 2) {
                                        showSnackbar("当前是最后一章");
                                        chapterIndex++;
                                        return null;
                                    }
                                    /*
                                    * 处于第一章还向前翻页
                                    * */
                                    if (chapterIndex <= 0 && integer == 0) {
                                        showSnackbar("当前是第一章，请勿向前翻页");
                                        return null;
                                    }
                                    /*
                                    * 处于最后一章还向后翻页
                                    * */
                                    if (chapterIndex >= mChapterList.size() - 1 && integer == 2) {
                                        showSnackbar("当前是最后一章，请勿向后翻页");
                                        return null;
                                    }
                                    if (chapterIndex == mChapterList.size() - 1 && integer == 1) {
                                        Log.e("翻页", "最后一页向前翻页");
                                        chapterIndex--;
                                        return null;
                                    }
                                    if (chapterIndex == 0 && integer == 1) {
                                        Log.e("翻页", "第一页向后翻页");
                                        chapterIndex++;
                                        return null;
                                    }
                                    if (integer == 1)
                                        return null;
                                    chapterIndex = integer == 0 ? chapterIndex - 1 : chapterIndex + 1;
//                                    Chapter chapter = mFragmentReaderList.get(integer).getChapter();
                                    Chapter chapter = null;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            /*
                                            * 加载前后两页的数据
                                            * */
                                            mFragmentReaderList.get(0).showChapter(mAdvanceDataModel.getChapter(mBook.getBookName(), chapterIndex - 1, mChapterList.get(chapterIndex - 1)));
                                            mFragmentReaderList.get(2).showChapter(mAdvanceDataModel.getChapter(mBook.getBookName(), chapterIndex + 1, mChapterList.get(chapterIndex + 1)));
                                        }
                                    }).start();
//                                    if (!chapter.getTitle().equals(mChapterList.get(chapterIndex))) {
                                    try {
                                        chapter = mAdvanceDataModel.getChapter(new URL(urlList.get(chapterIndex)));
                                        chapter.setTitle(mChapterList.get(chapterIndex));
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    return chapter;
//                                    }
//                                    return null;
                                }
                            })
//                            .delay(5000, TimeUnit.MILLISECONDS)
                            .subscribe(new Subscriber<Chapter>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e("翻页", e.getMessage());
                                }

                                @Override
                                public void onNext(final Chapter chapter) {
                                    if (chapterIndex >= 1 && chapterIndex <= mChapterList.size() - 2) {
                                        mFragmentReaderList.get(1).showChapter(chapter);
                                        mIdJieReaderContentVp.setCurrentItem(1, false);
                                    }
                                }
                            });
                }
//                Log.back_btn_bg("onPageScrolled", "position:" + position + "positionOffset:" + positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
//                Log.back_btn_bg("onPageSelected", "position:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.back_btn_bg("onPageChanged", "state:" + state);
            }
        });
        if (chapterIndex == 0)
            mIdJieReaderContentVp.setCurrentItem(0);
        else if (chapterIndex == mChapterList.size() - 1)
            mIdJieReaderContentVp.setCurrentItem(2);
        else
            mIdJieReaderContentVp.setCurrentItem(1);
        //        进入activity后根据chapter索引分情况对viewpager进行初始化

        Observable.just(mBook)
                .map(new Func1<Book, Chapter>() {
                    @Override
                    public Chapter call(Book book) {
                        try {
//                            转换成当前index对应的Chapter
                            return mAdvanceDataModel.getChapter(
                                    new URL(book.getChapterList().getChapterUrlList().get(chapterIndex)));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Chapter>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onNext(Chapter chapter) {
                       /*
                       * chapterIndex：
                       *                0： 当前的viewPager处于第一页，后两页将加载后两章内容
                       *                other: 当前viewPager处于第二页，第一页加载前一章内容，第三页加载后一章内容
                       *                chapterSize-1: 当前viewPager处于第三页，前两页加载前两章内容
                       * */
                        chapter.setTitle(mChapterList.get(chapterIndex));
//                        setViewPagerConfigure(chapter);
                        switch ((int) ((float) chapterIndex / (float) (urlList.size() - 1))) {
                            case 0:
                                mFragmentReaderList.get(0).showChapter(chapter);
                                mIdJieReaderContentVp.setCurrentItem(0);
                                break;
                            case 1:
                                mFragmentReaderList.get(2).showChapter(chapter);
                                mIdJieReaderContentVp.setCurrentItem(2);
                                break;
                            default:
                                mFragmentReaderList.get(1).showChapter(chapter);
                                mIdJieReaderContentVp.setCurrentItem(1);
                                break;
                        }

                    }
                });
    }

    private void setViewPagerConfigure(final Chapter chapter) {
        chapter.setIndex(chapterIndex);
        if (chapterIndex == 0) {
            (mFragmentReaderList.get(0)).showChapter(chapter);
            mIdJieReaderContentVp.setCurrentItem(0, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Chapter tmp = mAdvanceDataModel.getChapter(new URL(urlList.get(chapterIndex + 1)));
                        tmp.setTitle(mChapterList.get(chapterIndex + 1));
                        tmp.setIndex(chapterIndex + 1);
                        mFragmentReaderList.get(1).showChapter(tmp);
                        tmp = mAdvanceDataModel.getChapter(new URL(urlList.get(chapterIndex + 2)));
                        tmp.setTitle(mChapterList.get(chapterIndex + 2));
                        tmp.setIndex(chapterIndex + 2);
                        mFragmentReaderList.get(2).showChapter(tmp);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (chapterIndex == mChapterList.size() - 1) {
            (mFragmentReaderList.get(2)).showChapter(chapter);
            mIdJieReaderContentVp.setCurrentItem(2, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Chapter tmp = mAdvanceDataModel.getChapter(new URL(urlList.get(chapterIndex - 2)));
                        tmp.setTitle(mChapterList.get(chapterIndex - 2));
                        tmp.setIndex(chapterIndex - 2);
                        mFragmentReaderList.get(0).showChapter(tmp);
                        tmp = mAdvanceDataModel.getChapter(new URL(urlList.get(chapterIndex - 1)));
                        tmp.setTitle(mChapterList.get(chapterIndex - 1));
                        tmp.setIndex(chapterIndex - 1);
                        mFragmentReaderList.get(1).showChapter(tmp);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            (mFragmentReaderList.get(1)).showChapter(chapter);
            mIdJieReaderContentVp.setCurrentItem(1, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Chapter tmp = mAdvanceDataModel.getChapter(new URL(urlList.get(chapterIndex - 2)));
                        tmp.setTitle(mChapterList.get(chapterIndex - 1));
                        mFragmentReaderList.get(0).showChapter(tmp);
                        tmp.setIndex(chapterIndex - 1);
                        tmp = mAdvanceDataModel.getChapter(new URL(urlList.get(chapterIndex - 2)));
                        tmp.setTitle(mChapterList.get(chapterIndex + 1));
                        tmp.setIndex(chapterIndex + 1);
                        mFragmentReaderList.get(2).showChapter(tmp);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    private void initData() {
        getScreenSize();
        Bundle bundle = this.getIntent().getBundleExtra("bookbundle");
        mBook = (Book) bundle.getSerializable("book");
        mChapterList = mBook.getChapterList().getChapterTitleList();
        urlList = mBook.getChapterList().getChapterUrlList();
        mAdvanceDataModel = AdvanceDataModel.build(getApplicationContext(), this);
        chapterIndex = mBook.getReadChapterIndex();
    }

    RecyclerView.LayoutManager manager;

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
                public void onItemClick(View view, final int position) {
                    Observable.just(urlList)
                            .map(new Func1<List<String>, Chapter>() {
                                @Override
                                public Chapter call(List<String> strings) {
                                    try {
                                        chapterIndex = position;
                                        if (chapterIndex > mChapterList.size() - 1 || chapterIndex < 0)
                                            return null;
                                        return mAdvanceDataModel.getChapter(new URL(strings.get(position)));
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Chapter>() {
                                @Override
                                public void call(Chapter chapter) {
                                    if (chapter == null) {
                                        showSnackbar("获取章节失败");
                                        return;
                                    }
                                    chapter.setTitle(mChapterList.get(chapterIndex));
                                    setViewPagerConfigure(chapter);
                                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                                    toogleScreenState();
                                }
                            });
                }
            });
            mIdJieReaderLeftMenuRv.setAdapter(mRecycleAdapter);
        }
    }

    private void getScreenSize() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }

    @Override
    protected void onStop() {
        doBeforeClose();
        Log.e("onDestroy", "" + chapterIndex + "isCached:" + mBook.isCached());
        super.onStop();
    }

    private void doBeforeClose() {
        mBook.setHasUpdate(false);
        mAdvanceDataModel.updateBookReaderChapterIndex(mBook, chapterIndex);
    }

    public void showSnackbar(String value) {
        Observable.just(value)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Snackbar.make(mIdJieReaderLeftMenuRv, s, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
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
        getSupportActionBar().hide();
    }

    private void cancelFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().show();
    }

    private TextView tvShowTextSize;

    @OnClick({R.id.id_include_context_btn, R.id.id_include_night_btn, R.id.id_include_setting_btn, R.id.id_reader_left_menu_bottom_btn, R.id.id_reader_left_menu_cache_ll})
    public void onClick(View view) {
        switch (view.getId()) {
//            底栏弹出侧边栏
            case R.id.id_include_context_btn:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                toogleScreenState();
                break;
//            底栏夜间模式
            case R.id.id_include_night_btn:
                Log.e("radiobutton", mIdIncludeNightBtn.isChecked() + "");
                if (mIdIncludeNightBtn.isChecked())
                    mIdIncludeModeTv.setText("日间");
                else
                    mIdIncludeModeTv.setText("夜间");
                break;
//            底栏设置
            case R.id.id_include_setting_btn:
                toogleScreenState();
                if (setPopupWindow == null) {
                    View popupWindowView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.set_reader_config_popup, null, false);
                    setPopupWindow = new PopupWindow(popupWindowView
                            , WindowManager.LayoutParams.MATCH_PARENT
                            , WindowManager.LayoutParams.WRAP_CONTENT);
                    setPopupWindow.setFocusable(true);
                    setPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                    //                    设置阅读背景
                    ((RadioGroup) (popupWindowView.findViewById(R.id.id_set_popup_radio_group)))
                            .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                        }
                    });
                    //                    设置字体大小
//                    加大字体
                    (popupWindowView.findViewById(R.id.id_add_text_size_ibtn)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (FragmentReader reader : mFragmentReaderList)
                                reader.addTextSize();
                        }
                    });
//                    减小字体
                    (popupWindowView.findViewById(R.id.id_reduce_text_size_ibtn)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (FragmentReader reader : mFragmentReaderList)
                                reader.reduceTextSize();
                        }
                    });
//                    显示字体大小
                    tvShowTextSize = (TextView) popupWindowView.findViewById(R.id.id_set_popup_textsize_tv);
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
                                        case 1:
                                            viewPagerBackgroundId = R.drawable.read_default_background;
                                            textColorId = R.color.colorDefaultBackgroundText;
                                            break;
                                        case 2:
                                            viewPagerBackgroundId = R.color.colorNovelReadBackgroundBlue;
                                            textColorId = R.color.colorNovelReadBackgroundBlueText;
                                            break;
                                        case 3:
                                            viewPagerBackgroundId = R.color.colorNovelReadBackgroundgray;
                                            textColorId = R.color.colorNovelReadBackgroundgrayText;
                                            break;
                                        case 4:
                                            viewPagerBackgroundId = R.color.colorNovelReadBackgroundGraygreen;
                                            textColorId = R.color.colorNovelReadBackgroundGraygreenText;
                                            break;
                                        case 5:
                                            viewPagerBackgroundId = R.color.colorNovelReadBackgroundgreen1;
                                            textColorId = R.color.colorNovelReadBackgroundgreen1Text;
                                            break;
                                    }
                                    if (viewPagerBackgroundId != 0 && textColorId != 0) {
                                        mIdJieReaderContentVp
                                                .setBackgroundResource(viewPagerBackgroundId);
                                        setTextColor(getResources().getColor(textColorId));
                                    }
                                }
                            });
                }
                setPopupWindow.showAtLocation((
                        (ViewGroup) JieReader.this.findViewById(android.R.id.content))
                        .getChildAt(0), Gravity.BOTTOM, 0, 0);
                break;
//            侧边栏直达底部
            case R.id.id_reader_left_menu_bottom_btn:
                Log.e("bottomBtn", "clicked:" + ((LinearLayoutManager) manager).findFirstVisibleItemPosition());
//              直接用scrollToPositon不能够滑动到底部，可以通过滑动到接近底部的时候调用smoothScrollToPosition来滑动剩余部分
//                如果直接用smoothScrollToPosition，由于数据量过大，滑动的时间非常非常非常长。。。

//                如果当前最后一个可见item大于一半的数据量，则向上滑动到底，否则滑动到底部
                if (((LinearLayoutManager) manager).findLastVisibleItemPosition() > mChapterList.size() / 2) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_up_anim);
                    mIdReaderLeftMenuBottomBtn.startAnimation(animation);
                    mIdJieReaderLeftMenuRv.scrollToPosition(20);
                    mIdJieReaderLeftMenuRv.smoothScrollToPosition(0);
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_down_anim);
                    mIdReaderLeftMenuBottomBtn.startAnimation(animation);
                    mIdJieReaderLeftMenuRv.scrollToPosition(mChapterList.size() - 20);
                    mIdJieReaderLeftMenuRv.smoothScrollToPosition(mChapterList.size() + 1);
                }
                break;
//            侧边栏缓存所有章节
            case R.id.id_reader_left_menu_cache_ll:
                mIdReaderLeftMenuProgressBar.setMax(urlList.size() - 1);
                mIdReaderLeftMenuProgressBar.setProgress(0);
                mIdReaderLeftMenuProgressBar.setVisibility(View.VISIBLE);
                updateProgressBar = Observable.just(0).subscribeOn(AndroidSchedulers.mainThread());

                mAdvanceDataModel.cacheChapterFromList(urlList, new OnChapterCacheListener() {
                    @Override
                    public void onSuccess() {
                        Observable.just(cachedChapterCount++)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer integer) {
                                        mIdReaderLeftMenuProgressBar.setProgress(integer);
                                    }
                                });
                        if (cachedChapterCount >= mChapterList.size() - 1) {
                            mBook.setCached(true);
                        }
                    }

                    @Override
                    public void onFailed(String url) {
                        Log.e("onFailed", url);
                    }
                });
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_jie_read_drawer,menu);
        return true;
    }

    //    设置text大小
    private void setTextColor(int color) {
        for (FragmentReader fragmentReader : mFragmentReaderList)
            fragmentReader.setTextColor(color);
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

    int cachedChapterCount = 0;
    Observable updateProgressBar;

    @Override
    public void onBookAddSuccess(Book book) {

    }

    @Override
    public void onBookAddFailed() {

    }

    @Override
    public void onBookUpdateSuccess(String bookName) {

    }

    @Override
    public void onBookUpdateFailed() {

    }

    @Override
    public void onBookRemoveSuccess() {

    }

    @Override
    public void onChapterLoadSuccess(Chapter chapter) {
        /*if (chapterIndex > 0 && chapterIndex < mChapterList.size() - 1) {
            ((FragmentReaderImpl) mFragmentReaderList.get(1)).showChapter(chapter);
        } else if (chapterIndex == 0) {
            ((FragmentReaderImpl) mFragmentReaderList.get(0)).showChapter(chapter);
        } else if (chapterIndex == mChapterList.size() - 1) {
            ((FragmentReaderImpl) mFragmentReaderList.get(2)).showChapter(chapter);
        }*/
    }


    class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

        public CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) mFragmentReaderList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentReaderList.size();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.ViewHolder> {
        OnItemClickListener mListener = null;

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(JieReader.this).inflate(R.layout.reader_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v, position);
                }
            });
            holder.mTextView.setText(mChapterList.get(position));
        }

        @Override
        public int getItemCount() {
            return mChapterList.size();
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
