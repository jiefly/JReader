package com.gao.jiefly.jieflysbooks.Model.loader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.CustomDatabaseHelper;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.download.BaseHttpURLClient;
import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiefly on 2016/7/1.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BookLoader {
    private StringBuilder sb;
    private static final String TAG = "BookLoader";
    private CustomDatabaseHelper mCustomDatabaseHelper;

    public BookLoader(Context context) {
        mCustomDatabaseHelper = new CustomDatabaseHelper(context, "bookStore.db", null, 1);
    }

    public static BookLoader build(Context context) {
        return new BookLoader(context);
    }

    // 获取数据库中的小说
    public Book getBook(String bookName) {
        SQLiteDatabase db = mCustomDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from book where name=?", new String[]{bookName});
        if (cursor.moveToFirst()) {
            Book book = new Book();
            book.setBookName(cursor.getString(cursor.getColumnIndex("name")));
            book.setBookAuthor(cursor.getString(cursor.getColumnIndex("author")));
            book.setBookStatu(cursor.getString(cursor.getColumnIndex("statue")));
            book.setBookStyle(cursor.getString(cursor.getColumnIndex("bookType")));
            book.setBookUrl(cursor.getString(cursor.getColumnIndex("bookUrl")));
            book.setBookLastUpdate(cursor.getString(cursor.getColumnIndex("recentUpdate")));
            book.setBookNewTopicTitle(cursor.getString(cursor.getColumnIndex("recentTopic")));
            book.setBookNewTopicUrl(cursor.getString(cursor.getColumnIndex("recentTopicUrl")));
        }
        Log.e(TAG, "can't find the book you want,please add it");
        return null;
    }

    //    向数据库中添加小说
    public boolean addBook(String bookName) {
        Book book = getBookFromHttp(bookName);
        SQLiteDatabase db = mCustomDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("author", book.getBookAuthor());
        contentValues.put("name", book.getBookName());
        contentValues.put("recentTopic", book.getBookNewTopicTitle());
        contentValues.put("recentTopicUrl", book.getBookNewTopicUrl());
        contentValues.put("bookUrl", book.getBookUrl());
        contentValues.put("recentUpdate", book.getBookLastUpdate());
        contentValues.put("bookType", book.getBookStyle());
        contentValues.put("statue", book.getBookStatu());
        db.insert("Book", null, contentValues);
        if (!checkAddSuccess(bookName)) {
            Log.e(TAG, "add book failed");
            return false;
        }
        return true;
    }

    //    更新数据库中的书籍信息
    public void update(String bookName) {
        Book book = getBookFromHttp(bookName);
        SQLiteDatabase db = mCustomDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("recentTopic", book.getBookNewTopicTitle());
        contentValues.put("recentTopicUrl", book.getBookNewTopicUrl());
        contentValues.put("recentUpdate", book.getBookLastUpdate());
        db.update("Book", contentValues, "name=?", new String[]{bookName});
    }

    //    查询是否成功向数据库添加书籍
    private boolean checkAddSuccess(String bookName) {
        SQLiteDatabase db = mCustomDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from book where name=?", new String[]{bookName});
        return cursor != null;
    }

    private Book getBookFromHttp(String bookName) {
        if (sb == null)
            sb = new StringBuilder();
        try {
            String result = new BaseHttpURLClient()
                    .getWebResourse(new URL("http://www.uctxt.com/modules/article/search.php?searchkey="
                            + Utils.UrlEncoder(bookName, "gbk")));
            if (sb.length() > 0)
                sb.delete(0, sb.length());
            sb.append(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return findBookInHtml();
    }

    private Book findBookInHtml() {
        Pattern p = Pattern.compile("<title>(.*?)</title>");
        Matcher m = p.matcher(sb.toString());
        String tmp;
        if (m.find()) {
            tmp = m.group();
            Log.e("jiefly", "find title");
        } else {
            Log.e("jiefly", "can not find title");
            return null;
        }
//          判断title是否有搜索关键字，有的话则是搜索页面，否则为小说页面
        p = Pattern.compile("搜索");
        Book book;
        if (p.matcher(tmp).find()) {
            book = findBookInfoInSearchWeb();
            Log.e("jiefly", "find book in search");
        } else {
            book = findBookInfoInDetailWeb();
            Log.e("jiefly", "find book direct url");
        }
        return book;
    }

    private Book findBookInfoInDetailWeb() {
        Book book = new Book();
        Pattern p = Pattern.compile("<div class=\"book-about clrfix\"(.*?) <dl class=\"chapter-list clrfix\">");
        Matcher m = p.matcher(sb.toString());
        String tmp;
        if (m.find()) {
            tmp = m.group();
        } else {
            Log.e(TAG, "can't find book in method >>>>findBookInfoInDetailWeb<<<<");
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
            Log.e(TAG, "can't find book in method >>>>findBookInfoInDetailWeb<<<<");
            return null;
        }
        return book;
    }

    private Book findBookInfoInSearchWeb() {
        Pattern p = Pattern.compile("<div class=\"list-lastupdate\"(.*?)<li>(.*?)</li>");
        Matcher m = p.matcher(sb.toString());
        String tmp;
        if (m.find()) {
            tmp = m.group(2);
        } else {
            Log.e(TAG, "can't find book in method >>>>findBookInfoInSearchWeb<<<<");
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
            Log.e(TAG, "can't find book in method >>>>findBookInfoInSearchWeb<<<<");
            return null;
        }
        return book;
    }
}
