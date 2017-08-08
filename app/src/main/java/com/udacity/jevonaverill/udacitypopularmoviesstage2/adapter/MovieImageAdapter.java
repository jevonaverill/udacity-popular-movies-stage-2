package com.udacity.jevonaverill.udacitypopularmoviesstage2.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.R;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.model.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jevon.averill on 8/8/2017.
 */

public class MovieImageAdapter extends RecyclerView.Adapter<MovieImageAdapter.MovieImageViewHolder> {

    private ArrayList<Movie> mMovieList;
    private onMovieImageClickHandler mOnMovieImageClickHandler;

    public interface onMovieImageClickHandler {
        void onClick(Movie movie);
    }

    public MovieImageAdapter(onMovieImageClickHandler clickHandler){
        mMovieList = new ArrayList<>();
        mOnMovieImageClickHandler = clickHandler;
    }

    public void addMovies(ArrayList<Movie> movies){
        mMovieList.addAll(movies);
        notifyDataSetChanged();
    }

    public void clearMovies(){
        mMovieList.clear();
        notifyDataSetChanged();
    }

    public void saveInstanceState(Bundle outState){
        outState.putParcelableArrayList("MOVIES_ADAPTER", mMovieList);
    }

    public void restoreInstanceState(Bundle savedInstanceState){
        if (savedInstanceState.containsKey("MOVIES_ADAPTER")){
            ArrayList<Movie> savedMovies = savedInstanceState
                    .getParcelableArrayList("MOVIES_ADAPTER");
            mMovieList.clear();
            mMovieList.addAll(savedMovies);
            notifyDataSetChanged();
        }
    }
    @Override
    public MovieImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutItemId = R.layout.movie_image_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutItemId, parent, false);

        return new MovieImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieImageViewHolder holder, int position) {
        holder.setMovieImage(mMovieList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    class MovieImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.ivMovie)
        ImageView ivMovie;

        Context mContext;

        MovieImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        void setMovieImage(Movie movie){
            Uri posterUri = movie.getPosterUri(mContext.getString(R.string.movie_image_default_size));
            Picasso.with(mContext).load(posterUri).error(R.drawable.error)
                    .placeholder(R.drawable.placeholder).into(ivMovie);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie selectedMovie = mMovieList.get(position);
            mOnMovieImageClickHandler.onClick(selectedMovie);
        }
    }

}
