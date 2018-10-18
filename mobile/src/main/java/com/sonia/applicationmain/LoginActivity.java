package com.sonia.applicationmain;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView registerLink;
    private Button loginBtn;
    private FirebaseAuth auth;
    private EditText email;
    private EditText password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        registerLink = findViewById(R.id.sign_up_link);
        loginBtn = findViewById(R.id.login_button);
        email = (EditText)findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);


        registerLink.setOnClickListener(this);
        loginBtn.setOnClickListener(this);


        setTitle("Login in");

        auth = FirebaseAuth.getInstance();

       /* if( auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }*/



    }


    @Override
    public void onClick(View view) {

        if( view.getId() == R.id.sign_up_link){
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            LoginActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }else
        if(view.getId() == R.id.login_button){
            loginUser(email.getText().toString(),password.getText().toString());

        }
    }

    private void loginUser(String email,final String password) {

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            if(password.length() < 6)
                            {
                                Snackbar snackBar = Snackbar.make(findViewById(R.id.activity_login),"Password length must be over 6",Snackbar.LENGTH_SHORT);
                                snackBar.show();
                            }
                        }
                        else{
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        }
                    }
                });
    }
}
