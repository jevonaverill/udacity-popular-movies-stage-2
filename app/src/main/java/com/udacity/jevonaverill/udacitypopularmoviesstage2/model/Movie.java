package com.udacity.jevonaverill.udacitypopularmoviesstage2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.udacity.jevonaverill.udacitypopularmoviesstage2.BuildConfig;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.provider.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by jevon.averill on 8/8/2017.
 */

public class Movie implements Parcelable {

    public static final String EXTRA_MOVIE = "com.udacity.jevonaverill.udacitypopularmoviesstage2" +
            ".EXTRA_MOVIE";

    private static final String MOVIE_ID = "id";
    private static final String MOVIE_ORIGINAL_TITLE = "original_title";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_POSTER_PATH = "poster_path";
    private static final String MOVIE_VOTE_COUNT = "vote_count";
    private static final String MOVIE_VOTE_AVERAGE = "vote_average";
    private static final String MOVIE_RELEASE_DATE = "release_date";

    public long id;
    public String original_title;
    public String overview;
    public String release_date;
    public String poster_path;
    public double vote_average;
    public long vote_count;
    public Bitmap poster;
    public ArrayList<Trailer> trailerList;
    public ArrayList<Review> reviewList;

    public Movie(long id, String original_title, String overview, String poster_path, double vote_average,
                 long vote_count, String release_date) {
        this.id = id;
        this.original_title = original_title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.release_date = release_date;
        this.trailerList = new ArrayList<>();
        this.reviewList = new ArrayList<>();
    }

    public Movie(Bundle bundle)
    {
        this(bundle.getLong(MOVIE_ID),
                bundle.getString(MOVIE_ORIGINAL_TITLE),
                bundle.getString(MOVIE_OVERVIEW),
                bundle.getString(MOVIE_POSTER_PATH),
                bundle.getDouble(MOVIE_VOTE_AVERAGE),
                bundle.getLong(MOVIE_VOTE_COUNT),
                bundle.getString(MOVIE_RELEASE_DATE));
    }


    public Bitmap getPoster() { return poster; }

    public Uri getPosterUri(String posterSize) {
        return Uri.parse(BuildConfig.THE_MOVIE_DB_IMAGE_URL).buildUpon().appendPath(posterSize)
                .appendEncodedPath(this.poster_path).build();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.original_title);
        dest.writeString(this.overview);
        dest.writeString(this.poster_path);
        dest.writeDouble(this.vote_average);
        dest.writeString(this.release_date);
    }

    public void setPoster(Bitmap poster) { this.poster = poster; }

    public void setPosterFromCursor(Cursor cursor){
        byte[] bytes = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER));
        ByteArrayInputStream posterStream = new ByteArrayInputStream(bytes);
        this.poster = BitmapFactory.decodeStream(posterStream);
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailerList = trailers;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviewList = reviews;
    }

    public boolean saveToFavourites(Context context){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieEntry.MOVIE_ID, this.id);
        contentValues.put(MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE, this.original_title);
        contentValues.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, this.overview);
        contentValues.put(MovieContract.MovieEntry.MOVIE_POSTER_PATH, this.poster_path);
        contentValues.put(MovieContract.MovieEntry.MOVIE_VOTE_COUNT, this.vote_count);
        contentValues.put(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE, this.vote_average);
        contentValues.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, this.release_date);
        contentValues.put(MovieContract.MovieEntry.MOVIE_TRAILERS, Trailer.arrayToString(trailerList));
        contentValues.put(MovieContract.MovieEntry.MOVIE_REVIEWS, Review.arrayToString(reviewList));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.poster.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        contentValues.put(MovieContract.MovieEntry.MOVIE_POSTER, bytes);

        if (context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,contentValues)
                != null){
            Toast.makeText(context, "Add movie as favourite!", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, R.string.bookmark_added,Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Add Error!", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, R.string.bookmark_insert_error,Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean removeFromFavourites(Context context){
        long deletedRows = context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_ID + "=?", new String[]{Long.toString(this.id)});
        if (deletedRows > 0) {
            Toast.makeText(context, "Remove movie from favourites!", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, R.string.bookmark_deleted,Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Remove error!", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, R.string.bookmark_delete_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isFavourite(Context context){
        Cursor cursor = context.getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry.MOVIE_ID},
                        MovieContract.MovieEntry.MOVIE_ID + "=?",
                        new String[]{Long.toString(this.id)},null);
        if (cursor != null) {
            boolean isFavourite = cursor.getCount() > 0;
            cursor.close();
            return isFavourite;
        }
        return false;
    }

    private Movie(Parcel in) {
        id = in.readLong();
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        vote_count = in.readLong();
        vote_average = in.readDouble();
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putLong(MOVIE_ID, id);
        bundle.putString(MOVIE_ORIGINAL_TITLE, original_title);
        bundle.putString(MOVIE_OVERVIEW, overview);
        bundle.putString(MOVIE_POSTER_PATH, poster_path);
        bundle.putDouble(MOVIE_VOTE_AVERAGE, vote_average);
        bundle.putLong(MOVIE_VOTE_COUNT, vote_count);
        bundle.putString(MOVIE_RELEASE_DATE, release_date);
        return bundle;
    }

    public static Movie getMovieFromJson(JSONObject jsonObject) throws JSONException
    {
        return new Movie(jsonObject.getLong(MOVIE_ID),
                jsonObject.getString(MOVIE_ORIGINAL_TITLE),
                jsonObject.getString(MOVIE_OVERVIEW),
                jsonObject.getString(MOVIE_POSTER_PATH),
                jsonObject.getDouble(MOVIE_VOTE_AVERAGE),
                jsonObject.getLong(MOVIE_VOTE_COUNT),
                jsonObject.getString(MOVIE_RELEASE_DATE));
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

}
