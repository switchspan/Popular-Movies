package com.example.android.popularmovies;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ken on 10/5/2015.
 */
public class Review implements Parcelable {
    private final String TAG = Trailer.class.getSimpleName();

    // parcel keys
    private static final String KEY_ID = "id";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_URL = "url";

    private String id;
    private String author;
    private String content;
    private String url;

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Review(Parcel in) {
        String[] data = new String[4];
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        bundle.putString(KEY_ID, id);
        bundle.putString(KEY_AUTHOR, author);
        bundle.putString(KEY_CONTENT, content);
        bundle.putString(KEY_URL, url);

        out.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            // read the bundle containing the key value pairs from the parcel
            Bundle bundle = source.readBundle();

            Review _review = new Review(bundle.getString(KEY_ID),
                    bundle.getString(KEY_AUTHOR),
                    bundle.getString(KEY_CONTENT),
                    bundle.getString(KEY_URL));

            return _review;
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[0];
        }
    };

    // Properties
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
