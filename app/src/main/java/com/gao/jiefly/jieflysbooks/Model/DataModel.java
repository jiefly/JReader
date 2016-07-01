package com.gao.jiefly.jieflysbooks.Model;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
interface DataModel {
    //    获取小说的简介。。（Book）
    void getBookSuscribe(String url);

    //    获取小说的章节内容
    String getBookChapter(String url);

    //    获取小说的列表
    List<Chapter> getChapterList(String url) throws MalformedURLException;

    //    获取书籍
    Book getBook(String name);

    //    添加书籍
    void addBook(String name);

    //    获取某一章节
    Chapter getChapter(String bookName, int index);

    //    缓存某一章节
    void addChapter(String bookName, int index);

    //    获取DataModel对象
    DataModel getInstance();
}
