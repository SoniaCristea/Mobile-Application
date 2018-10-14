package com.sonia.applicationmain;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity{


    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        registerLink = findViewById(R.id.sign_up_link);



        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

                LoginActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            }
        });

    }

}
