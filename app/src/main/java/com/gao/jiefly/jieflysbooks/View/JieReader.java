package com.gao.jiefly.jieflysbooks.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.R;

import java.net.MalformedURLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jiefly on 2016/7/4.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class JieReader extends Activity {
    @InjectView(R.id.id_jie_reader_content_vp)
    CustomViewPager mIdJieReaderContentVp;
    @InjectView(R.id.id_jie_reader_left_menu_rv)
    RecyclerView mIdJieReaderLeftMenuRv;
    @InjectView(R.id.id_jie_reader_drawer_layout)
    DrawerLayout mDrawerLayout;
    private int mScreenWidth;
    private List<Chapter> mChapterList;
    private Book mBook;
    private AdvanceDataModel mAdvanceDataModel;
    private JieReader.CustomRecycleAdapter mRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jie_reader);
        ButterKnife.inject(this);

        Bundle bundle = this.getIntent().getBundleExtra("bookbundle");
        mBook = (Book) bundle.getSerializable("book");
        mAdvanceDataModel = AdvanceDataModel.build(getApplicationContext());
        try {
            mChapterList = mAdvanceDataModel.getChapterList(mBook.getBookName());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_reader_left_recycle_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mRecycleAdapter = new CustomRecycleAdapter();
            mRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Observable.just(mChapterList.get(position))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Chapter>() {
                                @Override
                                public void call(Chapter chapter) {
//                                    setContentText(chapter.getUrl());
//                                    mIdReaderTopicTitle.setText(chapter.getTitle());
//                                    mIdReaderLayout.showContent();
//                                    mIdReaderScrollView.scrollTo(0, 0);
                                }
                            });
                }
            });
            recyclerView.setAdapter(mRecycleAdapter);
        }
    }
    private void getScreenWidth(){
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
    }
   /* @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
//                如果是在左边手机屏幕边缘的位置向右滑动则默认为是打开侧边栏的手势，将由activity的onTouchEvent处理
                if (ev.getX()<mScreenWidth/8){
                    return false;
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int moveStartX = mScreenWidth;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                moveStartX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() - moveStartX > mScreenWidth/5) {
                    Log.i("jiefly", "up" + event.getX());
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }

        }
        return super.onTouchEvent(event);
    }*/

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
            holder.mTextView.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    mListener.onItemClick(v, position);
                }
            });
            holder.mTextView.setText(mChapterList.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return mChapterList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            public ViewHolder(android.view.View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.reader_item_tv);
            }
        }
    }
}
