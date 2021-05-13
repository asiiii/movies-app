package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getSimpleName();
    public MovieData movies;
    public SQLiteDatabase database;
    EditText movieTitle, year, director, listOfActors, review, rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing the instance of MovieData
        movies = new MovieData(this);

        //assigning Views to variables
        movieTitle = findViewById(R.id.editText_movie_title);
        year = findViewById(R.id.editText_year);
        director = findViewById(R.id.edittext_director);
        listOfActors = findViewById(R.id.editText_actor_list);
        rating = findViewById(R.id.editText_rating);
        review = findViewById(R.id.editText_review);

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
                    try {
                        //converts extracted String to int
                        //checks the range and the format
                        //setError() to null if valid
                        //setError to error message if not valid
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

        //rating input is validated by TextValidation class
        rating.addTextChangedListener(new TextValidation(rating) {

            @Override
            public void validate(EditText textView, String input) {
                if (!textView.getText().toString().isEmpty()) {
                    try {
                        //converts extracted String to int
                        //checks the range and the format
                        //setError() to null if valid
                        //setError to error message if not valid
                        int numInput = Integer.parseInt(textView.getText().toString());
                        if (numInput >= 1 && numInput <= 10) {
                            rating.setError(null);
                        } else {
                            Log.d(TAG, "Input is not within the range.");
                            rating.setError("Rating must be from 1 - 10!");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Input is not a whole number.");
                        rating.setError("Invalid input!");
                    }
                } else {
                    checkIfEmpty(textView);
                }
            }
        });
    }

    //method to save the details entered to database after 'Save' button is clicked
    public void onSaveButtonClick(View view) {
        //checks if all fields are filled
        // if yes proceeds with saving
        //if not AlertDialog is displayed
        if (!checkIfEmpty(movieTitle) && !checkIfEmpty(year) && !checkIfEmpty(director) && !checkIfEmpty(listOfActors)
                && !checkIfEmpty(rating) && !checkIfEmpty(review)) {

            //taking the values entered to the EditText and saving them in a variable
            String movieTitleInput = movieTitle.getText().toString();
            int yearInput = Integer.parseInt(year.getText().toString());
            String directorInput = director.getText().toString();
            String actorListInput = listOfActors.getText().toString();
            int ratingInput = Integer.parseInt(rating.getText().toString());
            String reviewInput = review.getText().toString();

            //Source: https://learn-eu-central-1-prod-fleet01-xythos.content.blackboardcdn.com/5d07709844dfc/11868833?X-Blackboard-Expiration=1618326000000&X-Blackboard-Signature=J%2FYxNTIUrBqTjL7d5RLm%2FFmfwCLBtT7gcf5gl9awDrs%3D&X-Blackboard-Client-Id=156017&response-cache-control=private%2C%20max-age%3D21600&response-content-disposition=inline%3B%20filename%2A%3DUTF-8%27%275COSC011C_Lecture5_Slides.pdf&response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20210413T090000Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21600&X-Amz-Credential=AKIAZH6WM4PL5M5HI5WH%2F20210413%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=9cfbd6b54c7e93adde3f1f8775c9e9334cbf13db7019ca2e1bb4fc1e1d556312
            //Source: https://learning.westminster.ac.uk/bbcswebdav/pid-3217942-dt-content-rid-27145073_1/xid-27145073_1
            //initialing SQLite database to be Writable
            database = movies.getWritableDatabase();
            //adding data from the user to ContentValues and inserting to database
            //Favourites is set to false
            ContentValues values = new ContentValues();
            values.put(TITLE, movieTitleInput);
            values.put(YEAR, yearInput);
            values.put(DIRECTOR, directorInput);
            values.put(ACTOR_LIST, actorListInput);
            values.put(RATING, ratingInput);
            values.put(REVIEW, reviewInput);
            values.put(FAVOURITES, false);

            database.insertOrThrow(TABLE_NAME, null, values);
            Log.d(TAG, "Record successfully added to database MovieData!");

            movieTitle.getText().clear();
            year.getText().clear();
            director.getText().clear();
            listOfActors.getText().clear();
            rating.getText().clear();
            review.getText().clear();

            alertDialog(this, "Registration successful!", "Movie has been added.");
        } else {
            Log.d(TAG, "Not al fields are filled.");
            alertDialog(this, "Error!", "Please fill ALL the fields, to complete the registration.");
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
                                Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789,: ]");
                        Matcher matcher = pattern.matcher(checkMe);
                        boolean valid = matcher.matches();
                        if (!valid) {
                            Log.d(TAG, "Invalid movieInput");
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
        Log.d(TAG, "Alert Dialog is called.");
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .create();
        dialog.show();
    }
}