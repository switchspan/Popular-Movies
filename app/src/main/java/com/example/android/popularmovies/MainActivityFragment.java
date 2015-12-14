package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final String MOVIES_KEY = "movies";
    private static final String SORT_KEY = "sort_by";
    private static final String FAVORITES_KEY = "favorites";

    private MovieAdapter _moviesAdapter;
    private ArrayList<Movie> _movies;
    //private ArrayList<Movie> _favoriteMovies;
    private FavoriteMovies _favoriteMovies;
    private String _sort_by;
    private FetchMoviesTask _fetchTask = null;
    private Boolean _canInitializeMoviesFromSavedState;
    private View _currentView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadState(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initializeMovieAdapter();
        initializeMovieList(view);
        getMovies(_sort_by);
        _currentView = view;
        _favoriteMovies = new FavoriteMovies(getActivity().getApplicationContext());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_sort_mostpopular) {
            _sort_by = getResources().getString(R.string.action_sort_popular_param);
            _canInitializeMoviesFromSavedState = false;
        }

        if (id == R.id.action_sort_highestrated) {
            _sort_by = getResources().getString(R.string.action_sort_rating_param);
            _canInitializeMoviesFromSavedState = false;
            getMovies(_sort_by);
        }

        if (id == R.id.action_sort_favorite) {
            _sort_by = getResources().getString(R.string.action_sort_favorite_param);
            _canInitializeMoviesFromSavedState = false;
        }

        getMovies(_sort_by);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(SORT_KEY, _sort_by);
        outState.putParcelableArrayList(MOVIES_KEY, _movies);
    }

    private void initializeMovieList(View view) {
        GridView gridView = (GridView) view.findViewById(R.id.list_item_movie);
        gridView.setAdapter(_moviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "setOnItemClickListener.onItemClick");
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Movie movie = _moviesAdapter.getItem(position);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });
    }

    public void initializeMovieAdapter() {
        _moviesAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
    }

    private void loadState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            intializeStateFromSavedState(savedInstanceState);
        } else {
            initializeNewState();
        }
    }

    private void intializeStateFromSavedState(Bundle savedInstanceState) {
        _movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
        _sort_by = savedInstanceState.getString(SORT_KEY);
        _canInitializeMoviesFromSavedState = true;
    }

    private void initializeNewState() {
        _movies = new ArrayList<>();
        _sort_by = "popularity.desc";
        _canInitializeMoviesFromSavedState = false;
    }

    private void getMovies(String sort_by) {
        Log.v(TAG, "getMovies");

        if (sort_by == getResources().getString(R.string.action_sort_favorite_param)) {
            _favoriteMovies.load();
            _movies = _favoriteMovies.getFavoriteMovies();
            _sort_by = getResources().getString(R.string.action_sort_popular_param);
            restoreMoviesFromState();
            initializeMovieList(_currentView);
            return;
        }

        cancelAnyFetchTasks();

        if (_canInitializeMoviesFromSavedState) {
            restoreMoviesFromState();
        } else {
            startMovieFetchTask(sort_by);
        }
    }

    private void restoreMoviesFromState() {
        Log.v(TAG, "restoreMoviesFromState");
        _moviesAdapter.clear();
        _moviesAdapter.addAll(_movies);
    }

    private void cancelAnyFetchTasks() {
        Log.v(TAG, "cancelAnyFetchTasks");
        if (_fetchTask != null && _fetchTask.getStatus() != AsyncTask.Status.FINISHED) _fetchTask.cancel(true);
    }

    private void startMovieFetchTask(String sort_by) {
        Log.v(TAG, "startMovieFetchTask");
//        if (isNetworkAvailable()) {
            _fetchTask = new FetchMoviesTask(_movies, _moviesAdapter, getActivity());
            _fetchTask.execute(sort_by);
//        } else {
//            Toast.makeText(getActivity().getApplicationContext(), "Unable to connect to Internet!", Toast.LENGTH_SHORT).show();
//        }
    }

//    private boolean isNetworkAvailable() {
//        Context currentContext = getActivity().getApplicationContext();
//        ConnectivityManager connectivityManager = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

}
