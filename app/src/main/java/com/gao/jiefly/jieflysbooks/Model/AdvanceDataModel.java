package com.gao.jiefly.jieflysbooks.Model;

import android.content.Context;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
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
public class AdvanceDataModel implements DataModel {
    private static final String TAG = "BaseDataModel";
    static Context mContext;
    private static volatile AdvanceDataModel instance = null;
    private BookLoader mBookLoader;
    private ChapterLoader mChapterLoader;

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
    public List<Chapter> getChapterList(String url) throws MalformedURLException {
        String bookName = mBookLoader.getBook(url).getBookName();
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
    public void removeBook(String[] name) {
        mBookLoader.removeBook(name);
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
    public void updateAllBooks() {
        List<Book> books = getBookList();
        for (Book b:books){
            updateBook(b);
        }
    }

    @Override
    public Chapter getChapter(String bookName, int index) {
        URL url = null;
        try {
            url = new URL(mBookLoader.getChapterList(bookName).get(index).getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getChapter(url);
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

    private boolean checkDataModelIsInit() {
        return instance != null;
    }
}
