package com.gao.jiefly.jieflysbooks.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

public class ReaderActivity extends Activity {
    List<String> data = new ArrayList<>();
    @InjectView(R.id.id_reader_left_order_btn)
    Button mIdReaderLeftOrderBtn;
    @InjectView(R.id.id_reader_topic_title)
    TextView mIdReaderTopicTitle;
    @InjectView(R.id.id_reader_topic_content)
    TextView mIdReaderTopicContent;
    @InjectView(R.id.id_reader_phone_info)
    TextView mIdReaderPhoneInfo;

    Book mBook;
    List<Chapter> mChapterList = new LinkedList<>();

    CustomRecycleAdapter mRecycleAdapter = null;
    @InjectView(R.id.id_reader_layout)
    SlidingMenu mIdReaderLayout;
    @InjectView(R.id.id_reader_scroll_view)
    ScrollView mIdReaderScrollView;
    AdvanceDataModel mAdvanceDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reader);
        ButterKnife.inject(this);
        Bundle bundle = this.getIntent().getBundleExtra("bookbundle");
        mBook = (Book) bundle.getSerializable("book");

        mAdvanceDataModel = AdvanceDataModel.build(getApplicationContext());
//        Log.d("readerActivity", mBook.toString());
//        initData();
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
                                    setContentText(chapter.getUrl());
                                    mIdReaderTopicTitle.setText(chapter.getTitle());
                                    mIdReaderLayout.showContent();
                                    mIdReaderScrollView.scrollTo(0, 0);
                                }
                            });
                }
            });
            recyclerView.setAdapter(mRecycleAdapter);
        }
        setContentText(mBook.getBookNewTopicUrl());
        mIdReaderTopicTitle.setText(mBook.getBookNewTopicTitle());
    }

    private void setContentText(final String url) {
        Observable.just(url)
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (mChapterList.size() < 1)
                            initData();
                        Chapter chapter = null;
                        try {
                            chapter = mAdvanceDataModel.getChapter(new URL(url));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        assert chapter != null;
                        return chapter.getContent();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("jiefly", e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
//                mIdReaderTopicContent.setText(s);
                        Observable.just(s)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        mIdReaderTopicContent.setText(s);

                                    }
                                });
                    }
                });
    }

    //    ture :正序排列
//    false: 倒序排列
    private boolean orderBy = true;

    @OnClick(R.id.id_reader_left_order_btn)
    public void onClick() {
//        orderBy = !orderBy;
        Observable.just("http://www.uctxt.com/book/1/1269/392324.html")
                .map(new Func1<String, Chapter>() {
                    @Override
                    public Chapter call(String s) {
                        try {
                            return mAdvanceDataModel.getBookChapterByUrl(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return new Chapter(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Chapter>() {
                    @Override
                    public void call(Chapter chapter) {
                        mIdReaderTopicContent.setText(chapter.getContent());
                        mIdReaderLayout.showContent();
                        mIdReaderScrollView.scrollTo(0, 0);
                    }
                });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private void initData() {
        Observable.just(mBook)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Book, List<Chapter>>() {
                    @Override
                    public List<Chapter> call(Book book) {
                        List<Chapter> result = null;
                        try {
                            result = mAdvanceDataModel.getChapterList(mBook.getBookName());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Chapter>>() {
                    @Override
                    public void call(List<Chapter> chapters) {
                        mChapterList = chapters;
                        mRecycleAdapter.notifyItemChanged(0);
                    }
                });
        /*for (int i = 100; i > 0; i--)
            data.add("hello" + i);*/
    }

    class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.ViewHolder> {
        OnItemClickListener mListener = null;

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ReaderActivity.this).inflate(R.layout.reader_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.reader_item_tv);
            }
        }
    }
}
