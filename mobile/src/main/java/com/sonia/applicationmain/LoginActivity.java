package com.sonia.applicationmain;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView registerLink;
    private Button loginBtn;
    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private LocationManager locationManager;
    private LocationListener listener;
    protected LatLng currentLocation;



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


        setTitle("Login");

        auth = FirebaseAuth.getInstance();

        /*if( auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this,MapsActivity.class));
        }*/


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLocation = new LatLng(location.getLongitude(), location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }

        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.

        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        loginUser(email.getText().toString(),password.getText().toString());



    }


    @Override
    public void onClick(View view) {

        if( view.getId() == R.id.sign_up_link){
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            LoginActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }else
        if(view.getId() == R.id.login_button){

            configure_button();
        }
    }

    private void loginUser(String email,final String password) {

        auth.signInWithEmailAndPassword("patriciab@gmail.com","blablabla")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.w("INFO", "Status of login " + task.isSuccessful());
                        if(!task.isSuccessful())
                        {
                            if(password.length() < 6)
                            {
                                Snackbar snackBar = Snackbar.make(findViewById(R.id.activity_login),"Password length must be over 6",Snackbar.LENGTH_SHORT);
                                snackBar.show();
                            }
                        }
                        else{

                            Intent i = new Intent(LoginActivity.this,MapsActivity.class);
                            i.putExtra("location",currentLocation);
                            startActivity(i);

                        }
                    }
                });
    }
}
