package com.example.TagAlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;

import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.Editable;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Login_Signup_Page extends AppCompatActivity {


    public static final String TAG = "PhoneAuthActivity";
    private String mVerificationId;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__signup__page);
        progressDialog = new ProgressDialog(this);
        final Button verifyBtn = findViewById(R.id._verifyBtn);
        final EditText phoneNumber = findViewById(R.id._phoneNumber);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (ActivityCompat.checkSelfPermission(Login_Signup_Page.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Login_Signup_Page.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            List<String> missingPermissions = new ArrayList<>();

            missingPermissions.add( Manifest.permission.RECEIVE_SMS);
            missingPermissions.add( Manifest.permission.SEND_SMS);

            ActivityCompat.requestPermissions(this, missingPermissions.toArray(new String[0]), 1);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }



            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    progressDialog.dismiss();
                    Toast.makeText(Login_Signup_Page.this , "Invalid request. Check your phone number again.." , Toast.LENGTH_SHORT).show();
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    progressDialog.dismiss();
                    Toast.makeText(Login_Signup_Page.this , "Too much request attempt. Try again next time.." , Toast.LENGTH_SHORT).show();
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
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                navigateToOtpPage();
            }

        };

        verifyBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             Editable phoneNo = phoneNumber.getText();
                                             VerifyPhoneNumber(phoneNo);
                                         }
                                     }

        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        Intent intent = new Intent(this, OtpActivity.class);
//        intent.putExtra(TAG , credential);
//        startActivity(intent);
    }

    private void navigateToOtpPage() {

            progressDialog.dismiss();
            Intent intent = new Intent(this, OtpActivity.class);
            intent.putExtra(TAG , mVerificationId);
            startActivity(intent);

    }

    private void VerifyPhoneNumber(Editable phoneNo)
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

    public void registerUser(View view) {
        Intent intent = new Intent(Login_Signup_Page.this, SignupProfile.class);
        startActivity(intent);
    }
}