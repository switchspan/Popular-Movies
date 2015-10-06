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
        loadReviewListItemView(reviewListItemView, review);

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
        listItemView = inflater.inflate(R.layout.list_item_review, parent, false);

        return listItemView;
    }

    private void loadReviewListItemView(View reviewListItemView, Review review) {
        Log.v(TAG, "loadReviewListItemView");
        TextView reviewerAuthor = (TextView) reviewListItemView.findViewById(R.id.reviewer_author_text);
        TextView reviewerComments = (TextView) reviewListItemView.findViewById(R.id.reviewer_comment_text);
        if (reviewerAuthor != null && reviewerComments != null) {
            reviewerAuthor.setText(String.format("%s says:", review.getAuthor()));
            reviewerComments.setText(String.format("%s", review.getContent()));
        }
    }
}
