package com.example.android.popularmovies;

/**
 * Created by Ken on 7/19/2015.
 */
public class Movie {

    private static final int DEFAULT_POSTER_WIDTH = 185;

    public int id;
    public String title;
    public String posterPath;

    public String getPosterImageUrl(int width) {
        if (posterPath == null || posterPath.length() < 1) return "";

        return String.format("http://image.tmdb.org/t/p/w%s/%s", width, posterPath);
    }

    public String getPosterImageUrl() {
        return getPosterImageUrl(DEFAULT_POSTER_WIDTH);
    }
}
