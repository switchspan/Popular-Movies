package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ken on 8/24/2015.
 */
public class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private List<Movie> movies = new ArrayList<Movie>();

    public ImageListAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.list_item_movie, movies);

        this.context = context;
        this.movies = movies;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.list_item_movie, parent, false);
        }
        Picasso.with(context)
                .load(movies.get(position).getPosterImageUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .fit()
                .into((ImageView) convertView);

        return convertView;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replace(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }
}
