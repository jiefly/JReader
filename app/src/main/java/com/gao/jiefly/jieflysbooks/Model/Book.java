package com.gao.jiefly.jieflysbooks.Model;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Book {
    private String bookName = null;
    private String bookAuthor = null;
    private int bookNewTopic;
    private String bookNewTopicTitle = null;
    private String bookNewTopicUrl = null;

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

    public int getBookNewTopic() {
        return bookNewTopic;
    }

    public void setBookNewTopic(int bookNewTopic) {
        this.bookNewTopic = bookNewTopic;
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
}
