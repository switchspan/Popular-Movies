package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ken on 10/5/2015.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {
    private final String TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers) {
        super(context, R.layout.list_item_trailer, trailers);
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        Log.v(TAG, "getView");

        View trailerListItemView = getTrailerListItemView(currentView, parent);
        Trailer trailer = getItem(position);
        loadTrailerListItemView(trailerListItemView, trailer);

        return trailerListItemView;
    }

    private View getTrailerListItemView(View currentView, ViewGroup parent) {
        Log.v(TAG, "getTrailerListItemView");
        return (currentView == null) ? getTrailerListItemViewFromLayout(parent) : currentView;
    }

    private View getTrailerListItemViewFromLayout(ViewGroup parent) {
        Log.v(TAG, "getTrailerListItemViewFromLayout");
        View listItemView;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        listItemView = inflater.inflate(R.layout.list_item_trailer, parent, false);

        return listItemView;
    }

    private void loadTrailerListItemView(View trailerListItemView, Trailer trailer) {
        Log.v(TAG, "loadTrailerListItemView");

        if (trailer == null) return;

        loadTrailerText(trailerListItemView, trailer);
    }

    private void loadTrailerText(View trailerListItemView, Trailer trailer) {
        Log.v(TAG, "loadTrailerText");

        TextView trailerLabel = (TextView) trailerListItemView.findViewById(R.id.movie_trailer_label);
        if (trailerLabel != null) {
            trailerLabel.setText(trailer.getTrailerLabel());
        }
    }

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
