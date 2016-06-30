package com.gao.jiefly.jieflysbooks.Model;

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
}
