package com.gao.jiefly.jieflysbooks.View;

import android.content.Context;
import android.os.Bundle;
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
import com.gao.jiefly.jieflysbooks.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiefly on 2016/7/4.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class JieReader extends AppCompatActivity {
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
    private ArrayList<Fragment> mFragmentReaderList;
    private int chapterIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_jie_reader);
        ButterKnife.inject(this);
        initData();
        initViewPager();
        initRecycleView();
    }

    private void initViewPager() {
        mFragmentReaderList = new ArrayList<>();
        FragmentReaderImpl currentFragment = new FragmentReaderImpl();
        mFragmentReaderList.add(new FragmentReaderImpl());
        mFragmentReaderList.add(currentFragment);
        mFragmentReaderList.add(new FragmentReaderImpl());

        mIdJieReaderContentVp.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager()));
        mIdJieReaderContentVp.setCurrentItem(1);
        mIdJieReaderContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                如果从左向右滑动

//                当由第一页切换至第二页或则第零页时，在切换成功的即页面滑动至完整的第二页或则第零页时，将当前页面强制设置为
//                第一页（并且更新第一页的数据），给用户一种可以无限翻页的效果
//                if (chapterIndex != 0 && chapterIndex != mChapterList.size()) {
                    if ((position == 0 && positionOffset == 0) || (position == 2 && positionOffset == 0)) {
                        mIdJieReaderContentVp.setCurrentItem(1, false);
                    }
//                Log.e("onPageScrolled", "position:" + position + "positionOffset:" + positionOffset);
//                }
            }

            @Override
            public void onPageSelected(int position) {
//                Log.e("onPageSelected", "position:" + position);
//                if (chapterIndex != 0 && chapterIndex != mChapterList.size()) {
                    if (position == 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ((FragmentReader) mFragmentReaderList.get(0)).showChapter(mAdvanceDataModel.getChapter(mBook.getBookName(), chapterIndex - 2,mChapterList.get(chapterIndex - 2)));
                                ((FragmentReader) mFragmentReaderList.get(1)).showChapter(mAdvanceDataModel.getChapter(mBook.getBookName(), chapterIndex - 1,mChapterList.get(chapterIndex - 1)));
                                ((FragmentReader) mFragmentReaderList.get(2)).showChapter(mAdvanceDataModel.getChapter(mBook.getBookName(), chapterIndex,mChapterList.get(chapterIndex)));
                            }
                        }).start();
                        chapterIndex--;
                    } else if (position == 2) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ((FragmentReader) mFragmentReaderList.get(0)).showChapter(mAdvanceDataModel.getChapter(mBook.getBookName(), chapterIndex,mChapterList.get(chapterIndex)));
                                ((FragmentReader) mFragmentReaderList.get(2)).showChapter(mAdvanceDataModel.getChapter(mBook.getBookName(), chapterIndex + 2,mChapterList.get(chapterIndex + 2)));
                                ((FragmentReader) mFragmentReaderList.get(1)).showChapter(mAdvanceDataModel.getChapter(mBook.getBookName(), chapterIndex + 1,mChapterList.get(chapterIndex + 1)));
                            }
                        }).start();

                        chapterIndex++;

                    }
//                }
               /* try {
                    ((FragmentReaderImpl) mFragmentReaderList.get(position)).showChapter(mAdvanceDataModel.getChapter(new URL(mChapterList.get(chapterIndex).)));

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.e("onPageChanged", "state:" + state);
            }
        });
    }

    private void initData() {
        getScreenSize();
        Bundle bundle = this.getIntent().getBundleExtra("bookbundle");
        mBook = (Book) bundle.getSerializable("book");
        mChapterList = mBook.getChapterList().getChapterTitleList();
        mAdvanceDataModel = AdvanceDataModel.build(getApplicationContext());
        /*try {
            mChapterList = mAdvanceDataModel.getChapterList(mBook.getBookName());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
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

                                    ((FragmentReaderImpl) mFragmentReaderList.get(1)).showChapter(chapter);
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
        Log.e("onDestroy",""+chapterIndex);
        super.onStop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                如果手指点击屏幕中心区域，则表示用户想要唤醒底部bar
                if (ev.getX() < mScreenWidth * 3 / 5 && ev.getX() > mScreenWidth * 2 / 5 && ev.getY() > mScreenHeight * 2 / 5 && ev.getY() < mScreenHeight * 3 / 5) {
                    boolean flag = mIdReaderBottomBar.isShown();
                    mIdReaderBottomBar.setVisibility(flag ? View.INVISIBLE : View.VISIBLE);
                    if (flag){
                        setFullScreen();
                    }else {
                        cancelFullScreen();
                    }
                    return false;
                }
        }
        return super.dispatchTouchEvent(ev);
    }
    private void setFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void cancelFullScreen(){
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

    class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

        public CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentReaderList.get(position);
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
