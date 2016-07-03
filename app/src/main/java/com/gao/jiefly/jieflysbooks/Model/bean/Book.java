package com.gao.jiefly.jieflysbooks.Model.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Book implements Serializable{
    private static final long serialVersionUID = -7060210544600464481L;
    private String bookName = null;
    private String bookAuthor = null;
    private String bookUrl = null;
    private String bookNewTopicTitle = null;
    private String bookNewTopicUrl = null;
    private String bookLastUpdate = null;
    private String bookStatu = null;
    private int bookTotalWords;
    private ChapterList mChapterList = null;
    @Override

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("类型：").append(bookStyle).append("\n")
                .append("作者：").append(bookAuthor).append("\n")
                .append("书名：").append(bookName).append("\n")
                .append("本书地址：").append(bookUrl).append("\n")
                .append("最新章节：").append(bookNewTopicTitle).append("\n")
                .append("最新章节地址：").append(bookNewTopicUrl).append("\n")
                .append("最后更新时间：").append(bookLastUpdate).append("\n")
                .append("总字数：").append(bookTotalWords).append("\n")
                .append("小说状态：").append(bookStatu).append("\n");
        return stringBuilder.toString();
    }
    public int getBookTotalWords() {
        return bookTotalWords;
    }
    public void setBookTotalWords(int bookTotalWords) {
        this.bookTotalWords = bookTotalWords;
    }
    public String getBookStatu() {
        return bookStatu;
    }
    public void setBookStatu(String bookStatu) {
        this.bookStatu = bookStatu;
    }
    public String getBookLastUpdate() {
        return bookLastUpdate;
    }
    public void setBookLastUpdate(String bookLastUpdate) {
        this.bookLastUpdate = bookLastUpdate;
    }
    public String getBookStyle() {
        return bookStyle;
    }

    public void setBookStyle(String bookStyle) {
        this.bookStyle = bookStyle;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    private String bookStyle = null;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookNewTopicTitle() {
        return bookNewTopicTitle;
    }

    public void setBookNewTopicTitle(String bookNewTopicTitle) {
        this.bookNewTopicTitle = bookNewTopicTitle;
    }

    public String getBookNewTopicUrl() {
        return bookNewTopicUrl;
    }

    public void setBookNewTopicUrl(String bookNewTopicUrl) {
        this.bookNewTopicUrl = bookNewTopicUrl;
    }

    public ChapterList getChapterList() {
        return mChapterList;
    }
    public List<Chapter> getList(){
        return chapterList2List(mChapterList);
    }
    public void setChapterList(ChapterList chapterList) {
        mChapterList = chapterList;
    }

    public static class ChapterList {
        private String bookName;
        private List<String> chapterTitleList;
        private List<String> chapterUrlList;

        public String getBookName() {
            return bookName;
        }

        public List<String> getChapterTitleList() {
            return chapterTitleList;
        }

        public List<String> getChapterUrlList() {
            return chapterUrlList;
        }

        public ChapterList(String bookName, List<String> chapterUrlList , List<String> chapterTitleList) {
            this.bookName = bookName;
            this.chapterUrlList = chapterUrlList;
            this.chapterTitleList = chapterTitleList;
        }
    }

    private List<Chapter> chapterList2List(Book.ChapterList chapterList) {
        List<Chapter> chapters = new LinkedList<>();
        List<String> urlList = chapterList.getChapterUrlList();
        List<String> titleList = chapterList.getChapterTitleList();

        for (int i = 0; i < chapterList.getChapterUrlList().size(); i++) {
            chapters.add(new Chapter(urlList.get(i), titleList.get(i), chapterList.getBookName()));
        }
        return chapters;
    }

    private Book.ChapterList list2ChapterList(List<Chapter> chapters) {
        List<String> url = new LinkedList<>();
        List<String> title = new LinkedList<>();
        for (Chapter c : chapters) {
            url.add(c.getUrl());
            title.add(c.getTitle());
        }
        return new Book.ChapterList(chapters.get(0).getBookName(), url, title);
    }
}
