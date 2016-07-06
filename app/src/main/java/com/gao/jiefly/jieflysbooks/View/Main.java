package com.gao.jiefly.jieflysbooks.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.listener.onDataStateListener;
import com.gao.jiefly.jieflysbooks.Present.PresentMain;
import com.gao.jiefly.jieflysbooks.R;

import java.net.MalformedURLException;
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
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Main extends Activity implements View, onDataStateListener, SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.id_main_add_book_fab)
    FloatingActionButton mIdMainAddBookFab;
    @InjectView(R.id.id_main_swipe_refresh_layout)
    SwipeRefreshLayout mIdMainSwipeRefreshLayout;
    private List<Book> data;
    BookListRecycleViewAdapter adapter;
    PopupWindow mPopupWindow = null;
    EditText etAddBookName;
    PresentMain mPresentMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.inject(this);
        mPresentMain = PresentMain.getInstance(getApplicationContext(), this);
//        mPresentMain.updateBookList();
        data = mPresentMain.getBookList();
        mIdMainSwipeRefreshLayout.setOnRefreshListener(this);
        adapter = new BookListRecycleViewAdapter();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(android.view.View view, final int position) {
                Toast.makeText(getApplicationContext(), position + "click", Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    return;
                }
                try {
                    mPresentMain.readBook(position - 1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemLongClick(android.view.View view, final int position) {
                if (position > 0) {
                    android.view.View popupWindow = LayoutInflater.from(Main.this)
                            .inflate(R.layout.delete_book_popupwindow, null);
                    final PopupWindow deletePopup = new PopupWindow(popupWindow
                            , WindowManager.LayoutParams.MATCH_PARENT
                            , WindowManager.LayoutParams.WRAP_CONTENT);
                    deletePopup.setFocusable(true);
                    deletePopup.setBackgroundDrawable(new BitmapDrawable());
                    deletePopup.showAtLocation((
                            (ViewGroup) Main.this.findViewById(android.R.id.content))
                            .getChildAt(0), Gravity.CENTER_VERTICAL, 0, 0);

                    popupWindow.findViewById(R.id.id_popup_delete_btn)
                            .setOnClickListener(
                                    new android.view.View.OnClickListener() {
                                        @Override
                                        public void onClick(android.view.View v) {
                                            mPresentMain.removeBook(new int[]{position - 1});
                                            deletePopup.dismiss();
                                        }
                                    });
                }
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_rv);
        RecyclerView.LayoutManager manager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
        }
    }

    private void initPopupWindow() {
        android.view.View popupWindow = LayoutInflater
                .from(Main.this).inflate(R.layout.add_book_popupwindow, null);
        mPopupWindow = new PopupWindow(popupWindow,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        etAddBookName = (EditText) popupWindow.findViewById(R.id.id_popup_book_name_et);
//                        取消
        Button btnCancle = (Button) popupWindow.findViewById(R.id.id_popup_cancle_btn);
        btnCancle.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                mPopupWindow.dismiss();
            }
        });
//                        确定
        Button btnConfirm = (Button) popupWindow.findViewById(R.id.id_popup_confirm_btn);
        btnConfirm.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mPresentMain.addBook(etAddBookName.getText().toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        etAddBookName.setText("");
                    }
                }).start();
                mPopupWindow.dismiss();
            }
        });
    }
/*    @Override
    protected void onRestart() {
        super.onRestart();
        data = mPresentMain.getBookList();
        Log.e("onRestart", data.size() + "");
    }*/

    @Override
    public void onSuccess(Book result) {
    }

    @Override
    public void onFailed() {
    }

    @OnClick(R.id.id_main_add_book_fab)
    public void onClick() {
        if (mPopupWindow == null) {
            initPopupWindow();
        }
        mPopupWindow.showAtLocation(((ViewGroup) Main.this.findViewById(android.R.id.content)).getChildAt(0), Gravity.NO_GRAVITY, 0, 0);

       /* Snackbar.make(mIdMainAddBookFab, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
    }

    @Override
    public void showBooks(List<Book> books) {
        Observable.from(books)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Book>() {
                    @Override
                    public void onCompleted() {
                        Log.i("showBooks", "success");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), "add show book failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Book book) {
                        data = mPresentMain.getBookList();
                        if (data.contains(book)) {
                            data.add(book);
                            adapter.notifyItemChanged(1, data.size());
                        } else {
                            data.add(book);
                            adapter.notifyItemInserted(data.size());
                        }
                    }
                });
    }

    @Override
    public void updateBook(String bookName) {
        data = mPresentMain.getBookList();
        final int index = getBookIndex(bookName);
        Observable.just("").subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                adapter.notifyItemChanged(index);
            }
        });
        Log.e("updateBook", bookName + "update success");
    }

    private int getBookIndex(String bookName) {
        //        加上头部
        int index = 1;
        for (Book b : data) {
            if (b.getBookName().equals(bookName)) {
                break;
            }
            index++;
        }
        return index;
    }

    @Override
    public void readBook(Book book) {
        Intent intent = new Intent();
        intent.setClass(Main.this, JieReader.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", book);
        intent.putExtra("bookbundle", bundle);
        startActivity(intent);
    }

    @Override
    public void addBook(Book book) {
        Observable.just(book)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Book>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onNext(Book book) {
                        data.add(book);
                        adapter.notifyItemInserted(data.size() + 1);
                    }
                });
    }

    @Override
    public void removeBook(Book removeBook) {
        int removeItem = 1;
        for (Book book : data) {
            if (book.getBookName().equals(removeBook.getBookName())) {
                break;
            }
            removeItem++;
        }
        adapter.notifyItemRemoved(removeItem);
    }

    @Override
    public void showSnackbar(String value) {
        Observable.just(value)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Snackbar.make(mIdMainAddBookFab, s, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        Observable.just(1)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        mPresentMain.updateBookList();
                        return 1;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        data = mPresentMain.getBookList();
                        if (data != null) {
                            adapter.notifyItemRangeChanged(1, data.size());
                        }
                        mIdMainSwipeRefreshLayout.setRefreshing(false);
                    }
                });
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
                headViewHolder.ivHead.setImageDrawable(getDrawable(R.drawable.head_canvas));
            } else if (holder instanceof ItemViewHolder) {
                position -= 1;
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Log.e("onBindViewHolder", position + "<--->" + data.get(position));
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
                    itemViewHolder.itemView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(android.view.View v) {
                            int position = itemViewHolder.getAdapterPosition();
                            mListener.onItemLongClick(itemViewHolder.itemView, position);
                            return true;
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            if (data == null)
                return 1;
            else
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

            public HeadViewHolder(android.view.View itemView) {
                super(itemView);
                ivHead = (ImageView) itemView.findViewById(R.id.id_iv_head);
            }
        }
    }
}
