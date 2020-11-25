package com.example.printme.ui.gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.printme.MainActivity;
import com.example.printme.R;
import com.example.printme.ui.helper.SessionManager;
import com.example.printme.ui.login.LoginActivity;

import static java.lang.Thread.sleep;

public class LaunchService extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_service);


        // Session manager
        session = new SessionManager(getApplicationContext());
        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        Intent intentpict = getIntent();

        intentpict.setClass(this,ListPicture.class);

        intentpict.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

       // Intent intentService=new Intent(this, ListPicture.class);
      //  Toast.makeText(this, "Activity Launch", Toast.LENGTH_LONG).show();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);


        ContextCompat.startForegroundService(this, intentpict);
        finish();
        stopService(intentpict);
    }

}
