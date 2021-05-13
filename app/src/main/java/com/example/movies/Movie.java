package com.example.movies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String name;
    private Boolean isFavourite;
    private String rating;
    private int year;
    private String movieId;
    private String imageURL;

    public Movie(String name) {
        this.name = name;
    }

    public Movie(String name, boolean isFavourite) {
        this.name = name;
        this.isFavourite = isFavourite;
    }

    public Movie(String name, int year) {
        this.name = name;
        this.year = year;
    }

    public Movie(String name, String id) {
        this.name = name;
        this.movieId = id;
    }

    protected Movie(Parcel in) {
        name = in.readString();
        rating = in.readString();
        imageURL = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setFavourite(Boolean favourite) {
        isFavourite = favourite;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(rating);
        dest.writeString(imageURL);

    }
}
