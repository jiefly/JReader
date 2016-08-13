package com.gao.jiefly.jieflysbooks.Model.listener;

/**
 * Created by jiefly on 2016/8/12.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface OnBookImageGetListener {
    void onSuccess(String  imageUrl);
    void onFailed(Exception error);
}
