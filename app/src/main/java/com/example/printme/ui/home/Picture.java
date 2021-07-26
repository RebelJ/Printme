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

        ArrayList<Picture[]> pict = new ArrayList<Picture[]>();
        Picture[] a  = new Picture[pop.size()];

        for (int i = 0; i < pop.size(); i++) {
            a[i] = new Picture(pop.get(i), "pop " + i);
        }


        //Picture[]  a = new Picture[pop.size()];

        /*    for (int i = 0; i < pop.size(); i++) {
                {
                    a =  pict.get(i);
                }
            }*/

            return a;

/*
        return new Picture[]{
                new Picture("http://i.imgur.com/zuG2bGQ.jpg", "Galaxy"),
                new Picture("http://i.imgur.com/ovr0NAF.jpg", "Space Shuttle"),
                new Picture("http://i.imgur.com/n6RfJX2.jpg", "Galaxy Orion"),
                new Picture("http://i.imgur.com/qpr5LR2.jpg", "Earth"),
                new Picture("http://i.imgur.com/pSHXfu5.jpg", "Astronaut"),
                new Picture("http://i.imgur.com/3wQcZeY.jpg", "Satellite"),
        };*/

    }

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
