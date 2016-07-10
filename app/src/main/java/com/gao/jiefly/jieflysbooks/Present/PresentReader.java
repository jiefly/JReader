package com.gao.jiefly.jieflysbooks.Present;

import android.content.Context;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

import java.util.List;

/**
 * Created by jiefly on 2016/7/4.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class PresentReader {
    private Book mBook;
    private List<Chapter> mChapterList;
    private AdvanceDataModel mAdvanceDataModel;

    public PresentReader(Book book , Context context) {
        mBook = book;
        mAdvanceDataModel = AdvanceDataModel.build(context);
        /*try {
            mChapterList = mAdvanceDataModel.getChapterList(book.getBookName());
        } catch (MalformedURLException back_btn_bg) {
            back_btn_bg.printStackTrace();
        }*/
    }


}
