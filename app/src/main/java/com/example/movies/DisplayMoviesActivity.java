package com.example.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.movies.Constants.FAVOURITES;
import static com.example.movies.Constants.TABLE_NAME;
import static com.example.movies.Constants.TITLE;

public class DisplayMoviesActivity extends AppCompatActivity {
    public static final String TAG = DisplayMoviesActivity.class.getSimpleName();
    public MovieData movies;
    public SQLiteDatabase database;
    public static final String[] FROM = {TITLE};
    public static final String ORDER_BY = TITLE;
    public ArrayList<Movie> movieList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    static final String MY_PREFERENCES = "MyPreferences";
    static final String IS_CHECKED_KEY = "isCheckedKey";
    MovieAdapter movieAdapter;
    ListView movieListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_movies);

        //initializing the instance of MovieData
        movies = new MovieData(this);

        //calling the displayMovies to retrieve data from database
        displayMovies();

        //checks if there are data in database
        //if so generates the ListView to display
        //if not AlertDialog is shown
        if (movieList.size() > 0) {
            //initializing MovieAdapter and setting it to the ListView
            movieAdapter = new MovieAdapter(this, movieList);

            movieListView = findViewById(R.id.listview_movies);
            movieListView.setAdapter(movieAdapter);
        } else {
            alertDialog2(this, "Cannot load movie list", "To add to Favourites, please register movies first.");
        }
    }

    //Source: https://learn-eu-central-1-prod-fleet01-xythos.content.blackboardcdn.com/5d07709844dfc/11868833?X-Blackboard-Expiration=1618326000000&X-Blackboard-Signature=J%2FYxNTIUrBqTjL7d5RLm%2FFmfwCLBtT7gcf5gl9awDrs%3D&X-Blackboard-Client-Id=156017&response-cache-control=private%2C%20max-age%3D21600&response-content-disposition=inline%3B%20filename%2A%3DUTF-8%27%275COSC011C_Lecture5_Slides.pdf&response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20210413T090000Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21600&X-Amz-Credential=AKIAZH6WM4PL5M5HI5WH%2F20210413%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=9cfbd6b54c7e93adde3f1f8775c9e9334cbf13db7019ca2e1bb4fc1e1d556312
    //Source: https://learning.westminster.ac.uk/bbcswebdav/pid-3217942-dt-content-rid-27145073_1/xid-27145073_1
    //method to retrieve data from database and save them to variables
    public void displayMovies() {
        Cursor cursor = getMovies();
        while (cursor.moveToNext()) {
            String movieTitle = cursor.getString(0);
            movieList.add(new Movie(movieTitle, false));
        }
        cursor.close();
        Log.d(TAG, "Movie titles retrieved from database ");
    }

    //method that initializes the ReadableDatabase and executes the SELECT query
    public Cursor getMovies() {
        database = movies.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, FROM, null, null, null, null, ORDER_BY);
        return cursor;
    }

    //method to save the selected movies as Favourites after the button click
    public void addToFavourites(View view) {
        boolean check = false;
        //checks if at least one checkbox has been selected
        //if not displays AlterDialog
        for (Movie movie : movieList) {
            if (movie.getFavourite() == true) {
                check = true;
                break;
            }
        }

        //if at least one CheckBox is ticked
        if (check) {

            //Source: https://learn-eu-central-1-prod-fleet01-xythos.content.blackboardcdn.com/5d07709844dfc/11868833?X-Blackboard-Expiration=1618326000000&X-Blackboard-Signature=J%2FYxNTIUrBqTjL7d5RLm%2FFmfwCLBtT7gcf5gl9awDrs%3D&X-Blackboard-Client-Id=156017&response-cache-control=private%2C%20max-age%3D21600&response-content-disposition=inline%3B%20filename%2A%3DUTF-8%27%275COSC011C_Lecture5_Slides.pdf&response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20210413T090000Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21600&X-Amz-Credential=AKIAZH6WM4PL5M5HI5WH%2F20210413%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=9cfbd6b54c7e93adde3f1f8775c9e9334cbf13db7019ca2e1bb4fc1e1d556312
            //Source: https://learning.westminster.ac.uk/bbcswebdav/pid-3217942-dt-content-rid-27145073_1/xid-27145073_1
            //initializes the database as Writable and adds each status of Favourites of the movies in ArrayList movieList to ContentValues
            //executes the update query to change Favourites to true for selected movies
            database = movies.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            for (Movie movie : movieList) {
                if (movie.getFavourite()) {
                    contentValues.put(FAVOURITES, movie.getFavourite().toString());
                    database.update(TABLE_NAME, contentValues, "MOVIE_TITLE = ? ", new String[]{movie.getName()});
                    Log.d(TAG, movie.getName() + " added to Favourites");
                }
            }
            Log.d(TAG, "Database is updated");
            alertDialog1(this, "Added to Favourites!");
        } else {
            alertDialog2(this, "Error!", "Please select the movies to be added to Favourites.");
            Log.d(TAG, "Waiting for user to select movies.");
        }
    }

    //method to create and display an alertDialog with only a Title
    //the value of the Title is passed into the method
    private void alertDialog1(Context context, String title) {
        Log.d(TAG, "Calling alert 1");
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .create();
        dialog.show();
    }

    //method to create and display a simple AlertDialog
    //Title and message of the alert is passed into the method
    private void alertDialog2(Context context, String title, String message) {
        Log.d(TAG, "Calling alert 2");
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .create();
        dialog.show();
    }

    // onSaveInstance and onRestoreInstance the save the state of the CheckBoxes during configuration changes
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("MOVIE_LIST", movieList);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        // Restore state members from saved instance
        movieList = savedInstanceState.getParcelableArrayList("MOVIE_LIST");
        movieAdapter = new MovieAdapter(this, movieList);
        movieListView.setAdapter(movieAdapter);
    }
}