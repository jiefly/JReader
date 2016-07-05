package com.gao.jiefly.jieflysbooks.Present;

import android.content.Context;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataModelListener;
import com.gao.jiefly.jieflysbooks.Utils.Utils;
import com.gao.jiefly.jieflysbooks.View.View;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiefly on 2016/6/26.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class PresentMain implements OnDataModelListener {
    private Context mContext;
    private List<Book> mBookList = new ArrayList<>();
    private static PresentMain instance = null;
    private AdvanceDataModel mAdvanceDataModel;
    View mView;

    private PresentMain(Context context, View view) {
        mContext = context;
        mAdvanceDataModel = AdvanceDataModel.build(context, this);
        mView = view;
        mBookList = mAdvanceDataModel.getBookList();
    }

    public static PresentMain getInstance(Context context, View view) {
        if (instance == null) {
            synchronized (PresentMain.class) {
                if (instance == null) {
                    instance = new PresentMain(context, view);
                }
            }
        }
        return instance;
    }

    //    显示书籍列表
    public void showBookList() {
        if (mBookList.size() == 0) {
//            初始化书籍列表
            mBookList.addAll(mAdvanceDataModel.getBookList());
        }
        mView.showBooks(mBookList);
    }

    //    增加书籍
    public void addBook(String bookName) throws MalformedURLException {
        if (mBookList != null) {
            for (Book book : mBookList) {
                if (book.getBookName().equals(bookName)) {
                    mView.showSnackbar("the book is exist,don't add again");
                    return;
                }
            }
        }
        mAdvanceDataModel.addBookSyn(bookName);
        Log.e("addBook","正在加载书籍");
        /*Book book = mAdvanceDataModel.addBook(bookName);
        Log.i("addBook", book.toString());
        //            mBookList.add(book);
        mView.addBook(book);*/
    }

    //    更新所有书籍
    public void updateBookList() {
        mAdvanceDataModel.updateAllBooks();
        mBookList = mAdvanceDataModel.getBookList();
    }
/*//    更新书籍的最近读取章节
    public void refreshChapterIndex(String bookName){
        mAdvanceDataModel.updateBookReaderChapterIndex();
    }*/

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
        mAdvanceDataModel.removeBookSyn(booksName);
    }

    //    阅读书籍
    public void readBook(int index) throws MalformedURLException {
        if (mBookList.get(index).getBookNewTopicTitle() != null) {
            Book book = mAdvanceDataModel.getBook(mBookList.get(index).getBookName());
            book.setChapterList(Utils.list2ChapterList(mAdvanceDataModel.getChapterList(book.getBookName())));
            mView.readBook(book);
        }
    }

    public void readRecentChapter(Book book) {
    }

    public List<Book> getBookList() {
        mBookList = mAdvanceDataModel.getBookList();
        return mBookList;
    }

    @Override
    public void onBookAddSuccess(Book book) {
        mView.addBook(book);
        Log.e("addBook","加载书籍完毕");
    }

    @Override
    public void onBookUpdataSuccess(String bookName) {
        mView.updateBook(bookName);
        Log.e("updateBook","更新书籍完毕");
    }

    @Override
    public void onBookRemoveSuccess() {
        Log.e("removeBook","删除书籍完毕");
    }

    @Override
    public void onChapterLoadSuccess() {

    }
}
