package com.example.printme.ui.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    private SQLiteDatabase database;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "databasePOP.db";

    // Login table name
    private static final String TABLE_USER = "user";
    // Image table name
    private static final String TABLE_IMAGE = "image";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ID_TABLE = "idtable";
    private static final String KEY_NAME = "name";
    private static final String KEY_FIRSTNAME = "firstName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_IDIMAGE = "idImage";
    private static final String KEY_IMAGE = "url";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FIRSTNAME + " TEXT," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
                + KEY_ID_TABLE + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," + KEY_IDIMAGE + " INTEGER," + KEY_IMAGE + " TEXT "  + ")";

        db.execSQL(CREATE_IMAGE_TABLE);
        db.execSQL(CREATE_LOGIN_TABLE);


        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        if(newVersion>oldVersion)

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String firstName, String name, String email, String uid, String created_at, String numAddress, String voieAddress,
                        String codePostal, String country, String region, String batiment, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, firstName); // firstName
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At



        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }


    /**
     * Storing user details in database
     * */
    public void addIdImage(Integer idImage, String url) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IDIMAGE, idImage); // idImage
        values.put(KEY_IMAGE, url); // Name



        // Inserting Row
        long id = db.insert(TABLE_IMAGE, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New image inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String>  getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("firstName", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("uid", cursor.getString(4));
            user.put("created_at", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting all id image from database
     * */
    public ArrayList<String> getImage() {
        ArrayList<String> image = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                image.add(cursor.getString(2));

                // Adding contact to list
                // reportList.add(reportTable);
            } while (cursor.moveToNext());


        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching image from Sqlite: " + image.toString());

        return image;
    }

    /**
     * Getting all id image from database
     * */
    public ArrayList<Integer> getIdImage() {
        ArrayList<Integer> image = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                image.add(cursor.getInt(1));
                // Adding contact to list
               // reportList.add(reportTable);
            } while (cursor.moveToNext());


        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching image from Sqlite: " + image.toString());

        return image;
    }




    /**
     * Getting user data from database
     * */
    public HashMap<String, String>  getNamenFirst() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("firstName", cursor.getString(1));
            user.put("name", cursor.getString(2));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user email from database
     * */
    public String  getUserEmail() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {

            user.put("email", cursor.getString(3));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user.toString();
    }

    /**
     * Getting user iud from database
     * */
    public String  getUserUid() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("uid", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user.toString();
    }




    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}