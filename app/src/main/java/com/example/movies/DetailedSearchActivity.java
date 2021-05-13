package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import static com.example.movies.Constants.ACTOR_LIST;
import static com.example.movies.Constants.DIRECTOR;
import static com.example.movies.Constants.FAVOURITES;
import static com.example.movies.Constants.RATING;
import static com.example.movies.Constants.REVIEW;
import static com.example.movies.Constants.TABLE_NAME;
import static com.example.movies.Constants.TITLE;
import static com.example.movies.Constants.YEAR;

public class DetailedSearchActivity extends AppCompatActivity {
    public static final String TAG = DetailedSearchActivity.class.getSimpleName();
    public MovieData movies;
    public SQLiteDatabase database;
    public static final String[] FROM = {TITLE, YEAR, DIRECTOR, ACTOR_LIST, RATING, REVIEW, FAVOURITES};
    String selectedMovieTitle, movieTitleText, directorText, actorListText, reviewText, yearNum;
    Boolean favouriteStatus;
    int ratingNum;
    TextView title, year, director, actorList, review;
    RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_search);

        //initializing the instance of MovieData
        movies = new MovieData(this);

        //saves Intent EXTRA to a variable
        selectedMovieTitle = getIntent().getStringExtra("movieTitle");

        //assigning Views to variables
        title = findViewById(R.id.textview_title);
        title.setText(selectedMovieTitle);
        year = findViewById(R.id.textView_year);
        director = findViewById(R.id.textView_director);
        actorList = findViewById(R.id.textView_actor_list);
        review = findViewById(R.id.textView_review);
        rating = findViewById(R.id.ratingBar2);
        getMovieDetails();
    }

    //method to initialize ReadableDatabase
    // execute the SELECT query that gets all the details fo the selected movie
    // and retrieve data from database and save them to variables
    public void getMovieDetails() {
        database = movies.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, FROM, TITLE + " = ?", new String[]{selectedMovieTitle}, null, null, null);
        while (cursor.moveToNext()) {
            movieTitleText = cursor.getString(0);
            yearNum = String.valueOf(cursor.getInt(1));
            directorText = cursor.getString(2);
            actorListText = cursor.getString(3);
            ratingNum = cursor.getInt(4);
            reviewText = cursor.getString(5);
            favouriteStatus = Boolean.valueOf(cursor.getString(6));
        }
        cursor.close();
        Log.d(TAG, "Movie details retrieved from database ");
        displayMovieDetails();
    }

    //method to displayed retrieved data
    private void displayMovieDetails() {
        //sets retrieved data to the relevant View
        year.setText(yearNum);
        director.setText(directorText);
        //Splits the list of actors by comma and appends it to a StringBuilder with  \n
        String[] actors = actorListText.split(",", actorListText.length());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < actors.length; i++) {
            if (i == actors.length - 1) {
                stringBuilder.append(actors[i].trim());
                break;
            }
            stringBuilder.append(actors[i].trim()).append("\n");
        }

        actorList.setText(stringBuilder.toString());
        review.setText(reviewText);
        rating.setRating(ratingNum);
    }
}