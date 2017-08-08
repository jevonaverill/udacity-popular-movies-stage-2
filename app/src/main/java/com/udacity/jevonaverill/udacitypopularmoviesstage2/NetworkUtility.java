package com.udacity.jevonaverill.udacitypopularmoviesstage2;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by jevon.averill on 8/8/2017.
 */

public class NetworkUtility {

    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";
    private static final String TRAILERS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";

    public URL buildMoviesUrl(int page, String sorting) {

        Uri builtUri = Uri.parse(BuildConfig.THE_MOVIE_DB_API_URL).buildUpon()
                .appendPath(sorting)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    public URL buildTrailersUrl(long id) {

        Uri builtUri = Uri.parse(BuildConfig.THE_MOVIE_DB_API_URL).buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(TRAILERS_PATH)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    public URL buildReviewsUrl(long id) {

        Uri builtUri = Uri.parse(BuildConfig.THE_MOVIE_DB_API_URL).buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    public String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }

    }

}
