package com.example.movies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    public static final String TAG = RatingsActivity.class.getSimpleName();
    ArrayList<Movie> movieList;
    TableLayout tableLayout;
    SharedPreferences sharedpreferences;
    static final String MY_PREFERENCES = "MyPreferences";
    static final String ARRAYLIST_KEY = "isCheckedKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //assigning Views to variables
        tableLayout = findViewById(R.id.table_layout);

        //saves Intent EXTRA to a variable
        Intent intent = getIntent();
        movieList = intent.getParcelableArrayListExtra("RESULTS");

        //checks if movieList has data
        //if so proceeds with displaying
        //if not displays AlertDialog
        if(movieList.size()>0){
            //loops through the movieList sent by previous Activity
            //adds Views to the Layout and displays the data
            for (Movie movie : movieList) {
                Log.d(TAG, "Displaying results of " + movie.getName() + "; Rating: " + movie.getRating());
                //setting the movie title and the identifying label
                TextView titleLabel = new TextView(this);
                titleLabel.setText(R.string.movie_title_label);
                titleLabel.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                titleLabel.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                titleLabel.setTextSize(16);
                titleLabel.setTextColor(getResources().getColor(R.color.font_color));

                TextView movieTitle = new TextView(this);
                String title = " "+ movie.getName();
                movieTitle.setText(title);
                movieTitle.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                movieTitle.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                movieTitle.setTextSize(16);
                movieTitle.setTextColor(getResources().getColor(R.color.font_color));
                movieTitle.setClickable(true);
                movieTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayImage(movie.getImageURL());
                    }
                });

                //setting the movie rating and the identifying label
                TextView ratingLabel = new TextView(this);
                ratingLabel.setText(R.string.rating_label);
                ratingLabel.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                ratingLabel.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                ratingLabel.setTextSize(16);
                ratingLabel.setTextColor(getResources().getColor(R.color.font_color));

                TextView rating = new TextView(this);
                rating.setText(movie.getRating());
                rating.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                rating.setTypeface(Typeface.SANS_SERIF);
                rating.setTextSize(16);
                rating.setTextColor(getResources().getColor(R.color.font_color));

                //setting the movie image and the identifying label
                TextView imageText = new TextView(this);
                imageText.setText(R.string.image_label);
                imageText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                imageText.setTextSize(16);
                imageText.setTextColor(getResources().getColor(R.color.font_color));

                //sets title, rating and image Views to three separate TableRows and adds them to the TableLayout
                TableRow tableRow1 = new TableRow(this);
                tableRow1.addView(titleLabel, 0);
                tableRow1.addView(movieTitle, 1);
                tableRow1.setBackgroundColor(getResources().getColor(R.color.component_color_2));
                tableLayout.addView(tableRow1, 0);

                TableRow tableRow2 = new TableRow(this);
                tableRow2.addView(ratingLabel, 0);
                tableRow2.addView(rating, 1);
                tableRow2.setBackgroundColor(getResources().getColor(R.color.component_color_2));
                tableLayout.addView(tableRow2, 1);
            }
        }else {
            alertDialog(this, "Error!" , "Unable to get data from IMDB");
        }
    }

    private void displayImage(String imageURL) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("IMAGE_URL", imageURL);
        startActivity(intent);
    }

    //method to create and display a simple AlertDialog
    //Title and message of the alert is passed into the method
    private void alertDialog(Context context, String title, String message) {
        Log.d(TAG, "Calling alert");
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .create();
        dialog.show();
    }
}