package com.gao.jiefly.jieflysbooks.Present;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnChapterCacheListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataModelListener;
import com.gao.jiefly.jieflysbooks.Service.UpdateBookService;
import com.gao.jiefly.jieflysbooks.Utils.ApplicationLoader;
import com.gao.jiefly.jieflysbooks.Utils.Utils;
import com.gao.jiefly.jieflysbooks.View.Main;
import com.gao.jiefly.jieflysbooks.View.View;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiefly on 2016/6/26.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class PresentMain implements OnDataModelListener {
    private List<Book> mBookList = new ArrayList<>();
    private static PresentMain instance = null;
    private AdvanceDataModel mAdvanceDataModel;
    private Service mService;
    private boolean isBound = false;
    public boolean isNeedUpdateBackgrond = true;
    View mView;
    private Context mContext;
    private UpdateBookService.UpdateBookBinder mBookBinder;
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBookBinder = (UpdateBookService.UpdateBookBinder) service;
            Log.e("presentMain", "bind service success ,connected" + "isBound:" + isBound);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("presentMain", "unbind service ,disconnected");
        }
    };

    private PresentMain(Context context, View view) {
        mContext = context;
        mAdvanceDataModel = AdvanceDataModel.build(context, this, OnDataModelListener.TYPE_ACTIVIT_LISTENER);
        mView = view;
        if (mAdvanceDataModel.getBookList() != null)
            mBookList.addAll(mAdvanceDataModel.getBookList());
        if (ApplicationLoader.getIntValue(ApplicationLoader.BOOK_ORDER)==ApplicationLoader.SORT_BY_UPDATE_TIME)
            sortListByUpdateTime(mBookList);
        else {
//            DONothing
        }
        isNeedUpdateBackgrond = ApplicationLoader.getBooleanValue(ApplicationLoader.IS_NEED_UPDATE_BG);
    }

    private void sortListByUpdateTime(List<Book> bookList) {
        if (bookList == null)
            return;
        Collections.sort(bookList, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                if (lhs.getUpdateDate().after(rhs.getUpdateDate()))
                    return -1;
                return 1;
            }
        });
    }

    public List<Book> getBookListOrderByUpdateTime() {
        sortListByUpdateTime(mBookList);
        return mBookList;
    }

    public List<Book> getBookListOrderByAddTime() {
        mBookList = mAdvanceDataModel.getBookList();
        return mBookList;
    }

    public static PresentMain getInstance(Context context, View view) {
        if (instance == null) {
            synchronized (PresentMain.class) {
                if (instance == null) {
                    instance = new PresentMain(context, view);
                }
            }
        }
        return instance;
    }

    //    绑定后台更新service
    public void bindUpdateBookService(Context context) {
        if (!isNeedUpdateBackgrond)
            return;
        Intent intent = new Intent(context, UpdateBookService.class);
        isBound = context.bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
        if (isBound) Log.e("bind", "bind success:" + isBound);
    }

    //    在前台不可见的时候取消绑定
    public void unBindUpdateBookService(Context context) {
        if (isBound) {
            context.unbindService(mServiceConnection);
        }
    }

    //  设置activity前台可见的时候不后台更新
    public void setUpdateFlag(boolean isNeedUpdate) {
        isNeedUpdate = isNeedUpdateBackgrond&&isNeedUpdate;
        if (mBookBinder == null)
            return;
        mBookBinder.setIsNeedUpdate(isNeedUpdate);
    }

    //    显示书籍列表
    public void showBookList() {
        if (mBookList.size() == 0) {
//            初始化书籍列表
            mBookList.addAll(mAdvanceDataModel.getBookList());
        }
        mView.showBooks(mBookList);
    }

    //    增加书籍
    public void addBook(String bookName) throws MalformedURLException {
        if (mBookList != null) {
            for (Book book : mBookList) {
                if (book.getBookName().equals(bookName)) {
                    mView.showSnackbar("小说已存在，请勿重复添加");
                    return;
                }
            }
        }
        mAdvanceDataModel.addBookSyn(bookName);
        Log.e("addBookFromInternet", "正在加载书籍");
        mView.showSnackbar("正在从网络上获取书籍，请稍后");
        /*Book book = mAdvanceDataModel.addBookFromInternet(bookName);
        Log.i("addBookFromInternet", book.toString());
        //            mBookList.add(book);
        mView.addBookFromInternet(book);*/
    }

    //    更新所有书籍
    public void updateBookList() {
        mAdvanceDataModel.updateAllBooks(OnDataModelListener.TYPE_ACTIVIT_LISTENER);
        mBookList = mAdvanceDataModel.getBookList();
    }
/*//    更新书籍的最近读取章节
    public void refreshChapterIndex(String bookName){
        mAdvanceDataModel.updateBookReaderChapterIndex();
    }*/

    //    删除书籍
    public void removeBook(int[] booksIndex) {
//       删除掉Present维护的列表中的书
        String[] booksName = new String[booksIndex.length];
        for (int i = 0; i < booksIndex.length; i++) {
            booksName[i] = mBookList.get(booksIndex[i]).getBookName();
            mView.removeBook(mBookList.get(booksIndex[i]));
            mBookList.remove(booksIndex[i]);
        }
//        删除掉数据库中的书
        mAdvanceDataModel.removeBookSyn(booksName);
    }

    //    阅读书籍
    public void readBook(final int index) throws MalformedURLException {
        if (mBookList.get(index).getBookNewTopicTitle() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Book book = null;
                    try {
                        book = mAdvanceDataModel.getBook(mBookList.get(index).getBookName());
                        book.setChapterList(Utils.list2ChapterList(mAdvanceDataModel.getChapterList(book.getBookName())));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    mView.readBook(book);
                    setUpdateFlag(false);
                }
            }).start();
        }
    }

    Map<Integer, Integer> cacheCountMap = new HashMap<>();

    //    缓存所有章节
    public void cacheAllChapter(final int position) {
//        Log.back_btn_bg("tag",""+position);
        if (mBookList.get(position).isCached()) {
            mView.showSnackbar("该小说已经缓存，请勿重复缓存");
            return;
        }
        cacheCountMap.put(position, 0);
        Observable.just(position)
                .observeOn(Schedulers.io())
                .map(new Func1<Integer, List<String>>() {
                    @Override
                    public List<String> call(Integer integer) {
                        List<String> result = null;
                        try {
                            result = Utils.list2ChapterList(mAdvanceDataModel.getChapterList(mBookList.get(integer).getBookName())).getChapterUrlList();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        return result;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> chaptersUrlList) {
                        final NumberProgressBar numberProgressBar = mView.getViewHolder(position).getNumberProgressBar();
                        numberProgressBar.setMax(chaptersUrlList.size() - 1);
                        numberProgressBar.setProgress(0);
                        if (chaptersUrlList != null) {
                            final List<String> finalChaptersUrlList = chaptersUrlList;
                            mAdvanceDataModel.cacheChapterFromList(chaptersUrlList, new OnChapterCacheListener() {
                                @Override
                                public void onSuccess() {
                                    Observable.just(cacheCountMap.get(position))
                                            .subscribeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Action1<Integer>() {
                                                @Override
                                                public void call(Integer integer) {
                                                    cacheCountMap.put(position, integer + 1);
                                                    numberProgressBar.setProgress(integer + 1);
                                                    if (!numberProgressBar.isShown())
                                                        numberProgressBar.setVisibility(android.view.View.VISIBLE);
                                                }
                                            });
                                    if (cacheCountMap.get(position) >= finalChaptersUrlList.size() - 1) {
                                        mBookList.get(position).setCached(true);
                                        numberProgressBar.setVisibility(android.view.View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFailed(String url) {

                                }
                            });
                        }
                    }
                });

    }

    public void readRecentChapter(Book book) {
    }

    public List<Book> getBookList() {
        mBookList = mAdvanceDataModel.getBookList();
        sortListByUpdateTime(mBookList);
        return mBookList;
    }

    @Override
    public void onBookAddSuccess(Book book) {
        mView.addBook(book);
        Log.e("addBookFromInternet", "加载书籍完毕");
    }

    @Override
    public void onBookAddFailed() {
        mView.showSnackbar("添加书籍失败\n请检查您的网络或者输入的小说名字是否正确");
    }

    private Main.BookListRecycleViewAdapter.ItemViewHolder mItemViewHolder;

    @Override
    public void onBookUpdateSuccess(String bookName, int type) {
        mView.updateBook(bookName);
        if (countUpdate++ >= mBookList.size() - 1) {
            mView.stopRefreshAnim();
            countUpdate = 0;
        }
        Log.e("updateBook", "更新书籍完毕");
    }

    int countUpdate = 0;

    @Override
    public void onBookUpdateFailed() {
        mView.showSnackbar("更新书籍失败\n请检查您的网络");
    }

    @Override
    public void onBookRemoveSuccess() {
        Log.e("removeBook", "删除书籍完毕");
    }

    @Override
    public void onChapterLoadSuccess(Chapter chapter) {

    }

    @Override
    public void onBookUpdateCompleted() {

    }

}