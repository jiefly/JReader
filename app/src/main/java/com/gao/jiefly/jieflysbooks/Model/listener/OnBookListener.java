package com.gao.jiefly.jieflysbooks.Model.listener;

import com.gao.jiefly.jieflysbooks.Model.bean.BookManager;

/**
 * Created by jiefly on 2016/8/31.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface OnBookListener {
    void onBookBaseInfoGetSuccess(BookManager book);
    void onBookCompleteInfoGetSuccess(BookManager book);
    void onBookAddFailed(Exception e);
    void onBookUpdateSuccess(BookManager book);
    void onBookUpdateFailed(Exception e);
    void onBookDownloadSuccess(BookManager book);
    void onBookDownloadUpdate(int count);
    void onBookDownloadFailed(Exception e);
}
