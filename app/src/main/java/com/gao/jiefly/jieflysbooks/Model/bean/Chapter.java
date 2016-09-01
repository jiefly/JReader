package com.gao.jiefly.jieflysbooks.Model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiefly on 2016/6/28.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Chapter implements Parcelable{
    public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>(){

        @Override
        public Chapter createFromParcel(Parcel source) {
            return new Chapter(source);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };
    private String url;
    private String title;
    private String bookName;
    private int index;
    private boolean isLocal = false;

    public Chapter(Parcel source) {

    }

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

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Chapter(String title, String bookName, int index, String url) {
        this.title = title;
        this.bookName = bookName;
        this.index = index;
        isLocal = true;
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof Chapter) {
            Chapter chapter = (Chapter) obj;
            if (chapter.url.equals(this.url) && chapter.title.equals(this.title))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return url.hashCode()*title.hashCode();
    }

    public Chapter(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
