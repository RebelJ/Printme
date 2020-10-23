package com.example.printme.ui.gallery;

import android.app.Dialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;

import com.example.printme.MainActivity;
import com.example.printme.R;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class ListPicture extends Service {

    private static Thread monThread=null;
    JSONParser jsonParser=new JSONParser();
    String ba1;
    public static String URL = "http://........../image.php";



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();

        Intent monIntent = new Intent(ListPicture.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, monIntent, PendingIntent.FLAG_ONE_SHOT);

        //Button
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Go", pendingIntent).build();
        //Notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Back to Application ?")
                .setContentTitle("Amazing news")
                .addAction(action) //add buton
                .build();

        //Send notification
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }

        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        if (monThread!=null)
        {
            monThread.isInterrupted();
        }
        super.onDestroy();
    }



    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Toast.makeText(this, "handleSindImage", Toast.LENGTH_LONG).show();


            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgPath = cursor.getString(columnIndex);
            cursor.close();
           // Bitmap photo = BitmapFactory.decodeFile((imgPath));
          //  imageprev.setImageBitmap(photo);

            Log.e("path", "----------------" + imgPath);

            // Image
            Bitmap bm = BitmapFactory.decodeFile(imgPath);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte[] ba = bao.toByteArray();
            ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);

            Log.e("base64", "-----" + ba1);

            // Upload image to server
            new uploadToServer().execute();

        }

    }
/*

    //this Method call Intent Services
    public static void uploadFile(Context context) {


        Settings.openDataBase(context);
        final String serverUrlString = "http://test/webservice.php";
        final String fileToUploadPath = Settings.getProfilePicPath();
        File fileObj = new File(fileToUploadPath);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(fileObj.getAbsolutePath(), bmOptions);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

        Uri tempUri = getImageUri(context, decoded);
        File finalFile = new File(getRealPathFromURI(context,tempUri));

        final String paramNameString = "image";
        final UploadRequest request = new UploadRequest(context, UUID.randomUUID().toString(), serverUrlString);
        request.addParameter("type", "account_edit");
        request.addParameter("username", Settings.getUserName());
        request.addParameter("email", Settings.getUserEmail());
        request.addParameter("user_id", Settings.getUserId());

        request.addFileToUpload(finalFile.getAbsolutePath(), paramNameString, finalFile.getName(),
                ContentType.APPLICATION_OCTET_STREAM);
        request.setNotificationConfig(R.drawable.app_icon, context.getString(R.string.app_name),
                context.getString(R.string.uploading), context.getString(R.string.upload_success),
                context.getString(R.string.upload_error), false);
        try {
            UploadService.startUpload(request);
        } catch (Exception exc) {
            Toast.makeText(context, "Malformed upload request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }


*/
    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared









        /*

            Toast.makeText(this, "handleSendMultipleImage", Toast.LENGTH_LONG).show();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUris, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgPath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap photo = BitmapFactory.decodeFile((imgPath));
            imageprev.setImageBitmap(photo);

            Log.e("path", "----------------" + picturePath);

            // Image
            Bitmap bm = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);

            Log.e("base64", "-----" + ba1);

            // Upload image to server
            new uploadToServer().execute();

*/
        }




    }



    class uploadToServer extends AsyncTask<Void, Void, String> {

        private Dialog pd = new Dialog(ListPicture.this);
        protected void onPreExecute() {
            super.onPreExecute();
            //pd.setMessage("image uploading!");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("base64", ba1);
            param.put("ImageName", System.currentTimeMillis() + ".jpg");


            JSONObject json=jsonParser.makeHttpRequest(URL,"POST",param);
            return "Success";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
        }
    }

}