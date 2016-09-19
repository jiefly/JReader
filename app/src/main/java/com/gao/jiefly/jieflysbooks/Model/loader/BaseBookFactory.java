package com.gao.jiefly.jieflysbooks.Model.loader;

import android.support.annotation.Nullable;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.BookManager;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnBookAddListener;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiefly on 2016/8/30.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BaseBookFactory extends BookFactory {
    private static final String TAG = BaseBookFactory.class.getSimpleName();
    private static final String SODU_PREFIX = "http://www.soduso.com";
    private Set<String> resourceIgnore = new HashSet<>();
    private Set<String> webNoCoverList = new HashSet<>();
    private final int cpuNum = 10;
    final Map<String, String[]> contentFeature = new HashMap<>();
    final Map<String, String[]> titleFeature = new HashMap<>();
    final Map<String, String[]> chapterFeature = new HashMap<>();
    private Document mDocument;
    private String bookName;
    private BookManager mBookManager;
    Map<String, BookFactoryConfiguration> configManager = new HashMap<>();
//    下载线程池，下载一个网页花费6s左右
    private ExecutorService downloaderService = Executors.newFixedThreadPool(cpuNum);
//    解析线程池，解析花费1s左右
    private ExecutorService parserService = Executors.newFixedThreadPool(cpuNum/5);

    public BaseBookFactory() {
        initConfig();

        webNoCoverList.add("http://www.shuqi6.com/");
        webNoCoverList.add("http://www.baoliny.com/");
        webNoCoverList.add("http://www.shuqu8.com/");
        resourceIgnore.add("起点中文网");
//        获取内容的定位点
        contentFeature.put("id", new String[]{"booktext", "BookText", "content", "contents"});
        contentFeature.put("class", new String[]{"centent"});
//        获取标题的定位点
        titleFeature.put("tag", new String[]{"h1"});
//        获取chapterlist的定位点
        chapterFeature.put("id", new String[]{"list", "xiaoshuolist", "xslist"});
        chapterFeature.put("class", new String[]{"chapterlist"});
    }

    private void initConfig() {
        configManager.put("爱上中文", new BookFactoryConfiguration()
                .setWebName("爱上中文")
                .setWebUrl("http://www.aszw520.com")
                .setAuthor("tag", "i")
                .setAuthor("text", "作者")
                .setStatu("tag", "i")
                .setStatu("text", "作品进度")
                .setType("tag", "i")
                .setType("text", "类别")
                .setNewChapter("class", "fl")
                .setNewChapter("text", "最新章节")
                .setChapterList("id", "at")
                .setChapter("id", "contents"));
        configManager.put("VIVI小说网", new BookFactoryConfiguration()
                .setWebName("VIVI小说网")
                .setWebUrl("http://www.zkvivi.com")
                .setChapterList("class", "main")
                .setChapter("class", "centent"));
        configManager.put("少年文学", new BookFactoryConfiguration()
                .setWebName("少年文学").setWebUrl("http://www.snwx.com")
                .setAuthor("tag", "i")
                .setAuthor("text", "作者")
                .setStatu("tag", "i")
                .setStatu("text", "状态")
                .setType("tag", "i")
                .setType("text", "类别")
                .setNewChapter("class", "bookurl")
                .setNewChapter("tag", "a")
                .setSuscribe("class", "intro")
                .setChapterList("id", "list")
                .setChapter("id", "BookText"));
        configManager.put("无弹窗小说网", new BookFactoryConfiguration()
                .setWebName("无弹窗小说网").setWebUrl("http://www.baoliny.com")
                .setChapterList("class", "readerListShow")
                .setChapter("id", "content"));
        configManager.put("七度书屋", new BookFactoryConfiguration()
                .setWebName("七度书屋").setWebUrl("http://www.7dsw.com")
                .setAuthor("tag", "i")
                .setAuthor("text", "作者")
                .setStatu("tag", "i")
                .setStatu("text", "状态")
                .setNewChapter("class", "bookurl")
                .setNewChapter("tag", "p")
                .setChapterList("id", "list")
                .setChapter("id", "BookText"));
        configManager.put("大海中文", new BookFactoryConfiguration()
                .setWebName("大海中文").setWebUrl("http://www.dhzw.com")
                .setAuthor("tag", "i")
                .setAuthor("text", "作者")
                .setStatu("tag", "i")
                .setStatu("text", "状态")
                .setType("tag", "i")
                .setType("text", "类别")
                .setNewChapter("class", "bookurl")
                .setNewChapter("tag", "p")
                .setSuscribe("class", "intro")
                .setChapterList("id", "list")
                .setChapter("id", "BookText"));
        configManager.put("书旗小说", new BookFactoryConfiguration()
                .setWebName("书旗小说").setWebUrl("http://www.shuqi6.com")
                .setAuthor("tag", "b")
                .setAuthor("text", "作者")
                .setStatu("tag", "b")
                .setStatu("text", "本书状态")
                .setType("tag", "i")
                .setType("text", "类别")
                .setChapterList("id", "xslist")
                .setChapter("id", "booktext"));
        configManager.put("手牵手小说网", new BookFactoryConfiguration()
                .setWebName("手牵手小说网").setWebUrl("http://www.sqsxs.com")
                .setNewChapter("id", "info")
                .setAuthor("tag", "p")
                .setAuthor("text", "作者")
                .setNewChapter("id", "info")
                .setStatu("tag", "p")
                .setStatu("text", "状态")
                .setNewChapter("id", "info")
                .setNewChapter("tag", "p")
                .setNewChapter("text", "最新章节")
                .setSuscribe("class", "intro")
                .setChapterList("id", "list")
                .setChapter("id", "content"));
        configManager.put("木鱼哥", new BookFactoryConfiguration()
                .setWebName("木鱼哥").setWebUrl("http://www.muyuge.com")
                .setChapterList("id", "xslist")
                .setChapter("id", "content"));

        configManager.put("奇书网", new BookFactoryConfiguration()
                .setWebName("奇书网").setWebUrl("http://www.55xs.com")
                .setChapterList("class", "list")
                .setChapter("id", "contents"));

    }

    @Override
    public void getBookByName(final String bookName, OnBookAddListener listener) {
        if (listener == null && mListener == null)
            return;
        if (listener != null)
            mListener = listener;
        if (mBookManager == null)
            mBookManager = new BookManager();
        this.bookName = bookName;
        mBookManager.setName(bookName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String updateUrl = getBookLastUpdateUrlFromSearch(bookName);
                getBookInfoListFromLastUpdateList(updateUrl, mListener);
            }
        }).start();
    }

    @Override
    public BookManager getBookByName(String bookName) {
        this.bookName = bookName;
        if (mBookManager == null)
            mBookManager = new BookManager();
        mBookManager.setName(bookName);
        String text = getBookLastUpdateUrlFromSearch(bookName);
        Set<BookInfo> infos = getBookInfoListFromLastUpdateList(text);
        if (infos == null)
            return null;
        for (BookInfo info : infos) {
            mBookManager.setLastUpdateDate(info.bookLastUpdateTime);
            mBookManager.addCover(info.bookCover);
            mBookManager.addResourse(info.bookSourceWebName, info.bookUrl);
        }
        return mBookManager;
    }

    public BookManager downLoadAll(BookManager src) {
        if (src == null)
            return null;
        ExecutorService service = Executors.newFixedThreadPool(10);
        List<Chapter> oldChapter = src.getChapters();
//        通过chapter的index作为键值存储chapter，之后便于对下载好的chapter重新排序
//        hashMap 不是线程安全的，currentHashMap才是
        final Map<Integer, Chapter> newChapter = new ConcurrentHashMap<>();
        for (final Chapter chapter : oldChapter) {
            if ((chapter.getContent() == null || chapter.getContent().isEmpty()) && chapter.getUrl() != null) {
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        long time = System.currentTimeMillis();
                        Chapter c = getChapterDirect(chapter.getUrl(), chapter.getIndex(), configManager);
                        Logger.i(c.getTitle(), System.currentTimeMillis() - time + "ms");
                        newChapter.put(c.getIndex(), c);
                    }
                });
            } else {
                newChapter.put(chapter.getIndex(), chapter);
            }
        }
        service.shutdown();
        try {
            service.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int chapterCount = oldChapter.size();
        oldChapter.clear();
        for (int i = 0; i < chapterCount; i++) {
            if (newChapter.containsKey(i) && newChapter.get(i) != null) {
                oldChapter.add(newChapter.get(i));
            }
        }
        mBookManager.setChapters(oldChapter);
        return mBookManager;
    }

    /*
    * 测试url地址能否到达
    * */
    private boolean urlCanArrieve(String url) {
        int status = 404;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection oc = (HttpURLConnection) urlObj.openConnection();
            oc.setUseCaches(false);
            oc.setConnectTimeout(500); // 设置超时时间
            status = oc.getResponseCode();// 请求状态
            if (200 == status) {
                // 200是请求地址顺利连通。。
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private String findBookCover(Document document, String webUrl) {
        if (document == null)
            return null;
        for (String useless : webNoCoverList) {
            if (document.baseUri() != null && document.baseUri().contains(useless))
                return null;
        }
        Elements elements = document.select("img[alt=" + bookName + "]");
        if (elements == null) {
            webNoCoverList.add(document.baseUri());
            return null;
        }
        if (elements.first() == null)
            return null;
        String cover = elements.first().attr("src");
        if (cover == null)
            return null;
        if (cover.startsWith(webUrl))
            return cover;
        return webUrl + cover;
    }

    private Document getDocumentByUrl(String url) {
        if (url == null || !url.startsWith("http://www")) {
            return null;
        }
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 403) {
                try {
                    document = getDocumentWhen403(url);
                    return document;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.out.printf("解决403之后还是获取网页失败");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    private void getBookInfoListFromLastUpdateList(String url, final OnBookAddListener listener) {
        if (url == null || " ".equals(url)) {
            listener.onBookAddFailed(new Exception("url error"));
            return;
        }
        ExecutorService executor = Executors.newFixedThreadPool(cpuNum);
        final Set<BookInfo> infos = new HashSet<>();
        url = SODU_PREFIX + url;
        mDocument = getDocumentByUrl(url);
        if (mDocument != null) {
            Elements results = mDocument.body().children();
            for (Element e : results) {
                final BookInfo bookInfo = element2BookInfo(e, bookName);
                if (bookInfo != null && !resourceIgnore.contains(bookInfo.getBookSourceWebName()))
                    infos.add(bookInfo);
            }
        }
        if (infos.isEmpty()) {
            listener.onBookAddFailed(new Exception("sodu小说最近更新页面为获取到相关数据"));
            return;
        }
        for (BookInfo info : infos) {
            final BookInfo finalInfo = info;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    BookInfo info = getBookUrlBySodu(finalInfo);
                    Document bookDocument = getDocumentByUrl(info.getBookUrl());
//                    如果有该网站的匹配配置文件则用该网站的匹配配置文件
//                    否则用通配文件
                    String cover = findBookCover(bookDocument, info.getBookSourceWebUrl());
                    synchronized (BookManager.class) {
                        if (mBookManager.getChapters().isEmpty())
                            setChapterList(bookDocument, info);
                        mBookManager.addResourse(info.bookSourceWebName, info.bookUrl);
                        mBookManager.addCover(cover);
                        mBookManager.setLastUpdateDate(info.bookLastUpdateTime);
                        if (mBookManager.getResourse().size() == 1) {
                            listener.onBookBaseInfoGetSuccess(mBookManager);
                        }

                        if (mBookManager.getResourse().size() == infos.size()) {
                            listener.onBookCompleteInfoGetSuccess(mBookManager);
                            mBookManager = downLoadAll(mBookManager);
                        }
                    }


                }
            });
        }
        executor.shutdown();
    }

    private void setChapterList(Document bookDocument, BookInfo info) {
        List<Chapter> chapters;
        if (configManager.containsKey(info.bookSourceWebName)) {
            chapters = getChapterListDirect(bookDocument
                    , configManager.get(info.bookSourceWebName).getConfig().get(BookFactoryConfiguration.CHAPTER_LIST));

        } else {
            chapters = getChapterList(bookDocument, chapterFeature);
        }
        if (chapters != null && !chapters.isEmpty())
            mBookManager.setChapters(chapters);
    }

    private Set<BookInfo> getBookInfoListFromLastUpdateList(String url) {
        if (url == null || " ".equals(url))
            return null;
        url = SODU_PREFIX + url;
        final Set<BookInfo> bookInfos = new HashSet<>();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            mDocument = Jsoup.connect(url).get();
            Elements results = mDocument.body().children();
            for (Element e : results) {
                final BookInfo bookInfo = element2BookInfo(e, bookName);
                if (bookInfo == null)
                    continue;
                if (resourceIgnore.contains(bookInfo.bookSourceWebName))
                    continue;

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        BookInfo info = getBookUrlBySodu(bookInfo);
                        synchronized (BookInfo.class) {
                            if (!bookInfos.contains(info)) {
                                Document bookDocument = getDocumentByUrl(info.getBookUrl());
                                List<Chapter> c = getChapterList(bookDocument, chapterFeature);
                                String cover = findBookCover(bookDocument, info.getBookSourceWebUrl());
                                if (urlCanArrieve(cover)) {
                                    info.setBookCover(cover);
                                }
                                bookInfos.add(info);
                            }
                        }
                    }
                });

            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return bookInfos;
    }

    private BookInfo element2BookInfo(Element e, String bookName) {
//        <div class="main-html">
//        <div style="width:560px;float:left;"><a href="http://www.sodu.cc/gourl.html?id=64588334&t=2e0c578ee8da4dfb6808607bd1318d2c&chapterurl=http://www.7dsw.com/book/2/2295/12388455.html" alt="第1438章 甲子之约" onclick="getpage(this)" target="_blank">造化之王 第1438章 甲子之约</a></div>
//        <div style="width:108px;float:left;"><a href="http://www.sodu.cc/newmulu_204551_9.html" class="tl">7度书屋</a></div>
//        <div style="width:88px;float:left;" class="xt1">2016-8-30 22:35:00</div>
//        </div>
        if (e == null || !"div".equals(e.tagName()) || bookName == null)
            return null;
        if (e.getElementsContainingText(bookName).size() == 0)
            return null;
        if (e.children() == null || e.children().size() != 3)
            return null;
        Elements elements = e.children();
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

    private Elements getConnectionATag(Element e) {
        return e.select("a[href]");
    }

    private List<Chapter> getChapterListDirect(Document d, Map<String, String> chapterFeature) {
        if (chapterFeature == null)
            return null;
        Map<String, String[]> result = mapString2StringArry(chapterFeature);
        return getChapterList(d, result);
    }

    private Map<String, String[]> mapString2StringArry(Map<String, String> src) {
        if (src == null)
            return null;
        Map<String, String[]> dst = new HashMap<>();
        for (Map.Entry<String, String> entry : src.entrySet()) {
            dst.put(entry.getKey(), new String[]{entry.getValue()});
        }
        return dst;
    }

    private List<Chapter> getChapterList(Document d, Map<String, String[]> chapterFeature) {
        if (d == null || chapterFeature == null)
            return null;
//        判断id
        List<Chapter> chapterList = new ArrayList<>();
        int currentcount = 0;
        if (chapterFeature.containsKey("id") && chapterFeature.get("id") != null) {
            for (String id : chapterFeature.get("id")) {
                Element e = d.body().getElementById(id);
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
            }
        }
//        判断class
        if (chapterFeature.containsKey("class") && chapterFeature.get("class") != null) {
            for (String className : chapterFeature.get("class")) {
                Elements e = d.body().getElementsByClass(className);
                if (e == null)
                    continue;
                for (Element mabyChapter : e) {
                    Elements chapters = mabyChapter.select("a[href]");
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
            }
        }
//        直接判断<a>
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

    private Chapter getChapterDirect(String url, int index, Map<String, BookFactoryConfiguration> config) {
        if (config == null)
            return null;
        for (Map.Entry<String, BookFactoryConfiguration> entry : config.entrySet()) {
            if (entry == null)
                continue;
            String webUrl = entry.getValue().getWebUrl();
            if (webUrl != null && url.contains(webUrl)) {
                return getChapter(url, index, mapString2StringArry(entry.getValue().getChapter()), mapString2StringArry(entry.getValue().getTitle()));
            }
        }
        return null;
    }

    private Chapter getChapter(String url, int index
            , Map<String, String[]> contentFeature
            , Map<String, String[]> titleFeature) {
        if (url == null
                || !url.startsWith("http://")
                || contentFeature == null
                || contentFeature.isEmpty()
                || titleFeature == null
                || titleFeature.isEmpty())
            return null;
        try {
            long time = System.currentTimeMillis();
            Document document = Jsoup.connect(url).get();
            Logger.i("download time", System.currentTimeMillis() - time + "ms");
            time = System.currentTimeMillis();
            Chapter chapter = parserChapterFromDocument(contentFeature, titleFeature, document);
            if (chapter != null)
                chapter.setIndex(index);
            Logger.i("parser time", System.currentTimeMillis() - time + "ms");
            return chapter;
        } catch (HttpStatusException e) {
//            爬虫被网站禁止，需要模拟真实环境
            if (e.getStatusCode() == 403) {
                System.out.printf("\n403 error:" + url);
                System.out.printf("\nusing user-agent to cheat webservice");
                try {
                    Document document = getDocumentWhen403(url);
                    Chapter c = parserChapterFromDocument(contentFeature, titleFeature, document);
                    if (c != null)
                        c.setIndex(index);
                    return c;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document getDocumentWhen403(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
        Document document = Jsoup.parse(connection.getInputStream(), connection.getContentEncoding(), url);
        connection.disconnect();
        return document;
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

    private String getDirectUrl(String url) throws IOException {
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
            String redirctUrl = getDirectUrl(bookInfo.getBookLastUpdateUrl());
            if (redirctUrl != null && !" ".equals(redirctUrl)) {
                if (!" ".equals(redirctUrl)) {
                    bookInfo.setBookLastUpdateUrl(redirctUrl);
                    String[] s = redirctUrl.split("/");
//                      第三个String为网站的网址
                    bookInfo.setBookSourceWebUrl("http://" + s[2]);
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
        if (searchUrl == null)
            return null;
        try {
            mDocument = Jsoup.connect(searchUrl + UrlEncoder(bookName, "gb2312")).timeout(3000).get();
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
    private String UrlEncoder(String value, String charsetName) {
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

    public class BookInfo {
        String bookName;
        String bookLastUpdateUrl;
        String bookLastUpdateTitle;
        Date bookLastUpdateTime;
        String bookSourceWebName;
        String bookSourceWebUrl;
        String chapterLastUpdateListUrlList;
        String bookUrl;
        String bookCover;
        List<Chapter> chapterList = new ArrayList<>();

        public List<Chapter> getChapterList() {
            return chapterList;
        }

        public void setChapterList(List<Chapter> chapterList) {
            this.chapterList = chapterList;
        }

        public void addChapter(Chapter c) {
            if (c != null)
                chapterList.add(c);
        }

        public String getBookCover() {
            return bookCover;
        }

        public void setBookCover(String bookCover) {
            this.bookCover = bookCover;
        }

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

        public String toJson() {
            return new Gson().toJson(this);
        }
    }

}
