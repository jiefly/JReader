package com.gao.jiefly.jieflysbooks.Model.loader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.CustomDatabaseHelper;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.download.BaseHttpURLClient;
import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
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
    private CustomDatabaseHelper mBookDatabaseHelper;
    private CustomDatabaseHelper mChapterListDatabaseHelper;

    public BookLoader(Context context) {
        mBookDatabaseHelper = new CustomDatabaseHelper(context, "bookStore.db", null, 1, CustomDatabaseHelper.BOOK_TYPE);
        mChapterListDatabaseHelper = new CustomDatabaseHelper(context, "chapterList.db", null, 1, CustomDatabaseHelper.CHAPTER_LIST_TYPE);
    }

    public static BookLoader build(Context context) {
        return new BookLoader(context);
    }

    //  删除数据库中的小说以及小说章节列表
    public boolean removeBook(String[] bookName) {
        SQLiteDatabase db = mBookDatabaseHelper.getWritableDatabase();
//        移除数据库中的小说章节列表
        SQLiteDatabase dbChapter = mChapterListDatabaseHelper.getWritableDatabase();
        dbChapter.delete("chapterList", "bookName=?", bookName);
        int result = db.delete("Book", "name=?", bookName);
        return result > 0;
    }

    //    更新数据库中的小说章节列表
    private void updateChapterList(String bookName) throws MalformedURLException {
        List<Chapter> chaptersFromHttp = getChapterListFromHttp(getBook(bookName).getBookUrl());
        Book.ChapterList chaptersFromDB = getChapterListFromDB(bookName);
        if (chaptersFromHttp != null) {
            if (chaptersFromDB == null || chaptersFromHttp.size() > chaptersFromDB.getChapterUrlList().size()) {
                List<String> chapterUrl = new LinkedList<>();
                List<String> chapterTopic = new LinkedList<>();
                ContentValues contentValues = new ContentValues();
                contentValues.put("bookName", bookName);
                for (int i = chaptersFromDB != null ? chaptersFromDB.getChapterTitleList().size() : 0; i < chaptersFromHttp.size(); i++) {
                    SQLiteDatabase db = mChapterListDatabaseHelper.getWritableDatabase();
                    contentValues.put("chapterTitle", chaptersFromHttp.get(i).getTitle());
                    contentValues.put("chapterUrl", chaptersFromHttp.get(i).getUrl());
                    db.insert("chapterList", null, contentValues);
                }
            }
        }
    }


    //更新Book中读者读到的章节index
    public void refreshReadChapterIndex(Book book, int index) {
        Log.e(TAG, "index:" + index+"isCached:"+book.isCached());
        SQLiteDatabase db = mBookDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("chapterIndex", index);
        contentValues.put("isCached", book.isCached()?0x10:0x01);
        db.update("Book", contentValues, "name=?", new String[]{book.getBookName()});
        db.close();
        try {
            Log.e(TAG, "after index:" + getBook(book.getBookName()).getReadChapterIndex()+"isCached:"+getBook(book.getBookName()).isCached());
//            updateBookChapterIndex(getBook(book.getBookName()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //从网络拉取小说列表
    private List<Chapter> getChapterListFromHttp(String url) throws MalformedURLException {
        URL urll = new URL(url);
        String srcHtml = new BaseHttpURLClient().getWebResourse(urll);
        List<Chapter> result = Utils.getChapterListFromHtml(srcHtml);
        for (Chapter c : result) {
            // 拼接好地址
            c.setUrl(url + "/" + c.getUrl());
        }
        return result;
    }

    //获取小说章节列表
    public List<Chapter> getChapterList(String bookName) throws MalformedURLException {
        Book.ChapterList chapterList = getChapterListFromDB(bookName);
        if (chapterList != null)
            return chapterList2List(chapterList);
        final List<Chapter> chapters = getChapterListFromHttp(getBook(bookName).getBookUrl());
        new Thread(new Runnable() {
            @Override
            public void run() {
                //        如果数据库中没有数据，则向数据库中添加数据
                addChapterList(list2ChapterList(chapters));
            }
        }).start();
        return chapters;
    }

    // 获取数据库中的小说章节列表
    private Book.ChapterList getChapterListFromDB(String bookName) {
        SQLiteDatabase db = mChapterListDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from chapterList where bookName=?", new String[]{bookName});
        List<String> chapterUrl = new LinkedList<>();
        List<String> chapterTitle = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                chapterUrl.add(cursor.getString(cursor.getColumnIndex("chapterUrl")));
                chapterTitle.add(cursor.getString(cursor.getColumnIndex("chapterTitle")));
            } while (cursor.moveToNext());
            return new Book.ChapterList(bookName, chapterUrl, chapterTitle);
        }
        return null;
    }

    // 从网络中获取小说列表最后向数据库中添加小说章节列表
    private boolean addChapterList(String bookName) throws MalformedURLException {
        List<Chapter> chapters = getChapterListFromHttp(getBook(bookName).getBookUrl());
        List<String> chapterUrl = new LinkedList<>();
        List<String> chapterTitle = new LinkedList<>();
        for (Chapter c : chapters) {
            chapterUrl.add(c.getUrl());
            chapterTitle.add(c.getTitle());
        }
        Book.ChapterList chapterList = new Book.ChapterList(bookName, chapterUrl, chapterTitle);
        addChapterList(chapterList);
        return true;
    }

    //直接将小说列表添加到数据库中
    private void addChapterList(Book.ChapterList chapterList) {
        SQLiteDatabase db = mChapterListDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("bookName", chapterList.getBookName());
        for (int i = 0; i < chapterList.getChapterUrlList().size(); i++) {
            contentValues.put("chapterUrl", chapterList.getChapterUrlList().get(i));
            contentValues.put("chapterTitle", chapterList.getChapterTitleList().get(i));
            db.insert("chapterList", null, contentValues);
        }
    }

    //获取数据库中的小说所有小说列表
    public List<Book> getBookList() {
        SQLiteDatabase db = mBookDatabaseHelper.getReadableDatabase();
        List<Book> data;
         /*
        * 查询数据
        * */
        Cursor cursor = db.query("Book", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            data = new LinkedList<>();
            do {
                Book book = new Book();
                book.setBookName(cursor.getString(cursor.getColumnIndex("name")));
                book.setBookAuthor(cursor.getString(cursor.getColumnIndex("author")));
                book.setBookStatu(cursor.getString(cursor.getColumnIndex("statue")));
                book.setBookStyle(cursor.getString(cursor.getColumnIndex("bookType")));
                book.setBookUrl(cursor.getString(cursor.getColumnIndex("bookUrl")));
                book.setBookLastUpdate(cursor.getString(cursor.getColumnIndex("recentUpdate")));
                book.setBookNewTopicTitle(cursor.getString(cursor.getColumnIndex("recentTopic")));
                book.setBookNewTopicUrl(cursor.getString(cursor.getColumnIndex("recentTopicUrl")));
                book.setReadChapterIndex(cursor.getInt(cursor.getColumnIndex("chapterIndex")));
                book.setCached(cursor.getShort(cursor.getColumnIndex("isCached")) == 0x10);
                data.add(book);
            } while (cursor.moveToNext());
            db.close();
            cursor.close();
            return data;
        }
        return null;
    }

    // 获取数据库中的小说
    public Book getBook(String bookName) throws MalformedURLException {
        SQLiteDatabase db = mBookDatabaseHelper.getReadableDatabase();
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
            book.setReadChapterIndex(cursor.getInt(cursor.getColumnIndex("chapterIndex")));
            book.setCached(cursor.getShort(cursor.getColumnIndex("isCached")) == 0x10);
            db.close();
            cursor.close();
            return book;
        }
        db.close();
        cursor.close();
        Log.e(TAG, "can't find the book you want,please add it");
        return null;
    }

    //    向数据库中添加小说
    public Book addBook(final String bookName) throws MalformedURLException {
        /*if (getBook(bookName) != null) {
            Log.w(TAG, "This book is exist,don't add again");
            return null;
        }*/
        Book book = getBookFromHttp(bookName);
        SQLiteDatabase db = mBookDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("author", book.getBookAuthor());
        contentValues.put("name", book.getBookName());
        contentValues.put("recentTopic", book.getBookNewTopicTitle());
        contentValues.put("recentTopicUrl", book.getBookNewTopicUrl());
        contentValues.put("bookUrl", book.getBookUrl());
        contentValues.put("recentUpdate", book.getBookLastUpdate());
        contentValues.put("bookType", book.getBookStyle());
        contentValues.put("statue", book.getBookStatu());
        contentValues.put("chapterIndex", book.getReadChapterIndex());
        contentValues.put("isCached",book.isCached()?0x10:0x01);
        db.insert("Book", null, contentValues);
        db.close();
        if (!checkAddSuccess(bookName)) {
            Log.e(TAG, "add book failed");
            return null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    addChapterList(bookName);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return book;
    }

    //    更新数据库中的书籍信息
    public boolean update(Book book) throws MalformedURLException {
        Book updateBook = updateBookByUrl(new URL(book.getBookUrl()));
        SQLiteDatabase db = mBookDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("recentTopic", updateBook.getBookNewTopicTitle());
        contentValues.put("recentTopicUrl", updateBook.getBookNewTopicUrl());
        contentValues.put("recentUpdate", updateBook.getBookLastUpdate());
        int result = db.update("Book", contentValues, "name=?", new String[]{updateBook.getBookName()});
        updateChapterList(book.getBookName());
        db.close();
        return result > 0;
    }

    //    更新数据库中书的最近读取章节
    public void updateBookChapterIndex(Book book) {
        Log.e("updateBookChapterIndex", book.getReadChapterIndex() + "");
        SQLiteDatabase db = mBookDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("chapterIndex", book.getReadChapterIndex());
        db.update("Book", contentValues, "name=?", new String[]{book.getBookName()});
        db.close();
        try {
            Log.e("updateBookChapterIndex", getBook(book.getBookName()).getReadChapterIndex() + "");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //    通过书籍的网址，获取书籍的更新
    private Book updateBookByUrl(URL url) {
        String values = new BaseHttpURLClient().getWebResourse(url);
        return findBookInfoInDetailWeb(values);
    }

    //    查询是否成功向数据库添加书籍
    private boolean checkAddSuccess(String bookName) {
        boolean isSuccess = false;
        SQLiteDatabase db = mBookDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from book where name=?", new String[]{bookName});
        isSuccess = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isSuccess;
    }

    //通过网络获取小说
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
        return findBookInHtml(bookName, sb.toString());
    }

    //从web源码中获取小说
    private Book findBookInHtml(String bookName, String values) {
        Pattern p = Pattern.compile("<title>(.*?)</title>");
        Matcher m = p.matcher(values);
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
            book = findBookInfoInSearchWeb(bookName, values);
            Log.e("jiefly", "find book in search");
        } else {
            book = findBookInfoInDetailWeb(values);
            Log.e("jiefly", "find book direct url");
        }
        if (book != null)
            book.setReadChapterIndex(0);
        return book;
    }

    //从小说index web页面获取小说
    private Book findBookInfoInDetailWeb(String values) {
        Book book = new Book();
        Pattern p = Pattern.compile("<div class=\"book-about clrfix\"(.*?) <dl class=\"chapter-list clrfix\">");
        Matcher m = p.matcher(values);
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

    //从搜索web页面获取小说
    private Book findBookInfoInSearchWeb(String bookName, String values) {
        Pattern p = Pattern.compile("<div class=\"list-lastupdate\">(.*?)</div>");
        Matcher m = p.matcher(values);
        String tmp;
        if (m.find()) {
            tmp = m.group(1);
        } else {
            Log.e(TAG, "can't find book in method >>>>findBookInfoInSearchWeb<<<<");
            return null;
        }
        p = Pattern.compile("<span class=\"class\">(.*?)</span><span class=\"name\"><a href=\"(.*?)\">(.*?)</a><small> / <a href=\"(.*?)\">(.*?)</a></small></span><span class=\"other\">(.*?)<small>(.*?)</small><small>(.*?)</small><small>(.*?)</small></span>");
        m = p.matcher(tmp);
        Book book = new Book();
        while (m.find()) {
            if (m.group(3).equals(bookName)) {
                book.setBookStyle(m.group(1));
                book.setBookUrl("http://www.uctxt.com/" + m.group(2));
                book.setBookName(m.group(3));
                book.setBookNewTopicUrl("http://www.uctxt.com" + m.group(4));
                book.setBookNewTopicTitle(m.group(5));
                book.setBookAuthor(m.group(6));
                book.setBookTotalWords(Integer.parseInt(m.group(7).replaceAll("K", "")) * 1000);
                book.setBookLastUpdate(m.group(8));
                book.setBookStatu(m.group(9));

                return book;
            }
        }
        Log.e(TAG, "can't find book in method >>>>findBookInfoInSearchWeb<<<<");
        return null;
    }

    private List<Chapter> chapterList2List(Book.ChapterList chapterList) {
        List<Chapter> chapters = new LinkedList<>();
        List<String> urlList = chapterList.getChapterUrlList();
        List<String> titleList = chapterList.getChapterTitleList();

        for (int i = 0; i < chapterList.getChapterUrlList().size(); i++) {
            chapters.add(new Chapter(urlList.get(i), titleList.get(i), chapterList.getBookName()));
        }
        return chapters;
    }

    private Book.ChapterList list2ChapterList(List<Chapter> chapters) {
        List<String> url = new LinkedList<>();
        List<String> title = new LinkedList<>();
        for (Chapter c : chapters) {
            url.add(c.getUrl());
            title.add(c.getTitle());
        }
        return new Book.ChapterList(chapters.get(0).getBookName(), url, title);
    }
}
