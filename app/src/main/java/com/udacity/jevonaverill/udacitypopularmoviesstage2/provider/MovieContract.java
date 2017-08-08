package com.udacity.jevonaverill.udacitypopularmoviesstage2.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jevon.averill on 8/7/2017.
 */

public class MovieContract {

    public static String CONTENT_AUTHORITY = "com.udacity.jevonaverill.udacitypopularmoviesstage2";
    public static String PATH_MOVIE = "movies";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "favourite_movies";

        public static final String MOVIE_ID = "id";
        public static final String MOVIE_ORIGINAL_TITLE = "original_title";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_POSTER = "poster";
        public static final String MOVIE_POSTER_PATH = "poster_path";
        public static final String MOVIE_VOTE_AVERAGE = "voteAverage";
        public static final String MOVIE_VOTE_COUNT = "voteCount";
        public static final String MOVIE_RELEASE_DATE = "releaseDate";
        public static final String MOVIE_TRAILERS = "trailers";
        public static final String MOVIE_REVIEWS = "reviews";

        public static final int KEY_ID = 0;
        public static final int KEY_ORIGINAL_TITLE = 1;
        public static final int KEY_OVERVIEW = 2;
        public static final int KEY_POSTER = 3;
        public static final int KEY_POSTER_PATH = 4;
        public static final int KEY_VOTE_AVERAGE = 5;
        public static final int KEY_VOTE_COUNT = 6;
        public static final int KEY_RELEASE_DATE = 7;
        public static final int KEY_TRAILERS = 8;
        public static final int KEY_REVIEWS = 9;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
        }

    }

}
