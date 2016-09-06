package com.gao.jiefly.jieflysbooks.Model.loader;

import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.downloader.VolleyClient;
import com.gao.jiefly.jieflysbooks.Model.listener.OnBookAddFromSoDuListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnBookImageGetListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnBookUpdateFromSoDuListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnChapterGetListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;
import com.gao.jiefly.jieflysbooks.Utils.ApplicationLoader;
import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiefly on 2016/8/12.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class GetBookFromSoDu {
    private Book mBook;
    private static final String URL_HEAD = "http://www.sodu.cc/result.html?searchstr=";
    private String regexBookUrl;
    private String regexUpdateResult;
    private OnBookAddFromSoDuListener listener;
    String url;
    StringBuffer sb;
    String webUrl = null;
    String webName = null;

    public GetBookFromSoDu() {
        mBook = new Book();
        sb = new StringBuffer();
    }

    public void getBookCoverUrl(final String bookName, final String webName, final OnBookImageGetListener listener) {
        regexUpdateResult = "<a href=\"(.*?)\">" + bookName + "</a>";
//        将小说名字转换为IEM码
        String transformedName = Utils.UrlDecoder(bookName);
        url = URL_HEAD + transformedName;
        //        在sodu搜索小说
        VolleyClient.build(ApplicationLoader.applicationContext).getWebResource(url, new OnDataStateListener() {
            @Override
            public void onSuccess(String result) {
//        获取搜索结果html
//        从中截取出要搜索小说的最新章节地址
                String path = Utils.getRegexResult(regexUpdateResult, result);
                if (path != null) {
                    url = path.replace("<a href=\"", "");
                    String end = sb.append("\">").append(bookName).append("</a>").toString();
                    url = url.replace(end, "");
//                    要更新小说的时候先从这个页面获取更新时间，通过更新时间来确定是否需要更新
//                    mBook.setBookUpdateTimeUrl(url);
                } else {
                    listener.onFailed(null);
                    Log.e("GetBookFromSoDu.getBook", "sodu中搜索书籍出错");
                    return;
                }
//                获取最近更新页面的html
                VolleyClient.build(null).getWebResource(url, new OnDataStateListener() {
                    @Override
                    public void onSuccess(String result) {
//
                        String rex = "<div class=\"main-html\">([\\s\\S]*?)</div>[\\s\\S][\\s\\S]</div>";
                        List<String> units = Utils.getRegexResult(rex, result, false);
                        if (units != null)
                            for (String unit : units) {
                                if (unit.contains(webName)) {
                                    rex = "hapterurl=(.*?)\" alt";
                                    url = Utils.getRegexResult(rex, unit);
                                    rex = "(\\D++)";
                                    int bookNumber;
                                    if (url != null) {
                                        String[] bookNum = url.split(rex);
                                        for (String num : bookNum) {
                                            if (num.length() > 1) {
                                                bookNumber = Integer.parseInt(num);
                                                url = "http://www.dashubao.cc/book/" + bookNumber / 1000 + "/" + bookNumber + "/";
                                                break;
                                            }
                                        }
                                        break;
                                    }
//                                    rex = "alt=\"(.*?)\" onclick";
//                                    String chapterTitle = Utils.getRegexResult(rex, unit)
//                                            .replace("alt=\"", "")
//                                            .replace("\" onclick", "");
//                                    rex = "class=\"xt1\">(.*?)</div>[\\s\\S][\\s\\S]</div>";
//                                    String lastUpdateTime = Utils.getRegexResult(rex, unit).replaceAll("class=\"xt1\">", "").replace("</div>\r\n</div>", "");
//                更新时间
//                                    mBook.setBookLastUpdate(lastUpdateTime);
//                更新章节标题
//                                    mBook.setBookNewTopicTitle(chapterTitle);
//                                hapterurl=http://www.qiushuixuan.cc/book/14/14757/12881130.html" alt
                                    //        更新章节的地址
//                                    mBook.setBookNewTopicUrl(formateUrl(url, '=', '"'));
//                网站地址
//                                    webUrl = formateUrl(url, '=', 'b');
//                                    url = formateUrl(url, '=', '_');
//                小说地址

                                }
                            }
                        if (url != null)
                            VolleyClient.build(null).getWebResource(url, new OnDataStateListener() {
                                @Override
                                public void onSuccess(String result) {
//                                        获取小说cover path
                                    result = getImage(result);
                                    listener.onSuccess(result);
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    listener.onFailed(e);
                                }
                            }, "gbk");
                    }

                    @Override
                    public void onFailed(Exception e) {
                        listener.onFailed(e);
                    }
                }, "UTF-8");
            }

            @Override
            public void onFailed(Exception e) {
                listener.onFailed(e);
            }
        }, "UTF-8");
    }

    public void getBook(final String bookName, final String webName, OnBookAddFromSoDuListener onBookAddFromSoDuListener) {
        this.webName = webName;
        mBook.setBookName(bookName);
        mBook.setBookResource(webName);
        this.listener = onBookAddFromSoDuListener;
        final long time = System.currentTimeMillis();
        regexUpdateResult = "<a href=\"(.*?)\">" + bookName + "</a>";
//        将小说名字转换为IEM码
        String transformedName = Utils.UrlDecoder(bookName);
        url = URL_HEAD + transformedName;
        //        在sodu搜索小说
        VolleyClient.build(ApplicationLoader.applicationContext).getWebResource(url, new OnDataStateListener() {
            @Override
            public void onSuccess(String result) {
//        获取搜索结果html
//        从中截取出要搜索小说的最新章节地址
                String path = Utils.getRegexResult(regexUpdateResult, result);
                if (path != null) {
                    url = path.replace("<a href=\"", "");
                    String end = sb.append("\">").append(bookName).append("</a>").toString();
                    url = url.replace(end, "");
//                    要更新小说的时候先从这个页面获取更新时间，通过更新时间来确定是否需要更新
                    mBook.setBookUpdateTimeUrl(url);
                } else
                    Log.e("GetBookFromSoDu.getBook", "sodu中搜索书籍出错");
//                获取最近更新页面的html
                VolleyClient.build(null).getWebResource(url, new OnDataStateListener() {
                    @Override
                    public void onSuccess(String result) {
//                        regexBookUrl = "<div class=\"main-html\">([\\s\\S]*?)class=\"xt1\">";
                        /*regexBookUrl = "<div class=\"main-html\">([\\s\\S]*?)</div>[\\s\\S][\\s\\S]</div>";
                        List<String> units = Utils.getRegexResult(regexBookUrl, result,false);
                        for (String unit:units) {
                            if (unit.contains(webName)) {
                                regexBookUrl = "hapterurl=(.*?)\" alt";
                                url = Utils.getRegexResult(regexBookUrl, unit);
                                regexBookUrl = "alt=\"(.*?)\" onclick";
                                String chapterTitle = Utils.getRegexResult(regexBookUrl,unit)
                                        .replace("alt=\"","")
                                        .replace("\" onclick","");
                                regexBookUrl = "class=\"xt1\">(.*?)</div>[\\s\\S][\\s\\S]</div>";
                                String lastUpdateTime = Utils.getRegexResult(regexBookUrl,unit).replaceAll("class=\"xt1\">","").replace("</div>\r\n</div>","");
                                mBook.setBookLastUpdate(lastUpdateTime);
                                mBook.setBookName(bookName);
                                mBook.setBookNewTopicTitle(chapterTitle);
//                                hapterurl=http://www.qiushuixuan.cc/book/14/14757/12881130.html" alt
                                //        获取到小说的地址
                                mBook.setBookNewTopicUrl(formateUrl(url,'=','"'));
                                webUrl = formateUrl(url,'=','b');
                                url = formateUrl(url,'=','/');
                                mBook.setBookUrl(url);

//                                http://www.qiushuixuan.cc/book/14/14757
                                Log.e("urlTime", System.currentTimeMillis() - time + "ms");

                            }
                        }*/
                        getBookUpdateInfo(mBook, result);
                        VolleyClient.build(null).getWebResource(mBook.getBookUrl(), new OnDataStateListener() {
                            @Override
                            public void onSuccess(String result) {
//                                        获取小说最近更新/cover
                                getAndSetBookLastupdateAndImage(result, mBook);
//                                        获取小说的author/type/
                                getAndSetBookAuthorType(result, mBook);
//                                        获取小说的章节列表
                                getAndSetBookChapterList(result, mBook);
                                listener.onSuccess(mBook);
                            }

                            @Override
                            public void onFailed(Exception e) {
                                listener.onFailed(e);
                                Log.e("GetBookFromSoDu", e.getMessage());
                            }
                        }, "gbk");
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e("GetBookFromSoDu", e.getMessage());
                        listener.onFailed(e);
                    }
                }, "UTF-8");
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("GetBookFromSoDu", e.getMessage());
                listener.onFailed(e);
            }
        }, "UTF-8");
    }

    private void getAndSetBookChapterList(String result, Book book) {
        book.setChapterList(Utils.getChapterListFromHtml(result));
    }

    public void getBookUpdateInfo(final Book book, final OnBookUpdateFromSoDuListener listener) {
        VolleyClient.build(ApplicationLoader.applicationContext).getWebResource(book.getBookUpdateTimeUrl(), new OnDataStateListener() {
            @Override
            public void onSuccess(String result) {
                listener.onSuccess(getBookUpdateInfo(book, result));
            }

            @Override
            public void onFailed(Exception e) {
                listener.onFailed(e);
            }
        }, "UTF-8");
    }

    public Book getBookUpdateInfo(Book book, String htmlStr) {
//        用来匹配这一段
//        <div class="main-html">
//        <div style="width:560px;float:left;"><a href="http://www.sodu.cc/gourl.html?id=62745029&t=081e3c94902e1ae1100c790642da804f&chapterurl=http://www.qiushuixuan.cc/book/14/14757/12590487.html" alt="第一九二八章 逼降" onclick="getpage(this)" target="_blank">飞天 第一九二八章 逼降</a></div>
//        <div style="width:108px;float:left;"><a href="http://www.sodu.cc/newmulu_108223_22.html" class="tl">秋水轩</a></div>
//        <div style="width:88px;float:left;" class="xt1">2016-8-12 1:40:00</div>
//        </div>
//        String htmlStr = VolleyClient.build(ApplicationLoader.applicationContext).getWebResourse(book.getBookUpdateTimeUrl(),"UTF-8");
        webName = book.getBookResource();
        String rex = "<div class=\"main-html\">([\\s\\S]*?)</div>[\\s\\S][\\s\\S]</div>";
        List<String> units = Utils.getRegexResult(rex, htmlStr, false);
        for (String unit : units) {
            if (unit.contains(webName)) {
                rex = "hapterurl=(.*?)\" alt";
                url = Utils.getRegexResult(rex, unit);
                rex = "alt=\"(.*?)\" onclick";
                String chapterTitle = Utils.getRegexResult(rex, unit)
                        .replace("alt=\"", "")
                        .replace("\" onclick", "");
                rex = "class=\"xt1\">(.*?)</div>[\\s\\S][\\s\\S]</div>";
                String lastUpdateTime = Utils.getRegexResult(rex, unit).replaceAll("class=\"xt1\">", "").replace("</div>\r\n</div>", "");
//                更新时间
                mBook.setBookLastUpdate(lastUpdateTime);
//                更新章节标题
                mBook.setBookNewTopicTitle(chapterTitle);
//                                hapterurl=http://www.qiushuixuan.cc/book/14/14757/12881130.html" alt
                //        更新章节的地址
                mBook.setBookNewTopicUrl(formateUrl(url, '=', '"'));
//                网站地址
                webUrl = formateUrl(url, '=', 'b');
                url = formateUrl(url, '=', '/');
//                小说地址
                mBook.setBookUrl(url);
                return mBook;
            }
        }
        return null;
    }

    public void updateBook(String url, final OnChapterGetListener listener) {
        VolleyClient.build(ApplicationLoader.applicationContext).getWebResource(url, new OnDataStateListener() {
            @Override
            public void onSuccess(String result) {
                listener.onSuccess(Utils.getChapterListFromHtml(result));
            }

            @Override
            public void onFailed(Exception e) {
                listener.onFailed(e);
            }
        }, "UTF-8");
    }

    /*
    * 只用于获取更新数据
    * */
    public Map<String, String> getBookLastUpdate(String url) {
        Map<String, String> update = new HashMap<>();
        String htmlStr = VolleyClient.build(ApplicationLoader.applicationContext).getWebResourse(url, "UTF-8");
        String regex = "<div class=\"booklistt clearfix\">([\\s\\S]*?)href=\"#bot\"";
        htmlStr = Utils.getRegexResult(regex, htmlStr);
//        获取title
        regex = "<b>正文 ([\\w\\W]*?)</b>";
        String updateTitle = Utils.getRegexResult(regex, htmlStr);
        updateTitle = updateTitle.replace("<b>正文 ", "").replace("</b>", "");
        update.put("title", updateTitle);
//        获取url
        regex = "<a href=\"(.*?)\" target=\"_blank\">";
        String updateUrl = Utils.getRegexResult(regex, htmlStr);
        updateUrl = updateUrl.replace("<a href=\"", "").replace("\" target=\"_blank\"", "");
        updateUrl = webUrl.substring(0, webUrl.length() - 1) + updateUrl;
        update.put("url", updateUrl);
        return update;
    }


    private void getAndSetBookLastupdateAndImage(String result, Book book) {
        String regex = "<div class=\"booklistt clearfix\">([\\s\\S]*?)href=\"#bot\"";
        result = Utils.getRegexResult(regex, result);
//        获取title
        regex = "<b>正文 ([\\w\\W]*?)</b>";
        String updateTitle = Utils.getRegexResult(regex, result);
        updateTitle = updateTitle.replace("<b>正文 ", "").replace("</b>", "");
        book.setBookNewTopicTitle(updateTitle);
//        获取url
        regex = "<a href=\"(.*?)\" target=\"_blank\">";
        String updateUrl = Utils.getRegexResult(regex, result);
        updateUrl = updateUrl.replace("<a href=\"", "").replace("\" target=\"_blank\"", "");
        updateUrl = webUrl.substring(0, webUrl.length() - 1) + updateUrl;
        book.setBookNewTopicUrl(updateUrl);
//        获取image url
        regex = "<img src=\"(.*?)\"";
        String imageUrl = Utils.getRegexResult(regex, result);
        imageUrl = imageUrl.replace("<img src=\"", "").replace("\"", "");
        imageUrl = webUrl.substring(0, webUrl.length() - 1) + imageUrl;
        book.setBookCover(imageUrl);
    }

    private String getImage(String result) {
        String regex = "<img src=\"([\\s\\S]*?)\" width=([\\s\\S]*?)></div>";
        result = Utils.getRegexResult(regex, result);
//        获取image url
        if (result == null)
            return ApplicationLoader.DEFAULT_BOOK_COVER;
        String imageUrl = formateUrl(result,'=','w');
        imageUrl = imageUrl.replaceAll("\"","").replaceAll(" ","");
        return imageUrl;
    }

    private void getAndSetBookAuthorType(String result, Book book) {
        String regex = "<span class=\"author\">作者：(.*?)  分类：(.*?)</span>";
        List<String> tmp = Utils.getRegexResult(regex, result, true);
        String author = tmp.get(1);
        String type = formateUrl(tmp.get(0), '类', '<');
        book.setBookStyle(type.substring(1));
        book.setBookAuthor(author);
    }

    private String formateUrl(String url, char startChar, char endChar) {
        int start = 0;
        int end = 0;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == startChar) {
                start = i + 1;
                break;
            }
        }
        for (int i = url.length() - 1; i >= 0; i--) {
            if (url.charAt(i) == endChar) {
                end = i;
                break;
            }
        }
        return url.substring(start, end);
    }

}
