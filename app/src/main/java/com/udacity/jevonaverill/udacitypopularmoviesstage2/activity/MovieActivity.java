package com.udacity.jevonaverill.udacitypopularmoviesstage2.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.jevonaverill.udacitypopularmoviesstage2.NetworkUtility;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.R;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.adapter.MovieImageAdapter;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.model.Movie;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.provider.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity extends AppCompatActivity implements MovieImageAdapter.onMovieImageClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    @BindView(R.id.rvMovieImage)
    RecyclerView mRecyclerView;

    @BindView(R.id.tvErrorMovieImage)
    TextView mErrorTextView;

    @BindView(R.id.tvErrorNoFavouriteFound)
    TextView mNoFavouritesTextView;

    @BindView(R.id.pbLoading)
    ProgressBar mProgressBar;

    @BindString(R.string.pref_sort_by_key)
    String sortBy;

    @BindString(R.string.pref_favourite_movies)
    String favouriteMovies;

    MovieImageAdapter mMovieImageAdapter;

    private static final int LOADER_ID = 0;

    private int mPagesLoaded;
    private int maxPages = 20;

    private GridLayoutManager mGridLayoutManager;

    private AlertDialog mSortingDialog;

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener;

    private String criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        } else {
            mGridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        }

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieImageAdapter = new MovieImageAdapter(this);

        mRecyclerView.setAdapter(mMovieImageAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!criteria.equals(favouriteMovies)) {
                    int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    int pastVisibleItems = mGridLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loadMovieImages();
                    }
                }
            }
        });

        mPagesLoaded = 0;

        mSortingDialog = initSortingDialog();

        initSharedPreferences();
        if (savedInstanceState != null) {
            mMovieImageAdapter.restoreInstanceState(savedInstanceState);
            mRecyclerView.scrollToPosition(savedInstanceState.getInt("SCROLL_POSITION"));
        } else {
            loadMovieImages();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMovieImageAdapter.saveInstanceState(outState);
        int scrollPosition = mGridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt("SCROLL_POSITION", scrollPosition);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoFavouritesTextView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private void showNoFavouritesMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mNoFavouritesTextView.setVisibility(View.VISIBLE);
    }

    private void showMovieImages() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mNoFavouritesTextView.setVisibility(View.INVISIBLE);
    }

    private void loadMovieImages() {
        if (mPagesLoaded < maxPages) {
            Bundle args = new Bundle();
            args.putInt("page", mPagesLoaded + 1);
            getSupportLoaderManager().restartLoader(LOADER_ID, args, this);
        }
    }

    private AlertDialog initSortingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sort By");
        builder.setItems(R.array.pref_sorting_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] criteria = getResources().getStringArray(R.array.pref_sorting_values);
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putString(sortBy, criteria[which]);
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    private void initSharedPreferences() {
        mSharedPrefs = getApplicationContext().getSharedPreferences("movie_preferences", MODE_PRIVATE);
        mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                criteria = sharedPreferences.getString(key, getString(R.string.pref_sort_by_popular));
                mPagesLoaded = 0;
                mMovieImageAdapter.clearMovies();
                loadMovieImages();
            }
        };
        mSharedPrefs.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        criteria = mSharedPrefs.getString(sortBy, favouriteMovies);
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(this, MovieDetailActivity.class);
        detailIntent.putExtra(Movie.EXTRA_MOVIE, movie.toBundle());
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_by) {
            if (mSortingDialog != null) {
                mSortingDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            ArrayList<Movie> mData;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (criteria.equals(favouriteMovies)) {
                    mMovieImageAdapter.clearMovies();
                    forceLoad();
                }
                else {
                    if (mData != null) {
                        deliverResult(mData);
                    } else {
                        if (mPagesLoaded == 0) {
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                        mErrorTextView.setVisibility(View.INVISIBLE);
                        forceLoad();
                    }
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                if (args.size() == 0) {
                    return null;
                }
                int page = args.getInt("page");
                NetworkUtility networkUtility = new NetworkUtility();
                if (!(criteria.equals(favouriteMovies))) {
                    URL request = networkUtility.buildMoviesUrl(page, criteria);
                    try {
                        String JSONResponse = networkUtility.getResponseFromHttpUrl(request);
                        ArrayList<Movie> result = fetchMoviesFromJson(JSONResponse);
                        mPagesLoaded++;
                        return result;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                } else {
                    Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
                    if (cursor!=null){
                        ArrayList<Movie> result = fetchMoviesFromCursor(cursor);
                        cursor.close();
                        return result;
                    }
                    return null;
                }

            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                mData = data;
                mProgressBar.setVisibility(View.INVISIBLE);
                super.deliverResult(data);
            }
        };
    }
    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (movies != null) {
            mMovieImageAdapter.addMovies(movies);
            showMovieImages();
        } else {
            if (criteria.equals(favouriteMovies)) {
                showNoFavouritesMessage();
            } else {
                showErrorMessage();
            }
        }
    }
    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    private ArrayList<Movie> fetchMoviesFromJson(String jsonStr) throws JSONException {
        final String KEY_MOVIES = "results";

        JSONObject json = new JSONObject(jsonStr);
        JSONArray movies = json.getJSONArray(KEY_MOVIES);
        ArrayList<Movie> result = new ArrayList<>();

        for (int i = 0; i < movies.length(); i++) {
            Movie resMovie = Movie.getMovieFromJson(movies.getJSONObject(i));
            result.add(resMovie);
        }
        return result;
    }
    private ArrayList<Movie> fetchMoviesFromCursor(Cursor cursor){
        ArrayList<Movie> result = new ArrayList<>();

        if (cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(
                    cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_OVERVIEW)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER_PATH)),
                    cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE)),
                    cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_VOTE_COUNT)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_RELEASE_DATE))
                );

                movie.setPosterFromCursor(cursor);

                result.add(movie);

            } while(cursor.moveToNext());
        }

        return result;
    }

}
