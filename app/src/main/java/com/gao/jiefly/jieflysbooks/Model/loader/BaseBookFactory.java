package com.gao.jiefly.jieflysbooks.Model.loader;

import android.support.annotation.Nullable;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiefly on 2016/8/30.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BaseBookFactory extends BookFactory {
    private static final String TAG = BaseBookFactory.class.getSimpleName();
    private static final String SODU_PREFIX = "http://www.soduso.com";
    private Document mDocument;
    private String bookName;
    int cpuNum = Runtime.getRuntime().availableProcessors();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            cpuNum
            , 10
            , 10000
            , TimeUnit.MILLISECONDS
            , new ArrayBlockingQueue<Runnable>(10));

    @Override
    public Book getBookByName(String bookName) {
        this.bookName = bookName;
        long start = System.currentTimeMillis();
        String text = getBookLastUpdateUrlFromSearch(bookName);
        System.out.printf("\ngetBookLastUpdateUrlFromSearch cost time:" + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
        Set<BookInfo> infos = getBookInfoListFromLastUpdateList(text);
        System.out.printf("\ngetBookInfoListFromLastUpdateList cost time:" + (System.currentTimeMillis() - start) + "ms");
        final Map<String, String[]> contentFeature = new HashMap<>();
        contentFeature.put("id", new String[]{"booktext", "BookText", "content", "contents"});
        contentFeature.put("class", new String[]{"centent"});
        final Map<String, String[]> titleFeature = new HashMap<>();
        titleFeature.put("tag", new String[]{"h1"});
        List<Chapter> chapters = new ArrayList<>();
        start = System.currentTimeMillis();
        if (infos != null) {
            for (final BookInfo info : infos) {
//                直接串行执行花费时间为excutor的两倍左右
                /*Chapter chapter = null;
                chapter = getChapter(info.getBookLastUpdateUrl(),contentFeature,titleFeature);
                if (chapter != null)
                    chapters.add(chapter);*/
                Future future = executor.submit(new Callable<Chapter>() {
                    @Override
                    public Chapter call() throws Exception {
                        return getChapter(info.getBookLastUpdateUrl(), contentFeature, titleFeature);
                    }
                });
                try {
                    if (future.get() != null)
                        chapters.add((Chapter) future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.printf("\ncost time:" + (System.currentTimeMillis() - start) / 1000 + "s");
        System.out.printf("" + chapters.size());
        return null;
    }

    private Set<BookInfo> getBookInfoListFromLastUpdateList(String url) {
        if (url == null || " ".equals(url))
            return null;
        url = SODU_PREFIX + url;
        Set<BookInfo> bookInfos = new HashSet<>();
        try {
            mDocument = Jsoup.connect(url).get();
            Elements results = mDocument.body().children();
            for (Element e : results) {
                if (!"div".equals(e.tagName()))
                    continue;
                if (e.getElementsContainingText(bookName).size() == 0)
                    continue;
                if (e.children() != null && e.children().size() == 3) {
                    BookInfo bookInfo = element2BookInfo(e);
                    if (bookInfo != null)
                        bookInfos.add(bookInfo);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        统一转换bookinfo中的跳转地址


        for (final BookInfo i : bookInfos) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    getBookUrlBySodu(i);
                }
            });
        }
        return bookInfos;
    }


    private Chapter getChapter(String url
            , Map<String, String[]> contentFeature
            , Map<String, String[]> titleFeature) {
        if (url == null || !url.startsWith("http://"))
            return null;
        try {
            Document document = Jsoup.connect(url).get();
            return parserChapterFromDocument(contentFeature, titleFeature, document);
        } catch (HttpStatusException e) {
//            爬虫被网站禁止，需要模拟真实环境
            if (e.getStatusCode() == 403) {
                System.out.printf("\n403 error:" + url);
                System.out.printf("\nusing user-agent to cheat webservice");
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
                    Document document = Jsoup.parse(connection.getInputStream(), connection.getContentEncoding(), url);
                    connection.disconnect();
                    return parserChapterFromDocument(contentFeature, titleFeature, document);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private Chapter parserChapterFromDocument(Map<String, String[]> contentFeature, Map<String, String[]> titleFeature, Document document) {
        String url = document.baseUri();
        Element content = null;
        String[] contentId;
//            这里目前只是用id来获取content内容
        if (contentFeature.containsKey("id") && (contentId = contentFeature.get("id")) != null) {
            for (String id : contentId) {
                content = document.body().getElementById(id);
                if (content != null)
                    break;
            }
        }
        if (content == null) {
            //没有获取到章节内容
//            从class中判断
            if (contentFeature.containsKey("class") && contentFeature.get("class") != null) {
                for (String classname : contentFeature.get("class")) {
                    Elements e = document.body().getElementsByClass(classname);
                    if (e != null && e.size() == 1) {
                        content = e.first();
                        break;
                    }
                }
            } else
                return null;
        }
        if (content == null)
            return null;
        Chapter chapter = new Chapter(url);
        chapter.setContent(content.text());
        String[] titleTag;
        Elements title = null;
//            这里目前只是用title的tag来获取title的内容
        if (titleFeature.containsKey("tag") && (titleTag = titleFeature.get("tag")) != null) {
            for (String tag : titleTag) {
                title = document.body().getElementsByTag(tag);
                if (title != null)
                    break;
            }
        }
        if (title != null && title.size() != 0) {
            chapter.setTitle(title.text());
        }
        return chapter;
    }

    private BookInfo element2BookInfo(Element e) {
        if (e == null)
            return null;
        Elements elements = e.children();
        if (elements.size() != 3)
            return null;
        BookInfo result = new BookInfo();
        Element first = elements.get(0).select("a").first();
        if (first != null && first.hasAttr("href") && first.hasText()) {
            String[] values = first.text().split("_");
            if (values.length == 2 && values[0].equals(bookName)) {
                result.setBookName(values[0]);
                result.setBookLastUpdateTitle(values[1]);
            }/*else {
                Log.e(TAG,"发生错误，搜索到的小说名字不符");
            }*/
            result.setBookLastUpdateUrl(SODU_PREFIX + first.attr("href"));
        } else
            return null;
        Element second = elements.get(1).select("a").first();
        if (second != null && second.hasAttr("href") && second.hasText()) {
            result.setBookSourceWebName(second.text());
            result.setChapterLastUpdateListUrlList(SODU_PREFIX + second.attr("href"));
        } else
            return null;

        Element third = elements.get(2);
        if (third != null && third.hasText()) {
            String time = third.text();
            Date updateTime = string2Date(time);
            result.setBookLastUpdateTime(updateTime);
        } else
            return null;
        return result;
    }

    private String getRedirctsUrl(String url) throws IOException {
        String redirctUrl = null;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(false);
        if (connection.getResponseCode() == 302) {
            redirctUrl = connection.getHeaderField("Location");
        }
        connection.disconnect();
        return redirctUrl;
    }

    private BookInfo getBookUrlBySodu(BookInfo bookInfo) {
        try {
            String redirctUrl = getRedirctsUrl(bookInfo.getBookLastUpdateUrl());
            if (redirctUrl != null && !" ".equals(redirctUrl)) {
                if (!" ".equals(redirctUrl)) {
                    bookInfo.setBookLastUpdateUrl(redirctUrl);
                    String[] s = redirctUrl.split("/");
//                      第三个String为网站的网址
                    bookInfo.setBookSourceWebUrl(s[2]);
                    String bookUrl = redirctUrl.replace(s[s.length - 1], "");
                    bookInfo.setBookUrl(bookUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookInfo;
    }

    private Date string2Date(String time) {
        if (time == null)
            return new Date();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date date = new Date();
        try {
            date = mSimpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public List<Book> getBooksByAuthor(String author) {
        return null;
    }

    @Override
    public List<Book> getTop10Books() {
        return null;
    }

    private String getBookLastUpdateUrlFromSearch(String bookName) {
        try {
            mDocument = Jsoup.connect(searchUrl + UrlEncoder(bookName, "gb2312")).get();
            Elements links = mDocument.select("a[target=_blank]");
            Element target = null;
            for (Element e : links) {
                if (e.children() != null) {
                    Element b = e.children().select("b").first();
                    if (b.text() != null && b.text().equals(bookName)) {
                        target = e;
                        break;
                    }
                }
            }
            if (target != null) {
                return target.hasAttr("href") ? target.attr("href") : "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private URL str2Url(String url) {
        if (url == null || "".equals(url))
            return null;
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //    将文字转码
    public String UrlEncoder(String value, String charsetName) {
        String reslut = null;
        try {
            reslut = URLEncoder.encode(value, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return reslut;
    }

    //    将文字解码
    public String UrlDecoder(String value) {
        String result = null;
        try {
            result = URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    class BookInfo {
        String bookName;
        String bookLastUpdateUrl;
        String bookLastUpdateTitle;
        Date bookLastUpdateTime;
        String bookSourceWebName;
        String bookSourceWebUrl;
        String chapterLastUpdateListUrlList;
        String bookUrl;

        public String getBookUrl() {
            return bookUrl;
        }

        public void setBookUrl(String bookUrl) {
            this.bookUrl = bookUrl;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public String getChapterLastUpdateListUrlList() {
            return chapterLastUpdateListUrlList;
        }

        public void setChapterLastUpdateListUrlList(String chapterLastUpdateListUrlList) {
            this.chapterLastUpdateListUrlList = chapterLastUpdateListUrlList;
        }

        public String getBookLastUpdateUrl() {
            return bookLastUpdateUrl;
        }

        public void setBookLastUpdateUrl(String bookLastUpdateUrl) {
            this.bookLastUpdateUrl = bookLastUpdateUrl;
        }

        public String getBookLastUpdateTitle() {
            return bookLastUpdateTitle;
        }

        public void setBookLastUpdateTitle(String bookLastUpdateTitle) {
            this.bookLastUpdateTitle = bookLastUpdateTitle;
        }

        public Date getBookLastUpdateTime() {
            return bookLastUpdateTime;
        }

        public void setBookLastUpdateTime(Date bookLastUpdateTime) {
            this.bookLastUpdateTime = bookLastUpdateTime;
        }

        public String getBookSourceWebName() {
            return bookSourceWebName;
        }

        public void setBookSourceWebName(String bookSourceWebName) {
            this.bookSourceWebName = bookSourceWebName;
        }

        public String getBookSourceWebUrl() {
            return bookSourceWebUrl;
        }

        public void setBookSourceWebUrl(String bookSourceWebUrl) {
            this.bookSourceWebUrl = bookSourceWebUrl;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (this == obj) return true;
            if (obj instanceof BookInfo) {
                BookInfo bookInfo = (BookInfo) obj;
                if (bookInfo.bookSourceWebName.equals(this.bookSourceWebName))
                    return true;
            }
            return false;
        }

        /**
         * 重写hashcode 方法，返回的hashCode 不一样才认定为不同的对象
         * bookInfo 里只要网站名字相同就认为是同一个对象
         */
        @Override
        public int hashCode() {
            return this.bookSourceWebName.hashCode();
        }
    }

}
