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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.bumptech.glide.Glide;
import com.example.printme.R;
import com.example.printme.ui.helper.SQLiteHandler;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SQLiteHandler db;
    SwipeRefreshLayout swipeRefreshLayout;
    HomeFragment.ImageGalleryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // homeViewModel =
                ViewModelProviders.of(this).get(com.example.printme.ui.Credit.HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_credit, container, false);
       // final TextView textView = root.findViewById(R.id.text_credit);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        //ArrayList imageUrlList = prepareData();
        adapter = new HomeFragment.ImageGalleryAdapter(getContext(),  Picture.getSpacePhotos(getContext()));
        recyclerView.setAdapter(adapter);

        super.onCreate(savedInstanceState);

        // Getting reference of swipeRefreshLayout and recyclerView
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swiperefreshlayout);
        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeFragment.ImageGalleryAdapter adapter = new HomeFragment.ImageGalleryAdapter(getContext(),  Picture.getSpacePhotos(getContext()));
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        return root;
    }




    private class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {

        @Override
        public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View photoView = inflater.inflate(R.layout.image, parent, false);
            ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {

            Picture spacePhoto = mSpacePhotos[position];
            ImageView imageView = holder.mPhotoImageView;

            Glide.with(mContext)
                    .load(spacePhoto.getUrl())
                    .placeholder(R.drawable.ic_cloud_off_red)
                    .into(imageView);

        }

        @Override
        public int getItemCount() {
            return (mSpacePhotos.length);
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mPhotoImageView;

            public MyViewHolder(View itemView) {

                super(itemView);mPhotoImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Picture spacePhoto = mSpacePhotos[position];
                    Intent intent = new Intent(mContext, PictureActivity.class);
                    intent.putExtra(PictureActivity.EXTRA_SPACE_PHOTO, spacePhoto);
                    startActivity(intent);
                    //adapter.notifyItemRemoved(position);
                }
            }
        }

        private Picture[] mSpacePhotos;
        private Context mContext;

        public ImageGalleryAdapter(Context context, Picture[] spacePhotos) {
            mContext = context;
            mSpacePhotos = spacePhotos;
        }
    }



}