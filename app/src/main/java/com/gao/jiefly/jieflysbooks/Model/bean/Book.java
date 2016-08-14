package com.gao.jiefly.jieflysbooks.Model.bean;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Book implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    private String bookName = null;
    private String bookAuthor = null;
    private String bookUrl = null;
    private String bookNewTopicTitle = null;
    private String bookNewTopicUrl = null;
    private String bookLastUpdate = null;
    private String bookUpdateTimeUrl = null;
    private String bookStatu = null;
    private String bookCover = null;
    private String bookResource = null;
    private int bookTotalWords;
    private ChapterList mChapterList = null;
    private int readChapterIndex = 0;
    private boolean isCached = false;
    private boolean isLocal = false;

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    private SimpleDateFormat mSimpleDateFormat;

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public void setHsaUpdateByShort(int hasUpdate) {
        this.hasUpdate = hasUpdate == 0x10;
    }

    public int getHasUpdate() {
        return hasUpdate ? 0x10 : 0x01;
    }

    private boolean hasUpdate = false;


    @Override
    public String toString() {
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
                .append("更新状态：").append(bookStatu).append("\n")
                .append("更新时间地址：").append(bookUpdateTimeUrl).append("\n")
                .append("封面图片：").append(bookCover).append("\n")
                .append("数据来源：").append(bookResource).append("\n")
                .append("小说状态：").append(isLocal ? "本地" : "在线");
        return stringBuilder.toString();
    }

    public int getReadChapterIndex() {
        return readChapterIndex;
    }

    public void setReadChapterIndex(int readChapterIndex) {
        this.readChapterIndex = readChapterIndex;
    }

    public int getBookTotalWords() {
        return bookTotalWords;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookUpdateTimeUrl() {
        return bookUpdateTimeUrl;
    }

    public void setBookUpdateTimeUrl(String bookUpdateTimeUrl) {
        this.bookUpdateTimeUrl = bookUpdateTimeUrl;
    }

    public void setBookTotalWords(int bookTotalWords) {
        this.bookTotalWords = bookTotalWords;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
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

    public String getBookResource() {
        return bookResource;
    }

    public void setBookResource(String bookResource) {
        this.bookResource = bookResource;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    private String bookStyle = null;

    public String getBookName() {
        return bookName;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
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

    public List<Chapter> getList() {
        return chapterList2List(mChapterList);
    }

    public void setChapterList(ChapterList chapterList) {
        mChapterList = chapterList;
    }

    public void setChapterList(List<Chapter> list) {
        mChapterList = list2ChapterList(list);
        for (Chapter chapter:list)
            Log.e("showChapter","title:"+chapter.getTitle()+"\nurl:"+chapter.getUrl());
    }

    public Date getUpdateDate() {
        if (mSimpleDateFormat == null)
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String s = getBookLastUpdate();
        Date date = null;
        try {
            Log.e("name", bookName);
            Log.e("date", s);
            date = mSimpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static class ChapterList implements Serializable {
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

        public ChapterList(String bookName, List<String> chapterUrlList, List<String> chapterTitleList) {
            this.bookName = bookName;
            this.chapterUrlList = chapterUrlList;
            this.chapterTitleList = chapterTitleList;
        }
    }

    private List<Chapter> chapterList2List(Book.ChapterList chapterList) {
        if (chapterList == null)
            return new ArrayList<>();
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
        return new Book.ChapterList(bookName, url, title);
    }
}
