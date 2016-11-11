package com.mahoucoder.misakagate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_DATELINE;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_EXTRA;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_FID;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_LASTPOST;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_PIC;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_SEASON;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_SUBJECT;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_TID;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.COLUMN_NAME_YEAR;
import static com.mahoucoder.misakagate.data.AnimeListContract.Anime.TABLE_NAME;

/**
 * Created by jamesji on 11/11/2016.
 */

public class AnimeListDBHelper extends SQLiteOpenHelper {

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String NOTNULL = " NOT NULL";
    private static final String COMMA_SEP = ",";
    public static final String DATABASE_NAME = "animations.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_NAME_FID + TYPE_INTEGER + NOTNULL + COMMA_SEP +
                    COLUMN_NAME_TID + TYPE_INTEGER + NOTNULL + COMMA_SEP +
                    COLUMN_NAME_SUBJECT + TYPE_TEXT + COMMA_SEP +
                    COLUMN_NAME_DATELINE + TYPE_INTEGER + COMMA_SEP +
                    COLUMN_NAME_LASTPOST + TYPE_INTEGER + COMMA_SEP +
                    COLUMN_NAME_PIC + TYPE_TEXT + COMMA_SEP +
                    COLUMN_NAME_YEAR + TYPE_TEXT + COMMA_SEP +
                    COLUMN_NAME_SEASON + TYPE_TEXT + COMMA_SEP +
                    COLUMN_NAME_EXTRA + TYPE_TEXT + ")";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public AnimeListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
