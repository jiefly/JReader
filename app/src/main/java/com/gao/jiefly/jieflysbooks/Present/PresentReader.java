package com.gao.jiefly.jieflysbooks.Present;

import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnChapterCacheListener;
import com.gao.jiefly.jieflysbooks.Model.listener.OnMoveNextChapterListener;
import com.gao.jiefly.jieflysbooks.R;
import com.gao.jiefly.jieflysbooks.Utils.ApplicationLoader;
import com.gao.jiefly.jieflysbooks.View.JReader;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jiefly on 2016/7/4.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class PresentReader implements  OnChapterCacheListener, OnMoveNextChapterListener {
    private static final String TAG = "PresentReader";
    public static final int NULL_BOOK_ERROR = 0x001;
    public static final int NULL_CHAPTER_LIST_ERROR = 0x010;
    public static final int CHAPTER_LIST_ERROR = 0x011;
    public static final int NULL_CHAPTER_ERROR = 0x100;
    public static final int NULL_CHAPTER_URL_ERROR = 0x101;
    public static final int GET_CHAPTER_ERROR = 0x110;
    private int cachedCount;
    private Book mBook;
    private List<Chapter> mChapterList;
    private AdvanceDataModel mAdvanceDataModel;
    private JReader view;
    private int mTextColor;
    private int mTextSize;
    private int mViewPagerBackground;
    private Date mDate;
    private String time;

    public PresentReader(Book book, JReader jReader) {
        mBook = book;
        view = jReader;
        mAdvanceDataModel = AdvanceDataModel.build(jReader.getApplicationContext());
        mChapterList = mBook.getList();
        initTime();
        initTextAndBackgroundColor();
        mTextSize = ApplicationLoader.getIntValue(ApplicationLoader.READ_TEXT_SIZE);
        if (mTextSize == 0)
            mTextSize = 20;
    }

    protected void initTime() {
        mDate = new Date();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mDate.setTime(System.currentTimeMillis());
                time = foMateTime(mDate);
            }
        };
//        三十秒更新一次时间
        timer.schedule(timerTask, 0, 1000 * 30);
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
    * 点击右下角向下滑动一页的距离
    * */
    public void scrollDownToNextPage(){
        view.scrollDownToNextPage(this);
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
        view.showLeftMenu();
        view.toogleScreenState();
    }

    public void dismissLeftMenu() {
        view.dismissLeftMenu();
    }

    /*
    * 点击了侧边的章节
    * */
    public void chooseItem(int position) {
//        检测position合法性
        if (position >= 0 || position < mChapterList.size()) {
            dismissLeftMenu();
            view.setCurrentFragment(position);
        }
    }

    /*
    * 下载所有章节
    * */
    public void downloadAllChapters(Book book) {
        view.showSnackbar("下载中，请稍后...");
        if (checkBook(book)) {
            List<String> urlList = book.getChapterList().getChapterUrlList();
            mAdvanceDataModel.cacheChapterFromList(urlList, this);
            view.setProgressMaxValue(urlList.size());
        }
    }

    public void updateBookReadChapterIndex(Book book, int index) {
        mAdvanceDataModel.updateBookReaderChapterIndex(book, index);
    }

    public void updateReadTime(long startTime) {
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
    public void showProgressDialog() {
        view.showProgressDialog();
    }

    public void showProgressDialog(String message) {
        view.showProgressDialog(message);
    }

    public void dismissProgressDialog() {
        view.dismissProgressDialog();
    }

    /*
    * 保存背景颜色以及字体颜色
    * */
    public void saveTextAndBackgroundColor(int textColor, int backgroundColor) {
        this.mTextColor = textColor;
        this.mViewPagerBackground = backgroundColor;
        switch (backgroundColor) {
            case R.drawable.read_default_background:
                ApplicationLoader.save(ApplicationLoader.READER_BACK_GROUND, ApplicationLoader.BACKGROUNT_DEFAULT);
                break;
            case R.color.colorNovelReadBackgroundgray:
                ApplicationLoader.save(ApplicationLoader.READER_BACK_GROUND, ApplicationLoader.BACKGROUNT_NIGHT);
                break;
            case R.color.colorNovelReadBackgroundGraygreen:
                ApplicationLoader.save(ApplicationLoader.READER_BACK_GROUND, ApplicationLoader.BACKGROUNT_GRAY_GREEN);
                break;
            case R.color.colorNovelReadBackgroundBlue:
                ApplicationLoader.save(ApplicationLoader.READER_BACK_GROUND, ApplicationLoader.BACKGROUNT_BLUE);
                break;
            case R.color.colorNovelReadBackgroundgreen1:
                ApplicationLoader.save(ApplicationLoader.READER_BACK_GROUND, ApplicationLoader.BACKGROUNT_GREEN1);
                break;
            default:
                ApplicationLoader.save(ApplicationLoader.READER_BACK_GROUND, ApplicationLoader.BACKGROUNT_DEFAULT);
        }
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
    public boolean checkBook(Book mBook) {
        if (mBook == null) {
            dealErrors(NULL_BOOK_ERROR);
            return false;
        }
        if (mBook.getChapterList() == null) {
            dealErrors(NULL_CHAPTER_LIST_ERROR);
            return false;
        }
        if (mBook.getChapterList().getChapterUrlList() == null
                || mBook.getChapterList().getChapterTitleList() == null
                || mBook.getChapterList().getChapterTitleList().size() != mBook.getChapterList().getChapterUrlList().size()) {
            dealErrors(CHAPTER_LIST_ERROR);
            return false;
        }
        return true;
    }


    @Override
    public void onSuccess() {
        view.updateProgressBar(++cachedCount);
        if (cachedCount >= mChapterList.size()) {
            mBook.setCached(true);
            mAdvanceDataModel.setBookIsCached(mBook);
        }
    }

    @Override
    public void onFailed(String url) {
        Log.e(TAG, "缓存失败：" + url);
    }

    public void initTextAndBackgroundColor() {
        int x = ApplicationLoader.getIntValue(ApplicationLoader.READER_BACK_GROUND);
        if (x == ApplicationLoader.BACKGROUNT_DEFAULT) {
            mViewPagerBackground = R.drawable.read_default_background;
            mTextColor = R.color.colorDefaultBackgroundText;
        } else if (x == ApplicationLoader.BACKGROUNT_NIGHT) {
            mViewPagerBackground = R.color.colorNovelReadBackgroundgray;
            mTextColor = R.color.colorNovelReadBackgroundgrayText;
        } else if (x == ApplicationLoader.BACKGROUNT_GRAY_GREEN) {
            mViewPagerBackground = R.color.colorNovelReadBackgroundGraygreen;
            mTextColor = R.color.colorNovelReadBackgroundGraygreenText;
        } else if (x == ApplicationLoader.BACKGROUNT_BLUE) {
            mViewPagerBackground = R.color.colorNovelReadBackgroundBlue;
            mTextColor = R.color.colorNovelReadBackgroundBlueText;
        } else if (x == ApplicationLoader.BACKGROUNT_GREEN1) {
            mViewPagerBackground = R.color.colorNovelReadBackgroundgreen1;
            mTextColor = R.color.colorNovelReadBackgroundgreen1Text;
        }
    }

    public int getBackgroundColor() {
        return mViewPagerBackground;
    }

    public int getTextColor() {
        return view.getResources().getColor(mTextColor);
    }

    public int getReaderTextSize() {
        return mTextSize;
    }

    public String getTime() {
        return time;
    }

    public void saveReaderTextSize(int size) {
        mTextSize = size;
        ApplicationLoader.save(ApplicationLoader.READ_TEXT_SIZE, mTextSize);
    }

    private String foMateTime(Date date) {

        String mHour;
        if (date.getHours() > 9)
            mHour = String.valueOf(mDate.getHours());
        else
            mHour = "0" + mDate.getHours();
        String mMinutes;
        if (date.getMinutes() > 9)
            mMinutes = String.valueOf(mDate.getMinutes());
        else
            mMinutes = "0" + date.getMinutes();
        return mHour + ":" + mMinutes;
    }

    @Override
    public void onNextChapter() {
        view.vpToNextPage();
    }
}
