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
    public static final String CREATE_BOOK = "create table book ("
            +"id integer primary key autoincrement, "
            +"author text, "
            +"name text, "
            +"statue text, "
            +"recentTopic text, "
            +"recentTopicUrl text, "
            +"bookUrl text,"
            +"bookType text)";
    private Context mContext;
    public CustomDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory,int version){
        super(context,name,cursorFactory,version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        Log.d("DatabaseHelper","create database success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
