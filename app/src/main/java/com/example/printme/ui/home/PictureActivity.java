package com.example.printme.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.printme.R;
import com.example.printme.ui.helper.SQLiteHandler;

public class PictureActivity extends AppCompatActivity {


    public static final String EXTRA_SPACE_PHOTO = "SpacePhotoActivity.SPACE_PHOTO";
    private ImageView mImageView;
    private Button btnDelete;
    private Button btnClose;
    private static SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        mImageView = (ImageView) findViewById(R.id.image);
        btnDelete = (Button) findViewById(R.id.BTN_Delete);
        btnClose = (Button) findViewById(R.id.BTN_CloseView);
        final Picture spacePhoto = getIntent().getParcelableExtra(EXTRA_SPACE_PHOTO);

        // Delete button Click Event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                db = new SQLiteHandler(getApplicationContext());
                if(db.deleteImage(spacePhoto.getUrl()))
                {

                    Toast.makeText(getApplicationContext(),
                            "Photo supprimé!", Toast.LENGTH_LONG)
                            .show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "ERREUR", Toast.LENGTH_LONG)
                            .show();
                }



            }
        });



        // Close button Click Event
        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                finish();

            }
        });

        Glide.with(this)
                .load(spacePhoto.getUrl())
                .asBitmap()
                .error(R.drawable.ic_cloud_off_red)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);
    }
}
