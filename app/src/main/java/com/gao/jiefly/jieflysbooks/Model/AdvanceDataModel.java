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

    public static AdvanceDataModel build(Context context, OnDataModelListener modelListener) {
        if (mOnDataModelListener == null)
            mOnDataModelListener = modelListener;
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
            book = mBookLoader.addBook(name);
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
                    book = mBookLoader.addBook(name);
                    onBookAddSuccess(book);
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
        try {
            mBookLoader.update(book);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBookSyn(final Book book) {
        isUpdateComplete = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBookLoader.update(book);
                    onBookUpdataSuccess(book.getBookName());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void cacheChapterFromList(List<String> urlList, OnChapterCacheListener onChapterCacheListener) {
        mChapterLoader.cacheAllChapter(urlList, onChapterCacheListener);
    }

    private volatile boolean isUpdateComplete = true;

    @Override
    public void updateAllBooks() {
        final List<Book> books = getBookList();
        if (books != null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Book b : books) {
                        while (!isUpdateComplete) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        updateBookSyn(b);
                    }
                }
            }).start();
    }

    @Override
    public void getChapterSyn(final String bookName, final String title, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Chapter chapter = null;
                try {
                    chapter = getChapter(new URL(url));
                    chapter.setBookName(bookName);
                    chapter.setTitle(title);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
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
        URL url = null;
        try {
            url = new URL(mBookLoader.getChapterList(bookName).get(index).getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getChapter(url);
    }

    public Chapter getChapter(String bookName, int index, String title) {
        Chapter chapter = getChapter(bookName, index);
        chapter.setTitle(title);
        return chapter;
    }


    @Override
    public Chapter getChapter(URL url) {
        try {
            return mChapterLoader.getChapterLoaderResult(url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addChapter(String bookName, int index) {
        try {
            mBookLoader.addBook(bookName);
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

    private boolean checkDataModelIsInit() {
        return instance != null;
    }

    @Override
    public void onBookAddSuccess(Book book) {
        mOnDataModelListener.onBookAddSuccess(book);
    }

    @Override
    public void onBookUpdataSuccess(String bookName) {
        isUpdateComplete = true;
        mOnDataModelListener.onBookUpdataSuccess(bookName);
    }

    @Override
    public void onBookRemoveSuccess() {
        mOnDataModelListener.onBookRemoveSuccess();
    }

    @Override
    public void onChapterLoadSuccess(Chapter chapter) {
        mOnDataModelListener.onChapterLoadSuccess(chapter);
    }
}
