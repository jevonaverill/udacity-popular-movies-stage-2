package com.udacity.jevonaverill.udacitypopularmoviesstage2.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.BuildConfig;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.NetworkUtility;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.R;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.adapter.TrailerAdapter;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.model.Movie;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.model.Review;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.model.Trailer;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.provider.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jevon.averill on 8/8/2017.
 */

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    Movie mMovie;

    @BindView(R.id.tvMovieDetailsTitle)
    TextView mTitle;

    @BindView(R.id.tvMovieDetailsReleaseDate)
    TextView mReleaseDate;

    @BindView(R.id.tvMovieDetailsVoteAverage)
    TextView mVoteAverage;

    @BindView(R.id.tvOverview)
    TextView mOverview;

    @BindView(R.id.ivMovieDetails)
    ImageView ivMovieDetails;

    @BindView(R.id.lvTrailers)
    ListView mTrailersListView;

    @BindView(R.id.btnFavourite)
    ImageButton mImageButtonFavourite;

    ArrayList<Trailer> mTrailerList;
    ArrayList<Review> mReviewList;

    TrailerAdapter mTrailerAdapter;

    private static final int LOADER_ID = 818;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        mTrailerAdapter = new TrailerAdapter(this);
        mTrailersListView.setAdapter(mTrailerAdapter);

        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra(Movie.EXTRA_MOVIE)){
            mMovie = new Movie(callerIntent.getBundleExtra(Movie.EXTRA_MOVIE));
            mTitle.setText(mMovie.original_title);

            String initialDate = mMovie.release_date;
            SimpleDateFormat input = new SimpleDateFormat("yyyy-dd-mm");
            SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy");

            try {
                Date newDate = input.parse(initialDate);
                mReleaseDate.setText(String.format(getString(R.string.movie_release_date), output.format(newDate)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mVoteAverage.setText(String.format(getString(R.string.movie_vote_average), mMovie.vote_average));
            mOverview.setText(mMovie.overview);
            final Bitmap[] posterBitmap = new Bitmap[1];
            Bundle args = new Bundle();
            if (mMovie.isFavourite(this)){
                mImageButtonFavourite.setImageResource(android.R.drawable.btn_star_big_on);
                args.putBoolean("local",true);
            } else {
                mImageButtonFavourite.setImageResource(android.R.drawable.btn_star_big_off);
                Picasso.with(this).load(mMovie.getPosterUri(getString(R.string.movie_image_default_size)))
                        .into(new Target(){
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                posterBitmap[0] = bitmap;
                                mMovie.setPoster(posterBitmap[0]);
                                ivMovieDetails.setImageBitmap(posterBitmap[0]);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }
                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                args.putBoolean("local",false);
            }
            ivMovieDetails.setImageBitmap(mMovie.getPoster());

            mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Uri uri = mTrailerAdapter.getTrailerUri(position);
                    if (uri != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });
            getSupportLoaderManager().restartLoader(LOADER_ID, args, this);
        }
    }

    @OnClick(R.id.share)
    public void share(View v) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, mMovie.original_title);
        share.putExtra(Intent.EXTRA_TEXT, mTrailerList.get(0).url);
        startActivity(Intent.createChooser(share, "Share Movie Trailer!"));
    }

    @OnClick(R.id.btnFavourite)
    public void toggleFavourite(View v) {
        Context context = getApplicationContext();
        if (!mMovie.isFavourite(context)) {
            if (mMovie.saveToFavourites(context)) {
                mImageButtonFavourite.setImageResource(android.R.drawable.btn_star_big_on);
            }
        } else {
            if (mMovie.removeFromFavourites(context)) {
                mImageButtonFavourite.setImageResource(android.R.drawable.btn_star_big_off);
            }
        }
    }

    @OnClick(R.id.btnReview)
    public void seeReviews(View v) {
        String reviewsString = Review.arrayToString(mReviewList);
        Intent reviewsIntent = new Intent(getApplicationContext(), ReviewActivity.class);
        reviewsIntent.putExtra(getString(R.string.movie_reviews_intent_extra), reviewsString);
        startActivity(reviewsIntent);
    }

    @Override
    public Loader<Object> onCreateLoader(final int id, final Bundle args) {

        return new AsyncTaskLoader<Object>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Void loadInBackground() {

                if (args != null && args.size() != 0) {
                    boolean local = args.getBoolean("local");
                    long id = mMovie.id;

                    if (!local) {
                        NetworkUtility networkUtility = new NetworkUtility();
                        URL requestTrailersUrl = networkUtility.buildTrailersUrl(id);
                        URL requestReviewsUrl = networkUtility.buildReviewsUrl(id);
                        try {
                            String JSONResponseTrailers = networkUtility.getResponseFromHttpUrl(requestTrailersUrl);
                            mTrailerList = fetchTrailersFromJson(JSONResponseTrailers);

                            String JSONResponseReviews = networkUtility.getResponseFromHttpUrl(requestReviewsUrl);
                            mReviewList = fetchReviewsFromJson(JSONResponseReviews);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Cursor cursor = getContentResolver()
                                .query(MovieContract.MovieEntry.CONTENT_URI,
                                        new String[]{MovieContract.MovieEntry.MOVIE_TRAILERS, MovieContract.MovieEntry.MOVIE_REVIEWS, MovieContract.MovieEntry.MOVIE_POSTER},
                                        MovieContract.MovieEntry.MOVIE_ID + "=?",
                                        new String[]{Long.toString(id)}, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            mTrailerList = Trailer.stringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TRAILERS)));
                            mReviewList = Review.stringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_REVIEWS)));
                            mMovie.setPosterFromCursor(cursor);
                            cursor.close();
                        }

                    }
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mMovie.setTrailers(mTrailerList);
        mMovie.setReviews(mReviewList);
        ivMovieDetails.setImageBitmap(mMovie.getPoster());
        if (mTrailerList!=null){
            mTrailerAdapter.setTrailers(mTrailerList);
            setListViewHeightBasedOnChildren(mTrailersListView);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private ArrayList<Trailer> fetchTrailersFromJson(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        JSONArray trailers = json.getJSONArray("results");
        ArrayList<Trailer> result = new ArrayList<>();

        for (int i = 0; i< trailers.length(); i++){
            JSONObject trailerObject = trailers.getJSONObject(i);
            String site = trailerObject.getString("site");
            if (site.equals("YouTube")){
                String url = BuildConfig.THUMBNAIL_URL+trailerObject.getString("key");
                result.add(new Trailer(trailerObject.getString("name"),url));
            }
        }

        return result;
    }

    private ArrayList<Review> fetchReviewsFromJson(String jsonString) throws JSONException{
        JSONObject json = new JSONObject(jsonString);
        JSONArray trailers = json.getJSONArray("results");
        ArrayList<Review> result = new ArrayList<>();

        for (int i = 0; i< trailers.length(); i++){
            JSONObject trailerObject = trailers.getJSONObject(i);
            result.add(new Review(trailerObject.getString("author"),trailerObject.getString("content")));
        }

        return result;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {

        TrailerAdapter trailerAdapter = (TrailerAdapter) listView.getAdapter();
        if (trailerAdapter == null) {
            return;
        }

        int elements = trailerAdapter.getCount();

        if (elements>0) {
            View listItem = trailerAdapter.getView(0, null, listView);

            listItem.measure(0,0);

            int totalHeight = listItem.getMeasuredHeight() * (elements+2);

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight() * (trailerAdapter.getCount()-1));
            listView.setLayoutParams(params);
        }
    }

}
