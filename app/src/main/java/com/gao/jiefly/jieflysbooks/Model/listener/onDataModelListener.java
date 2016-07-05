package com.gao.jiefly.jieflysbooks.Model.listener;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;

/**
 * Created by jiefly on 2016/7/5.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface OnDataModelListener {
    void onBookAddSuccess(Book book);
    void onBookUpdataSuccess(String bookName);
    void onBookRemoveSuccess();
    void onChapterLoadSuccess();
}
