package com.gao.jiefly.jieflysbooks.Model.listener;

import com.gao.jiefly.jieflysbooks.Model.bean.BookManager;

/**
 * Created by jiefly on 2016/8/31.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface OnBookAddListener {
    void onBookBaseInfoGetSuccess(BookManager book);
    void onBookCompleteInfoGetSuccess(BookManager book);
    void onBookAddFailed(Exception e);
}
