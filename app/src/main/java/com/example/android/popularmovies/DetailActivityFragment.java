package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Inspect the intent for the movie data
        // This in part from the Udacity discussion forums Sabo/Nico (https://discussions.udacity.com/t/how-do-i-use-intent-to-get-and-display-movie-details/27778/8)
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");
            TextView titleText = (TextView)rootView.findViewById(R.id.detail_title_text);
            TextView overviewText = (TextView)rootView.findViewById(R.id.detail_overview_text);
            TextView voteText = (TextView)rootView.findViewById(R.id.detail_voteaverage_text);
            TextView releaseDateText = (TextView) rootView.findViewById(R.id.detail_release_date_text);

            titleText.setText(movie.getTitle());
            overviewText.setText(movie.getOverview());
            voteText.setText(movie.getVoteAverageAsString());
            releaseDateText.setText(movie.getReleaseDateAsString());
            ImageView thumbNailView = (ImageView)rootView.findViewById(R.id.detail_poster_thumbnail);

            Picasso.with(rootView.getContext())
                    .load(movie.getPosterThumbnailUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .into(thumbNailView);
        }

        return rootView;
    }
}
