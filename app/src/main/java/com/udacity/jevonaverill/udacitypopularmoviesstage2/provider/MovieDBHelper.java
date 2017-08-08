package com.udacity.jevonaverill.udacitypopularmoviesstage2.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jevon.averill on 8/7/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UdacityPopularMovies.db";

    private static final int DB_VERSION = 1;

    public MovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_QUERY =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "(" +
                        MovieContract.MovieEntry.MOVIE_ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.MOVIE_POSTER + " BLOB NOT NULL, " +
                        MovieContract.MovieEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.MOVIE_VOTE_COUNT + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.MOVIE_TRAILERS + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.MOVIE_REVIEWS + " TEXT NOT NULL" +
                        ")";
        db.execSQL(SQL_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }

}
