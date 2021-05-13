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
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.movies.Constants.FAVOURITES;
import static com.example.movies.Constants.TABLE_NAME;
import static com.example.movies.Constants.TITLE;

public class FavouritesActivity extends AppCompatActivity {
    public static final String TAG = FavouritesActivity.class.getSimpleName();
    public MovieData movies;
    public SQLiteDatabase database;
    public static final String[] FROM = {TITLE, FAVOURITES};
    public static final String ORDER_BY = TITLE;
    public ArrayList<Movie> movieList = new ArrayList<Movie>();
    SharedPreferences sharedpreferences;
    static final String MY_PREFERENCES = "MyPreferences";
    static final String IS_CHECKED_KEY = "isCheckedKey";
    MovieAdapter movieAdapter;
    ListView movieListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //initializing the instance of MovieData
        movies = new MovieData(this);

        //calling the displayMovies to retrieve data from database
        displayMovies();


        //checks if there are no Favourite movies in database
        //if so generates the ListView to display
        //if not AlertDialog is shown
        if (movieList.size() > 0) {
            //initializing MovieAdapter and setting it to the ListView
            movieAdapter = new MovieAdapter(this, movieList);

            movieListView = findViewById(R.id.listview_favourites);
            movieListView.setAdapter(movieAdapter);
        }else{
            alertDialog2(this, "Cannot load movie list", "To update Favourites, please register movies first.");
        }
    }

    //method to retrieve data from database and save them to variables
    public void displayMovies(){
        Cursor cursor = getMovies();
        while(cursor.moveToNext()){
            String movieTitle = cursor.getString(0);
            Boolean favourites = Boolean.valueOf(cursor.getString(1));
            movieList.add(new Movie(movieTitle, favourites));
        }
        cursor.close();
        Log.d(TAG, "Movie titles retrieved from database ");
    }

    //method that initializes the ReadableDatabase and executes the SELECT query
    //query retrieves the names of the titles and favourite status of the movies whose favourite status are true
    public Cursor getMovies(){
        database = movies.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, FROM, FAVOURITES + " = ?", new String[] {"true"}, null, null, ORDER_BY);
        return cursor;
    }

    //method to save the deselected movies as not Favourites after the button click
    public void saveFavourites(View view){
        //initializes the database as Writable and updates each status of Favourites of the movies in ArrayList movieList to ContentValues
        //executes the update query to change Favourites to false for the deselected selected movies
            ContentValues contentValues = new ContentValues();
            database = movies.getWritableDatabase();
            for(Movie movie: movieList){
                if(!movie.getFavourite()){
                    contentValues.put(FAVOURITES, movie.getFavourite().toString());
                    database.update(TABLE_NAME, contentValues, TITLE + " = ? ", new String[] {movie.getName()});
                    Log.d(TAG, movie.getName() + " removed from Favourites");
                }
            }
            Log.d(TAG, "Database is updated");
            alertDialog1(this, "Favourites list updated!");
            movieList.clear();
            movieAdapter.clear();

            //calls display method and sets MovieAdapter again to update the ListView displayed
            displayMovies();
            movieAdapter = new MovieAdapter(this, movieList);

            movieListView = findViewById(R.id.listview_favourites);
            movieListView.setAdapter(movieAdapter);
            if(movieList.isEmpty()){
                alertDialog2(this, "Cannot load movie list", "To update Favourites, please register movies first.");
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
        movieAdapter= new MovieAdapter(this, movieList);
        movieListView.setAdapter(movieAdapter);
    }

    //onPause, onStop and onResume to save CheckBox status to SavedPreferences
    public void onPause() {
        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        for (int i = 0; i < movieAdapter.getCount(); i++) {
            editor.putBoolean(IS_CHECKED_KEY + i, movieAdapter.getItem(i).getFavourite());
        }
        editor.apply();
        super.onPause();
    }

    public void onStop() {
        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        for (int i = 0; i < movieAdapter.getCount(); i++) {
            editor.putBoolean(IS_CHECKED_KEY + i, movieAdapter.getItem(i).getFavourite());
        }
        editor.apply();
        super.onStop();
    }

    public void onResume() {
        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        for (int i = 0; i < movieList.size(); ++i) {
            Boolean bool = sharedpreferences.getBoolean(IS_CHECKED_KEY + i, false);
            if (!bool.equals(false)) {
                movieAdapter.getItem(i).setFavourite(bool);
            }
        }
        super.onResume();
    }
}