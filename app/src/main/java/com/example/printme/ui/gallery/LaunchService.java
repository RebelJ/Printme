package com.example.printme.ui.gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.printme.R;

import static java.lang.Thread.sleep;

public class LaunchService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_service);


        Intent intentpict = getIntent();

        intentpict.setClass(this,ListPicture.class);

       // Intent intentService=new Intent(this, ListPicture.class);
        Toast.makeText(this, "Activity Launch", Toast.LENGTH_LONG).show();
        startService(intentpict);



        stopService(intentpict);
    }

}
