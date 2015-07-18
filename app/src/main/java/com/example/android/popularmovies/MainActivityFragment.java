package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View returnView = inflater.inflate(R.layout.fragment_main, container, false);

        ImageView picassoView = (ImageView) returnView.findViewById(R.id.picasso_image);

        //Picasso.with(returnView.getContext()).load("http://i.imgur.com/DvpvklR.png").into(picassoView);
        Picasso.with(returnView.getContext()).load("http://i.dailymail.co.uk/i/pix/2015/01/26/2508DF8C00000578-0-image-a-125_1422246959340.jpg").into(picassoView);
        //http://barfblog.com/wp-content/uploads/2015/01/bearded_dragon_pic.jpg
        //http://i.dailymail.co.uk/i/pix/2015/01/26/2508DF8C00000578-0-image-a-125_1422246959340.jpg
        return returnView;
    }
}
