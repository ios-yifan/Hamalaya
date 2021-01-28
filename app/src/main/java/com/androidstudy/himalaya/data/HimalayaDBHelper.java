package com.androidstudy.himalaya.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.androidstudy.himalaya.utils.Constants;

public class HimalayaDBHelper extends SQLiteOpenHelper {
    public HimalayaDBHelper(Context context) {
        //name 数据库名字
        //factory 游标工厂
        //版本
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据表
        String subTbSql = "create table " + Constants.SUB_TB_NAME + "(" +
                Constants.SUB_ID + " integer primary key autoincrement, " +
                Constants.SUB_COVER_URL + " varchar, " +
                Constants.SUB_TITLE + " varchar," +
                Constants.SUB_DESCRIPTION + " varchar," +
                Constants.SUB_PLAY_COUNT + " integer," +
                Constants.SUB_TRACKS_COUNT + " integer," +
                Constants.SUB_AUTHOR_NAME + " varchar," +
                Constants.SUB_ALBUM_ID + " integer" +
                ")";

        db.execSQL(subTbSql);

        //创建历史记录表
        String historyTbSql = "create table " + Constants.HISTORY_TB_NAME + "(" +
                Constants.HISTORY_ID + " integer primary key autoincrement, " +
                Constants.HISTORY_TRACK_ID + " integer, " +
                Constants.HISTORY_TITLE + " varchar," +
                Constants.HISTORY_COVER + " varchar," +
                Constants.HISTORY_PLAY_COUNT + " integer," +
                Constants.HISTORY_DURATION + " integer," +
                Constants.HISTORY_AUTHOR + " varchar," +
                Constants.HISTORY_UPDATE_TIME + " integer" +
                ")";
        db.execSQL(historyTbSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
