package com.structit.apiclient.data.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.structit.apiclient.data.PlayItem;

import java.util.ArrayList;

public class DataHandler {
    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "database.db";

    private SQLiteDatabase mDatabase = null;
    private DataHelper mHandler = null;

    public DataHandler(Context pContext) {
        this.mHandler = new DataHelper(pContext, DB_NAME, null,
                DB_VERSION);
    }

    public void open() {
        if(this.mHandler != null) {
            this.mDatabase = this.mHandler.getWritableDatabase();
        } // Else do nothing
    }

    public long addPlayItem(PlayItem item) {
        ContentValues values = new ContentValues();
        values.put(PlayData.COLUMN_ID, item.getId());
        values.put(PlayData.COLUMN_NAME, item.getName());
        values.put(PlayData.COLUMN_AUTHOR, item.getAuthor());
        values.put(PlayData.COLUMN_RECORD, item.getRecord());
        values.put(PlayData.COLUMN_URL, item.getUrl());
        values.put(PlayData.COLUMN_FILE, item.getFile());

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + PlayData.TABLE
                + " WHERE "
                + PlayData.COLUMN_ID
                + "="
                + item.getId(), null);

        if(cursor.moveToFirst()) {
            return mDatabase.update(PlayData.TABLE, values,
                    PlayData.COLUMN_ID + " = " + item.getId(),
                    null);
        } else {
            return mDatabase.insert(PlayData.TABLE, null, values);
        }
    }

    public ArrayList<PlayItem> getPlayList() {
        ArrayList<PlayItem> list = new ArrayList<PlayItem>();

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + PlayData.TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                PlayItem playItem = new PlayItem(cursor.getInt(PlayData.NUM_COLUMN_ID),
                        cursor.getString(PlayData.NUM_COLUMN_NAME),
                        cursor.getString(PlayData.NUM_COLUMN_AUTHOR),
                        cursor.getString(PlayData.NUM_COLUMN_RECORD),
                        cursor.getString(PlayData.NUM_COLUMN_URL),
                        cursor.getString(PlayData.NUM_COLUMN_FILE));
                list.add(playItem);
            } while (cursor.moveToNext());
        } // Else do nothing

        return list;
    }

    public String getPlayFile(int playId) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + PlayData.TABLE
                + " WHERE "
                + PlayData.COLUMN_ID
                + "="
                + playId, null);

        if(cursor.moveToFirst()) {
            return cursor.getString(PlayData.NUM_COLUMN_FILE);
        } else {
            return "";
        }
    }

    public void drop() {
        mDatabase.execSQL("delete from " + PlayData.TABLE);
    }

    public void close() {
        if(this.mDatabase != null) {
            this.mDatabase.close();
        } // Else do nothing
    }
}
