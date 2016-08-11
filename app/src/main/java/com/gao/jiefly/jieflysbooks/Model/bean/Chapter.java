package com.gao.jiefly.jieflysbooks.Model.bean;

/**
 * Created by jiefly on 2016/6/28.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Chapter {
    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
    private String bookName;
    private int index;
    private boolean isLocal = false;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Chapter(String url, String title, String bookName) {
        this.url = url;
        this.title = title;
        this.bookName = bookName;

    }
    public Chapter (String title,String bookName,int index,String url){
        this.title = title;
        this.bookName = bookName;
        this.index = index;
        isLocal = true;
        this.url = url;
    }
    public Chapter(String url){
        this.url = url;
    }
}
