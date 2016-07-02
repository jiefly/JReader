package com.gao.jiefly.jieflysbooks.View;

/**
 * Created by jiefly on 2016/7/2.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface Reader {
//    显示章节内容
    void showChapterContent(String content);
//    显示章节标题
    void showChapterTitle(String title);
//    显示下方状态栏
    void showPhoneStatue();
//    改变章节列表排列顺序
    void toogleChapterListOrder();
}
