package com.example.TagAlong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class NoFriendList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_friend_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void backToProfile(View view) {
        finish();
    }
}