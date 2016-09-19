package com.gao.jiefly.jieflysbooks.Model.loader;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.BookManager;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnBookListener;

import java.util.List;

/**
 * Created by jiefly on 2016/8/30.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public abstract class BookFactory {
    private String webUrl;
    String searchUrl;
    OnBookListener mListener;

    public abstract BookManager getBookByName(String bookName);

    public abstract Chapter getChapter(Chapter chapter);

    public abstract Chapter getChapter(String url);

    public abstract void updateBook(BookManager bookManager,OnBookListener listener);

    public abstract void getBookByName(String bookName, OnBookListener listener);

    public abstract void downloadAll(String bookName, OnBookListener listener);

    public abstract List<Book> getBooksByAuthor(String author);

    public abstract List<Book> getTop10Books();

    public BookFactory setBookResourceWeb(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public BookFactory setBookAddListener(OnBookListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public BookFactory setBookSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
        return this;
    }
}
