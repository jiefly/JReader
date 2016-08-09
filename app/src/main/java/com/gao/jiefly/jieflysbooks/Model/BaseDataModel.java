package com.gao.jiefly.jieflysbooks.Model;

import android.content.Context;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.download.BaseHttpURLClient;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataStateListener;
import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BaseDataModel implements DataModel {
    private static final String TAG = "BaseDataModel";
    static Context mContext;
    private static volatile BaseDataModel instance = null;
    StringBuilder sb = new StringBuilder();

    public void setOnDataStateListener(OnDataStateListener OnDataStateListener) {
        mOnDataStateListener = OnDataStateListener;
    }

    OnDataStateListener mOnDataStateListener = null;

    public BaseDataModel(OnDataStateListener OnDataStateListener) {
        mOnDataStateListener = OnDataStateListener;
    }

    private BaseDataModel() {
    }

    @Override
    public List<Book> getBookList() {
        return null;
    }

    @Override
    public Chapter getBookChapterByUrl(String url) throws IOException {
        return null;
    }

    @Override
    public Chapter getBookChapterByIndex(String bookName, int index) throws IOException {
        return null;
    }

    @Override
    public List<Chapter> getChapterList(String url) throws MalformedURLException {
        URL urll = new URL(url);
        String srcHtml = new BaseHttpURLClient().getWebResourse(urll);
        List<Chapter> result = Utils.getChapterListFromHtml(srcHtml);
        for (Chapter c : result) {
//            拼接好地址
            c.setUrl(url + "/" + c.getUrl());
        }
        return result;
    }

    @Override
    public Book getBook(String name) {
        if (!checkDataModelIsInit())
            return null;

        return null;
    }

    private boolean checkDataModelIsInit() {
        if (instance == null)
            return false;
        return true;
    }

    @Override
    public Book addBook(String name) {
        return null;
    }

    @Override
    public void addBookSyn(String name) {

    }

    @Override
    public void removeBook(String[] name) {

    }

    @Override
    public void removeBookSyn(String[] name) {

    }

    @Override
    public void updateBook(Book book) {

    }

    @Override
    public void updateBookSyn(Book book ,int type) {

    }

    @Override
    public void updateAllBooks(int type) {

    }

    @Override
    public Chapter getChapter(String bookName, int index) {
        return null;
    }

    @Override
    public Chapter getChapter(String url) {
        return null;
    }


    @Override
    public void getChapterSyn(String bookName, String title, String url) {

    }

    @Override
    public void addChapter(String bookName, int index) {

    }

    @Override
    public void addChapter(String bookName, URL url) {

    }

    public static BaseDataModel build(Context context) {
        if (mContext == null)
            mContext = context;
        if (instance == null)
            synchronized (BaseDataModel.class) {
                if (instance == null) {
                    instance = new BaseDataModel();
                }
            }
        return instance;
    }

    @Override
    public DataModel getInstance() {
        if (instance != null)
            return instance;
        Log.e(TAG, "please build first!!!");
        return null;
    }

    @Override
    public void updateBookReaderChapterIndex(Book book, int index) {

    }


    public void getBookSuscribe(final String bookName) {
        try {
            String result = new BaseHttpURLClient()
                    .getWebResourse(new URL("http://www.uctxt.com/modules/article/search.php?searchkey="
                            + Utils.UrlEncoder(bookName, "gbk")));
            if (sb.length() > 0)
                sb.delete(0, sb.length());
            sb.append(result);
            findBookInfo();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sb.length() > 0)
                        sb.delete(0, sb.length());
                    //http://so.biquge.la/cse/search?q=%E5%AE%8C%E7%BE%8E%E4%B8%96%E7%95%8C&click=1&s=7138806708853866527&nsid=
//                    http://www.uctxt.com/toplist/lastupdate-1
                    URL url = new URL("http://www.uctxt.com/modules/article/search.php?searchkey=" + Utils.UrlEncoder(bookName, "gbk"));
//                    Log.back_btn_bg("jielfy", url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
//                    connection.disconnect();
                    String lines;
                    while ((lines = bufferedReader.readLine()) != null)
                        sb.append(lines);
//                    首先找出页面title，根据title判断当前处于搜索页面还是小说页面
                    Pattern p = Pattern.compile("<title>(.*?)</title>");
                    Matcher m = p.matcher(sb.toString());
                    String tmp = null;
                    if (m.find()) {
                        tmp = m.group();
                        Log.back_btn_bg("jiefly", "find title");
                    } else {
                        Log.back_btn_bg("jiefly", "can not find title");

                        return;
                    }
//                    判断title是否有搜索关键字，有的话则是搜索页面，否则为小说页面
                    p = Pattern.compile("搜索");
                    Book book;
                    if (p.matcher(tmp).find()) {
                        book = findBookInfoInSearch();
                        Log.back_btn_bg("jiefly", "find search");
                    } else {
                        book = findBookInfoInDetail();
                        Log.back_btn_bg("jiefly", "can not find search");
                    }
                    mOnDataStateListener.onSuccess(book);
                } catch (IOException back_btn_bg) {
                    back_btn_bg.printStackTrace();
                }
            }
        }).start();*/
    }

    public String getBookChapter(final String url) {
        String result;
        try {
            result = new BaseHttpURLClient().getWebResourse(new URL(url));
            String tmp = Utils.delHTMLTag(result);
            Pattern p = Pattern.compile("下一章书签([\\w\\W]*)推荐上一章");
            final Matcher m = p.matcher(tmp);
            if (m.find())
                Log.e("jiefly---", m.group(1));
            if (sb.length() > 0)
                sb.delete(0, sb.length());
            sb.append(m.group(1));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void findBookInfo() {
        Pattern p = Pattern.compile("<title>(.*?)</title>");
        Matcher m = p.matcher(sb.toString());
        String tmp;
        if (m.find()) {
            tmp = m.group();
            Log.e("jiefly", "find title");
        } else {
            Log.e("jiefly", "can not find title");
            return;
        }
//                    判断title是否有搜索关键字，有的话则是搜索页面，否则为小说页面
        p = Pattern.compile("搜索");
        Book book;
        if (p.matcher(tmp).find()) {
            book = findBookInfoInSearch();
            Log.e("jiefly", "find search");
        } else {
            book = findBookInfoInDetail();
            Log.e("jiefly", "can not find search");
        }
//        mOnDataStateListener.onSuccess(book);
    }

    private Book findBookInfoInDetail() {
        Book book = new Book();
        Pattern p = Pattern.compile("<div class=\"book-about clrfix\"(.*?) <dl class=\"chapter-list clrfix\">");
        Matcher m = p.matcher(sb.toString());
        String tmp;
        if (m.find()) {
            tmp = m.group();
//            Log.back_btn_bg("jiefly", tmp);
        } else {
            mOnDataStateListener.onFailed(null);
            return null;
        }
        p = Pattern.compile("<div class=\"l\"><h1>(.*?)</h1><em>作者.(.*?)</em></div>(.*?)onClick=\"recom*\\((.*?)\\);\"(.*?)</b><a href=\"(.*?)\">(.*?)</a>(.*?)状态.<i>(.*?)</i>(.*?)更新时间.<i>(.*?)</i>");
        m = p.matcher(tmp);
        if (m.find()) {
            book.setBookName(m.group(1));
            book.setBookAuthor(m.group(2));
            String url;
            url = "http://www.uctxt.com/" + "book" + "/" + Integer.valueOf(m.group(4)) / 1000 + "/" + m.group(4);
            book.setBookUrl(url);
            book.setBookNewTopicUrl(url + "/" + m.group(6));
            book.setBookNewTopicTitle(m.group(7));
            book.setBookStatu(m.group(9));
            book.setBookLastUpdate(m.group(11));
        } else {
            mOnDataStateListener.onFailed(null);
            return null;
        }
        return book;
    }

    private Book findBookInfoInSearch() {
        Pattern p = Pattern.compile("<div class=\"list-lastupdate\"(.*?)<li>(.*?)</li>");
        Matcher m = p.matcher(sb.toString());
        String tmp;
        if (m.find()) {
            tmp = m.group(2);
        } else {

            mOnDataStateListener.onFailed(null);
            return null;
        }
        p = Pattern.compile("<span class=\"class\">(.*?)</span><span class=\"name\"><a href=\"(.*?)\">(.*?)</a><small> / <a href=\"(.*?)\">(.*?)</a></small></span><span class=\"other\">(.*?)<small>(.*?)</small><small>(.*?)</small><small>(.*?)</small></span>");
        m = p.matcher(tmp);
        Book book = new Book();
        if (m.find()) {
            book.setBookStyle(m.group(1));
            book.setBookUrl("http://www.uctxt.com/" + m.group(2));
            book.setBookName(m.group(3));
            book.setBookNewTopicUrl("http://www.uctxt.com" + m.group(4));
            book.setBookNewTopicTitle(m.group(5));
            book.setBookAuthor(m.group(6));
            book.setBookTotalWords(Integer.parseInt(m.group(7).replaceAll("K", "")) * 1000);
            book.setBookLastUpdate(m.group(8));
            book.setBookStatu(m.group(9));
        } else {

            mOnDataStateListener.onFailed(null);
            return null;
        }
        return book;
    }
}
