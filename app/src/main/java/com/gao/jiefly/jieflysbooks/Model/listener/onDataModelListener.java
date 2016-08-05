package com.gao.jiefly.jieflysbooks.Model.listener;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

/**
 * Created by jiefly on 2016/7/5.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface OnDataModelListener {
    void onBookAddSuccess(Book book);
    void onBookAddFailed();
    void onBookUpdateSuccess(String bookName);
    void onBookUpdateFailed();
    void onBookRemoveSuccess();
    void onChapterLoadSuccess(Chapter chapter);
    void onBookUpdateCompleted();
}
