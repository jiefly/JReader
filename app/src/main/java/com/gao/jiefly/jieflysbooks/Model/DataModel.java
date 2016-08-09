package com.gao.jiefly.jieflysbooks.Model;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
interface DataModel {
    //    获取数据库中的所有小说
    List<Book> getBookList();

    //    获取小说的章节内容
    Chapter getBookChapterByUrl(String url) throws IOException;

    Chapter getBookChapterByIndex(String bookName, int index) throws IOException;

    //    获取小说的列表
    List<Chapter> getChapterList(String url) throws MalformedURLException;

    //    获取书籍
    Book getBook(String name) throws MalformedURLException;

    //    添加书籍 (同步)
    Book addBook(String name);

    //    添加书籍（异步）
    void addBookSyn(String name);

    //    删除书籍
    void removeBook(String[] name);

    //    删除书籍（异步）
    void removeBookSyn(String[] name);

    //    更新书籍
    void updateBook(Book book);
//    更新书籍（异步）
    void updateBookSyn(Book book,int type);

    //    更新所有书籍
    void updateAllBooks(int type);

    //    获取某一章节
    Chapter getChapter(String bookName, int index);

    Chapter getChapter(String url);

    void getChapterSyn(String bookName , String title , String url);

    //    缓存某一章节
    void addChapter(String bookName, int index);

    void addChapter(String bookName, URL url);

    //    获取DataModel对象
    DataModel getInstance();

    //    更新小说的当前读书进度
    void updateBookReaderChapterIndex(Book book, int index);
}
