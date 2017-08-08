package com.udacity.jevonaverill.udacitypopularmoviesstage2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.udacity.jevonaverill.udacitypopularmoviesstage2.R;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.adapter.ReviewAdapter;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.model.Review;

import java.util.ArrayList;

/**
 * Created by jevonaverill on 8/8/17.
 */

public class ReviewActivity extends AppCompatActivity {

    ListView mReviewListView;
    ReviewAdapter mReviewAdapter;
    TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mReviewListView = (ListView) findViewById(R.id.lvReviews);
        mErrorTextView = (TextView) findViewById(R.id.tvReviewsError);
        mReviewAdapter = new ReviewAdapter(this);

        Intent intent = getIntent();

        mReviewListView.setAdapter(mReviewAdapter);

        if (intent.hasExtra(getString(R.string.movie_reviews_intent_extra))){
            ArrayList<Review> result = Review.stringToArray(intent.getStringExtra(getString(R.string.movie_reviews_intent_extra)));

            if (result.size() > 0) {
                mReviewAdapter.setReviews(result);
            } else{
                mReviewListView.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.VISIBLE);
            }
        }
    }

}
