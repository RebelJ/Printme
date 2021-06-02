package com.example.printme.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.printme.ui.helper.SQLiteHandler;

import java.util.ArrayList;

public class StorageListPicture {
    String imageUrl;

    public String getUrl() {
        return imageUrl;
    }

    public void setUrl(String url) {
        this.imageUrl = url;
    }


/*

    private ArrayList<Picture[]> pictureList = new ArrayList<>();

    public void addToList(Picture[] pictList) {
        pictureList.add(pictList);
    }

    public ArrayList<Picture[]> getPictureList() {
        return pictureList;

    }*/
}