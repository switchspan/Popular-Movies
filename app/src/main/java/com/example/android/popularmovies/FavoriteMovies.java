package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ken.taylor on 10/7/2015.
 */
public class FavoriteMovies {

    private static final String TAG = FavoriteMovies.class.getSimpleName();
    public static final String FAVORITEMOVIES = "favoritemovies";
    public static final String FAVORITES_KEY = "favorite_movies";
    private Context _context;
    private ArrayList<Movie> _favoriteMovies = new ArrayList<>();

    public FavoriteMovies(Context context) {
        _context = context;
        load();
    }

    public ArrayList<Movie> getFavoriteMovies() {
        return _favoriteMovies;
    }

    public void update(Movie movie) {
        Log.v(TAG, "update");
        Log.v(TAG, String.format("Movie = %s", movie.getTitle()));
        String toastMessage;

        if (_favoriteMovies.size() < 1 || !_favoriteMovies.contains(movie)) {
            _favoriteMovies.add(movie);
            toastMessage = String.format("Added %s", movie.getTitle());
        } else {
            toastMessage = String.format("Removed %s", movie.getTitle());
        }

        Toast.makeText(_context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public void save() {
        Log.v(TAG, "saveFavoriteMovies");
        Gson gson = new Gson();
        String favoritesJson = gson.toJson(_favoriteMovies);
        Log.v(TAG, favoritesJson);

        SharedPreferences sharedPref = _context.getSharedPreferences(FAVORITEMOVIES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString(FAVORITES_KEY, favoritesJson);
        editor.commit();
    }

    public void load() {
        Log.v(TAG, "loadFavoriteMovies");
        SharedPreferences sharedPref = _context.getSharedPreferences(FAVORITEMOVIES, Context.MODE_PRIVATE);
        String favoritesJson = sharedPref.getString(FAVORITES_KEY, "");
        Log.v(TAG, favoritesJson);
        if (favoritesJson.length() < 1) {
            Log.w(TAG, "No favorite movies saved!");
            return;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Movie>>() {}.getType();
        _favoriteMovies = gson.fromJson(favoritesJson, listType);
    }

}
