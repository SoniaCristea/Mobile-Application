package com.sonia.applicationmain;
/**
 * Created by Sonia on 10/2/2018.
 */


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth auth;
    private EditText input_email;
    private EditText input_pass;
    private Button registerBtn;
    private Snackbar snackbar;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        setTitle("Register");



        registerBtn = findViewById(R.id.register_button);
        input_email = findViewById(R.id.email);
        input_pass = findViewById(R.id.password);
        backButton = findViewById(R.id.back_button);

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(this);
        backButton.setOnClickListener(this);



        auth = FirebaseAuth.getInstance();
    }


    @Override
    public void onClick(View view) {

        if( view.getId() == R.id.back_button){
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            RegisterActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }else{
            if(view.getId() == R.id.register_button){
                signUpUser(input_email.getText().toString(),input_pass.getText().toString());
            }
        }
    }

    private void signUpUser(String email, String password) {

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            snackbar = Snackbar.make(findViewById(R.id.activity_register),"Error: "+task.getException(),Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        else{
                            snackbar = Snackbar.make(findViewById(R.id.activity_register),"Register success! ",Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
    }
}