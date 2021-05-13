package com.example.movies;

import android.provider.BaseColumns;
//Source: https://learn-eu-central-1-prod-fleet01-xythos.content.blackboardcdn.com/5d07709844dfc/11868833?X-Blackboard-Expiration=1618326000000&X-Blackboard-Signature=J%2FYxNTIUrBqTjL7d5RLm%2FFmfwCLBtT7gcf5gl9awDrs%3D&X-Blackboard-Client-Id=156017&response-cache-control=private%2C%20max-age%3D21600&response-content-disposition=inline%3B%20filename%2A%3DUTF-8%27%275COSC011C_Lecture5_Slides.pdf&response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20210413T090000Z&X-Amz-SignedHeaders=host&X-Amz-Expires=21600&X-Amz-Credential=AKIAZH6WM4PL5M5HI5WH%2F20210413%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=9cfbd6b54c7e93adde3f1f8775c9e9334cbf13db7019ca2e1bb4fc1e1d556312
//Source: https://learning.westminster.ac.uk/bbcswebdav/pid-3217942-dt-content-rid-27145073_1/xid-27145073_1
public interface Constants extends BaseColumns {
    public static final String TABLE_NAME = "movie_data";

    //table column names
    public static final String TITLE = "movie_title";
    public static final String YEAR = "year";
    public static final String DIRECTOR = "director";
    public static final String ACTOR_LIST = "actor_list";
    public static final String RATING = "rating";
    public static final String REVIEW = "review";
    public static final String FAVOURITES = "favourites";
}
