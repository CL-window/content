package com.cl.android.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chenling on 2016/3/20.
 */
public class SlackDbHelper extends SQLiteOpenHelper {

    public SlackDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i("slack", "SlackDbHelper SQLiteDatabase..........");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String ddl="create table users (_id integer primary key autoincrement,username varchar(100))";
        db.execSQL(ddl);
        Log.i("slack","onCreate SQLiteDatabase..........");
    }

    //更新表，版本号变化时
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String ddl="create table users (id integer primary key autoincrement,username varchar(100),password varchar(100))";
        db.execSQL(ddl);
        Log.i("slack", "onUpgrade SQLiteDatabase..........");
    }
}
