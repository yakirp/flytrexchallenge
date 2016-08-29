package com.yakirp.flytrexchallenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yakirp on 8/30/2016.
 */
public class MoviesAdapter extends ArrayAdapter<FlytrexMovie> {
    public MoviesAdapter(Context context, List<FlytrexMovie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FlytrexMovie movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.title);
        TextView tvHome = (TextView) convertView.findViewById(R.id.signature);
        // Populate the data into the template view using the data object
        tvName.setText(movie.getTitle());
        tvHome.setText(movie.isVerified() == true ? "" : "Signature is wrong");
        // Return the completed view to render on screen
        return convertView;
    }
}
