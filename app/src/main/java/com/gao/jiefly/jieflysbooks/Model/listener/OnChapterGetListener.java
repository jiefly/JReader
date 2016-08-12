package com.gao.jiefly.jieflysbooks.Model.listener;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

import java.util.List;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface OnChapterGetListener {
    void onSuccess(List<Chapter> result);
    void onFailed(Exception e);
}
