package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.TagAlong.Models.MatchedUsers;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.OperationHelper.DistanceOperation;
import com.example.TagAlong.OperationHelper.FriendListOperation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ArrayList<String> matchUsers ;
    private Map<String, Uri> friendsImage;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        matchUsers = new ArrayList<>();
        friendsImage = new HashMap<>();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (ActivityCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        // Logic to handle location object
                        if (location != null) {
                            DistanceOperation.getInstance().SaveMyCurrentLocation(location);
                        }
                        else
                        {
                            return;
                        }

                    }
                });

        if(mAuth.getCurrentUser() != null)
        {

            Handler databaseHandler =new Handler();

            databaseHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainMenu.class);
                    startActivity(intent);
                    finish();

                }
            },5000);

            GetMyFriendInStartup(mAuth.getUid());

        }
        else
        {
            handler=new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(SplashScreen.this, Login_Signup_Page.class);
                    startActivity(intent);
                    finish();
                }
            },3000);

        }

    }

    private void GetMyFriendInStartup(String userId) {

        Handler firebaseHandler = new Handler();

        firebaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FriendListOperation.Instance().SaveMyFriendList(matchUsers);
            }
        },4000);

        DocumentReference docRef =  DbOperation.getInstance().db.collection("userMatchesList").document(userId);
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

                }
            }
        });

    }

}