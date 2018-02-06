package com.example.android.moviesdatabase;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by Emad on 06/02/2018.
 */

public class PreviewActivity extends AppCompatActivity {

    ArrayList<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        imagePaths = getIntent().getStringArrayListExtra("EXTRA_POSTER_URL");

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        ImageAdapter adapter = new ImageAdapter(this, imagePaths);

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
