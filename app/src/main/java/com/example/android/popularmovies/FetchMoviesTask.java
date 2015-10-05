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

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        if (validateParameters(params)) return null;

        String sort_by = params[0].trim();



        try {
            Uri builtUri = createApiUri(sort_by, discover_base_url, api_key);


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

    private Uri createApiUri(String sort_by) {
        // Construct the URL for the movie database API query
        // Possible parameters are available at the Movie Database's API page, at
        // https://www.themoviedb.org/documentation/api
        final String SORTBY_PARAM = "sort_by";
        final String APIKEY_PARAM = "api_key";
        String discover_base_url;
        String api_key; //TODO: check strings.xml for proper api key

        discover_base_url = _context.getResources().getString(R.string.movie_api_discover_endpoint);
        api_key = _context.getResources().getString(R.string.api_key);

        return Uri.parse(discover_base_url).buildUpon()
                .appendQueryParameter(SORTBY_PARAM, sort_by)
                .appendQueryParameter(APIKEY_PARAM, api_key)
                .build();
    }

    private boolean validateParameters(String[] params) {
        if (params.length != 1) {
            Log.e(TAG, "Invalid number of parameters passed!");
            return true;
        }
        return false;
    }

    private ArrayList<Movie> getMoviePostersFromJson(String moviesJsonStr) throws JSONException {
        // These are the names fo the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_USER_RATING = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";

        Log.v(TAG, "getMoviePostersFromJson");
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);
        Log.d(TAG, moviesJson.toString());

        ArrayList<Movie> resultMovies = new ArrayList<>();

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
