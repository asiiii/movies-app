package com.example.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.movies.Constants.TABLE_NAME;
import static com.example.movies.Constants.TITLE;
import static com.example.movies.Constants.YEAR;

public class RatingsActivity extends AppCompatActivity {
    public static final String TAG = RatingsActivity.class.getSimpleName();
    public MovieData movies;
    public SQLiteDatabase database;
    public static final String[] FROM = {TITLE, YEAR};
    public static final String ORDER_BY = TITLE;
    public ArrayList<Movie> movieList = new ArrayList<Movie>();
    public ArrayList<Movie> receivedMovieList;
    RadioGroup radioGroup;
    private static final String API_KEY = "k_jxs90dqv";
    int selectedButtonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        //initializing the instance of MovieData
        movies = new MovieData(this);

        //calling the getMovies to retrieve data from database
        getMovies();

        radioGroup = findViewById(R.id.radiogroup_movies);

        //checks if there are data in database
        //if so generates the RadioButtons to display
        //if not AlertDialog is shown
        if (movieList.size() > 0) {
            //creating and adding RadioButtons to layout
            int i = 0;
            for (Movie movie : movieList) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setId(i);
                i++;
                //RadioButton will display the movie title and year
                String displayText = movie.getName() + " " + movie.getYear();
                radioButton.setText(displayText);
                radioButton.setTextColor(getResources().getColor(R.color.font_color));
                radioButton.setTextSize(14);
                radioButton.setTypeface(Typeface.SANS_SERIF);
                radioButton.setAllCaps(true);
                radioGroup.addView(radioButton);

                //setOnCheckedChangeListener for the RadioGroup saves the ID of the selected button everytime it changes
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        selectedButtonId = radioGroup.getCheckedRadioButtonId();
                    }
                });
            }
        } else {

            alertDialog(this, "Cannot load movie list", "To display the list, please register movies first.");
        }


    }

    //method to initialize ReadableDatabase
    // execute the SELECT query that gets the title and year fo the selected movie
    // and retrieve data from database and add them to an ArrayList as a Movie object
    public Cursor getMovies() {
        database = movies.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, FROM, null, null, null, null, ORDER_BY);
        while (cursor.moveToNext()) {
            String movieTitle = cursor.getString(0);
            int year = cursor.getInt(1);
            movieList.add(new Movie(movieTitle, year));
        }
        cursor.close();
        return cursor;
    }

    //method to get selected radio button and call for the methods to access the IMDB API
    public void searchIMDB(View view) {
        //gets the checked RadioButton and checks if a RadioButton has been clicked
        //if so proceeds with execution
        //if not displayed AlertDialog
        //Source: https://stackoverflow.com/questions/18179124/android-getting-value-from-selected-radiobutton
        int selectedMovieId = radioGroup.getCheckedRadioButtonId();
        if (selectedMovieId != -1) {
            RadioButton selectedMovieBtn = findViewById(selectedMovieId);
            String selectedMovie = selectedMovieBtn.getText().toString();

            //receivedMovieList, the ArrayList to strore the details retrieved from the API as a Movie object is initialized
            receivedMovieList = new ArrayList<>();
            try {
                //calls method to get details for the SearchTitle API
                setContentView(R.layout.progress_bar);
                getMovieTitle(selectedMovie);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            alertDialog(this, "No radio button selected!", "Please select a movie to proceed.");
        }
    }

    //Source: https://learn-eu-central-1-prod-fleet01-xythos.content.blackboardcdn.com/5d07709844dfc/11991277?X-Blackboard-Expiration=1618326000000&X-Blackboard-Signature=TJJKN6BJgyZWGv37Zzdy2uuZcRC6O1X36nRQz7njV0s%3D&X-Blackboard-Client-Id=156017&response-cache-control=private%2C%20max-age%3D21600&response-content-disposition=inline%3B%20filename%2A%3DUTF-8%27%275COSC011C_Lecture6_Slides.pdf&response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20210413T090000Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21600&X-Amz-Credential=AKIAZH6WM4PL5M5HI5WH%2F20210413%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=8c8714380c398b1d60f0091363a00ff72dd346c0c8ea513415c687ca2bc1f6db
    //method that contains the Thread that accesses the SearchTitle API
    private void getMovieTitle(String selectedMovie) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //defines the URL
                String urlText = "https://imdb-api.com/en/API/SearchTitle/" + API_KEY + "/" + selectedMovie;
                InputStream inputStream;
                try {
                    //establishes connection
                    URL movieUrl = new URL(urlText);
                    HttpURLConnection connection = (HttpURLConnection) movieUrl.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(20000);
                    connection.setRequestMethod("GET");
                    connection.connect();

                    inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    //InputStream is saved to a StringBuilder
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    inputStream.close();

                    //JSON values are obtained from the StringBuilder
                    try {
                        JSONObject json = new JSONObject(stringBuilder.toString());
                        JSONArray json_array = json.getJSONArray("results");

                        //Movie title, id and imageURL are retrieved and added to receivedMovieList as a Movie Object
                        for (int i = 0; i < json_array.length(); i++) {
                            String movie = json_array.getJSONObject(i).getString("title");

                            String id = json_array.getJSONObject(i).getString("id");
                            receivedMovieList.add(new Movie(movie, id));

                            String imageURL = json_array.getJSONObject(i).getString("image");
                            receivedMovieList.get(i).setImageURL(imageURL);
                            Log.d(TAG, "Retrieved movie title:" + movie + ";ID: " + id);

                            //calling method to get the movie rating using the ID of the retrieved movie and the loop index at this iteration
                            getMovieRating(id, i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        //calls method to display the retrieved data
        displayDetails();
    }

    //method to retrieve the movie rating from the UserRating API
    private void getMovieRating(String id, int i) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //defines the URL
                String urlText = "https://imdb-api.com/en/API/UserRatings/" + API_KEY + "/" + id;
                InputStream inputStream;
                try {
                    //establishes connection
                    URL ratingUrl = new URL(urlText);
                    HttpURLConnection connection = (HttpURLConnection) ratingUrl.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(20000);
                    connection.setRequestMethod("GET");
                    connection.connect();

                    inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";

                    //InputStream is saved to a StringBuilder
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    inputStream.close();
                    String rating = null;

                    //JSON values are obtained from the StringBuilder
                    try {
                        JSONObject json = new JSONObject(stringBuilder.toString());
                        try {
                            //rating is retrieved and assigned to variable
                            rating = json.getString("totalRating");
                            //checks if rating is null
                            //if so value of rating variable is set as "Rating not available"
                            //else it is set with the retrieved values
                            if (rating.equals("null") || rating.equals("")) {
                                receivedMovieList.get(i).setRating("Rating not available");
                            } else {
                                receivedMovieList.get(i).setRating(rating);
                            }
                        } catch (Exception e) {
                            receivedMovieList.get(i).setRating("Rating inaccessible.");
                        }
                        Log.d(TAG, "Retrieved movie title: " + receivedMovieList.get(i).getName() + "; Rating: " + rating);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        thread.join();
    }

    //Source: https://medium.com/pongploydev/android-pass-object-arraylist-modelobject-from-activity-or-fragment-to-bundle-153847af90f0
    //method to open new Activity and display data
    private void displayDetails() {
        //receivedMovieList is passed to new Activity as a ParcelableArrayListExtra
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putParcelableArrayListExtra("RESULTS", receivedMovieList);
        Log.d(TAG, "Sending data to be displayed to ResultsActivity");
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

    // onSaveInstance and onRestoreInstance the save the state of the RadioButtons during configuration changes
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("SELECTED_BUTTON", selectedButtonId);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        // Restore state members from saved instance
        selectedButtonId = savedInstanceState.getInt("SELECTED_BUTTON");
        RadioButton radioButton = findViewById(selectedButtonId);
        radioButton.setChecked(true);
    }
}