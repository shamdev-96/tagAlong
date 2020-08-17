package com.example.TagAlong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class NoFoundMatch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_found_match);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void backToMenu(View view) {
        finish();
    }
}