package com.gao.jiefly.jieflysbooks.View;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataModelListener;
import com.gao.jiefly.jieflysbooks.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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
    Button mIdIncludeContextBtn;
    @InjectView(R.id.id_include_night_btn)
    Button mIdIncludeNightBtn;
    @InjectView(R.id.id_include_setting_btn)
    Button mIdIncludeSettingBtn;
    private int mScreenWidth;
    private int mScreenHeight;
    private List<String> mChapterList;
    private Book mBook;
    private AdvanceDataModel mAdvanceDataModel;
    private CustomRecycleAdapter mRecycleAdapter;
    private List<FragmentReader> mFragmentReaderList;
    private int chapterIndex;
    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_jie_reader);
        ButterKnife.inject(this);
        initData();
        initViewPager();
        initRecycleView();
        Log.e("chapterListSize", mChapterList.size() + "");
    }

    private void initViewPager() {
        mFragmentReaderList = new LinkedList<>();
        final FragmentReaderImpl currentFragment = new FragmentReaderImpl();
        final FragmentReaderImpl prevFragment = new FragmentReaderImpl();
        final FragmentReaderImpl nextFragment = new FragmentReaderImpl();
        mFragmentReaderList.add(prevFragment);
        mFragmentReaderList.add(currentFragment);
        mFragmentReaderList.add(nextFragment);
//        进入activity后根据chapter索引分情况对viewpager进行初始化
        Observable.just(mBook)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
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
                        setViewPagerConfigure(chapter);
                    }
                });
        mIdJieReaderContentVp.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager()));

        mIdJieReaderContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("onPPageScrolled", "position:" + position+"positionOffset:"+positionOffset);

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
                            .filter(new Func1<Integer, Boolean>() {
                                @Override
                                public Boolean call(Integer integer) {
                                    return integer != 1;
                                }
                            })
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
                                        return null;
                                    }
                                    if (chapterIndex == 0 && integer == 1) {
                                        return null;
                                    }
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
//                Log.e("onPageScrolled", "position:" + position + "positionOffset:" + positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
//                Log.e("onPageSelected", "position:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.e("onPageChanged", "state:" + state);
            }
        });
        if (chapterIndex == 0)
            mIdJieReaderContentVp.setCurrentItem(0);
        else if (chapterIndex == mChapterList.size() - 1)
            mIdJieReaderContentVp.setCurrentItem(2);
        else
            mIdJieReaderContentVp.setCurrentItem(1);
    }

    private void setViewPagerConfigure(Chapter chapter) {
        if (chapterIndex == 0) {
            (mFragmentReaderList.get(0)).showChapter(chapter);
            mIdJieReaderContentVp.setCurrentItem(0, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mFragmentReaderList.get(1).showChapter(mAdvanceDataModel.getChapter(new URL(mBook.getChapterList().getChapterUrlList().get(chapterIndex + 1))));
                        mFragmentReaderList.get(2).showChapter(mAdvanceDataModel.getChapter(new URL(mBook.getChapterList().getChapterUrlList().get(chapterIndex + 2))));
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
                        mFragmentReaderList.get(0).showChapter(mAdvanceDataModel.getChapter(new URL(mBook.getChapterList().getChapterUrlList().get(chapterIndex - 2))));
                        mFragmentReaderList.get(1).showChapter(mAdvanceDataModel.getChapter(new URL(mBook.getChapterList().getChapterUrlList().get(chapterIndex - 1))));
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
                        mFragmentReaderList.get(0).showChapter(mAdvanceDataModel.getChapter(new URL(mBook.getChapterList().getChapterUrlList().get(chapterIndex - 1))));
                        mFragmentReaderList.get(2).showChapter(mAdvanceDataModel.getChapter(new URL(mBook.getChapterList().getChapterUrlList().get(chapterIndex + 1))));
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

    private void initRecycleView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_jie_reader_left_menu_rv);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mRecycleAdapter = new CustomRecycleAdapter();
            mRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    Observable.just(mBook.getChapterList().getChapterUrlList())
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
                                    /*if (chapterIndex == 0) {
                                        (mFragmentReaderList.get(0)).showChapter(chapter);
                                        mIdJieReaderContentVp.setCurrentItem(0);
                                    } else if (chapterIndex == mChapterList.size()) {
                                        (mFragmentReaderList.get(2)).showChapter(chapter);
                                        mIdJieReaderContentVp.setCurrentItem(2);
                                    } else {
                                        ( mFragmentReaderList.get(1)).showChapter(chapter);
                                        mIdJieReaderContentVp.setCurrentItem(1);
                                    }*/
                                    setViewPagerConfigure(chapter);
                                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                                }
                            });
                }
            });
            recyclerView.setAdapter(mRecycleAdapter);
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
        mAdvanceDataModel.updateBookReaderChapterIndex(mBook, chapterIndex);
        Log.e("onDestroy", "" + chapterIndex);
        super.onStop();
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
                if (ev.getX() < mScreenWidth * 3 / 5 && ev.getX() > mScreenWidth * 2 / 5 && ev.getY() > mScreenHeight * 2 / 5 && ev.getY() < mScreenHeight * 3 / 5) {
                    boolean flag = mIdReaderBottomBar.isShown();
                    mIdReaderBottomBar.setVisibility(flag ? View.INVISIBLE : View.VISIBLE);
                    if (flag) {
                        setFullScreen();
                    } else {
                        cancelFullScreen();
                    }
                    return false;
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void cancelFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @OnClick({R.id.id_include_context_btn, R.id.id_include_night_btn, R.id.id_include_setting_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_include_context_btn:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.id_include_night_btn:
                break;
            case R.id.id_include_setting_btn:
                break;
        }
    }

    @Override
    public void onBookAddSuccess(Book book) {

    }

    @Override
    public void onBookUpdataSuccess(String bookName) {

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