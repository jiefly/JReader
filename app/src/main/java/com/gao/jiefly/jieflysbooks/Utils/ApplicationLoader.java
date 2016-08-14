package com.gao.jiefly.jieflysbooks.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationLoader extends Application {

    public static volatile Context applicationContext = null;
    static SharedPreferences mSharedPreferences;
    static SharedPreferences.Editor mEditor;
    public static int DEFAULT_INT = 0;
    public static Boolean DEFAULT_BOOLEAN = true;
    public static String DEFAULT_STRING = "jiefly";
    public static String IMAGE_WEB_NAME = "秋水轩";
    public static String DEFAULT_BOOK_COVER = "http://www.qiushuixuan.cc/modules/article/images/nocover.jpg";
    //    总共阅读时长
    public static String TOTAL_READ_TIME = "totleReadTime";
    //    今日阅读时长
    public static String DAILY_READ_TIME = "dailyReadTime";
    //    当前日期
    public static String CURRENT_DAY = "currentDay";
    //    是否第一次打开app
    public static String FIRST_TIME = "firstTime";
    //    app打开次数
    public static String APP_OPEN_COUNT = "appOpenCount";
    //    是否需要后台更新
    public static String IS_NEED_UPDATE_BG = "isNeedUpdateBackGround";
    //    阅读背景颜色
    public static String READER_BACK_GROUND = "readBackGround";
    //    阅读字体大小
    public static String READ_TEXT_SIZE = "readTextSize";
    //    书籍排序方式
    public static String BOOK_ORDER = "bookOrderWay";
    //    后台书籍更新频率
    public static String UPDATE_FREQUENCE = "updateFrequence";

    //    按加入时间排序
    public static int SORT_BY_ADD_TIME = 0x10;
    //    按更新时间排序
    public static int SORT_BY_UPDATE_TIME = 0x11;
    //    智能排序
    public static int SORT_SMART = 0x01;

    public static int BACKGROUNT_DEFAULT = 0x000;
    public static int BACKGROUNT_NIGHT = 0x110;
    public static int BACKGROUNT_BLUE = 0x100;
    public static int BACKGROUNT_GRAY_GREEN = 0x010;
    public static int BACKGROUNT_GREEN1 = 0x001;

    public static int UPDATE_FREQUENCE_1 = 1;
    public static int UPDATE_FREQUENCE_2 = 2;
    public static int UPDATE_FREQUENCE_3 = 3;
    public static int UPDATE_FREQUENCE_4 = 4;
    public static int UPDATE_FREQUENCE_5 = 5;
    public static int UPDATE_FREQUENCE_6 = 6;

    @Override
    public void onCreate() {
        super.onCreate();
        //        初始化SharedPrefereces
        mSharedPreferences = getSharedPreferences("localConfig", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        applicationContext = getApplicationContext();

    }

    public static void save(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public static void save(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public static void save(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public static int getIntValue(String key) {
        return mSharedPreferences.getInt(key, DEFAULT_INT);
    }

    public static String getStringValue(String key) {
        return mSharedPreferences.getString(key, DEFAULT_STRING);
    }

    public static Boolean getBooleanValue(String key) {
        return mSharedPreferences.getBoolean(key, DEFAULT_BOOLEAN);
    }

}
