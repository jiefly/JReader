package com.gao.jiefly.jieflysbooks.Model.loader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiefly on 2016/9/1.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BookFactoryConfiguration {
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String CHAPTER_LIST = "chapterList";
    public static final String CHAPTER = "chapter";
    public static final String TYPE = "type";
    public static final String STATU = "statu";
    public static final String SUBSCRICE = "subscribe";
    public static final String NEW_CHAPTER = "newChapter";
    public static final String WEB_NAME = "webName";
    public static final String WEB_URL = "webUrl";

    Map<String, String> webName;
    Map<String, String> title;
    Map<String, String> author;
    Map<String, String> chapterList;
    Map<String, String> chapter;
    Map<String, String> type;
    Map<String, String> statu;
    Map<String, String> subscribe;
    Map<String, String> newChapter;
    Map<String, String> webUrl;
    static Map<String, Map<String, String>> config;

    public BookFactoryConfiguration() {
        config = new HashMap<>();
        title = new HashMap<>();
        title.put("tag","h1");
        webUrl = new HashMap<>();
        author = new HashMap<>();
        chapterList = new HashMap<>();
        chapter = new HashMap<>();
        type = new HashMap<>();
        statu = new HashMap<>();
        subscribe = new HashMap<>();
        newChapter = new HashMap<>();
        webName = new HashMap<>();
    }

    public BookFactoryConfiguration setWebName(String webName) {
        this.webName.put(WEB_NAME, webName);
        return this;
    }

    public BookFactoryConfiguration setWebUrl(String webUrl) {
        this.webUrl.put(WEB_URL, webUrl);
        return this;
    }

    public Map<String, Map<String, String>> getConfig() {
        if (webName.isEmpty())
            return null;
        if (title != null)
            config.put(TITLE, title);
        if (author != null)
            config.put(AUTHOR, author);
        if (chapterList != null)
            config.put(CHAPTER_LIST, chapterList);
        if (chapter != null)
            config.put(CHAPTER, chapter);
        if (type != null)
            config.put(TYPE, type);
        if (statu != null)
            config.put(STATU, statu);
        if (subscribe != null)
            config.put(SUBSCRICE, subscribe);
        if (newChapter != null)
            config.put(NEW_CHAPTER, newChapter);
        return config;
    }


    public BookFactoryConfiguration setTitle(String key, String value) {
        title.put(key, value);
        return this;
    }

    public String getWebUrl() {
        if (webUrl.isEmpty())
            return null;
        return webUrl.get(WEB_URL);
    }

    public BookFactoryConfiguration setSuscribe(String key, String value) {
        title.put(key, value);
        return this;
    }

    public BookFactoryConfiguration setAuthor(String key, String value) {
        author.put(key, value);
        return this;
    }

    public BookFactoryConfiguration setChapterList(String key, String value) {
        chapterList.put(key, value);
        return this;
    }

    public BookFactoryConfiguration setChapter(String key, String value) {
        chapter.put(key, value);
        return this;
    }

    public BookFactoryConfiguration setType(String key, String value) {
        type.put(key, value);
        return this;
    }

    public BookFactoryConfiguration setStatu(String key, String value) {
        statu.put(key, value);
        return this;
    }

    public BookFactoryConfiguration setNewChapter(String key, String value) {
        newChapter.put(key, value);
        return this;
    }

    public Map<String, String> getWebName() {
        return webName;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public Map<String, String> getAuthor() {
        return author;
    }

    public Map<String, String> getChapterList() {
        return chapterList;
    }

    public Map<String, String> getChapter() {
        return chapter;
    }

    public Map<String, String> getType() {
        return type;
    }

    public Map<String, String> getStatu() {
        return statu;
    }

    public Map<String, String> getSubscribe() {
        return subscribe;
    }

    public Map<String, String> getNewChapter() {
        return newChapter;
    }
}
