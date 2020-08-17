package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.example.TagAlong.ListAdapter.FriendAdapter;
import com.example.TagAlong.ListAdapter.PlaceAdapter;
import com.example.TagAlong.Models.MatchedUsers;
import com.example.TagAlong.Models.Place;
import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.OperationHelper.FriendListOperation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class MyFriendListPage extends AppCompatActivity {

    private ListView friendList;
    private ArrayList<String> friends;
    private ArrayList<Profile> friendProfile;
    private FriendAdapter friendAdapter;
    private FirebaseAuth mAuth;
    private ActionBar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend_list_page);

        toolbar = getSupportActionBar();

        toolbar.setTitle("Friend List");

        mAuth = FirebaseAuth.getInstance();

        friendList = findViewById(R.id.myFriendListView);

        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Getting your friend list.");
        progressDialog.show();

        friends = FriendListOperation.Instance().GetMyFriendlist();

        friendProfile = new ArrayList<>();

        FirebaseFirestore db = DbOperation.getInstance().db;

        GetAllFriendsProfile(db);

    }

    private void GetAllFriendsProfile(FirebaseFirestore db) {

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                progressDialog.dismiss();
                friendAdapter = new FriendAdapter(MyFriendListPage.this , friendProfile);
                friendList.setAdapter(friendAdapter);

                friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FriendAdapter adapter = (FriendAdapter) parent.getAdapter();
                        Profile selectedFriend = adapter.friendList.get(position);

                        Intent intent = new Intent(MyFriendListPage.this , ProfilePage.class);
                        intent.putExtra("matchUser" , selectedFriend.UserId);
                        startActivity(intent);
//
                    }
                });
            }
        },3000);

            for(final String userId : friends)
            {
                DocumentReference docRef = db.collection("users").document(userId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> placeMap = document.getData();
                                Profile matchProfile = new Profile(placeMap);
                                matchProfile.UserId = userId;
                                friendProfile.add(matchProfile);
                            }
                        }
                    }
                });
            }
        }

}