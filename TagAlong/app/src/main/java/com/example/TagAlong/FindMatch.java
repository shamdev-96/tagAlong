package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.TagAlong.Models.MatchedUsers;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.OperationHelper.FriendListOperation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FindMatch extends AppCompatActivity {

    private Handler handler;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_match);

        mAuth = FirebaseAuth.getInstance();
        final TextView loadText = findViewById(R.id.loadingText);
        final LottieAnimationView findLottie = findViewById(R.id.findLottie);
        final LottieAnimationView matchLottie = findViewById(R.id.matchLottie);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loadText.setText("Finding your match..");

        Intent intent = getIntent();
        final String placeName = intent.getStringExtra("placeName");


                DocumentReference docRef =  DbOperation.getInstance().db.collection("matchedUsers").document(placeName);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> map = document.getData();
                                MatchedUsers user = new MatchedUsers(map);
                                CheckToGetMatch(user);

                            }
                        }
                    }
                });


            }

    public void CheckToGetMatch(MatchedUsers user)
    {

        int countMatch = user.Users.size();

        //here means only the current user is selectedPlace

        if(countMatch == 1)
        {
            Intent intent = new Intent(FindMatch.this , NoFoundMatch.class);
            startActivity(intent);
            finish();

        }
        else
        {
            String selectedUser = "";

            ArrayList<String> list = FriendListOperation.Instance().GetMyFriendlist();

            //remove current user from the list first;

            user.Users.remove(mAuth.getUid());

            if(user.Users.size() == 1)
            {
                selectedUser = user.Users.get(0);
            }

            else
            {
                int min = 0;
                int max = user.Users.size() - 1;

                int random_int = (int)(Math.random() * (max - min + 1) + min);
                selectedUser = user.Users.get(random_int);

            }

            if(list.size() > 0 && list.contains(selectedUser) )
            {
                Intent intent = new Intent(FindMatch.this , NoFoundMatch.class);
                startActivity(intent);
                finish();
            }
            else //means list is 0 and
            {
                Intent intent = new Intent(FindMatch.this , FoundMatch.class);
                intent.putExtra("matchUser" , selectedUser);
                startActivity(intent);
                finish();
            }

        }
    }
}