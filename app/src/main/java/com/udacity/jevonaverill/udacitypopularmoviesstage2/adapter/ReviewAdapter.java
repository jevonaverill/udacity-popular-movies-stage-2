package com.udacity.jevonaverill.udacitypopularmoviesstage2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.udacity.jevonaverill.udacitypopularmoviesstage2.R;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.model.Review;

import java.util.ArrayList;

/**
 * Created by jevon.averill on 8/8/2017.
 */

public class ReviewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Review> reviewList;

    public ReviewAdapter(Context context){
        this.context = context;
        reviewList = new ArrayList<>();
    }

    public void setReviews(ArrayList<Review> data){
        reviewList.clear();
        reviewList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Review getItem(int position) {
        if (position>=0 && position < reviewList.size()){
            return reviewList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (getItem(position) == null){
            return -1L;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View reviewItem = convertView;
        Review review = getItem(position);
        if (reviewItem == null) {
            try {
                LayoutInflater layoutInflater;
                layoutInflater = LayoutInflater.from(context);
                reviewItem = layoutInflater.inflate(R.layout.review_item_list, null);

            } catch (Exception ex) {
                Log.e(context.getClass().getSimpleName(), ex.getMessage());
            }
        }
        ((TextView) reviewItem.findViewById(R.id.tvAuthor)).setText(
                String.format(context.getString(R.string.movie_review_author), review.author));
        ((TextView) reviewItem.findViewById(R.id.tvReview)).setText(review.content);
        return reviewItem;
    }

}
