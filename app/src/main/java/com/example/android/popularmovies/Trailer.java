package com.example.android.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Ken on 10/5/2015.
 */
public class Trailer implements Parcelable {

    private final String TAG = Trailer.class.getSimpleName();
    private final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch";
    private final String VIDEO_KEY = "v";

    // parcel keys
    private static final String KEY_ID = "id";
    private static final String KEY_TRAILER = "trailer_key";
    private static final String KEY_NAME = "name";
    private static final String KEY_SITE = "site";
    private static final String KEY_RESOLUTIONP = "resolutionP";
    private static final String KEY_TYPE = "type";

    private String id;
    private String trailer_key;
    private String name;
    private String site;
    private int resolutionP;
    private String type;

    public Trailer(String id, String trailer_key, String name, String site, int resolutionP, String type) {
        this.id = id;
        this.trailer_key = trailer_key;
        this.name = name;
        this.site = site;
        this.resolutionP = resolutionP;
        this.type = type;
    }

    public Trailer(Parcel in) {
        String[] data = new String[6];
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        bundle.putString(KEY_ID, id);
        bundle.putString(KEY_TRAILER, trailer_key);
        bundle.putString(KEY_NAME, name);
        bundle.putString(KEY_SITE, site);
        bundle.putInt(KEY_RESOLUTIONP, resolutionP);
        bundle.putString(KEY_TYPE, type);

        out.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            // read the bundle containing the key value pairs from the parcel
            Bundle bundle = source.readBundle();

            Trailer __trailer = new Trailer(bundle.getString(KEY_ID),
                    bundle.getString(KEY_TRAILER),
                    bundle.getString(KEY_NAME),
                    bundle.getString(KEY_SITE),
                    bundle.getInt(KEY_RESOLUTIONP),
                    bundle.getString(KEY_SITE));

            return __trailer;
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrailerKey() {
        return trailer_key;
    }

    public void setTrailerKey(String key) {
        this.trailer_key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getResolutionP() {
        return resolutionP;
    }

    public void setResolutionP(int resolutionP) {
        this.resolutionP = resolutionP;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYouTubeUrl() {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(VIDEO_KEY, trailer_key)
                .build();
        Log.d(TAG, builtUri.toString());
        return builtUri.toString();
    }

    public String getTrailerLabel() {
        return String.format("%s (%s)", name, type);
    }
}
