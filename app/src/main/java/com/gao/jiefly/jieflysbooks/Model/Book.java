package com.gao.jiefly.jieflysbooks.Model;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Book {
    private String bookName = null;
    private String bookAuthor = null;
    private String bookNewTopic;
    private String bookUrl = null;
    private String bookNewTopicTitle = null;
    private String bookNewTopicUrl = null;
    private String bookLastUpdate = null;
    private String bookStatu = null;

    public int getBookTotalWords() {
        return bookTotalWords;
    }

    public void setBookTotalWords(int bookTotalWords) {
        this.bookTotalWords = bookTotalWords;
    }

    private int bookTotalWords;

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

    public String getBookNewTopic() {
        return bookNewTopic;
    }

    public void setBookNewTopic(String bookNewTopic) {
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

    class BookTopic{
        String content;
        String currentTopicUrl;
        String nextTopicUrl;
        String prevTopicUrl;
    }
}
