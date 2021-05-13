package com.example.movies;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

//Source: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#attaching-event-handlers-within-adapter
public class MovieAdapter extends ArrayAdapter<Movie> {
    CheckBox checkBox;

    public MovieAdapter(@NonNull Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Movie movie = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_listview, parent, false);

        }
        checkBox = convertView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                movie.setFavourite(isChecked);

            }
        });

        // Populate the data into the template view using the data object
        checkBox.setText(movie.getName());
        checkBox.setChecked(movie.getFavourite());
        // Return the completed view to render on screen
        return convertView;
    }
}
