package com.example.printme.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.printme.R;
import com.example.printme.ui.helper.SQLiteHandler;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SQLiteHandler db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // homeViewModel =
                ViewModelProviders.of(this).get(com.example.printme.ui.Credit.HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_credit, container, false);
       // final TextView textView = root.findViewById(R.id.text_credit);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
      final  RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList imageUrlList = prepareData();
        HomeFragment.ImageGalleryAdapter adapter = new HomeFragment.ImageGalleryAdapter(getContext(),  imageUrlList);
        recyclerView.setAdapter(adapter);
         super.onCreate(savedInstanceState);

        return root;
    }


    private ArrayList prepareData() {

        ArrayList<String> pop = new ArrayList<String>();
        // SQLite database handler
        db = new SQLiteHandler(getContext());
        pop = db.getImage();

        ArrayList imageUrlList = new ArrayList<>();
        for (int i = 0; i < pop.size(); i++) {
            StorageListPicture imageUrl = new StorageListPicture();
            imageUrl.setUrl(pop.get(i));
            imageUrlList.add(imageUrl);
        }
        Log.d("MainActivity", "List count: " + imageUrlList.size());
        return imageUrlList;
    }


    private class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {



        @Override
        public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View photoView = inflater.inflate(R.layout.fragment_credit, parent, false);
            ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {

          //  Picture spacePhoto = mSpacePhotos[position];
            ImageView imageView = holder.mPhotoImageView;




            Glide.with(mContext)
                    .load(mSpacePhotos.get(position).getUrl())
                    .placeholder(R.drawable.ic_cloud_off_red)
                    .into(imageView);

        }

        @Override
        public int getItemCount() {
            return (mSpacePhotos.size());
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mPhotoImageView;

            public MyViewHolder(View itemView) {

                super(itemView);
                mPhotoImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    StorageListPicture spacePhoto = mSpacePhotos.get(position);
                    Intent intent = new Intent(mContext, PictureActivity.class);
                    intent.putExtra(PictureActivity.EXTRA_SPACE_PHOTO, spacePhoto.getUrl());
                    startActivity(intent);
                }
            }
        }

        private ArrayList<StorageListPicture> mSpacePhotos;
        private Context mContext;

        public ImageGalleryAdapter(Context context, ArrayList<StorageListPicture> spacePhotos) {
            mContext = context;
            mSpacePhotos = spacePhotos;
        }
    }



}