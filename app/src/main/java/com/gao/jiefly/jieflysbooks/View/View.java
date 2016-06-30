package com.gao.jiefly.jieflysbooks.View;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;

import java.util.List;

/**
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface View {
    void showBookList(List<Book> books);
    void showBookRecentTopic(Book book);
    void addBook(Book book);
}
