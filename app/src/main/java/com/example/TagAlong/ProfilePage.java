package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ProfilePage extends AppCompatActivity {

    private String matchUserId;
    private String TAG = "Data";
    private Profile matchProfile;
    private TextView match_username;
    private TextView match_age;
    private TextView match_location;
    private TextView match_gender;
    private TextView match_about;
    private ImageView match_image;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Intent intent = getIntent();
        matchUserId = intent.getStringExtra("matchUser");

        match_username = findViewById(R.id._username);
        match_age = findViewById(R.id._age);
        match_location = findViewById(R.id._location);
        match_gender = findViewById(R.id._gender);
        match_about = findViewById(R.id._about);
        match_image = findViewById(R.id.matchProfileImage);
        backButton = findViewById(R.id.backTo);

        FirebaseFirestore db = DbOperation.getInstance().db;

        getUserProfile(db);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

    }

    public void getUserProfile(FirebaseFirestore db) {
        DocumentReference docRef = db.collection("users").document(matchUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> placeMap = document.getData();
                        matchProfile = new Profile(placeMap);
                        UpdateViewWithData(matchProfile);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void UpdateViewWithData(Profile matchProfile) {

        match_username.setHint(matchProfile.Name);
        match_gender.setHint(matchProfile.Gender);
        match_age.setHint(String.valueOf(matchProfile.Age));
        match_location.setHint(matchProfile.LocationName);
        match_about.setHint(matchProfile.About);
        Picasso.get().load(matchProfile.ImageUri).resize(110,100).into(match_image);

    }

    public void callUser(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + matchProfile.PhoneNumber));

        if (ActivityCompat.checkSelfPermission(ProfilePage.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);

    }

    @SuppressLint("IntentReset")
    public void emailUser(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        String[] TO = {matchProfile.Email, ""};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, matchProfile.About);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ProfilePage.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToFriendList(View view) {
        finish();
    }
}