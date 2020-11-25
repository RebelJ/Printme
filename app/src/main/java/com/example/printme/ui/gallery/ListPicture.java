package com.example.printme.ui.gallery;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.printme.BuildConfig;
import com.example.printme.MainActivity;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.printme.ui.app.AppConfig.URL_UPLOAD;


 public class ListPicture extends Service {

    private static Thread monThread = null;
    public String imgPath;
    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();
    private static final int STORAGE_PERMISSION_CODE = 4655;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent monIntent = new Intent(ListPicture.this, MainActivity.class);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.NAMESPACE = "com.example.yourapp";



        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
             //   Toast.makeText(this, "action send", Toast.LENGTH_LONG).show();
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

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
   /*     if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }*/
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
            imgPath = cursor.getString(columnIndex);
            cursor.close();
            Log.e("path", "----------------" + imgPath);
        }
        uploadMultipart();
         //   new Upload().execute();
        }




    public void uploadMultipart() {

        try {

            final String name = "test1";

            Toast.makeText(this, "Uploading file. Please wait...", Toast.LENGTH_SHORT).show();
            final String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request

            String boundary = "---------------------------14737809831466499882746641449";
            new MultipartUploadRequest(this, uploadId, URL_UPLOAD)
                    .addHeader("Content-Type", "multipart/form-data; ")
                    .addFileToUpload(imgPath, "image") //Adding file
                    .addParameter("name", name) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .setDelegate(uploadReceiver)
                    .startUpload();

        } catch (Exception exc) {
            // Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Error While uploading...", Toast.LENGTH_SHORT).show();
        }
    }



    class Upload extends AsyncTask<Void, Void, String> {


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            final String name = "test1";

            //Calling the upload Image method
            //Uploading code
            try{

                String uploadId = UUID.randomUUID().toString();
                //Creating a multi part request
                MultipartUploadRequest req = new MultipartUploadRequest((Context) getBaseContext(), uploadId, URL_UPLOAD)
                        .addFileToUpload(imgPath, "image") //Adding file
                        .addParameter("name", name) //Adding text parameter to the request
                        // .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2);
                // req.startUpload();

                String uploadCurrentFile = req.startUpload();
                if (uploadCurrentFile != null && sendToServer()) {
                    req.setAutoDeleteFilesAfterSuccessfulUpload(true);
                }
                else req.setAutoDeleteFilesAfterSuccessfulUpload(false);

            } catch (Exception exc) {
                Log.e("AndroidUploadService", exc.getMessage(), exc);
            }

            Log.e("path", "----------------" + imgPath);
            return "Success";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

/*
    @Override
    public void onProgress(int progress) {
        //your implementation
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        //your implementation
    }

    @Override
    public void onError(Exception exception) {
        //your implementation
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        //your implementation
    }

    @Override
    public void onCancelled() {
        //your implementation
    }

*/

    public String getTextFromInputStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();

        String currentLine;
        try {
            while ((currentLine = reader.readLine()) != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString().trim();
    }

    public boolean sendToServer() throws IOException {
        final String scripturlstring = URL_UPLOAD;
        java.net.URL scripturl = new URL(scripturlstring);
        HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

        InputStream answerInputStream = connection.getInputStream();
        final String answer = getTextFromInputStream(answerInputStream);

        if (answer.equals("{\"error\":true,\"error_msg\":\"Required parameters file and name is missing!\"}")) return true;
        else return false;
    }




}