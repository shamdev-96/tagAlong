package com.example.TagAlong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.TagAlong.Fragments.ProfileFragment;
import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.OperationHelper.ProfileOperation;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity {


    private EditText editName;
    private EditText editAge;
    private EditText editAbout;
    private FirebaseAuth  mAuth;
    private String LocationName;
    private ProgressDialog progressDialog;
    private  String API_KEY = "AIzaSyDBUeta8oBbZkKeZSoftnfnnJGpE370-1c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
     editName=findViewById(R.id.editName);
     editAge=findViewById(R.id.editAge);
     editAbout=findViewById(R.id.editAbout);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }

    progressDialog = new ProgressDialog(this);
    mAuth = FirebaseAuth.getInstance();

    AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.userLocation);

        autocompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID,
                com.google.android.libraries.places.api.model.Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.libraries.places.api.model.Place place) {
                // TODO: Get info about the selected place.
               LocationName = place.getName();
            }

            @Override
            public void onError( Status status) {

            }
        });

        LoadProfileData(autocompleteFragment);

        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editAbout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int)ev.getRawX();
                int rawY = (int)ev.getRawY();
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void LoadProfileData(AutocompleteSupportFragment autocompleteFragment) {

        Profile user = ProfileOperation.getInstance().GetCurrentProfileData();
        editName.setText(user.Name);
        editAge.setText(String.valueOf(user.Age));
        editAbout.setText(user.About);
        autocompleteFragment.setHint(user.LocationName);
        LocationName = user.LocationName;
    }


    public void saveEditedProfile(View view) {

        progressDialog.setMessage("Updating your profile");
        progressDialog.show();
        Profile user = ProfileOperation.getInstance().GetCurrentProfileData();
        String editedName = editName.getText().toString();
        String editedAge = editAge.getText().toString();
        String editedAbout = editAbout.getText().toString();
        String editedLocation = LocationName;

        if((editedName == null || editedName == "") || (editedAge == null || editedAge == "") ||
                (editedAbout == null || editedAbout == "")  ) {
            Toast.makeText(EditProfile.this, "Must fill all fields! ", Toast.LENGTH_LONG).show();
            return;
        }

        user.Name = editedName;
        user.Age = editedAge;
        user.About = editedAbout;
        user.LocationName = editedLocation;

        ProfileOperation.getInstance().SaveAllProfileData(user);

        Map editedData = new HashMap<>();

        editedData.put("name" , user.Name);
        editedData.put("about" , user.About);
        editedData.put("age" , user.Age);
        editedData.put("gender" , user.Gender);
        editedData.put("locationName" , user.LocationName);
        editedData.put("phoneNumber" , user.PhoneNumber);
        editedData.put("email" , user.Email);
        editedData.put("imageUri" , user.ImageUri);

        FirebaseFirestore db = DbOperation.getInstance().db;
        db.collection("users").document(mAuth.getUid()).set(editedData);
        progressDialog.dismiss();
        finish();
//        onDataReceived(user);


    }

    public void cancelEdit(View view) {
        finish();
    }

}