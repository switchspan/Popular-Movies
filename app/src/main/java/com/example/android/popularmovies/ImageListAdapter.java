package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ken on 8/24/2015.
 */
public class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

//    private String[] imageUrls;
    private List<Movie> movies;

    public ImageListAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.list_item_movie, movies);

        this.context = context;
//        this.imageUrls = imageUrls;
        this.movies = movies;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.list_item_movie, parent, false);
        }

        //TODO: Fix this to take us to the correct screen
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();


                CharSequence text = "You clicked it!";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
            }
        });

        Picasso.with(context)
                .load(movies.get(position).getPosterImageUrl())
                .fit()
                .into((ImageView) convertView);

        return convertView;
    }
}
