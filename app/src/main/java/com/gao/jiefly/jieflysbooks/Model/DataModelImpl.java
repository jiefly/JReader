package com.gao.jiefly.jieflysbooks.Model;

import android.util.Log;

import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class DataModelImpl implements DataModel {
    StringBuilder sb = new StringBuilder();
    onDataStateListener mOnDataStateListener = null;

    public DataModelImpl(onDataStateListener onDataStateListener) {
        mOnDataStateListener = onDataStateListener;
    }

    @Override
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
//                    Log.e("jielfy", url.toString());
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
                    mOnDataStateListener.onSuccess(book);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
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
        mOnDataStateListener.onSuccess(book);
    }

    @Override
    public String getBookTopic(final String url) {
        final StringBuilder stringBuilder = new StringBuilder();

            String result;
            try {
                result = new BaseHttpURLClient().getWebResourse(new URL(url));
                String tmp = Utils.delHTMLTag(result);
//            Log.d("jiefly",tmp);
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
        /*URL urlBookTopic;
        String lines;
        try {
            BufferedReader bufferedReader;
            HttpURLConnection connection;
            urlBookTopic = new URL(url);

            connection = (HttpURLConnection) urlBookTopic.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
            while ((lines = bufferedReader.readLine()) != null)
                stringBuilder.append(lines);
//            Log.d("jiefly",stringBuilder.toString());
            String tmp = Utils.delHTMLTag(stringBuilder.toString());
//            Log.d("jiefly",tmp);
            Pattern p = Pattern.compile("下一章书签([\\w\\W]*)推荐上一章");
            final Matcher m = p.matcher(tmp);
            if (m.find())
                Log.e("jiefly---", m.group(1));
            if (stringBuilder.length() > 0)
                stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append(m.group(1));
//            stringBuilder.append(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.e("jiefly---",stringBuilder.toString());
        return stringBuilder.toString();*/

    }

    private Book findBookInfoInDetail() {
        Book book = new Book();
        Pattern p = Pattern.compile("<div class=\"book-about clrfix\"(.*?) <dl class=\"chapter-list clrfix\">");
        Matcher m = p.matcher(sb.toString());
        String tmp;
        if (m.find()) {
            tmp = m.group();
//            Log.e("jiefly", tmp);
        } else {
            mOnDataStateListener.onFailed();
            return null;
        }
        /*tmp = tmp.replaceAll(" ","");
        Log.e("jiefly",tmp);*/
//        onclick="recom\((.*?)\);"(.*?)<a href="(.*?)">(.*?)</a>(.*?)span class="r">状态：<i>(.*?)</i>字数：<i>937610</i>更新时间：<i>(.*?)</i></span>
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
            mOnDataStateListener.onFailed();
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

            mOnDataStateListener.onFailed();
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

            mOnDataStateListener.onFailed();
            return null;
        }
        return book;

    }

}
