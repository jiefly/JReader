package com.gao.jiefly.jieflysbooks.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gao.jiefly.jieflysbooks.Model.Book;
import com.gao.jiefly.jieflysbooks.Model.DataModelImpl;
import com.gao.jiefly.jieflysbooks.Model.onDataStateListener;
import com.gao.jiefly.jieflysbooks.R;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Main extends Activity implements View, onDataStateListener {
    private List<Book> data = new LinkedList<>();
    DataModelImpl dataModel;
    BookListRecycleViewAdapter adapter;
    Book book;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dataModel = new DataModelImpl(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataModel.getBookSuscribe("完美世界");
                dataModel.getBookSuscribe("飞天");
                dataModel.getBookSuscribe("寒门状元");
                dataModel.getBookSuscribe("黑铁之堡");
                dataModel.getBookSuscribe("一念永恒");
                dataModel.getBookSuscribe("巫神记");
                dataModel.getBookSuscribe("五行天");
            }
        }).start();
        /*book = new Book();
        book.setBookAuthor("jiefly");
        book.setBookName("hello world");
        book.setBookLastUpdate("2016.6.23");
        book.setBookNewTopicTitle("这是一个测试");
        book.setBookNewTopicUrl("");

        for (int i = 10; i > 0; i--) {
            data.add(book);
        }*/


        adapter = new BookListRecycleViewAdapter();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(android.view.View view, final int position) {
                Toast.makeText(getApplicationContext(), position - 1 + "click", Toast.LENGTH_SHORT).show();
                if (data.get(position - 1) != null && data.get(position - 1).getBookNewTopicUrl().startsWith("http://")) {
                    Observable.just(data.get(position - 1))
                            .observeOn(Schedulers.io())
                            .map(new Func1<Book, String>() {
                                @Override
                                public String call(Book book) {

                                    return book.getBookNewTopicUrl();
                                }
                            })
                            .map(new Func1<String, String>() {
                                @Override
                                public String call(String s) {
                                    return dataModel.getBookTopic(s);
                                }
                            })
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
//                                    Log.e("jiefly", s);
                                    Intent intent = new Intent();
                                    intent.setClass(Main.this, ReaderActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("book", data.get(position -1));
                                    intent.putExtra("bookbundle", bundle);
                                    startActivity(intent);
                                }
                            });
                }
            }

            @Override
            public void onItemLongClick(android.view.View view, int position) {
                Toast.makeText(getApplicationContext(), position + "long click", Toast.LENGTH_SHORT).show();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_rv);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void showBookList(List<Book> books) {

    }

    @Override
    public void showBookRecentTopic(Book book) {

    }

    @Override
    public void addBook(Book book) {

    }

    @Override
    public void onSuccess(Book result) {
        Observable.just(result)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        data.add(book);
                        adapter.notifyItemInserted(0);
                    }
                });
    }

    @Override
    public void onFailed() {

    }

    public interface OnItemClickListener {
        void onItemClick(android.view.View view, int position);

        void onItemLongClick(android.view.View view, int position);
    }

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_HEADER = 2;
    public static final int TYPE_FOOTER = 3;

    class BookListRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private OnItemClickListener mListener = null;

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return TYPE_HEADER;
            if (position == data.size() + 1)
                return TYPE_FOOTER;
            return TYPE_NORMAL;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_NORMAL:
                    return new ItemViewHolder(LayoutInflater.from(Main.this).inflate(R.layout.item_main, parent, false));
                case TYPE_FOOTER:

                    break;
                case TYPE_HEADER:
                    return new HeadViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_head, parent, false));
            }
            return null;

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeadViewHolder) {
                HeadViewHolder headViewHolder = (HeadViewHolder) holder;
                headViewHolder.btnAddBook.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Log.e("jiefly", "add book button clicked!");
                    }
                });
                headViewHolder.ivHead.setImageDrawable(getDrawable(R.drawable.head_canvas));
            } else if (holder instanceof ItemViewHolder) {
                position--;
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.tvBookAuthor.setText(data.get(position).getBookAuthor());
                itemViewHolder.tvBookName.setText(data.get(position).getBookName());
                itemViewHolder.tvRecentUpdateTopic.setText(data.get(position).getBookNewTopicTitle());
                itemViewHolder.tvRecentUpdateTime.setText(data.get(position).getBookLastUpdate());
                itemViewHolder.ivBook.setImageDrawable(getDrawable(R.mipmap.ic_launcher));
                if (mListener != null) {
                    itemViewHolder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(android.view.View v) {
                            int position = itemViewHolder.getAdapterPosition();
                            mListener.onItemClick(itemViewHolder.itemView, position);
                        }
                    });
                    itemViewHolder.tvRecentUpdateTime.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(android.view.View v) {
                            int position = itemViewHolder.getAdapterPosition();
                            mListener.onItemLongClick(itemViewHolder.itemView, position);
                            return false;
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return data.size() + 1;
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView tvBookName;
            TextView tvBookAuthor;
            TextView tvRecentUpdateTopic;
            TextView tvRecentUpdateTime;
            ImageView ivBook;

            public ItemViewHolder(android.view.View itemView) {
                super(itemView);
                tvBookAuthor = (TextView) itemView.findViewById(R.id.id_item_book_author);
                tvBookName = (TextView) itemView.findViewById(R.id.id_item_book_name);
                tvRecentUpdateTime = (TextView) itemView.findViewById(R.id.id_item_book_recent_update_time);
                tvRecentUpdateTopic = (TextView) itemView.findViewById(R.id.id_item_book_recent_update);
                ivBook = (ImageView) itemView.findViewById(R.id.id_iv_book);
            }
        }

        public class HeadViewHolder extends RecyclerView.ViewHolder {
            ImageView ivHead;
            Button btnAddBook;

            public HeadViewHolder(android.view.View itemView) {
                super(itemView);
                ivHead = (ImageView) itemView.findViewById(R.id.id_iv_head);
                btnAddBook = (Button) itemView.findViewById(R.id.id_btn_add_book);
            }
        }
    }
}
