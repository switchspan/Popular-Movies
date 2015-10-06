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
import java.util.ArrayList;

/**
 * Created by Ken on 10/5/2015.
 */
public class FetchTrailersTask  extends AsyncTask<String, Void, ArrayList<Trailer>> {
    private final String TAG = FetchTrailersTask.class.getSimpleName();

    private ArrayList<Trailer> _trailers = null;
    private TrailerAdapter _trailerAdapter;
    private Context _context;

    public FetchTrailersTask(ArrayList<Trailer> trailers, TrailerAdapter trailerAdapter, Context context) {
        _trailers = trailers;
        _trailerAdapter = trailerAdapter;
        _context = context;
    }

    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {
        Log.v(TAG, "doInBackground");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailersJsonStr = null; // Will contain the raw JSON response as a string.

        if (!hasValidParameters(params)) return null;

        String movieId = params[0].trim();

        try {
            URL url = createApiUrl(movieId);
            urlConnection = getOpenConnectionFromUrl(url);

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

            trailersJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
            // If the code didn't successfully get the trailers data, there's no point in attempting
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
            return getTrailersFromJson(trailersJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null; // This will only happen if there was an error getting or parsing the trailer data
    }

    private URL createApiUrl(String movieId) {
        // Construct the URL for the movie database API query
        // Possible parameters are available at the Movie Database's API page, at
        // https://www.themoviedb.org/documentation/api
        final String VIDEOS_PATH = "videos";
        final String APIKEY_PARAM = "api_key";
        String trailer_base_url;
        String api_key;

        trailer_base_url = _context.getResources().getString(R.string.movie_api_trailer_endpoint);
        api_key = _context.getResources().getString(R.string.api_key);

        try {
            Uri builtUri = Uri.parse(trailer_base_url).buildUpon()
                    .appendPath(movieId)
                    .appendPath(VIDEOS_PATH)
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
        if (params.length != 1) {
            Log.e(TAG, "Invalid number of parameters passed!");
            return false;
        }
        return true;
    }

    private ArrayList<Trailer> getTrailersFromJson(String trailersJsonStr) throws JSONException {
        Log.v(TAG, "getTrailersFromJson");
        // These are the names fo the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        ArrayList<Trailer> resultTrailers = new ArrayList<>();
        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        Log.d(TAG, trailersJson.toString());

        JSONArray trailerArray = trailersJson.getJSONArray(TMDB_RESULTS);

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject currentTrailerJson = trailerArray.getJSONObject(i); // Get the current JSON object in the array
            resultTrailers.add(mapTrailerFromJsonObject(currentTrailerJson));
        }
        return resultTrailers;
    }

    private Trailer mapTrailerFromJsonObject(JSONObject currentTrailerJson) {
        final String TMDB_ID = "id";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";
        final String TMDB_SITE = "site";
        final String TMDB_SIZE = "size";
        final String TMDB_TYPE = "type";
        Trailer currentTrailer = null;

        try {
            currentTrailer = new Trailer( currentTrailerJson.getString(TMDB_ID),
                    currentTrailerJson.getString(TMDB_KEY),
                    currentTrailerJson.getString(TMDB_NAME),
                    currentTrailerJson.getString(TMDB_SITE),
                    currentTrailerJson.getInt(TMDB_SIZE),
                    currentTrailerJson.getString(TMDB_TYPE));
        } catch (JSONException ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        return currentTrailer;
    }

    @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        Log.v(TAG, "onPostExecute");

        if (trailers != null && !trailers.isEmpty()) {
            loadTrailers(trailers);
        } else {
            String toastMessage = "No trailers where found!";
            Toast.makeText(_context, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void loadTrailers(ArrayList<Trailer> trailers) {
        Log.v(TAG, "loadTrailers");
        _trailers.clear();
        _trailerAdapter.clear();

        _trailers.addAll(trailers);
        _trailerAdapter.addAll(trailers);
    }

}
