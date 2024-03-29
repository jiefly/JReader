package com.gao.jiefly.jieflysbooks.View;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnMoveNextChapterListener;

/**
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface FragmentReader {
    void showChapter(Chapter chapter);
    Chapter getChapter();
    void addTextSize();
    void reduceTextSize();
    void setTextColor(int color);
    void setTime(String time);
    void setTextSize(int textSize);
    void scrollDownToNextPage(OnMoveNextChapterListener listener);
}
