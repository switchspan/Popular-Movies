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
 * Created by Ken on 10/6/2015.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {
    private final String TAG = FetchReviewsTask.class.getSimpleName();

    private ArrayList<Review> _reviews = null;
    private ReviewAdapter _reviewAdapter;
    private Context _context;

    public FetchReviewsTask(ArrayList<Review> reviews, ReviewAdapter reviewAdapter, Context context) {
        _reviews = reviews;
        _reviewAdapter = reviewAdapter;
        _context = context;
    }

    @Override
    protected ArrayList<Review> doInBackground(String... params) {
        Log.v(TAG, "doInBackground");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewsJsonStr = null; // Will contain the raw JSON response as a string.

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

            reviewsJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
            // If the code didn't successfully get the reviews data, there's no point in attempting
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
            return getReviewsFromJson(reviewsJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null; // This will only happen if there was an error getting or parsing the review data
    }

    private URL createApiUrl(String movieId) {
        // Construct the URL for the movie database API query
        // Possible parameters are available at the Movie Database's API page, at
        // https://www.themoviedb.org/documentation/api
        final String REVIEWS_PATH = "reviews";
        final String APIKEY_PARAM = "api_key";
        String review_base_url;
        String api_key;

        review_base_url = _context.getResources().getString(R.string.movie_api_review_endpoint);
        api_key = _context.getResources().getString(R.string.api_key);

        try {
            Uri builtUri = Uri.parse(review_base_url).buildUpon()
                    .appendPath(movieId)
                    .appendPath(REVIEWS_PATH)
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

    private ArrayList<Review> getReviewsFromJson(String reviewsJsonStr) throws JSONException {
        Log.v(TAG, "getReviewsFromJson");
        // These are the names fo the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        ArrayList<Review> resultReviews = new ArrayList<>();
        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        Log.d(TAG, reviewsJson.toString());

        JSONArray reviewArray = reviewsJson.getJSONArray(TMDB_RESULTS);

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject currentReviewJson = reviewArray.getJSONObject(i); // Get the current JSON object in the array
            resultReviews.add(mapReviewFromJsonObject(currentReviewJson));
        }
        return resultReviews;
    }

    private Review mapReviewFromJsonObject(JSONObject currentReviewJson) {
        final String TMDB_ID = "id";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_URL = "url";
        Review currentReview = null;

        try {
            currentReview = new Review( currentReviewJson.getString(TMDB_ID),
                    currentReviewJson.getString(TMDB_AUTHOR),
                    currentReviewJson.getString(TMDB_CONTENT),
                    currentReviewJson.getString(TMDB_URL));
        } catch (JSONException ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        return currentReview;
    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        Log.v(TAG, "onPostExecute");

        if (reviews != null && !reviews.isEmpty()) {
            loadReviews(reviews);
        } else {
            String toastMessage = "No trailers where found!";
            Toast.makeText(_context, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void loadReviews(ArrayList<Review> reviews) {
        Log.v(TAG, "loadReviews");
        _reviews.clear();
        _reviewAdapter.clear();

        _reviews.addAll(reviews);
        _reviewAdapter.addAll(reviews);
    }

}
