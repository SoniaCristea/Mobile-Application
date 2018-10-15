package com.sonia.applicationmain;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity{

    private TextView registerLink;
    private Button loginBtn;
    


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        registerLink = findViewById(R.id.sign_up_link);
       loginBtn = findViewById(R.id.login_button);
       setTitle("Login in");





        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));


                LoginActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            }
        });



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));


                LoginActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });


    }




}
