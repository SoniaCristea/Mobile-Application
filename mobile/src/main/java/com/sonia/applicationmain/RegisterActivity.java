package com.sonia.applicationmain;
/**
 * Created by Sonia on 10/2/2018.
 */


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "CustomAuthActivity";
    private FirebaseAuth auth;
    private String mCustomToken;
    private TokenBroadcastReceiver mTokenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mTokenReceiver = new TokenBroadcastReceiver() {
            @Override
            public void onNewToken(String token) {
                Log.d(TAG, "onNewToken:" + token);
                setCustomToken(token);
            }
        };
        auth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        System.out.println(currentUser);
        //updateUI(currentUser);
    }
    // [END on_start_check_user]

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTokenReceiver, TokenBroadcastReceiver.getFilter());
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mTokenReceiver);
    }

    private void startSignIn() {
        // Initiate sign in with custom token
        // [START sign_in_custom]

        System.out.println(mCustomToken);
        auth.signInWithCustomToken(mCustomToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = auth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
        // [END sign_in_custom]
    }



    private void setCustomToken(String token) {
        mCustomToken = token;

        String status;
        if (mCustomToken != null) {
            status = "Token:" + mCustomToken;
        } else {
            status = "Token: null";
        }

        // Enable/disable sign-in button and show the token
        findViewById(R.id.register).setEnabled((mCustomToken != null));
        //((TextView) findViewById(R.id.textTokenStatus)).setText(status);
    }


    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.register) {
            startSignIn();

        }

    }

}