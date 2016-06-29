package com.gao.jiefly.jieflysbooks.Model;

/**
 * Created by jiefly on 2016/6/26.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BookChapter {
    String content;
    String currentTopicName;
    String nextTopicName;
    String prevTopicName;
    String currentTopicUrl;
    String nextTopicUrl;
    String prevTopicUrl;
    public BookChapter(String content, String currentTopicUrl, String nextTopicUrl, String prevTopicUrl){
        this.content = content;
        this.currentTopicUrl = currentTopicUrl;
        this.nextTopicUrl = nextTopicUrl;
        this.prevTopicUrl = prevTopicUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCurrentTopicUrl() {
        return currentTopicUrl;
    }

    public void setCurrentTopicUrl(String currentTopicUrl) {
        this.currentTopicUrl = currentTopicUrl;
    }

    public String getNextTopicUrl() {
        return nextTopicUrl;
    }

    public void setNextTopicUrl(String nextTopicUrl) {
        this.nextTopicUrl = nextTopicUrl;
    }

    public String getPrevTopicUrl() {
        return prevTopicUrl;
    }

    public void setPrevTopicUrl(String prevTopicUrl) {
        this.prevTopicUrl = prevTopicUrl;
    }

    public String getCurrentTopicName() {
        return currentTopicName;
    }

    public void setCurrentTopicName(String currentTopicName) {
        this.currentTopicName = currentTopicName;
    }

    public String getNextTopicName() {
        return nextTopicName;
    }

    public void setNextTopicName(String nextTopicName) {
        this.nextTopicName = nextTopicName;
    }

    public String getPrevTopicName() {
        return prevTopicName;
    }

    public void setPrevTopicName(String prevTopicName) {
        this.prevTopicName = prevTopicName;
    }
}
