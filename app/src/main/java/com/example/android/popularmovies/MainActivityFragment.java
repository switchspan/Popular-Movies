package com.example.android.popularmovies;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final String MOVIES_KEY = "movies";
    private static final String SORT_KEY = "sort_by";

    private MovieAdapter _moviesAdapter;
    //private ImageListAdapter _moviesAdapter;
    private List<Movie> _movies;
    private String _sort_by;
    private Boolean _hasInitializedFromSavedState;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_sort_mostpopular) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute(getResources().getString(R.string.action_sort_popular_param));
            return true;
        }

        if (id == R.id.action_sort_highestrated) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute(getResources().getString(R.string.action_sort_rating_param));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initializeMovieAdapter();
        loadState(savedInstanceState);

        // Check to see if we have savedInstance state and get the movies from it.
        if (savedInstanceState != null) {
            _movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
        } else {
            _moviesAdapter = new ImageListAdapter(view.getContext(), _movies);
        }

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(getResources().getString(R.string.action_sort_popular_param));

        GridView gridView = (GridView) view.findViewById(R.id.list_item_movie);
        gridView.setAdapter(_moviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Movie movie = _moviesAdapter.getItem(position);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        return view;
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
        _hasInitializedFromSavedState = true;
    }

    private void initializeNewState() {
        _movies = new ArrayList<Movie>();
        _sort_by = "popularity.desc";
        _hasInitializedFromSavedState = false;
    }


}
