package com.example.movies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchRegisterActivity(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void launchDisplayMoviesActivity(View view){
        Intent intent = new Intent(this, DisplayMoviesActivity.class);
        startActivity(intent);
    }

    public void launchFavouritesActivity(View view){
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }

    public void launchEditActivity(View view){
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    public void launchSearchActivity(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void launchRatingsActivity(View view){
        Intent intent = new Intent(this, RatingsActivity.class);
        startActivity(intent);
    }
}