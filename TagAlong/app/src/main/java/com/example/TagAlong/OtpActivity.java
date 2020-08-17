package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.TagAlong.Models.MatchedUsers;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.OperationHelper.DistanceOperation;
import com.example.TagAlong.OperationHelper.FriendListOperation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private boolean isFromRegistration;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<String> matchUsers ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        matchUsers = new ArrayList<>();
        isFromRegistration = false;
        mAuth = FirebaseAuth.getInstance();
        final Button loginBtn = findViewById(R.id._loginBtn);
        final EditText otpNumber = findViewById(R.id._otpNumber);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        if(username != null)
            isFromRegistration = true;

        mVerificationId = intent.getStringExtra(Login_Signup_Page.TAG);

        loginBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Editable code = otpNumber.getText();
                                            ProceedToLogin(code);
                                        }
                                    }

        );
    }

    private void ProceedToLogin(Editable code) {

        if(isFromRegistration)
        {
            progressDialog.setMessage("Creating your account");
        }
        else
        {

            progressDialog.setMessage("Logging in..");
        }

        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, String.valueOf(code));
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            progressDialog.dismiss();

                            GetUserLocation();

                            if(isFromRegistration)
                            {
                                NavigateToRegistrationPage();
                            }
                            else
                            {
                                NavigateToMainMenu();
                            }

//                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("PhoneVerification", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                               progressDialog.dismiss();
                                Toast.makeText(OtpActivity.this, "Invalid code", Toast.LENGTH_LONG).show();

                            }
                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                progressDialog.dismiss();
                                Toast.makeText(OtpActivity.this, "You already registered!", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }

    private void GetUserLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        if (location != null) {
                            DistanceOperation.getInstance().SaveMyCurrentLocation(location);
                            // Logic to handle location object
                        }
                    }
                });
    }

    private void NavigateToMainMenu() {

        Handler databaseHandler =new Handler();

        databaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FriendListOperation.Instance().SaveMyFriendList(matchUsers);
                Toast.makeText(OtpActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(OtpActivity.this , MainMenu.class);;
                startActivity(intent);
                finish();

            }
        },3000);

        GetMyFriendInStartup(mAuth.getUid());

    }

    private void GetMyFriendInStartup(String userId) {

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

    private  void NavigateToRegistrationPage()
    {
        Intent intent = new Intent(this, UserRegistration.class);;
        startActivity(intent);
        finish();
    }
}