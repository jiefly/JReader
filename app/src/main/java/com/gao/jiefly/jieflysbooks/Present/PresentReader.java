package com.gao.jiefly.jieflysbooks.Present;

import android.support.v4.view.ViewPager;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Utils.ApplicationLoader;
import com.gao.jiefly.jieflysbooks.View.JReader;

import java.util.List;

/**
 * Created by jiefly on 2016/7/4.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class PresentReader implements ViewPager.OnPageChangeListener {
    public static final int NULL_BOOK_ERROR = 0x001;
    public static final int NULL_CHAPTER_LIST_ERROR = 0x010;
    public static final int CHAPTER_LIST_ERROR = 0x011;
    public static final int NULL_CHAPTER_ERROR = 0x100;
    public static final int NULL_CHAPTER_URL_ERROR = 0x101;
    public static final int GET_CHAPTER_ERROR = 0x110;
    private Book mBook;
    private List<Chapter> mChapterList;
    private AdvanceDataModel mAdvanceDataModel;
    private JReader view;

    public PresentReader(Book book, JReader jReader) {
        mBook = book;
        view = jReader;
        mAdvanceDataModel = AdvanceDataModel.build(jReader.getApplicationContext());
        mChapterList = mBook.getList();
    }

    /*
    * 获取章节数据
    * */
    public Chapter getCompleteChapter(Chapter chapter) {
        if (chapter == null) {
            dealErrors(NULL_CHAPTER_ERROR);
            return null;
        }

        if (chapter.getUrl() == null && !chapter.getUrl().equals("")) {
            dealErrors(NULL_CHAPTER_URL_ERROR);
            return null;
        }

        Chapter result = mAdvanceDataModel.getChapter(chapter.getUrl());

        if (chapter.getTitle() != null)
            result.setTitle(chapter.getTitle());

        if (result == null) {
            dealErrors(GET_CHAPTER_ERROR);
            return null;
        }

        return result;
    }


    /*
    * 退出全屏模式
    * */
    public void showBottomAndToolBar() {

    }

    /*
    * 显示侧边章节目录
    * */
    public void showLeftMenu() {

    }

    /*
    * 点击了侧边的章节
    * */
    public void chooseItem(int position) {

    }

    /*
    * 下载所有章节
    * */
    public void downloadAllChapters(Book book) {

    }

    public void updateBookReadChapterIndex(Book book,int index){
        mAdvanceDataModel.updateBookReaderChapterIndex(book,index);
    }


    public void updateReadTime(long startTime){
        int totleTime = ApplicationLoader.getIntValue(ApplicationLoader.TOTAL_READ_TIME);
        int dailyTime = ApplicationLoader.getIntValue(ApplicationLoader.DAILY_READ_TIME);
        int timeInThisSession = (int) ((System.currentTimeMillis() - startTime) / 60 / 1000);
//        保存今日阅读时间
        ApplicationLoader.save(ApplicationLoader.DAILY_READ_TIME, timeInThisSession + dailyTime);
//        保存历史阅读时间
        ApplicationLoader.save(ApplicationLoader.TOTAL_READ_TIME, timeInThisSession + totleTime);
    }

    /*
    * 显示加载数据中动画
    * */
    public void showProgressDialog(){
        view.showProgressDialog();
    }

    public void showProgressDialog(String message){
        view.showProgressDialog(message);
    }

    public void dismissProgressDialog(){
        view.dismissProgressDialog();
    }
    /*
    * 传入的book为空错误
    * */
    public void dealErrors(int errorType) {
        switch (errorType) {
//            初始化Jreader时候传入的book为空
            case NULL_BOOK_ERROR:
                view.showToast("亲，您要看的书籍找不到啦！请重新点击该书籍，或者重新添加该书籍");
                view.backToMain();
                break;
//            book的章节列表为空
            case NULL_CHAPTER_LIST_ERROR:
                view.showToast("亲，这本书的章节列表找不到啦！请重新点击该书籍，或者重新添加该书籍");
                view.backToMain();
                break;
//            章节列表中的url列表或title列表为空或者size不相等
            case CHAPTER_LIST_ERROR:
                view.showToast("亲，这本书发生了异常哦！");
                break;
            case NULL_CHAPTER_ERROR:
                view.showSnackbar("传入的章节为空，不能返回章节完全体");
                break;
            case NULL_CHAPTER_URL_ERROR:
                view.showSnackbar("传入的章节地址为空，不能返回章节完全体");
                break;
            case GET_CHAPTER_ERROR:
                view.showSnackbar("获取章节失败");
                break;
        }
    }

    /*
    * 检查main传过来的book的合法性
    * */
    public void checkBook() {
        if (mBook == null)
            dealErrors(NULL_BOOK_ERROR);
        if (mBook.getChapterList() == null)
            dealErrors(NULL_CHAPTER_LIST_ERROR);
        if (mBook.getChapterList().getChapterUrlList() == null
                || mBook.getChapterList().getChapterTitleList() == null
                || mBook.getChapterList().getChapterTitleList().size() != mBook.getChapterList().getChapterUrlList().size())
            dealErrors(CHAPTER_LIST_ERROR);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
