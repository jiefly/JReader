package com.gao.jiefly.jieflysbooks.Model.loader;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;

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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    @Override
    public Book getBookByName(String bookName) {
        this.bookName = bookName;
        String text = getBookLastUpdateUrlFromSearch(bookName);
        getBookInfoListFromLastUpdateList(text);
//        System.out.printf(text);
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
        return bookInfos;
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
        getBookUrlBySodu(result);
        return result;
    }

    private BookInfo getBookUrlBySodu(BookInfo bookInfo) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(bookInfo.getBookLastUpdateUrl()).openConnection();
//            设置不进行重定向，而是拦截重定向的response，从而获得最新章节的地址
            connection.setInstanceFollowRedirects(false);
            if (connection.getResponseCode() == 302) {
                List<String> desUrls = connection.getHeaderFields().get("Location");
                if (desUrls != null && desUrls.size() == 1) {
                    String lastUpdateChapterUrl = desUrls.get(0);
                    if (!" ".equals(lastUpdateChapterUrl)) {
                        bookInfo.setBookLastUpdateUrl(lastUpdateChapterUrl);
                        String[] s = lastUpdateChapterUrl.split("/");
//                    第三个String为网站的网址
                        bookInfo.setBookSourceWebUrl(s[2]);
                        String bookUrl = lastUpdateChapterUrl.replace(s[s.length - 1], "");
                        bookInfo.setBookUrl(bookUrl);
                    }
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
