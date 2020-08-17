package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.OperationHelper.ProfileOperation;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignupProfile extends AppCompatActivity {

    private String mVerificationId;
    private  String myUsername;
    private String myPhoneNo;
    private String myEmail;
    private ProgressDialog progressDialog;
    public static final String TAG = "PhoneAuthActivity";
    private EditText phoneNumber;
    private EditText username;
    private  EditText email;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_profile);

        phoneNumber = findViewById((R.id.register_phoneNumber));
        username = findViewById(R.id.register_username);
        email = findViewById(R.id.register_email);

        progressDialog = new ProgressDialog(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    progressDialog.dismiss();
                    Toast.makeText(SignupProfile.this , "Invalid request. Check your phone number again.." , Toast.LENGTH_SHORT).show();
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    progressDialog.dismiss();
                    Toast.makeText(SignupProfile.this , "Too much request attempt. Try again next time.." , Toast.LENGTH_SHORT).show();
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                navigateToOtpPage();
            }

        };

    }

    public void backToLogin(View view) {
        Intent intent = new Intent(SignupProfile.this, Login_Signup_Page.class);
        startActivity(intent);
    }

    @SuppressLint("ShowToast")
    public void verifyRegistraion(View view) {

        myPhoneNo = String.valueOf(phoneNumber.getText());
        myUsername = String.valueOf(username.getText());
        myEmail = String.valueOf(email.getText());


        //myUsername = String.valueOf(uname);

        if(myPhoneNo == null)
        {
            Toast.makeText(SignupProfile.this , "Please insert phone number", Toast.LENGTH_SHORT);
            return;
        }
        if(myUsername == null)
        {
            Toast.makeText(SignupProfile.this , "Please insert  username", Toast.LENGTH_SHORT);
            return;
        }

        if(myEmail == null)
        {
            Toast.makeText(SignupProfile.this , "Please insert  email", Toast.LENGTH_SHORT);
            return;
        }

        VerifyPhoneNumber(myPhoneNo);

    }

    private void VerifyPhoneNumber(String phoneNo)
    {
        CharSequence text = "Verify your phone number (" + phoneNo + ")";
        progressDialog.setMessage(text);
        progressDialog.show();

        String phoneString = "+6" + phoneNo;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneString,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }

    private void navigateToOtpPage() {

        progressDialog.dismiss();
        Intent intent = new Intent(this, OtpActivity.class);

        Profile initialProfile = new Profile();

        initialProfile.Name = myUsername;
        initialProfile.PhoneNumber = myPhoneNo;
        initialProfile.Email = myEmail;

        ProfileOperation.getInstance().SaveInitialData(initialProfile);

        intent.putExtra("username" , myUsername);
        intent.putExtra(TAG , mVerificationId);
        startActivity(intent);
        finish();

    }
}