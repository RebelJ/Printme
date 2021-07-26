package com.example.printme.ui.gallery;


import android.app.Service;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.printme.BuildConfig;
import com.example.printme.ui.helper.SQLiteHandler;
import com.example.printme.ui.helper.SessionManager;
import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static com.example.printme.ui.app.AppConfig.URL_UPLOAD;
import static com.example.printme.ui.app.AppConfig.URL_UPLOAD_PELICULLE;


public class ListPicture extends Service {
     private SessionManager session;
     private SQLiteHandler db;
     private static Thread monThread = null;

     String uploadFilePath ;
     int serverResponseCode = 0;

     public class LocalBinder extends Binder
     {
         public ListPicture getService()
         {
             return ListPicture.this;
         }
     }

     // Create the instance on the service.
     private final LocalBinder binder = new LocalBinder();

     // Return this instance from onBind method.
     // You may also return new LocalBinder() which is
     // basically the same thing.
     @Nullable
     @Override
     public IBinder onBind(Intent intent)
     {
         return binder;
     }


    @Override
    public void onCreate() {
        super.onCreate();

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.NAMESPACE = "com.example.Printme";
        // SQLite database handler
        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
        if (monThread!=null)
        {
            monThread.isInterrupted();
        }
        super.onDestroy();
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION); intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        if (imageUris != null) {
            if (intent.getClipData() != null) {
                ClipData mClipData = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mClipData = intent.getClipData();
                }
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);

                    Uri uri = item.getUri();

                    String packageName = "com.example.printme.ui.gallery";
                    this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    uploadFilePath = cursor.getString(columnIndex);
                    Log.e("path", "----------------" + uploadFilePath);
                    cursor.close();


                    new Thread(new Runnable() {
                        public void run() {

                            try {
                                uploadFile(uploadFilePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();
                }
            }

        }
    }

     void handleSendImage(Intent intent) {
         Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

         intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION); intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
         if (imageUri != null) {

             // Update UI to reflect image being shared
             String[] filePathColumn = {MediaStore.Images.Media.DATA};
             Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
             cursor.moveToFirst();
             int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
             uploadFilePath = cursor.getString(columnIndex);
             cursor.close();

             new Thread(new Runnable() {
                 public void run() {

                     try {
                         uploadFile(uploadFilePath );



                     }
                     catch(final IOException e) {
                         e.printStackTrace();

                         new Handler(Looper.getMainLooper()).post(new Runnable() {
                             @Override
                             public void run() {
                                 Toast.makeText(getApplicationContext(), "upload error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                             }
                         });
                     }
                 }
             }).start();


         }
     }


     public int uploadFile(String sourceFileUri)throws IOException {


         File sourceFile = new File(sourceFileUri);

         if (!sourceFile.isFile()) {

             Log.e("uploadFile", "Source File not exist :"
                     +uploadFilePath);
             return 0;
         }
         else
         {

             try {
                 String charset = "UTF-8";



                 String iud = session.getUid();

                 MultipartUtility multipart = new MultipartUtility(URL_UPLOAD, charset);
                 multipart.addFormField("idUser", iud);
                 multipart.addFilePart("filename", new File(uploadFilePath));
                 String response = multipart.finish(); // response from server.
                 Log.e("Upload file to server", "Done: " + response);


                 try {
                     JSONObject jObj = new JSONObject(response);
                     boolean error = jObj.getBoolean("error");

                     // Check for error node in json
                     if (!error) {
                         Integer idImage  = jObj.getInt("idImage");
                         // Now store the id image in SQLite
                         db.addIdImage(idImage, uploadFilePath );


                         ArrayList<Integer> peliculle = new ArrayList<>();
                         new Handler(Looper.getMainLooper()).post(new Runnable() {
                             @Override
                             public void run() {
                                 Toast.makeText(getApplicationContext(), "Image enregistrée" , Toast.LENGTH_LONG).show();
                             }
                         });

                          peliculle = db.getIdImage();


                         if(peliculle.size() == 24)
                         {
                             // function send peliculle
                            uploadPeliculle(peliculle);

                         }

                     }

                 } catch (final JSONException e) {
                     // JSON error
                     e.printStackTrace();
                     new Handler(Looper.getMainLooper()).post(new Runnable() {
                         @Override
                         public void run() {
                             Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                         }
                     });

                 }

                 } catch (final MalformedURLException ex) {

                     ex.printStackTrace();
                     Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                     new Handler(Looper.getMainLooper()).post(new Runnable() {
                         @Override
                         public void run() {
                             Toast.makeText(getApplicationContext(), "Server error" + ex.getMessage(), Toast.LENGTH_LONG).show();
                         }
                     });
             } catch (final Exception e) {

                 e.printStackTrace();

                 Log.e("Upload file to server Exception", "Exception : "
                         + e.getMessage(), e);
                 new Handler(Looper.getMainLooper()).post(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(getApplicationContext(), "Upload Server error: " +e.getMessage(), Toast.LENGTH_LONG).show();
                     }
                 });
             }
             return serverResponseCode;

         } // End else block
     }


     public int uploadPeliculle(ArrayList peliculle)throws IOException {


                String pel = peliculle.toString();
             try {
                 String charset = "UTF-8";

                 String iud = session.getUid();

                 MultipartUtility multipart = new MultipartUtility(URL_UPLOAD_PELICULLE, charset);
                 multipart.addFormField("idUser", iud);
                 multipart.addFormField("table", pel);
                // multipart.addFilePart("table", new File(uploadFilePath));
                 String response = multipart.finish(); // response from server.
                 Log.e("Upload file to server", "Done: " + response);


                 try {
                     JSONObject jObj = new JSONObject(response);
                     boolean error = jObj.getBoolean("error");

                     // Check for error node in json
                     if (!error) {
                         final Integer rep  = jObj.getInt("msg");
                         new Handler(Looper.getMainLooper()).post(new Runnable() {
                             @Override
                             public void run() {
                                 Toast.makeText(getApplicationContext(), "pellicule terminé" + rep, Toast.LENGTH_LONG).show();
                             }
                         });



                         // Now store the id image in SQLite
                     }

                 } catch (final JSONException e) {
                     // JSON error
                     e.printStackTrace();
                     new Handler(Looper.getMainLooper()).post(new Runnable() {
                         @Override
                         public void run() {
                             Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                         }
                     });

                 }





             } catch (final MalformedURLException ex) {

                 ex.printStackTrace();
                 Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                 new Handler(Looper.getMainLooper()).post(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(getApplicationContext(), "Upload error: " +ex.getMessage(), Toast.LENGTH_LONG).show();
                     }
                 });
             } catch (final Exception e) {

                 e.printStackTrace();

                 Log.e("Upload file to server Exception", "Exception : "
                         + e.getMessage(), e);
                 new Handler(Looper.getMainLooper()).post(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                     }
                 });
             }
             return serverResponseCode;
     }


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