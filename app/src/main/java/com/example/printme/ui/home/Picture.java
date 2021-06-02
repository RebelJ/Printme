package com.example.printme.ui.home;

import android.content.Context;
import android.os.Parcelable;


import android.os.Parcel;
import android.util.Log;

import com.example.printme.ui.helper.SQLiteHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Picture implements Parcelable {

    private String mUrl;
    private String mTitle;

    private static SQLiteHandler db;

    public Picture(String url, String title) {
        mUrl = url;
        mTitle = title;
    }

    protected Picture(Parcel in) {
        mUrl = in.readString();
        mTitle = in.readString();
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

/*
    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public static  Picture[] getSpacePhotos(Context context) {
        ArrayList<String> pop = new ArrayList<String>();
        // SQLite database handler
        db = new SQLiteHandler(context);
        pop = db.getImage();
      //  Log.e("sqlite", "imagedata: " + pop.get(0));

        ArrayList <Picture> pict = new ArrayList<Picture>();

        for(int i =0; i<pop.size(); i++) {
            pict.add(new Picture(pop.get(i), "pop " + i));
        }



        return  new Picture {
                for(int i =0; i<pop.size(); i++) {
                     pict.get(i)
                }
        };

    }
*/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUrl);
        parcel.writeString(mTitle);
    }
}
