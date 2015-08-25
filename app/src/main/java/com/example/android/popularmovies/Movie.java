package com.example.android.popularmovies;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ken on 7/19/2015.
 */
public class Movie implements Parcelable {

    private static final String TAG = Movie.class.getSimpleName();
    private static final int DEFAULT_POSTER_WIDTH = 185;
    private static final int DEFAULT_THUMBNAIL_WIDTH = 92;

    // create date formatter for release date
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // parcel keys
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSTER = "posterPath";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_VOTE_AVERAGE = "vote_average";

    private int id;
    private String title;
    private String posterPath;
    private String overview;
    private Date release_date;
    private double vote_average;


    public Movie(int id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    public Movie(int id, String title, String posterPath, String overview, Date release_date, Double vote_average) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.release_date = release_date;
        this.vote_average = vote_average;
    }

    public Movie(Parcel in) {
        String[] data = new String[6];
    }

    public String getPosterImageUrl(int width) {
        if (getPosterPath() == null || getPosterPath().length() < 1) return "";

        return String.format("http://image.tmdb.org/t/p/w%s/%s", width, getPosterPath());
    }

    public String getPosterImageUrl() {
        return getPosterImageUrl(DEFAULT_POSTER_WIDTH);
    }

    public String getPosterThumbnailUrl() { return getPosterImageUrl(DEFAULT_THUMBNAIL_WIDTH); }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        bundle.putInt(KEY_ID, id);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_POSTER, posterPath);
        bundle.putString(KEY_OVERVIEW, overview);
        bundle.putString(KEY_RELEASE_DATE, dateFormat.format(release_date));
        bundle.putDouble(KEY_VOTE_AVERAGE, vote_average);

        out.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            // read the bundle containing the key value pairs from the parcel
            Bundle bundle = source.readBundle();

            Movie _movie = new Movie(bundle.getInt(KEY_ID),
                    bundle.getString(KEY_POSTER));

            _movie.setTitle(bundle.getString(KEY_TITLE));
            _movie.setOverview(bundle.getString(KEY_OVERVIEW));
            _movie.setVoteAverage(bundle.getDouble(KEY_VOTE_AVERAGE));

            try {
                _movie.setReleaseDate(dateFormat.parse(bundle.getString(KEY_RELEASE_DATE)));
            } catch (ParseException pe) {
                Log.w(TAG, "Unable to get release date!");
                Log.e(TAG, pe.getMessage());
                throw new IllegalArgumentException();
            }
            return _movie;
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return release_date;
    }

    public String getReleaseDateAsString() {
        return dateFormat.format(getReleaseDate());
    }

    public void setReleaseDate(Date release_date) {
        this.release_date = release_date;
    }

    public double getVoteAverage() {
        return vote_average;
    }

    public String getVoteAverageAsString() { return String.format("%.1f/10", getVoteAverage()); }

    public void setVoteAverage(double vote_average) {
        this.vote_average = vote_average;
    }
}
