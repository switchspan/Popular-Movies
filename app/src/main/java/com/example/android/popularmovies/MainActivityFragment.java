package com.example.android.popularmovies;

import android.net.Uri;
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
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

//    private ArrayAdapter<Movie> mMoviesAdapter;
    private ImageListAdapter mMoviesAdapter;

    public static String[] testMovieImages = {
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
            "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
    };

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

        if (id == R.id.action_sort_popular) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute("popularity.desc");
            return true;
        }

        if (id == R.id.action_sort_highestrated) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute("vote_average.desc");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        List<Movie> moviesList = new ArrayList<Movie>();
        moviesList.add(new Movie(1,"/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        moviesList.add(new Movie(2, "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));

        mMoviesAdapter = new ImageListAdapter(view.getContext(), moviesList);

        GridView gridView = (GridView) view.findViewById(R.id.list_item_movie);
        gridView.setAdapter(mMoviesAdapter);

        return view;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getMoviePostersFromJson(String moviesJsonStr) throws JSONException {
            //TODO: Add debug logging in this method
            // These are the names fo the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_ID = "id";
            final String TMDB_TITLE = "title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_USER_RATING = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

            Log.d(TAG, moviesJson.toString());

            Movie[] resultMovies = new Movie[(movieArray.length() - 1)];
            for (int i = 0; i < movieArray.length(); i++) {
                // Get the current JSON object in the array
                JSONObject movieJson = movieArray.getJSONObject(i);

                Movie currentMovie = new Movie( movieJson.getInt(TMDB_ID), moviesJson.getString(TMDB_POSTER_PATH));
                currentMovie.title = moviesJson.getString(TMDB_TITLE);
                currentMovie.overview = moviesJson.getString(TMDB_OVERVIEW);
                currentMovie.vote_average = movieJson.getDouble(TMDB_USER_RATING);

                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    currentMovie.release_date = format.parse(moviesJson.getString(TMDB_RELEASE_DATE));
                } catch (ParseException pe) {
                    Log.w(TAG, "Unable to get release date!");
                    Log.e(TAG, pe.getMessage());
                    throw new IllegalArgumentException();
                }

                resultMovies[i] = currentMovie;
            }

            return resultMovies;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String sort_by = "popularity.desc";
            //String api_key = "0000000000000000000000000"; //TODO: add the correct API key here
            String api_key = "237b90b4f3037d7c7197f149554644cb";

            try {
                // Construct the URL for the movie database API query
                // Possible parameters are available at the Movie Database's API page, at
                // https://www.themoviedb.org/documentation/api
                final String DISCOVER_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORTBY_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(DISCOVER_BASE_URL).buildUpon()
                        .appendQueryParameter(SORTBY_PARAM, sort_by)
                        .appendQueryParameter(APIKEY_PARAM, api_key)
                        .build();

                Log.d(TAG, builtUri.toString());
                URL url = new URL(builtUri.toString());


            } catch (IOException e) {
                Log.e(TAG, "Error", e);
                // If the code didn't successfully get the movies data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviePostersFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie data
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                Log.d(TAG, "Movies found - Adding movies");
                mMoviesAdapter.clear();
                for (Movie movie : movies) {
                    mMoviesAdapter.add(movie);
                }
            }
        }

    }
}
