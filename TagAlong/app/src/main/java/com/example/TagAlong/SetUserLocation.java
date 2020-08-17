package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.TagAlong.Fragments.ProfileFragment;
import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.OperationHelper.ProfileOperation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SetUserLocation extends AppCompatActivity {


    private String TAG = "UserLocation";
    private  String API_KEY = "AIzaSyDBUeta8oBbZkKeZSoftnfnnJGpE370-1c";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
//    private GeoPoint UserLocation;
    private String UserLocationName;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_location);


        db = DbOperation.getInstance().db;
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressDialog = new ProgressDialog(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.locationFragmnet);

        // Specify the types of place data to return.

        autocompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME));

        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME);


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.libraries.places.api.model.Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress() +  ", " + place.getPlusCode());
                UserLocationName = place.getName();
              ;
            }

            @Override
            public void onError( Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }


    public void finalizeRegister(View view) {

        //
        /*
            1. Create hash map from Profile object to Document Cloud Firestore
            2. Save data into Cloud Firestore
         */

        progressDialog.setMessage("Finalizing your account");
        progressDialog.show();

        Profile updatedProfile = ProfileOperation.getInstance().GetCurrentProfileData();

        updatedProfile.LocationName = UserLocationName;

        LoadProfileImageUrl(updatedProfile);

    }

    private void LoadProfileImageUrl(final Profile updatedProfile) {

        final String userId = mAuth.getUid();

            storageReference.child(userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    updatedProfile.ImageUri = uri.toString();;
                    SaveDataIntoLocal(updatedProfile , userId);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    updatedProfile.ImageUri = ("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png");
                    SaveDataIntoLocal(updatedProfile , userId);
                }
            });
    }

    private  void SaveDataIntoLocal(Profile updatedProfile , String userId)
    {
        ProfileOperation.getInstance().SaveAllProfileData(updatedProfile);
        Profile latestProfileData = ProfileOperation.getInstance().GetCurrentProfileData();

        Map<String, Object> docData = new HashMap<>();

        docData.put("name", latestProfileData.Name);
        docData.put("about", latestProfileData.About);
        docData.put("age", latestProfileData.Age);
        docData.put("gender", latestProfileData.Gender);
        // docData.put("location", new GeoPoint(latestProfileData.Location.getLatitude() , latestProfileData.Location.getLongitude()));
        docData.put("locationName", latestProfileData.LocationName);
        docData.put("phoneNumber", latestProfileData.PhoneNumber);
        docData.put("email", latestProfileData.Email);
        docData.put("imageUri" , latestProfileData.ImageUri);

        db.collection("users").document(userId).set(docData);

        progressDialog.dismiss();

        Toast.makeText(this,"Your account is successfully created" , Toast.LENGTH_LONG);

        Intent intent = new Intent(this , MainMenu.class);

        startActivity(intent);
    }
}