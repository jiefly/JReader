package com.gao.jiefly.jieflysbooks.Model.loader;

import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.download.VolleyClient;
import com.gao.jiefly.jieflysbooks.Model.listener.OnBookAddFromSoDuListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;
import com.gao.jiefly.jieflysbooks.Utils.ApplicationLoader;
import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.util.List;

/**
 * Created by jiefly on 2016/8/12.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class GetBookFromSoDu{
    private Book mBook;
    private static final String URL_HEAD = "http://www.sodu.cc/result.html?searchstr=";
    private String regexBookUrl;
    private String regexUpdateResult;
    private OnBookAddFromSoDuListener listener;
    String url;
    StringBuffer sb;
    String webUrl = null;
    public GetBookFromSoDu() {
        mBook = new Book();
        sb = new StringBuffer();
    }

    public void getBook(final String bookName, final String webName,OnBookAddFromSoDuListener onBookAddFromSoDuListener) {
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
                if (path!=null) {
                    url = path.replace("<a href=\"", "");
                    String end = sb.append("\">").append(bookName).append("</a>").toString();
                    url = url.replace(end, "");
                } else
                    Log.e("GetBookFromSoDu.getBook", "sodu中搜索书籍出错");
//                获取最近更新页面的html
                VolleyClient.build(null).getWebResource(url, new OnDataStateListener() {
                    @Override
                    public void onSuccess(String result) {
                        regexBookUrl = "<div class=\"main-html\">([\\s\\S]*?)class=\"xt1\">";

                        List<String> units = Utils.getRegexResult(regexBookUrl, result,false);
                        for (String unit:units) {
                            if (unit.contains(webName)) {
                                regexBookUrl = "hapterurl=(.*?)\" alt";
                                url = Utils.getRegexResult(regexBookUrl, unit);
                                regexBookUrl = "alt=\"(.*?)\" onclick";
                                String chapterTitle = Utils.getRegexResult(regexBookUrl,unit)
                                        .replace("alt=\"","")
                                        .replace("\" onclick","");
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
                                VolleyClient.build(null).getWebResource(url, new OnDataStateListener() {
                                    @Override
                                    public void onSuccess(String result) {
//                                        获取小说最近更新/cover
                                        getAndSetBookLastupdateAndImage(result,mBook);
//                                        获取小说的author/type/
                                        getAndSetBookAuthorType(result,mBook);
//                                        获取小说的章节列表
                                        getAndSetBookChapterList(result,mBook);
                                        listener.onSuccess(mBook);
                                    }

                                    @Override
                                    public void onFailed(Exception e) {
                                        listener.onFailed(e);
                                        Log.e("GetBookFromSoDu",e.getMessage());
                                    }
                                },"gbk");
                            }
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e("GetBookFromSoDu",e.getMessage());
                        listener.onFailed(e);
                    }
                }, "UTF-8");
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("GetBookFromSoDu",e.getMessage());
                listener.onFailed(e);
            }
        }, "UTF-8");
    }

    private void getAndSetBookChapterList(String result, Book book) {
        book.setChapterList(Utils.getChapterListFromHtml(result));
        sb.append("fdsf");
    }

    private void getAndSetBookLastupdateAndImage(String result, Book book) {
        String regex = "<div class=\"booklistt clearfix\">([\\s\\S]*?)href=\"#bot\"";
        result = Utils.getRegexResult(regex,result);
//        获取title
        regex = "<b>正文 ([\\w\\W]*?)</b>";
        String updateTitle = Utils.getRegexResult(regex,result);
        updateTitle = updateTitle.replace("<b>正文 ","").replace("</b>","");
        book.setBookNewTopicTitle(updateTitle);
//        获取url
        regex = "<a href=\"(.*?)\" target=\"_blank\">";
        String updateUrl = Utils.getRegexResult(regex,result);
        updateUrl = updateUrl.replace("<a href=\"","").replace("\" target=\"_blank\"","");
        updateUrl = webUrl.substring(0,webUrl.length() - 1)+updateUrl;
        book.setBookNewTopicUrl(updateUrl);
//        获取image url
        regex = "<img src=\"(.*?)\"";
        String imageUrl = Utils.getRegexResult(regex,result);
        imageUrl = imageUrl.replace("<img src=\"","").replace("\"","");
        imageUrl = webUrl.substring(0,webUrl.length() - 1) + imageUrl;
        book.setBookCover(imageUrl);
    }

    private void getAndSetBookAuthorType(String result, Book book) {
        String regex = "<span class=\"author\">作者：(.*?)  分类：(.*?)</span>";
        List<String> tmp = Utils.getRegexResult(regex,result,true);
        String author = tmp.get(1);
        String type = formateUrl(tmp.get(0),'类','<');
        book.setBookStyle(type.substring(1));
        book.setBookAuthor(author);
    }

    private String formateUrl(String url,char startChar,char endChar) {
        int start = 0;
        int end = 0;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == startChar) {
                start = i+1;
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
