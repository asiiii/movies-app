package com.example.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.movies.Constants.ACTOR_LIST;
import static com.example.movies.Constants.DIRECTOR;
import static com.example.movies.Constants.TABLE_NAME;
import static com.example.movies.Constants.TITLE;

public class SearchActivity extends AppCompatActivity {
    public static final String TAG = SearchActivity.class.getSimpleName();
    public MovieData movies;
    public SQLiteDatabase database;
    ArrayList<String> resultMovieTitles = new ArrayList();
    TextView searchResultLabel;
    EditText searchText;
    ListView movieListView;
    ArrayAdapter<String> movieAdapter;
    String searchFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //initializing the instance of MovieData
        movies = new MovieData(this);

        //assigning Views to variables
        searchText = findViewById(R.id.editText_search);
        searchResultLabel = findViewById(R.id.search_result_label);
    }

    //method to execute the search on button click
    public void launchSearch(View view) {
        //checks if the search field is filled
        // if yes proceeds with teh search
        //if not AlertDialog is displayed
        if(!checkIfEmpty(searchText)){
            resultMovieTitles.clear();
            //gets the String input, assigns it to a variable and displays in the TextView
            searchFor = searchText.getText().toString();
            String searchForText = "\"" + searchFor + "\"...";
            searchResultLabel.setText(getString(R.string.search_result_label, searchForText));
            searchResultLabel.setBackgroundColor(getResources().getColor(R.color.component_color_2));

            //calls method to execute the search
            getResult();

            //checks if ArrayList is empty
            //if so an AlertDialog is displayed
            //if not the movie titles are displayed in a ListView
            if (resultMovieTitles.isEmpty()) {
                alertDialog(this, "Not found", "The input given cannot be located in the system.");
            } else {
                //ArrayAdapter is initialized and set with the ListView
                movieAdapter = new ArrayAdapter<String>(this, R.layout.layout_edit_listview, resultMovieTitles);
                movieListView = findViewById(R.id.listview_search_results);
                movieListView.setAdapter(movieAdapter);

                //setOnItemClickListener is used to execute the Intent that opens a new Activity with more details of the movie
                //(extra functionality)
                movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String movieTitle = (String) movieListView.getItemAtPosition(position);
                        Intent intent = new Intent(SearchActivity.this, DetailedSearchActivity.class);
                        intent.putExtra("movieTitle", movieTitle);
                        startActivity(intent);
                    }
                });
            }
        }else{
            Log.d(TAG, "Field is filled.");
            alertDialog(this, "Error!", "Please fill the search field, to start the search.");
        }

    }

    //method to search database for String input
    private void getResult() {
        //initializes database as ReadableDatabase
        database = movies.getReadableDatabase();

        //Source: https://stackoverflow.com/questions/9061410/android-sqlite-query-with-multiple-where
        //Source: https://stackoverflow.com/questions/9076561/android-sqlitedatabase-query-with-like
        //raw query is used to construct the SELECT statement to retrieve the matching movie title
        //searches the columns title, director and list of actors for the String input
        //LIKE clause with wildcard placeholder % is used to execute the searc
        Cursor cursor = database.rawQuery("SELECT " + TITLE + " FROM " + TABLE_NAME + " WHERE "
                        + TITLE + " LIKE ? OR "
                        + DIRECTOR + " LIKE ? OR "
                        + ACTOR_LIST + " LIKE ?",
                new String[]{"%" + searchFor + "%", "%" + searchFor + "%", "%" + searchFor + "%"});

        //retrieved titles are added to an ArrayList of Strings
        while (cursor.moveToNext()) {
            String movieTitle = cursor.getString(0);
            resultMovieTitles.add(movieTitle);
        }
        cursor.close();
        Log.d(TAG, "Movie titles retrieved from database ");
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

    // onSaveInstance and onRestoreInstance the save the state of the TextView and ListView during configuration changes
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("SEARCH_TERM", searchFor);
        outState.putStringArrayList("MOVIE_LIST", resultMovieTitles);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        // Restore state members from saved instance
        searchFor = savedInstanceState.getString("SEARCH_TERM");
        getResult();
        if (searchFor != null) {
            String searchForText = "\"" + searchFor + "\"...";
            searchResultLabel.setText(getString(R.string.search_result_label, searchForText));
            searchResultLabel.setBackgroundColor(getResources().getColor(R.color.component_color_2));
            resultMovieTitles = savedInstanceState.getStringArrayList("MOVIE_LIST");
            movieAdapter = new ArrayAdapter<String>(this, R.layout.layout_edit_listview, resultMovieTitles);
            movieListView = findViewById(R.id.listview_search_results);
            movieListView.setAdapter(movieAdapter);
            movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String movieTitle = (String) movieListView.getItemAtPosition(position);
                    Intent intent = new Intent(SearchActivity.this, DetailedSearchActivity.class);
                    intent.putExtra("movieTitle", movieTitle);
                    startActivity(intent);
                }
            });
        }
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