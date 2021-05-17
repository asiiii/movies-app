package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.movies.Constants.TABLE_NAME;
import static com.example.movies.Constants.TITLE;

public class EditActivity extends AppCompatActivity {
    public static final String TAG = EditActivity.class.getSimpleName();
    public MovieData movies;
    public SQLiteDatabase database;
    public static final String[] FROM = {TITLE};
    public static final String ORDER_BY = TITLE;
    public ArrayAdapter<String> movieAdapter;
    public ArrayList<String> movieList = new ArrayList<String>();
    ListView movieListView;
    private ActionMode actionMode;
    int itemPosition;
    String movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //initializing the instance of MovieData
        movies = new MovieData(this);

        //calling the displayMovies to retrieve data from database
        displayMovies();

        //checks if there are data in database
        //if so generates the ListView to display
        //if not AlertDialog is shown
        if (movieList.size() > 0) {
            //initializing ArrayAdapter and setting it to the ListView
            movieAdapter = new ArrayAdapter<String>(this, R.layout.layout_edit_listview, movieList);
            movieListView = findViewById(R.id.listview_edit);
            movieListView.setAdapter(movieAdapter);

            //Source: https://www.youtube.com/watch?v=7GxxLxpwJOA
            //setOnItemClickListener is used to open the Activity with that movies details, onClick
            movieListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    itemPosition = position;
                    if(actionMode != null){
                        return false;
                    }
                    actionMode = startActionMode(actionModeCallback);
                    return true;
                }

            });
        }
    }

    //method to retrieve data from database and save them to variables
    public void displayMovies() {
        Cursor cursor = getMovies();
        while (cursor.moveToNext()) {
            String movieTitle = cursor.getString(0);
            movieList.add(movieTitle);
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

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_context_action_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId()==R.id.edit_action_bar) {
                openEditMovie();
                return true;
            }else if(item.getItemId() == R.id.delete_action_bar){
                openDeleteMovie();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    private void openEditMovie(){
        String movieTitle = (String) movieListView.getItemAtPosition(itemPosition);
        Intent intent = new Intent(EditActivity.this, EditMovieActivity.class);
        intent.putExtra("movieTitle", movieTitle);
        startActivity(intent);
    }

    private void openDeleteMovie(){
        movieTitle = (String) movieListView.getItemAtPosition(itemPosition);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirm deletion")
                .setMessage("Are you sure you want to delete " + movieTitle +"?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .create();
        dialog.show();
    }

    private void deletionDialog(){
        AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                .setTitle("Deletion Successful!")
                .setMessage("You have deleted " + movieTitle +"from your movie list")
                .setPositiveButton("Return to Home page", dialogClickListener2)
                .setNegativeButton("Return to Edit Movies", dialogClickListener2)
                .create();
        dialog.show();
    }

    // Set click listener for alert dialog buttons
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    // User clicked the Yes button
                    database = movies.getReadableDatabase();
                    //database.delete(TABLE_NAME, TITLE + " = ?", new String[]{movieTitle});
                    deletionDialog();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // User clicked the No button
                    break;
            }
        }
    };

    // Set click listener for alert dialog buttons
    DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // User clicked the No button
                    displayMovies();
                    movieAdapter = new ArrayAdapter<String>(EditActivity.this, R.layout.layout_edit_listview, movieList);
                    movieListView.setAdapter(movieAdapter);
                    break;
            }
        }
    };

}