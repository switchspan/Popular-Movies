package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ken on 10/4/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private final String TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.list_item_movie, movies);
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        Log.v(TAG, "getView");

        View movieListItemView = getMovieListItemView(currentView, parent);
        Movie movie = getItem(position);
        loadMovieListItemView(movieListItemView, movie);

        return movieListItemView;
    }

    private View getMovieListItemView(View currentView, ViewGroup parent) {
        Log.v(TAG, "getMovieListItemView");
        return (currentView == null) ? getMovieListItemViewFromLayout(parent) : currentView;
    }

    private View getMovieListItemViewFromLayout(ViewGroup parent) {
        Log.v(TAG, "getMovieListItemViewFromLayout");
        View listItemView;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        listItemView = inflater.inflate(R.layout.list_item_movie, parent, false);

        return listItemView;
    }

    private void loadMovieListItemView(View movieListItemView, Movie movie) {
        Log.v(TAG, "loadMovieListItemView");

        if (movie == null) return;

        loadMovieImage(movieListItemView, movie);
//        loadMovieName(movieListItemView, movie);
    }

    private void loadMovieImage(View movieListItemView, Movie movie) {
        Log.v(TAG, "loadMovieImage");

        ImageView thumbnailImageView = (ImageView) movieListItemView.findViewById(R.id.list_item_movie_imageview);
        if (thumbnailImageView != null) {
            loadImageIntoImageView(movie, thumbnailImageView);
        }
    }

    private void loadImageIntoImageView(Movie movie, ImageView thumbnailImageView) {
        Log.v(TAG, "loadImageIntoImageView");
        if (movie.getPosterThumbnailUrl() != null && movie.getPosterThumbnailUrl().isEmpty()) {
            Picasso.with(getContext())
                    .load(movie.getPosterThumbnailUrl())
                    .into(thumbnailImageView);
        } else {
            Log.i(TAG, String.format("No image found for URL: %s", movie.getPosterThumbnailUrl()));
            thumbnailImageView.setImageBitmap(null);
        }
    }

//    private void loadMovieName(View movieListItemView, Movie movie) {
//        Log.v(TAG, "loadMovieName");
//        TextView movieNameView = (TextView) movieListItemView.findViewById(R.id.list_item_movie)
//    }
}
