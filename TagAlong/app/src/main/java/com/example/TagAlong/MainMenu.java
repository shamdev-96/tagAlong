package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MenuItem;

import com.example.TagAlong.Fragments.ExploreFragment;
import com.example.TagAlong.Fragments.ProfileFragment;
import com.example.TagAlong.OperationHelper.FriendListOperation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainMenu extends AppCompatActivity {

    private ActionBar toolbar;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Loading your view");
        progressDialog.show();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        toolbar = getSupportActionBar();
        toolbar.setTitle(Html.fromHtml("<font color='#000000'>Explore </font>"));
        toolbar.setIcon(R.drawable.explore_icon);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                loadFragment(new ExploreFragment());
                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            }
        },3000);

        SaveFriendImage();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;


            switch (item.getItemId()) {
                case R.id.explore:
                    fragment = new ExploreFragment();
                    loadFragment(fragment);
                    toolbar.setTitle(Html.fromHtml("<font color='#000000'>Explore </font>"));
                    return true;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    toolbar.setTitle(Html.fromHtml("<font color='#000000'>My Profile </font>"));
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load com.example.syafi.smartetn.app.fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void SaveFriendImage() {

        final ArrayList<String> matchUsers = FriendListOperation.Instance().GetMyFriendlist();

       final Map<String, Uri> friendsImage =  new HashMap<>() ;

        if(matchUsers.size() == 0)
            return;

        Handler imageHandler=new Handler();

        imageHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FriendListOperation.Instance().SaveMyFriendProfileImage(friendsImage);

            }
        },2000);

        for(final String perUser : matchUsers)
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