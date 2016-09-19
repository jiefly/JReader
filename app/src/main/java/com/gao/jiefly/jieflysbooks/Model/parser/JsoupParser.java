package com.gao.jiefly.jieflysbooks.Model.parser;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.loader.BaseBookFactory;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jiefly on 2016/9/6.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class JsoupParser extends Parser {
    @Override
    String getBookCoverUrl(Document document) {
        return null;
    }

    @Override
    BaseBookFactory.BookInfo getBookInfo(Document document) {
        return null;
    }

    @Override
    List<Chapter> getBookChapterList(Document d) {
        Map<String, String> chapterFeature = mConfiguration.getChapterList();
        if (d == null || chapterFeature == null)
            return null;
//        判断id
        List<Chapter> chapterList = new ArrayList<>();
        int currentcount = 0;
        for (String key : chapterFeature.keySet()) {
            switch (key) {
                case "id":
                    Element e = d.body().getElementById(chapterFeature.get(key));
                    if (e == null)
                        continue;
                    Elements chapters = e.select("a[href]");
                    if (chapters == null)
                        continue;
                    for (Element chapter : chapters) {
                        Chapter c = new Chapter(chapter.baseUri() + chapter.attr("href"));
                        c.setTitle(chapter.text());
                        c.setIndex(currentcount++);
                        chapterList.add(c);
                    }
                    return chapterList;
                case "class":
                    Elements elements = d.body().getElementsByClass(chapterFeature.get(key));
                    if (elements == null)
                        continue;
                    for (Element mabyChapter : elements) {
                        chapters = mabyChapter.select("a[href]");
                        if (chapters == null)
                            continue;
                        for (Element chapter : chapters) {
                            Chapter c = new Chapter(chapter.baseUri() + chapter.attr("href"));
                            c.setTitle(chapter.text());
                            c.setIndex(currentcount++);
                            chapterList.add(c);
                        }
                        return chapterList;
                    }
                    break;
                case "tag":
                    Elements results = d.body().select("a[href]");
                    if (results == null)
                        return null;
                    for (Element chapter : results) {
                        Chapter c = new Chapter(chapter.baseUri() + chapter.attr("href"));
                        c.setTitle(chapter.text());
                        c.setIndex(currentcount++);
                        chapterList.add(c);
                    }
                    return chapterList;
            }
        }
        return null;
    }

    @Override
    Chapter getChapter(Document document, Chapter src) {
        Element tmp = null;
        Map<String, String> chapterFeature = mConfiguration.getChapter();
//        get content
        for (String key : chapterFeature.keySet()) {
//            parser chapter when src's is null or ""
            if (src.getContent() == null || src.getContent().equals(""))
                switch (key) {
                    case "id":
                        tmp = document.body().getElementById(chapterFeature.get(key));
                        break;
                    case "class":
                        Elements elements = document.body().getElementsByClass(chapterFeature.get(key));
                        if (elements != null && elements.size() == 1)
                            tmp = elements.first();
                        break;

                }
            if (tmp != null)
                src.setContent(tmp.text());
        }
//        get title
        tmp = null;
        Map<String, String> titleFeature = mConfiguration.getTitle();
        for (String key : titleFeature.keySet()) {
            if (src.getTitle() != null && !src.getTitle().equals(""))
                break;
            switch (key) {
                case "id":
                    tmp = document.body().getElementById(chapterFeature.get(key));
                    break;
                case "class":
                    Elements elements = document.body().getElementsByClass(chapterFeature.get(key));
                    if (elements != null && elements.size() == 1)
                        tmp = elements.first();
                    break;
                case "tag":
                    elements = document.body().getElementsByTag(chapterFeature.get(key));
                    if (elements != null && !elements.isEmpty())
                        tmp = elements.first();
                    break;
            }
        }
        if (tmp != null)
            src.setTitle(tmp.text());
        return src;
    }
}
