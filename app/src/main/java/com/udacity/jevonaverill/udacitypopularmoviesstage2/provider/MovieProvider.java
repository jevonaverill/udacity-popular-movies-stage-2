package com.udacity.jevonaverill.udacitypopularmoviesstage2.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by jevon.averill on 8/7/2017.
 */

public class MovieProvider extends ContentProvider {

    public static final int MOVIE_CODE = 101;

    private static UriMatcher uriMatcher = buildUriMatcher();

    private MovieDBHelper mMovieDBHelper;

    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE_CODE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDBHelper = new MovieDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)){
            case MOVIE_CODE:{
                SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,selection,selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri = null;

        switch (uriMatcher.match(uri)){
            case MOVIE_CODE: {
                SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0){
                    returnUri =  MovieContract.MovieEntry.buildMovieUri(id);
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown URI: " + uri);
            }
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case MOVIE_CODE: {
                SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
                return db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
