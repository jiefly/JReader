package com.gao.jiefly.jieflysbooks.Present;

import android.content.Context;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.View.View;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by jiefly on 2016/6/26.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Present {
    private Context mContext;
    private List<Book> mBookList;
    private static Present instance = null;
    private AdvanceDataModel mAdvanceDataModel;
    View mView;

    private Present(Context context, View view) {
        mContext = context;
        mAdvanceDataModel = AdvanceDataModel.build(context);
        mView = view;
        mBookList = mAdvanceDataModel.getBookList();
    }

    public static Present getInstance(Context context, View view) {
        if (instance == null) {
            synchronized (Present.class) {
                if (instance == null) {
                    instance = new Present(context, view);
                }
            }
        }
        return instance;
    }

    //    显示书籍列表
    public void showBookList() {
        if (mBookList == null) {
//            初始化书籍列表
            mBookList = mAdvanceDataModel.getBookList();
        }
        mView.showBooks(mBookList);
    }

    //    增加书籍
    public void addBook(String bookName) throws MalformedURLException {
        for (Book book : mBookList) {
            if (book.getBookName().equals(bookName)) {
                mView.showSnackbar("the book is exist,don't add again");
                return;
            }
        }
        Book book = mAdvanceDataModel.addBook(bookName);
        Log.i("addBook", book.toString());
        if (book != null) {
//            mBookList.add(book);
            mView.addBook(book);
        }
    }

    //    更新书籍
    public void updateBookList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdvanceDataModel.updateAllBooks();
                mBookList = mAdvanceDataModel.getBookList();
            }
        }).start();
    }

    //    删除书籍
    public void removeBook(int[] booksIndex) {
//       删除掉Present维护的列表中的书
        String[] booksName = new String[booksIndex.length];
        for (int i = 0; i < booksIndex.length; i++) {
            booksName[i] = mBookList.get(booksIndex[i]).getBookName();
            mView.removeBook(mBookList.get(booksIndex[i]));
            mBookList.remove(booksIndex[i]);
        }
//        删除掉数据库中的书
        mAdvanceDataModel.removeBook(booksName);
    }

    //    阅读书籍
    public void readBook(int index) {
        if (mBookList.get(index).getBookNewTopicTitle() != null)
            mView.readBook(mBookList.get(index));
    }

    public void readRecentChapter(Book book) {
    }

    public List<Book> getBookList() {
        return mBookList;
    }
}
