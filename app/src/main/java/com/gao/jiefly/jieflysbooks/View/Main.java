package com.gao.jiefly.jieflysbooks.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;
import com.gao.jiefly.jieflysbooks.Present.PresentMain;
import com.gao.jiefly.jieflysbooks.R;
import com.gao.jiefly.jieflysbooks.Service.UpdateBookService;
import com.gao.jiefly.jieflysbooks.Utils.AndroidUtilities;
import com.gao.jiefly.jieflysbooks.Utils.ApplicationLoader;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.ArrayList;
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
public class Main extends AppCompatActivity implements View, OnDataStateListener, SwipeRefreshLayout.OnRefreshListener, PopupWindow.OnDismissListener {
    @InjectView(R.id.id_main_add_book_fab)
    FloatingActionButton mIdMainAddBookFab;
    @InjectView(R.id.id_main_swipe_refresh_layout)
    SwipeRefreshLayout mIdMainSwipeRefreshLayout;
    @InjectView(R.id.id_rv)
    RecyclerView mIdRv;
    private List<Book> data;
    BookListRecycleViewAdapter adapter;
    PopupWindow itemMainPop = null;
    PopupWindow itemHeadPop = null;
    EditText etAddBookName;
    PresentMain mPresentMain;
    android.view.View viewHeadPop;
    public static final int SCAN_FLAG = 1;
    CheckBox checkBox;
    TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        new GetBookFromSoDu().getBookCoverUrl("寒门状元", "秋水轩", new OnBookImageGetListener() {
//            @Override
//            public void onSuccess(String url) {
//                Log.e("success",url);
//            }
//
//            @Override
//            public void onFailed(Exception error) {
//
//            }
//        });
        /*Book book = new Book();
        book.setBookUrl("http://www.qiushuixuan.cc/book/14/14757/");
        book.setBookUpdateTimeUrl("http://www.sodu.cc/mulu_368538.html");
        book.setBookResource("秋水轩");
      new GetBookFromSoDu().getBookUpdateInfo(book, new OnBookUpdateFromSoDuListener() {
          @Override
          public void onSuccess(Book book) {
              Book s = book;
              Log.e("success",s.toString());
          }

          @Override
          public void onFailed(Exception error) {

          }
      });*/

//        加载本地书籍
//        LocalBookSegmentation.getInstance();
        mPresentMain = PresentMain.getInstance(getApplicationContext(), this);
//        mPresentMain.bindUpdateBookService(Main.this);
        if (mPresentMain.isNeedUpdateBackgrond) {
            startBackGroundUpdateService();
        }
        ButterKnife.inject(this);
        data = mPresentMain.getBookList();
        mIdMainSwipeRefreshLayout.setOnRefreshListener(this);
        adapter = new BookListRecycleViewAdapter();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(android.view.View view, final int position) {
                if (position == 0) {
                    if (itemHeadPop == null) {
                        viewHeadPop = LayoutInflater.from(Main.this)
                                .inflate(R.layout.item_head_new_popup, null);
                        itemHeadPop = new PopupWindow(viewHeadPop, WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT);
                        itemHeadPop.setOnDismissListener(Main.this);
                        mTextView = (TextView) viewHeadPop.findViewById(R.id.id_main_head_sort_tv);
                        checkBox = (CheckBox) (viewHeadPop.findViewById(R.id.id_main_head_pop_update_cb));
                        if (ApplicationLoader.getBooleanValue(ApplicationLoader.IS_NEED_UPDATE_BG))
                            checkBox.setChecked(true);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    ApplicationLoader.save(ApplicationLoader.IS_NEED_UPDATE_BG, true);
                                    mPresentMain.setUpdateFlag(false);
                                } else {
                                    ApplicationLoader.save(ApplicationLoader.IS_NEED_UPDATE_BG, false);
                                    mPresentMain.setUpdateFlag(false);
                                }
                                mPresentMain.isNeedUpdateBackgrond = isChecked;
                            }
                        });

//                        是否开启更新推送
                        viewHeadPop.findViewById(R.id.id_main_head_pop_update_ll).setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                checkBox.setChecked(!checkBox.isChecked());
                                itemHeadPop.dismiss();
                            }
                        });
//                        设置
                        viewHeadPop.findViewById(R.id.id_main_head_setting_update_ll).setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent intent = new Intent(Main.this, SettingActivity.class);
                                startActivity(intent);
                                itemHeadPop.dismiss();
                            }
                        });
//                        导入本地书籍
                        viewHeadPop.findViewById(R.id.id_main_head_pop_local_ll).setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent addLocalBookIntent = new Intent(Main.this, ScanTxtView.class);
                                addLocalBookIntent.putExtra("type", ScanTxtView.TYPE_SCAN);
                                startActivityForResult(addLocalBookIntent, SCAN_FLAG);
                                itemHeadPop.dismiss();
                            }
                        });
//                        第一次点击pop时 初始化textView
                        if (ApplicationLoader.getIntValue(ApplicationLoader.BOOK_ORDER) == ApplicationLoader.SORT_BY_ADD_TIME) {
                            mTextView.setText("排序方式(更新时间)");
                        } else {
                            mTextView.setText("排序方式(添加时间)");
                        }
//                        书籍排序
                        viewHeadPop.findViewById(R.id.id_main_head_sort_ll).setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                if (ApplicationLoader.getIntValue(ApplicationLoader.BOOK_ORDER) != ApplicationLoader.SORT_BY_ADD_TIME) {
                                    ApplicationLoader.save(ApplicationLoader.BOOK_ORDER, ApplicationLoader.SORT_BY_ADD_TIME);
                                    mTextView.setText("排序方式(更新时间)");
                                    data = mPresentMain.getBookListOrderByAddTime();
                                    if (data != null)
                                        adapter.notifyItemRangeChanged(1, data.size());

                                } else {
                                    ApplicationLoader.save(ApplicationLoader.BOOK_ORDER, ApplicationLoader.SORT_BY_UPDATE_TIME);
                                    mTextView.setText("排序方式(添加时间)");
                                    data = mPresentMain.getBookListOrderByUpdateTime();
                                    if (data != null)
                                        adapter.notifyItemRangeChanged(1, data.size());
                                }
                                itemHeadPop.dismiss();
                            }
                        });
                    }
                    itemHeadPop.setFocusable(true);
                    itemHeadPop.setBackgroundDrawable(new BitmapDrawable());
                    backgroundAlpha(0.5f);
                    itemHeadPop.showAtLocation((
                            (ViewGroup) Main.this.findViewById(android.R.id.content))
                            .getChildAt(0), Gravity.RIGHT | Gravity.TOP, 0, 0);


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
                    deletePopup.setOnDismissListener(Main.this);
                    backgroundAlpha(0.5f);
                    deletePopup.showAtLocation((
                            (ViewGroup) Main.this.findViewById(android.R.id.content))
                            .getChildAt(0), Gravity.BOTTOM, 0, 0);
//删除

                    popupWindow.findViewById(R.id.id_popup_delete_btn)

                            .setOnClickListener(
                                    new android.view.View.OnClickListener() {
                                        @Override
                                        public void onClick(android.view.View v) {
                                            deletePopup.dismiss();
                                            new AlertDialog.Builder(Main.this)
                                                    .setTitle("确定删除小说")
                                                    .setNegativeButton("取消", null)
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            mPresentMain.removeBook(new int[]{position - 1});
                                                        }
                                                    }).show();

                                        }
                                    });
//                    缓存所有
                    popupWindow.findViewById(R.id.id_popup_cache_btn)
                            .setOnClickListener(
                                    new android.view.View.OnClickListener() {
                                        @Override
                                        public void onClick(android.view.View v) {
                                            mPresentMain.cacheAllChapter(position - 1);
                                            deletePopup.dismiss();
                                        }
                                    });
//                    置顶
                    popupWindow.findViewById(R.id.id_popup_top_btn)
                            .setOnClickListener(
                                    new android.view.View.OnClickListener() {
                                        @Override
                                        public void onClick(android.view.View v) {

                                            deletePopup.dismiss();
                                        }
                                    });
                }
            }
        });
        RecyclerView.LayoutManager manager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        if (mIdRv != null) {
            mIdRv.setLayoutManager(manager);
            mIdRv.setAdapter(adapter);
            mIdMainAddBookFab.attachToRecyclerView(mIdRv);
            mIdMainAddBookFab.setColorNormalResId(R.color.theme_green);
            mIdMainAddBookFab.setColorPressedResId(R.color.justWhite);
        }


    }

    private void startBackGroundUpdateService() {
        //            默认十分钟更新一次
        int time = 60 * 1000;
        switch (ApplicationLoader.getIntValue(ApplicationLoader.UPDATE_FREQUENCE)){
//                关闭更新
            case 1:
                time = Integer.MAX_VALUE;
                break;
//                五分钟
            case 2:
                time = 5*60*1000;
                break;
//                十分钟
            case 3:
                time = 60 * 1000;
                break;
//                三十分钟
            case 4:
                time = 30*60*1000;
                break;
//                一个小时
            case 5:
                time = 60*60*1000;
                break;
//                两个小时
            case 6:
                time = 120*60*1000;
                break;
        }
        Log.e("openBackgroundService","time:"+time);
        Intent intent = new Intent(getApplicationContext(), UpdateBookService.class);
        intent.putExtra("time", time);
        startService(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initPopupWindow() {
        android.view.View popupWindow = LayoutInflater
                .from(Main.this).inflate(R.layout.add_book_popupwindow, null);
        itemMainPop = new PopupWindow(popupWindow,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        itemMainPop.setFocusable(true);
        itemMainPop.setOnDismissListener(Main.this);
        itemMainPop.setBackgroundDrawable(new BitmapDrawable());
        etAddBookName = (EditText) popupWindow.findViewById(R.id.id_popup_book_name_et);
//                        取消
        Button btnCancle = (Button) popupWindow.findViewById(R.id.id_popup_cancle_btn);
        btnCancle.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                itemMainPop.dismiss();
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
                        Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                etAddBookName.setText("");
                            }
                        });
                    }
                }).start();
                itemMainPop.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e("main", "onDestroy");
        if (AndroidUtilities.isServiceRunning("com.gao.jiefly.jieflysbooks.Service.UpdateBookService"))
            mPresentMain.unBindUpdateBookService(getBaseContext());
        ApplicationLoader.save(ApplicationLoader.FIRST_TIME, false);
        super.onDestroy();
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        data = mPresentMain.getBookList();
        Log.back_btn_bg("onRestart", data.size() + "");
    }*/

   /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_scan_txt, menu);
        return true;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        mPresentMain.setUpdateFlag(false);
        Log.e("main", "onResume");
        data = mPresentMain.getBookList();
        if (data != null)
            adapter.notifyItemRangeChanged(0, data.size());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresentMain.bindUpdateBookService(getBaseContext());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("main", "onrestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("main", "onpause");
//        if (isFinishing()) {
////            onDestroy 有bug activity ｅｘｉｔ 的时候ondestroy不会调用
////            于是在onPause中判断当前是否是退出activity，是的话在这里做相关操作
//            mPresentMain.unBindUpdateBookService(getBaseContext());
//        }
        mPresentMain.setUpdateFlag(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("main", "onstop");
//        mPresentMain.setUpdateFlag(true);
    }

    @OnClick(R.id.id_main_add_book_fab)
    public void onClick() {
        if (itemMainPop == null) {
            initPopupWindow();
        }
        backgroundAlpha(0.5f);
        itemMainPop.showAtLocation(((ViewGroup)
                Main.this.findViewById(android.R.id.content))
                .getChildAt(0), Gravity.CENTER, 0, 0);

       /* Snackbar.make(mIdMainAddBookFab, "Replae with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
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
                        Toast.makeText(getApplicationContext(), "show book failed", Toast.LENGTH_SHORT).show();
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
                        Log.e("error", e.getMessage());
                        showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onNext(Book book) {
                        if (data == null)
                            data = new ArrayList<>();
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
        if (data.size() < 4)
            mIdMainAddBookFab.show();
    }

    @Override
    public void showSnackbar(String value) {
        Observable.just(value)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Snackbar.make(mIdMainAddBookFab, s, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                });
    }

    @Override
    public BookListRecycleViewAdapter.ItemViewHolder getViewHolder(int position) {
        return (BookListRecycleViewAdapter.ItemViewHolder) mIdRv
                .getChildViewHolder(mIdRv.getChildAt(position + 1));
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        if (data == null) {
            stopRefreshAnim();
            return;
        }
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
                    }
                });

    }

    @Override
    public void stopRefreshAnim() {
        Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                mIdMainSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onSuccess(String result) {

    }

    @Override
    public void onFailed(Exception e) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (itemHeadPop != null && itemHeadPop.isShowing()) {
                itemHeadPop.dismiss();
                return false;
            }
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private long exitTime = 0;

    @Override
    public void onDismiss() {
        backgroundAlpha(1f);
    }

    public interface OnItemClickListener {
        void onItemClick(android.view.View view, int position);

        void onItemLongClick(android.view.View view, int position);
    }

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_HEADER = 2;
    public static final int TYPE_FOOTER = 3;

    public class BookListRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
                headViewHolder.ivHead.setImageResource(R.drawable.head_canvas);
                headViewHolder.mImageButton.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        mListener.onItemClick(v, 0);
                    }
                });
            } else if (holder instanceof ItemViewHolder) {
                position -= 1;
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Log.e("onBindViewHolder", position + "<--->" + data.get(position));

                itemViewHolder.tvBookName.setText(data.get(position).getBookName());
                itemViewHolder.tvRecentUpdateTime.setText(data.get(position).getBookLastUpdate());
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemViewHolder.ivBook.setImageDrawable(getApplicationContext().getDrawable(R.mipmap.ic_launcher));
                }*/
                if (data.get(position).isLocal()) {
                    itemViewHolder.tvCoverName.setText(data.get(position).getBookName());
                    itemViewHolder.tvCoverType.setText("本地");
                    itemViewHolder.tvCoverName.setVisibility(android.view.View.VISIBLE);
                    itemViewHolder.tvCoverType.setVisibility(android.view.View.VISIBLE);
                    itemViewHolder.ivBook.setImageResource(R.drawable.local_cover);
                    itemViewHolder.ivBookUpdateFlag.setVisibility(android.view.View.GONE);
                    itemViewHolder.llBookAuthor.setVisibility(android.view.View.GONE);
                    itemViewHolder.tvRecentUpdateTopicTitle.setText("章节数：");
                    itemViewHolder.tvRecentUpdateTimeTitle.setText("添加时间：");
                    itemViewHolder.tvRecentUpdateTopic.setText(data.get(position).getList().size() + "章");
                } else {
                    itemViewHolder.tvBookAuthor.setText(data.get(position).getBookAuthor());
                    itemViewHolder.ivBookUpdateFlag.setVisibility(data.get(position).isHasUpdate() ? android.view.View.VISIBLE : android.view.View.INVISIBLE);
//                    itemViewHolder.ivBook.setImageResource(R.drawable.nocover);
                    Picasso.with(getApplicationContext())
                            .load(data.get(position).getBookCover()).into(itemViewHolder.ivBook);
                    itemViewHolder.tvRecentUpdateTopicTitle.setText("最近更新：");
                    itemViewHolder.tvRecentUpdateTimeTitle.setText("最后更新时间：");
                    itemViewHolder.llBookAuthor.setVisibility(android.view.View.VISIBLE);
                    itemViewHolder.tvRecentUpdateTopic.setText(data.get(position).getBookNewTopicTitle());
                    itemViewHolder.tvCoverName.setVisibility(android.view.View.GONE);
                    itemViewHolder.tvCoverType.setVisibility(android.view.View.GONE);
                }
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
            TextView tvCoverType;
            TextView tvCoverName;
            TextView tvRecentUpdateTopicTitle;
            TextView tvRecentUpdateTimeTitle;
            ImageView ivBook;
            ImageView ivBookUpdateFlag;
            LinearLayout llBookAuthor;

            public NumberProgressBar getNumberProgressBar() {
                return mNumberProgressBar;
            }

            public NumberProgressBar mNumberProgressBar;
/*            TextView tvUpdateInfo;
            ProgressBar pbCacheAllChapter;*/

            public ItemViewHolder(android.view.View itemView) {
                super(itemView);
                tvBookAuthor = (TextView) itemView.findViewById(R.id.id_item_book_author);
                tvBookName = (TextView) itemView.findViewById(R.id.id_item_book_name);
                tvRecentUpdateTime = (TextView) itemView.findViewById(R.id.id_item_book_recent_update_time);
                tvRecentUpdateTopic = (TextView) itemView.findViewById(R.id.id_item_book_recent_update);
                tvCoverType = (TextView) itemView.findViewById(R.id.id_item_book_cover_type);
                tvCoverName = (TextView) itemView.findViewById(R.id.id_item_book_cover_name);
                ivBook = (ImageView) itemView.findViewById(R.id.id_iv_book);
                ivBookUpdateFlag = (ImageView) itemView.findViewById(R.id.id_item_book_new_flag_iv);
                mNumberProgressBar = (NumberProgressBar) itemView.findViewById(R.id.id_main_progress_bar);
                llBookAuthor = (LinearLayout) itemView.findViewById(R.id.id_item_book_author_ll);
                tvRecentUpdateTimeTitle = (TextView) itemView.findViewById(R.id.id_item_book_recent_update_time_title);
                tvRecentUpdateTopicTitle = (TextView) itemView.findViewById(R.id.id_item_book_recent_update_title);
/*                tvUpdateInfo = (TextView) itemView.findViewById(R.id.id_item_update_info_tv);
                pbCacheAllChapter = (ProgressBar) itemView.findViewById(R.id.id_item_progress_bar);*/
            }
        }

        public class HeadViewHolder extends RecyclerView.ViewHolder {
            ImageView ivHead;
            ImageButton mImageButton;

            public HeadViewHolder(android.view.View itemView) {
                super(itemView);
                ivHead = (ImageView) itemView.findViewById(R.id.id_iv_head);
                mImageButton = (ImageButton) itemView.findViewById(R.id.id_item_main_options_ibtn);
            }
        }
    }
}
