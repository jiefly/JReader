package com.gao.jiefly.jieflysbooks.Model.listener;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

/**
 * Created by jiefly on 2016/7/5.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface OnDataModelListener {
    int TYPE_SERVICE_LISTENER = 0x11;
    int TYPE_ACTIVIT_LISTENER = 0x10;
    void onBookAddSuccess(Book book);
    void onBookAddFailed();
    void onBookUpdateSuccess(String bookName,int type);
    void onBookUpdateFailed();
    void onBookRemoveSuccess();
    void onChapterLoadSuccess(Chapter chapter);
    void onBookUpdateCompleted();
}
