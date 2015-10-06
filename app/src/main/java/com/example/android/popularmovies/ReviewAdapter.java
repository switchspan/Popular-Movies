package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Ken on 10/5/2015.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {
    private final String TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, ArrayList<Review> trailers) {
        super(context, R.layout.list_item_trailer, trailers);
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        Log.v(TAG, "getView");

        View reviewListItemView = getReviewListItemView(currentView, parent);
        Review review = getItem(position);

        return reviewListItemView;
    }

    private View getReviewListItemView(View currentView, ViewGroup parent) {
        Log.v(TAG, "getReviewListItemView");
        return (currentView == null) ? getReviewListItemViewFromLayout(parent) : currentView;
    }

    private View getReviewListItemViewFromLayout(ViewGroup parent) {
        Log.v(TAG, "getReviewListItemViewFromLayout");
        View listItemView;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        listItemView = inflater.inflate(R.layout.list_item_review, parent,         false);

        return listItemView;
    }

    //    private void loadMovieListItemView(View movieListItemView, Movie movie) {
    //        Log.v(TAG, "loadMovieListItemView");
    //
    //        if (movie == null) return;
    //
    //        loadMovieImage(movieListItemView, movie);
    //    }

    //    private void loadMovieImage(View movieListItemView, Movie movie) {
    //        Log.v(TAG, "loadMovieImage");
    //
    //        ImageView thumbnailImageView = (ImageView) movieListItemView.findViewById(R.id.list_item_movie_imageview);
    //        if (thumbnailImageView != null) {
    //            loadImageIntoImageView(movie, thumbnailImageView);
    //        }
    //    }

    //    private void loadImageIntoImageView(Movie movie, ImageView thumbnailImageView) {
    //        Log.v(TAG, "loadImageIntoImageView");
    //        String imageUrl = movie.getPosterImageUrl();
    //        if (imageUrl != null && !imageUrl.isEmpty()) {
    //            Picasso.with(getContext())
    //                    .load(imageUrl)
    //                    .placeholder(R.drawable.image_placeholder)
    //                    .error(R.drawable.image_error)
    //                    .into(thumbnailImageView);
    //        } else {
    //            Log.i(TAG, String.format("No image found for URL: %s", imageUrl));
    //            thumbnailImageView.setImageBitmap(null);
    //        }
    //    }
}
