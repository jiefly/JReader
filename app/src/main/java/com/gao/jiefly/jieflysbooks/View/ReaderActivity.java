package com.gao.jiefly.jieflysbooks.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.Book;
import com.gao.jiefly.jieflysbooks.Model.DataModelImpl;
import com.gao.jiefly.jieflysbooks.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
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

    DataModelImpl mDataModel;
    Book mBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reader);
        ButterKnife.inject(this);
        Bundle bundle = this.getIntent().getBundleExtra("bookbundle");
        mBook = (Book) bundle.getSerializable("book");
        Log.d("readerActivity",mBook.toString());
        initData();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_reader_left_recycle_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            recyclerView.setAdapter(new CustomRecycleAdapter());
        }
        Observable.just(mBook.getBookNewTopicUrl())
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (mDataModel == null)
                            mDataModel = new DataModelImpl(null);
                        return mDataModel.getBookTopic(s);
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
                                mIdReaderTopicTitle.setText(mBook.getBookNewTopicTitle());
                            }
                        });
            }
        });
    }


    private void initData() {
        for (int i = 100; i > 0; i--)
            data.add("hello" + i);
    }

    class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ReaderActivity.this).inflate(R.layout.reader_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
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
