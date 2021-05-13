package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.movies.Constants.ACTOR_LIST;
import static com.example.movies.Constants.DIRECTOR;
import static com.example.movies.Constants.FAVOURITES;
import static com.example.movies.Constants.RATING;
import static com.example.movies.Constants.REVIEW;
import static com.example.movies.Constants.TABLE_NAME;
import static com.example.movies.Constants.TITLE;
import static com.example.movies.Constants.YEAR;

public class EditMovieActivity extends AppCompatActivity {
    public static final String TAG = EditMovieActivity.class.getSimpleName();
    public MovieData movies;
    public SQLiteDatabase database;
    public static final String[] FROM = {TITLE, YEAR, DIRECTOR, ACTOR_LIST, RATING, REVIEW, FAVOURITES};
    String selectedMovieTitle, movieTitleText, directorText, actorListText, reviewText;
    int yearNum, ratingNum;
    TextView header;
    EditText movieTitle, year, director, listOfActors, review;//,rating;
    Boolean favouriteStatus;
    RadioGroup radioGroup;
    RadioButton favourites, notFavourites;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        //initializing the instance of MovieData
        movies = new MovieData(this);

        //saves Intent EXTRA to a variable
        selectedMovieTitle = getIntent().getStringExtra("movieTitle");

        //sets the name of the movie in a TextView
        header = findViewById(R.id.edit_movie_header);
        header.setText(getString(R.string.edit_movie_header_text, selectedMovieTitle));

        //assigning Views to variables
        movieTitle = findViewById(R.id.editText_edit_movie_title);
        director = findViewById(R.id.edittext_edit_director);
        year = findViewById(R.id.editText_edit_year);
        listOfActors = findViewById(R.id.editText_edit_actor_list);
        review = findViewById(R.id.editText_edit_review);
        favourites = findViewById(R.id.radioButton_favourite);
        notFavourites = findViewById(R.id.radioButton_unfavourite);
        radioGroup = findViewById(R.id.radio_group);
        ratingBar = findViewById(R.id.ratingBar);

        //calling getMovie method to get details of the movie
        getMovieDetails();

        //calling checkInput for the text EditText
        checkInput(movieTitle);
        checkInput(director);
        checkInput(listOfActors);
        checkInput(review);

        //year input is validated by TextValidation class
        year.addTextChangedListener(new TextValidation(year) {

            @Override
            public void validate(EditText textView, String input) {
                if (!textView.getText().toString().isEmpty()) {
                    //converts extracted String to int
                    //checks the range and the format
                    //setError() to null if valid
                    //setError to error message if not valid
                    try {
                        int numInput = Integer.parseInt(textView.getText().toString());
                        if (numInput > 1895) {
                            year.setError(null);
                        } else {
                            Log.d(TAG, "Year is not valid.");
                            year.setError("Year must be after 1895!");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Input is not a whole number.");
                        year.setError("Invalid input!");
                    }
                } else {
                    checkIfEmpty(textView);
                }
            }
        });

        //prevents from rating being 0
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (rating == 0){
                        ratingBar.setRating(1);
                    }
            }
        });

    }

    //method to initialize ReadableDatabase
    // execute the SELECT query that gets all the details fo the selected movie
    // and retrieve data from database and save them to variables
    public void getMovieDetails() {
        database = movies.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, FROM, TITLE + " = ?", new String[]{selectedMovieTitle}, null, null, null);
        while (cursor.moveToNext()) {
            movieTitleText = cursor.getString(0);
            yearNum = cursor.getInt(1);
            directorText = cursor.getString(2);
            actorListText = cursor.getString(3);
            ratingNum = cursor.getInt(4);
            reviewText = cursor.getString(5);
            favouriteStatus = Boolean.valueOf(cursor.getString(6));
        }
        cursor.close();
        Log.d(TAG, "Movie details retrieved from database ");
        //calls method to display the retrieved data
        displayMovieDetails();
    }

    //method to displayed retrieved data
    public void displayMovieDetails() {
        //sets retrieved data to the relevant View
        movieTitle.setText(movieTitleText);
        year.setText(String.valueOf(yearNum));
        director.setText(directorText);
        listOfActors.setText(actorListText);
        ratingBar.setRating(ratingNum);
        review.setText(reviewText);
        if (favouriteStatus == true) {
            favourites.setChecked(true);
        } else {
            notFavourites.setChecked(true);
        }
    }

    //method to update the database with edited data after 'Update' button is clicked
    public void launchUpdateButton(View view) {
        //checks if all fields are filled
        // if yes proceeds with saving
        //if not AlertDialog is displayed
        if (!checkIfEmpty(movieTitle) && !checkIfEmpty(year) && !checkIfEmpty(director) && !checkIfEmpty(listOfActors)
                && ratingBar.getRating() > 0 && !checkIfEmpty(review)) {


            //taking the values entered to the EditText and saving them in a variable
            movieTitleText = movieTitle.getText().toString();
            yearNum = Integer.parseInt(year.getText().toString());
            directorText = director.getText().toString();
            actorListText = listOfActors.getText().toString();
            ratingNum = Math.round(ratingBar.getRating());
            reviewText = review.getText().toString();
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.radioButton_favourite:
                    favouriteStatus = true;
                    break;
                default:
                    favouriteStatus = false;
                    break;
            }

            //initialing SQLite database to be Writable
            database = movies.getWritableDatabase();
            //adding data from the user to ContentValues and updating that record in the database
            //WHERE clause is used to update only the selected movie
            ContentValues contentValues = new ContentValues();
            contentValues.put(TITLE, movieTitleText);
            contentValues.put(YEAR, yearNum);
            contentValues.put(DIRECTOR, directorText);
            contentValues.put(ACTOR_LIST, actorListText);
            contentValues.put(RATING, ratingNum);
            contentValues.put(REVIEW, reviewText);
            contentValues.put(FAVOURITES, String.valueOf(favouriteStatus));
            database.update(TABLE_NAME, contentValues, TITLE + " = ? ", new String[]{selectedMovieTitle});
            Log.d(TAG, "Record successfully updated in database MovieData!");
            alertDialog(this, "Update Successful!", "Movie details have updated to new values.");
        } else {
            Log.d(TAG, "Not al fields are filled.");
            alertDialog(this, "Error!", "Please fill ALL the fields to complete the update.");
        }
    }

    //method to check if a given field is empty
    //if so error message is displayed
    private boolean checkIfEmpty(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            editText.setError("This is a required field!");
            return true;
        } else {
            editText.setError(null);
            return false;
        }
    }

    //method to check Text inputs
    private void checkInput(EditText editText) {
        editText.addTextChangedListener(new TextValidation(editText) {
            @Override
            public void validate(EditText textView, String input) {
                if (!input.isEmpty()) {
                    //takes a each character in the String and checks if it matches the Pattern given
                    //if not error message is shown
                    for (int i = 0; i < input.length(); i++) {
                        String checkMe = String.valueOf(input.charAt(i));
                        Pattern pattern =
                                Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789,:]");
                        Matcher matcher = pattern.matcher(checkMe);
                        boolean valid = matcher.matches();
                        if (!valid) {
                            Log.d("", "Invalid movieInput");
                            editText.setError("No special characters!");
                        } else {
                            editText.setError(null);
                        }
                    }
                } else {
                    checkIfEmpty(textView);
                }
            }
        });
    }

    //method to create and display a simple AlertDialog
    //Title and message of the alert is passed into the method
    private void alertDialog(Context context, String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .create();
        dialog.show();
    }
}