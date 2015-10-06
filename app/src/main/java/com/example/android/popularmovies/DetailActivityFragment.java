package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String TAG = DetailActivityFragment.class.getSimpleName();
    private static final String TRAILERS_KEY = "trailers";
    private static final String REVIEWS_KEY = "reviews";

    private TrailerAdapter _trailersAdapter;
    private ArrayList<Trailer> _trailers;
    private Movie _movie;
    private FetchTrailersTask _fetchTask = null;
    private Boolean _canInitializeTrailersFromSavedState;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadState(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Inspect the intent for the movie data
        // This in part from the Udacity discussion forums Sabo/Nico (https://discussions.udacity.com/t/how-do-i-use-intent-to-get-and-display-movie-details/27778/8)
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("movie")) {
            initializeTrailersAdapter();
            updateViewsFromIntent(rootView, intent);
            getTrailers(Integer.toString(_movie.getId()));
            initializeTrailerList(rootView);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRAILERS_KEY, _trailers);

    }

    private void updateViewsFromIntent(View rootView, Intent intent) {
        _movie = intent.getParcelableExtra("movie");

        TextView titleText = (TextView)rootView.findViewById(R.id.detail_title_text);
        TextView overviewText = (TextView)rootView.findViewById(R.id.detail_overview_text);
        TextView voteText = (TextView)rootView.findViewById(R.id.detail_voteaverage_text);
        TextView releaseDateText = (TextView) rootView.findViewById(R.id.detail_release_date_text);

        titleText.setText(_movie.getTitle());
        overviewText.setText(_movie.getOverview());
        voteText.setText(_movie.getVoteAverageAsString());
        releaseDateText.setText(_movie.getReleaseDateAsString());
        ImageView thumbNailView = (ImageView)rootView.findViewById(R.id.detail_poster_thumbnail);

        Picasso.with(rootView.getContext())
                .load(_movie.getPosterThumbnailUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .into(thumbNailView);
    }

    public void initializeTrailersAdapter() {
        _trailersAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
    }

    private void initializeTrailerList(View view) {
        ListView listView = (ListView) view.findViewById(R.id.list_item_trailer);
        listView.setAdapter(_trailersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "setOnItemClickListener.onItemClick");
                Trailer trailer = _trailersAdapter.getItem(position);
                Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getYouTubeUrl()));
                startActivity(youTubeIntent);
                Log.i(TAG, "Playing video");
            }
        });
    }

    private void loadState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            intializeStateFromSavedState(savedInstanceState);
        } else {
            initializeNewState();
        }
    }

    private void intializeStateFromSavedState(Bundle savedInstanceState) {
        _trailers = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
        _canInitializeTrailersFromSavedState = true;
    }

    private void initializeNewState() {
        _trailers = new ArrayList<>();
        _canInitializeTrailersFromSavedState = false;
    }

    private void getTrailers(String movieId) {
        Log.v(TAG, "getTrailers");

        cancelAnyFetchTasks();

        if (_canInitializeTrailersFromSavedState) {
            restoreTrailersFromState();
        } else {
            startTrailerFetchTask(movieId);
        }
    }

    private void restoreTrailersFromState() {
        Log.v(TAG, "restoreTrailersFromState");
        _trailersAdapter.clear();
        _trailersAdapter.addAll(_trailers);
    }

    private void cancelAnyFetchTasks() {
        Log.v(TAG, "cancelAnyFetchTasks");
        if (_fetchTask != null && _fetchTask.getStatus() != AsyncTask.Status.FINISHED) _fetchTask.cancel(true);
    }

    private void startTrailerFetchTask(String movieId) {
        Log.v(TAG, "startTrailerFetchTask");
        _fetchTask = new FetchTrailersTask(_trailers, _trailersAdapter, getActivity());
        _fetchTask.execute(movieId);
    }
}
