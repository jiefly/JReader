package com.gao.jiefly.jieflysbooks.Model.listener;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;

/**
 * Created by jiefly on 2016/8/12.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface OnBookAddFromSoDuListener {
    void onSuccess(Book book);
    void onFailed(Exception error);
}
