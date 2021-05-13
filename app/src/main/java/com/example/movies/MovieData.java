package com.example.movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.movies.Constants.ACTOR_LIST;
import static com.example.movies.Constants.DIRECTOR;
import static com.example.movies.Constants.FAVOURITES;
import static com.example.movies.Constants.RATING;
import static com.example.movies.Constants.REVIEW;
import static com.example.movies.Constants.TABLE_NAME;
import static com.example.movies.Constants.TITLE;
import static com.example.movies.Constants.YEAR;

//Source: https://learn-eu-central-1-prod-fleet01-xythos.content.blackboardcdn.com/5d07709844dfc/11868833?X-Blackboard-Expiration=1618326000000&X-Blackboard-Signature=J%2FYxNTIUrBqTjL7d5RLm%2FFmfwCLBtT7gcf5gl9awDrs%3D&X-Blackboard-Client-Id=156017&response-cache-control=private%2C%20max-age%3D21600&response-content-disposition=inline%3B%20filename%2A%3DUTF-8%27%275COSC011C_Lecture5_Slides.pdf&response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20210413T090000Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21600&X-Amz-Credential=AKIAZH6WM4PL5M5HI5WH%2F20210413%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=9cfbd6b54c7e93adde3f1f8775c9e9334cbf13db7019ca2e1bb4fc1e1d556312
//Source: https://learning.westminster.ac.uk/bbcswebdav/pid-3217942-dt-content-rid-27145073_1/xid-27145073_1
public class MovieData extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieData(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + TABLE_NAME + "("
        + TITLE + " TEXT PRIMARY KEY NOT NULL,"
        + YEAR + " YEAR,"
        + DIRECTOR + " TEXT,"
        + ACTOR_LIST + " TEXT,"
        + RATING + " INT,"
        + REVIEW + " TEXT, "
        + FAVOURITES + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
