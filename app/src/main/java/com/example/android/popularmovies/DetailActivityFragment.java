package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");
            ((TextView)rootView.findViewById(R.id.detail_title_text)).setText(movie.getTitle());
            ((TextView)rootView.findViewById(R.id.detail_overview_text)).setText(movie.getOverview());
            ((TextView)rootView.findViewById(R.id.detail_voteaverage_text)).setText(movie.getVoteAverageAsString());
            ((TextView)rootView.findViewById(R.id.detail_releasedate_text)).setText(movie.getReleaseDateAsString());
            ImageView thumbNailView = (ImageView)rootView.findViewById(R.id.detail_poster_thumbnail);
//            Picasso.with(rootView.getContext()).load(movie.getPosterThumbnailUrl()).into(thumbNailView);
        }

        return rootView;
    }
}
