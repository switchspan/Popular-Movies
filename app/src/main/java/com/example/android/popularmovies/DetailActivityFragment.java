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
    private ReviewAdapter _reviewsAdapter;
    private ArrayList<Review> _reviews;
    private Movie _movie;
    private FetchTrailersTask _fetchTrailersTask = null;
    private FetchReviewsTask _fetchReviewsTask = null;
    private Boolean _canInitializeTrailersFromSavedState;
    private Boolean _canInitializeReviewsFromSavedState;

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
            initializeAdapters();
            updateViewsFromIntent(rootView, intent);
            getTrailers(Integer.toString(_movie.getId()));
            getReviews(Integer.toString(_movie.getId()));
            initializeTrailerList(rootView);
            initializeReviewList(rootView);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRAILERS_KEY, _trailers);
        outState.putParcelableArrayList(REVIEWS_KEY, _reviews);

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

    public void initializeAdapters() {
        _trailersAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
        _reviewsAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
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

    private void initializeReviewList(View view) {
        ListView listView = (ListView) view.findViewById(R.id.list_item_review);
        listView.setAdapter(_reviewsAdapter);
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
        _reviews = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
        _canInitializeTrailersFromSavedState = true;
        _canInitializeReviewsFromSavedState = true;
    }

    private void initializeNewState() {
        _trailers = new ArrayList<>();
        _reviews = new ArrayList<>();
        _canInitializeTrailersFromSavedState = false;
        _canInitializeReviewsFromSavedState = false;
    }

    private void getTrailers(String movieId) {
        Log.v(TAG, "getTrailers");

        cancelAnyTrailerFetchTasks();

        if (_canInitializeTrailersFromSavedState) {
            restoreTrailersFromState();
        } else {
            startTrailerFetchTask(movieId);
        }
    }

    private void getReviews(String movieId) {
        Log.v(TAG, "getReviews");

        cancelAnyReviewFetchTasks();

        if (_canInitializeReviewsFromSavedState) {
            restoreReviewsFromState();
        } else {
            startReviewFetchTask(movieId);
        }
    }

    private void restoreTrailersFromState() {
        Log.v(TAG, "restoreTrailersFromState");
            _trailersAdapter.clear();
            _trailersAdapter.addAll(_trailers);
    }

    private void restoreReviewsFromState() {
        Log.v(TAG, "restoreReviewsFromState");
        _reviewsAdapter.clear();
        _reviewsAdapter.addAll(_reviews);
    }

    private void cancelAnyTrailerFetchTasks() {
        Log.v(TAG, "cancelAnyTrailerFetchTasks");
        if (_fetchTrailersTask != null && _fetchTrailersTask.getStatus() != AsyncTask.Status.FINISHED) _fetchTrailersTask.cancel(true);
    }

    private void cancelAnyReviewFetchTasks() {
        Log.v(TAG, "cancelAnyReviewFetchTasks");
        if (_fetchReviewsTask != null && _fetchReviewsTask.getStatus() != AsyncTask.Status.FINISHED) _fetchReviewsTask.cancel(true);
    }

    private void startTrailerFetchTask(String movieId) {
        Log.v(TAG, "startTrailerFetchTask");
        _fetchTrailersTask = new FetchTrailersTask(_trailers, _trailersAdapter, getActivity());
        _fetchTrailersTask.execute(movieId);
    }

    private void startReviewFetchTask(String movieId) {
        Log.v(TAG, "startReviewFetchTaskFetchTask");
        _fetchReviewsTask = new FetchReviewsTask(_reviews, _reviewsAdapter, getActivity());
        _fetchReviewsTask.execute(movieId);
    }
}
