package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //saves Intent EXTRA to a variable
        Intent intent = getIntent();
        String imageURL = intent.getStringExtra("IMAGE_URL");

        //assigning Views to variables
        ImageView imageView = findViewById(R.id.imageView);
        try {
            //calling method to load the image
            getImage(imageView, imageURL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Source: https://stackoverflow.com/questions/4223472/how-to-display-an-image-from-the-internet-in-android/4223594
    //method consisting of a Thread to download the image from the Internet and set is to an ImageView as a Drawable
    private void getImage(ImageView imageView, String url) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream;
                try {
                    URL imageURL = new URL(url);
                    inputStream = (InputStream) imageURL.getContent();
                    Drawable d = Drawable.createFromStream(inputStream, "src");
                    imageView.setImageDrawable(d);
                    Log.d("hi", "heyyyy");
                    inputStream.close();
                    imageView.setMinimumWidth(500);
                    imageView.setMinimumHeight(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
    }
}