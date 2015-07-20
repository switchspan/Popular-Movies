package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<Movie> mMoviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //TODO: Finish setting up the array adapter and binding it to the appropriate listview and imageview
//                List<Movie> movieArrayList = new ArrayList<>();
        Movie testMovie = new Movie();
        testMovie.id = 1;
        testMovie.posterPath = "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
//        movieArrayList.add(testMovie);
//
//        mMoviesAdapter = new ArrayAdapter<>(
//                getActivity(),
//                R.layout.list_item_movie,
//                R.id.list_item_movie_imageview,
//                movieArrayList
//        );


        View returnView = inflater.inflate(R.layout.fragment_main, container, false);

        ImageView picassoView = (ImageView) returnView.findViewById(R.id.picasso_image);

        //TODO: Remove this test code below
        //Picasso.with(returnView.getContext()).load("http://i.imgur.com/DvpvklR.png").into(picassoView);
        //Picasso.with(returnView.getContext()).load("http://i.dailymail.co.uk/i/pix/2015/01/26/2508DF8C00000578-0-image-a-125_1422246959340.jpg").into(picassoView);
        Picasso.with(returnView.getContext()).load(testMovie.getPosterImageUrl()).into(picassoView);
        //http://barfblog.com/wp-content/uploads/2015/01/bearded_dragon_pic.jpg
        //http://i.dailymail.co.uk/i/pix/2015/01/26/2508DF8C00000578-0-image-a-125_1422246959340.jpg
        return returnView;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        private Movie[] getMoviePostersFromJson(String moviesJsonStr) throws JSONException {
            //TODO: Add debug loging in this method
            // These are the names fo the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_ID = "id";
            final String TMDB_TITLE = "title";
            final String TMDB_POSTER_PATH = "poster_path";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

            Movie[] resultMovies = new Movie[(movieArray.length() - 1)];
            for (int i = 0; i < movieArray.length(); i++) {
                // Get the current JSON object in the array
                JSONObject movieJson = movieArray.getJSONObject(i);

                Movie currentMovie = new Movie();
                currentMovie.id = movieJson.getInt(TMDB_ID);
                currentMovie.title = moviesJson.getString(TMDB_TITLE);
                currentMovie.posterPath = moviesJson.getString(TMDB_POSTER_PATH);
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
            String api_key = "0000000000000000000000000"; //TODO: add the correct API key here

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

                URL url = new URL(builtUri.toString());


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
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
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviePostersFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie data
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mMoviesAdapter.clear();
                for (Movie movie : movies) {
                    mMoviesAdapter.add(movie);
                }
            }
        }

    }
}
