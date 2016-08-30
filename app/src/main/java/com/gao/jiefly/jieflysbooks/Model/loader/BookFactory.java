package com.gao.jiefly.jieflysbooks.Model.loader;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;

import java.util.List;

/**
 * Created by jiefly on 2016/8/30.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public abstract class BookFactory {
    protected String webUrl;
    protected String searchUrl;

    public abstract Book getBookByName(String bookName);

    public abstract List<Book> getBooksByAuthor(String author);

    public abstract List<Book> getTop10Books();

    public BookFactory setBookResourceWeb(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public BookFactory setBookSearchUrl(String searchUrl){
        this.searchUrl = searchUrl;
        return this;
    }
}
