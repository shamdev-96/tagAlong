package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.TagAlong.Models.MatchedUsers;
import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.OperationHelper.FriendListOperation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoundMatch extends AppCompatActivity {

    private TextView matchName;
    private TextView matchDetails;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String matchUserId;
    private ImageView profileImage;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private  final Map<String, Uri> friendsImage = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_match);
        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        matchUserId = intent.getStringExtra("matchUser");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        matchName = findViewById(R.id.matchName);
        matchDetails = findViewById(R.id.matchDetail);
        profileImage = findViewById(R.id.profileImage);

        db = DbOperation.getInstance().db;

        LoadProfileImage();
        LoadViewData(db);
    }

    private void LoadProfileImage() {

        storageReference.child(matchUserId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(220,210).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(FoundMatch.this , "Unable to load user image" , Toast.LENGTH_LONG);
                Picasso.get().load(Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png")).resize(220,210).into(profileImage);

            }
        });

    }

    private void LoadViewData(FirebaseFirestore db) {

        DocumentReference docRef = db.collection("users").document(matchUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> placeMap = document.getData();
                        Profile matchProfile = new Profile(placeMap);
                        matchName.setText("" + matchProfile.Name + "," + matchProfile.Age );
                        matchDetails.setHint("" + matchProfile.About + "");

                    }
                }
            }
        });
    }

    public void acceptMatch(View view) {

        progressDialog.setMessage("Adding into your friend list");
        progressDialog.show();

        final ArrayList<String> matchUsers = new ArrayList<>();
        DocumentReference docRef =  DbOperation.getInstance().db.collection("userMatchesList").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        MatchedUsers user = new MatchedUsers(map);
                        for(String perUser :  user.Users)
                        {
                            matchUsers.add(perUser);
                        }
                    }

                    if(!matchUsers.contains(matchUserId))
                    {
                        matchUsers.add(matchUserId);
                    }

                    Handler firebaseHandler = new Handler();

                    firebaseHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Map<String, Object> docData = new HashMap<>();
                            docData.put("users" , matchUsers);

                            db.collection("userMatchesList").document(mAuth.getUid()).set(docData);

//                            Map<String, Object> myData = new HashMap<>();
//                            myData.put("users" , mAuth.getUid());

                            AddToMyFriendList();

                            FriendListOperation.Instance().SaveMyFriendList(matchUsers);

                        }
                    },5000);

                    SaveFriendImage(matchUsers);

                }
            }
        });
    }

    private void AddToMyFriendList() {

        final ArrayList<String> existList = new ArrayList<>();
        final Map<String, Object> docData = new HashMap<>();
                DocumentReference docRef =  DbOperation.getInstance().db.collection("userMatchesList").document(matchUserId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> map = document.getData();
                                MatchedUsers user = new MatchedUsers(map);
                                for(String perUser :  user.Users)
                                {
                                    existList.add(perUser);
                                }

                            }

                            existList.add(mAuth.getUid());
                            docData.put("users" , existList);

                            db.collection("userMatchesList").document(matchUserId).set(docData);

                        }
                    }
                });

    }

    private void SaveFriendImage(final ArrayList<String> matchUsers) {

       Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                progressDialog.dismiss();

                FriendListOperation.Instance().SaveMyFriendProfileImage(friendsImage);

                Toast.makeText(FoundMatch.this , "You have added new match" , Toast.LENGTH_LONG).show();

                finish();
            }
        },4000);

        for(final String perUser : matchUsers)
        {
            if(perUser != mAuth.getUid())
            {
                storageReference.child(perUser).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        friendsImage.put(perUser , uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        friendsImage.put(perUser , Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"));
                    }
                });
            }
        }

       }

    public void rejectMatch(View view) {

        finish();
    }
}