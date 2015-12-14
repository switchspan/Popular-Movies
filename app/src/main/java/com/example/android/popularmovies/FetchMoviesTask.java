package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Ken on 10/4/2015
 * Pulled this class out of the MainActivityFragment and added a constructor to separate the concerns.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private final String TAG = FetchMoviesTask.class.getSimpleName();

    private ArrayList<Movie> _movies = null;
    private MovieAdapter _movieAdapter;
    private Context _context;

    public FetchMoviesTask(ArrayList<Movie> movies, MovieAdapter movieAdapter, Context context) {
        _movies = movies;
        _movieAdapter = movieAdapter;
        _context = context;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        Log.v(TAG, "doInBackground");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null; // Will contain the raw JSON response as a string.

        if (!hasValidParameters(params)) return null;

        String sort_by = params[0].trim();

        try {
            URL url = createApiUrl(sort_by);
            urlConnection = getOpenConnectionFromUrl(url);

            if (urlConnection == null) return null;
            
            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null) return null; // no data...nothing to do

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) return null; // Stream was empty.  No point in parsing.

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

        return null; // This will only happen if there was an error getting or parsing the movie data
    }

    private URL createApiUrl(String sort_by) {
        // Construct the URL for the movie database API query
        // Possible parameters are available at the Movie Database's API page, at
        // https://www.themoviedb.org/documentation/api
        Log.v(TAG, "createApiUrl");
        final String SORTBY_PARAM = "sort_by";
        final String APIKEY_PARAM = "api_key";
        String discover_base_url;
        String api_key;

        discover_base_url = _context.getResources().getString(R.string.movie_api_discover_endpoint);
        api_key = _context.getResources().getString(R.string.api_key);

        try {
            Uri builtUri = Uri.parse(discover_base_url).buildUpon()
                .appendQueryParameter(SORTBY_PARAM, sort_by)
                .appendQueryParameter(APIKEY_PARAM, api_key)
                .build();

            Log.d(TAG, builtUri.toString());
            return new URL(builtUri.toString());
        } catch (MalformedURLException ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        return null;
    }

    private HttpURLConnection getOpenConnectionFromUrl(URL url) {
        Log.v(TAG, "getOpenConnectionFromUrl");
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            return urlConnection;
        } catch (IOException ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        return null;
    }

    private boolean hasValidParameters(String[] params) {
        Log.v(TAG, "hasValidParameters");
        if (params.length != 1) {
            Log.e(TAG, "Invalid number of parameters passed!");
            return false;
        }
        return true;
    }

    private ArrayList<Movie> getMoviePostersFromJson(String moviesJsonStr) throws JSONException {
        Log.v(TAG, "getMoviePostersFromJson");
        // These are the names fo the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        ArrayList<Movie> resultMovies = new ArrayList<>();
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        Log.d(TAG, moviesJson.toString());

        JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject currentMovieJson = movieArray.getJSONObject(i); // Get the current JSON object in the array
            resultMovies.add(mapMovieFromJsonObject(currentMovieJson));
        }
        return resultMovies;
    }

    private Movie mapMovieFromJsonObject(JSONObject currentMovieJson) {
        Log.v(TAG, "mapMovieFromJsonObject");
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_USER_RATING = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";
        Movie currentMovie = null;

        try {
            currentMovie = new Movie( currentMovieJson.getInt(TMDB_ID), currentMovieJson.getString(TMDB_POSTER_PATH));
            currentMovie.setTitle(currentMovieJson.getString(TMDB_TITLE));
            currentMovie.setOverview(currentMovieJson.getString(TMDB_OVERVIEW));
            currentMovie.setVoteAverage(currentMovieJson.getDouble(TMDB_USER_RATING));
            String releaseDate = currentMovieJson.getString(TMDB_RELEASE_DATE);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            currentMovie.setReleaseDate(format.parse(releaseDate));
        } catch (ParseException pe) {
            Log.w(TAG, "Unable to get release date!");
        } catch (JSONException ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        return currentMovie;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        Log.v(TAG, "onPostExecute");

        if (movies != null && !movies.isEmpty()) {
            loadMovies(movies);
        } else {
            String toastMessage = "No movies where found!";
            Toast.makeText(_context, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void loadMovies(ArrayList<Movie> movies) {
        Log.v(TAG, "loadMovies");
        _movies.clear();
        _movieAdapter.clear();

        _movies.addAll(movies);
        _movieAdapter.addAll(movies);
    }
}
