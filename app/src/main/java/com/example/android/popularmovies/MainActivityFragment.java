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
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private ImageListAdapter mMoviesAdapter;

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

        List<Movie> moviesList = new ArrayList<Movie>();
        moviesList.add(new Movie(1,"/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        moviesList.add(new Movie(2, "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));

        mMoviesAdapter = new ImageListAdapter(view.getContext(), moviesList);

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(getResources().getString(R.string.action_sort_popular_param));

        GridView gridView = (GridView) view.findViewById(R.id.list_item_movie);
        gridView.setAdapter(mMoviesAdapter);

        return view;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String TAG = FetchMoviesTask.class.getSimpleName();

        private List<Movie> getMoviePostersFromJson(String moviesJsonStr) throws JSONException {
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

            List<Movie> resultMovies = new ArrayList<Movie>();

            for (int i = 0; i < movieArray.length(); i++) {
                // Get the current JSON object in the array
                JSONObject currentMovieJson = movieArray.getJSONObject(i);

                Movie currentMovie = new Movie( currentMovieJson.getInt(TMDB_ID), currentMovieJson.getString(TMDB_POSTER_PATH));
                currentMovie.setTitle(currentMovieJson.getString(TMDB_TITLE));
                currentMovie.setOverview(currentMovieJson.getString(TMDB_OVERVIEW));
                currentMovie.setVoteAverage(currentMovieJson.getDouble(TMDB_USER_RATING));

                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String releaseDate = currentMovieJson.getString(TMDB_RELEASE_DATE);
                    currentMovie.setReleaseDate(format.parse(releaseDate));
                } catch (ParseException pe) {
                    Log.w(TAG, "Unable to get release date!");
//                    Log.e(TAG, pe.getMessage());
//                    throw new IllegalArgumentException();
                }
                resultMovies.add(currentMovie);
            }

            return resultMovies;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String sort_by = "popularity.desc";

            if (params[0] != null) sort_by = params[0].trim();

            String api_key = getResources().getString(R.string.api_key); //TODO: check strings.xml for proper api key

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

                // Create the request to the movie database API
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // no data...nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();


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
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                Log.d(TAG, "Movies found - Adding movies");
                mMoviesAdapter.clear();
                mMoviesAdapter.replace(movies);
            }
        }

    }
}
