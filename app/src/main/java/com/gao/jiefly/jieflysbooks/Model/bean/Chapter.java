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
    private String name;
    private int index;

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

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public Chapter(String url, String name, int index) {
        this.url = url;
        this.name = name;
        this.index = index;
    }
    public Chapter(String url){
        this.url = url;
    }
}