package com.gao.jiefly.jieflysbooks.Model;

import android.content.Context;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnChapterCacheListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataModelListener;
import com.gao.jiefly.jieflysbooks.Model.loader.BookLoader;
import com.gao.jiefly.jieflysbooks.Model.loader.ChapterLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by jiefly on 2016/7/2.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class AdvanceDataModel implements DataModel, OnDataModelListener {
    private static final String TAG = "BaseDataModel";
    static Context mContext;
    private static volatile AdvanceDataModel instance = null;
    private BookLoader mBookLoader;
    private ChapterLoader mChapterLoader;
    private int chapterSize = 0;
    private static OnDataModelListener mOnDataModelListener;
    private static OnDataModelListener mServiceOnDataModelListener;

    private AdvanceDataModel() {
        mBookLoader = new BookLoader(mContext);
        mChapterLoader = ChapterLoader.build(mContext);
    }

    public static AdvanceDataModel build(Context context) {
        if (mContext == null)
            mContext = context;
        if (instance == null)
            synchronized (BaseDataModel.class) {
                if (instance == null) {
                    instance = new AdvanceDataModel();
                }
            }
        return instance;
    }

    public static AdvanceDataModel build(Context context, OnDataModelListener modelListener, int type) {
        if (type == OnDataModelListener.TYPE_ACTIVIT_LISTENER) {
            if (mOnDataModelListener == null)
                mOnDataModelListener = modelListener;
        } else if (type == OnDataModelListener.TYPE_SERVICE_LISTENER) {
            if (mServiceOnDataModelListener == null)
                mServiceOnDataModelListener = modelListener;
        }
        return build(context);
    }

    @Override
    public List<Book> getBookList() {
        return mBookLoader.getBookList();
    }

    @Override
    public Chapter getBookChapterByUrl(String url) throws IOException {
        return mChapterLoader.getChapterLoaderResult(url);
    }

    @Override
    public Chapter getBookChapterByIndex(String bookName, int index) throws IOException {
        return mChapterLoader.getChapterLoaderResult(mBookLoader.getChapterList(bookName).get(index).getUrl());
    }

    @Override
    public List<Chapter> getChapterList(String bookName) throws MalformedURLException {
        return mBookLoader.getChapterList(bookName);
    }

    @Override
    public Book getBook(String name) throws MalformedURLException {
        return mBookLoader.getBook(name);
    }

    @Override
    public Book addBook(String name) {
        Book book = null;
        try {
            book = mBookLoader.addBookFromInternet(name);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return book;
    }

    @Override
    public void addBookSyn(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Book book;
                    book = mBookLoader.addBookFromInternet(name);
                    if (book != null)
                        onBookAddSuccess(book);
                    else
                        onBookAddFailed();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void removeBook(String[] name) {
        mBookLoader.removeBook(name);
    }

    @Override
    public void removeBookSyn(final String[] name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBookLoader.removeBook(name);
                onBookRemoveSuccess();
            }
        }).start();
    }

    @Override
    public void updateBook(Book book) {
        mBookLoader.update(book);
    }

    @Override
    public void updateBookSyn(final Book book, final int type) {
        isUpdateComplete = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mBookLoader.update(book))
                    onBookUpdateSuccess(book.getBookName(), type);
                else {
                    onBookUpdateFailed();
                }
            }
        }).start();
    }

    public void cacheChapterFromList(List<String> urlList, OnChapterCacheListener onChapterCacheListener) {
        mChapterLoader.cacheAllChapter(urlList, onChapterCacheListener);
    }

    public void setBookIsCached(Book book) {
        mBookLoader.setBookIsCached(book);
    }

    private volatile boolean isUpdateComplete = true;
    Thread updateBookThread;
    private boolean isUpdateFailed = false;

    @Override
    public void updateAllBooks(final int type) {
        final List<Book> books = mBookLoader.getOnLineBooks();
        if (books != null)
            updateBookThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis();
                    for (Book b : books) {
                        if (!isUpdateFailed) {
                            while (!isUpdateComplete) {
                                try {
                                    if (isUpdateFailed)
                                        break;
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!isUpdateFailed)
                                updateBookSyn(b, type);
                        }
                    }
                    Log.e("updateTime", System.currentTimeMillis() - time + "ms");
                }
            });
        if (!updateBookThread.isAlive())
            updateBookThread.start();
    }

    @Override
    public void getChapterSyn(final String bookName, final String title, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Chapter chapter = null;
                chapter = getChapter(url);
                chapter.setBookName(bookName);
                chapter.setTitle(title);
                onChapterLoadSuccess(chapter);
            }
        }).start();
    }

    @Override
    public Chapter getChapter(String bookName, int index) {
//        if (chapterSize == 0) {
        try {
            chapterSize = getChapterList(bookName).size();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        }
        Log.e(TAG, "chapter size:" + chapterSize + "><><><><" + index);
        if (index < 0 || index > chapterSize - 1) {
            Chapter chapter = new Chapter("null", "null", bookName);
            chapter.setContent("前面没有更多内容了，客官别翻啦");
            return chapter;
        }
        String url = null;
        try {
            url = mBookLoader.getChapterList(bookName).get(index).getUrl();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getChapter(url);
    }

    private boolean checkDataModelIsInit() {
        return instance != null;
    }

    public List<Book> getOnLineBookList() {
        return mBookLoader.getOnLineBooks();
    }

    public Chapter getChapter(String bookName, int index, String title) {
        Chapter chapter = getChapter(bookName, index);
        if (chapter == null)
            return chapter;
        chapter.setTitle(title);
        chapter.setIndex(index);
        return chapter;
    }

    public void updateBookHasUpdate(String bookName, boolean hasUpdate) {
        mBookLoader.updateBookHasUpdate(bookName, hasUpdate);
    }

    @Override
    public Chapter getChapter(String url) {
        try {
            return mChapterLoader.getChapterLoaderResult(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addChapter(String bookName, int index) {
        try {
            mBookLoader.addBookFromInternet(bookName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addChapter(String bookName, URL url) {

    }

    @Override
    public AdvanceDataModel getInstance() {
        if (instance != null)
            return instance;
        Log.e(TAG, "please build first!!!");
        return null;
    }

    @Override
    public void updateBookReaderChapterIndex(Book book, int index) {
        mBookLoader.refreshReadChapterIndex(book, index);
    }


    @Override
    public void onBookAddSuccess(Book book) {
        mOnDataModelListener.onBookAddSuccess(book);
    }

    @Override
    public void onBookAddFailed() {
        mOnDataModelListener.onBookAddFailed();
    }

    @Override
    public void onBookUpdateSuccess(String bookName, int type) {
        isUpdateComplete = true;
        switch (type) {
            case OnDataModelListener.TYPE_ACTIVIT_LISTENER:
                mOnDataModelListener.onBookUpdateSuccess(bookName, OnDataModelListener.TYPE_ACTIVIT_LISTENER);
                break;
            case OnDataModelListener.TYPE_SERVICE_LISTENER:
                mServiceOnDataModelListener.onBookUpdateSuccess(bookName, OnDataModelListener.TYPE_SERVICE_LISTENER);
                break;
        }
    }

    @Override
    public void onBookUpdateFailed() {
        isUpdateFailed = true;
        mOnDataModelListener.onBookAddFailed();
    }

    @Override
    public void onBookRemoveSuccess() {
        mOnDataModelListener.onBookRemoveSuccess();
    }

    @Override
    public void onChapterLoadSuccess(Chapter chapter) {
        mOnDataModelListener.onChapterLoadSuccess(chapter);
    }

    @Override
    public void onBookUpdateCompleted() {

    }
}