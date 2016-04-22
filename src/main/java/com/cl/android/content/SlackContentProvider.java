package com.cl.android.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

/**
 * Created by chenling on 2016/3/20.
 * 自定义的 ContentProvider，提供自定义的数据的CRUD
 */
public class SlackContentProvider extends ContentProvider {

    private SlackDbHelper slackDbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private String databasename;
    private String tablename;
    private File sdcardDir;
    @Override
    public boolean onCreate() {
        Log.i("slack", "onCreate SlackContentProvider..........");
        databasename = "userinfo";
        tablename = "users";
        sdcardDir= Environment.getExternalStorageDirectory();
        Log.i("slack", "onCreate.........."+sdcardDir.getAbsolutePath());
        slackDbHelper = new SlackDbHelper(getContext(),databasename,null,1);
        sqLiteDatabase = slackDbHelper.getWritableDatabase();//得到可读可写的数据库
        //return true 表示创建
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i("slack", "query SlackContentProvider..........");
        //public Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
        return sqLiteDatabase.query(tablename,projection,selection,selectionArgs,null,null,sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i("slack", "insert SlackContentProvider..........");
        //public long insert (String table, String nullColumnHack, ContentValues values)
        sqLiteDatabase.insert(tablename,null,values);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i("slack", "delete SlackContentProvider..........");
        return sqLiteDatabase.delete(tablename,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i("slack", "update SlackContentProvider..........");
        return sqLiteDatabase.update(tablename,values,selection,selectionArgs);
    }
}
