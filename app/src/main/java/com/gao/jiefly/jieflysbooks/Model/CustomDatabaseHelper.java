package com.gao.jiefly.jieflysbooks.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class CustomDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "CustomDatabaseHelper";
    public static final String CREATE_BOOK = "create table book ("
            + "id integer primary key autoincrement, "
            + "author text, "
            + "name text, "
            + "statue text, "
            + "recentTopic text, "
            + "recentTopicUrl text, "
            + "recentUpdate text, "
            + "bookUrl text, "
            + "chapterIndex int,"
            + "isCached int,"
            + "hasUpdate int,"
            + "bookType text)";
    public static final String CREATE_CHAPTER_LIST = "create table chapterList ("
            + "id integer primary key autoincrement, "
            + "bookName text, "
            + "chapterTitle text, "
            + "chapterUrl text)";
    public static final int BOOK_TYPE = 0;
    public static final int CHAPTER_LIST_TYPE = 1;
    private int databaseType;
    private Context mContext;

    public CustomDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version) {
        super(context, name, cursorFactory, version);
        mContext = context;
        databaseType = BOOK_TYPE;
    }

    public CustomDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version, int databaseType) {
        super(context, name, cursorFactory, version);
        mContext = context;
        this.databaseType = databaseType;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        switch (databaseType) {
            case BOOK_TYPE:
                db.execSQL(CREATE_BOOK);
                Log.i(TAG, "create book database success");
                break;
            case CHAPTER_LIST_TYPE:
                db.execSQL(CREATE_CHAPTER_LIST);
                Log.i(TAG, "create chapter list database success");
                break;
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
