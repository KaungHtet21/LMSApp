package com.khk.lmsapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khk.lmsapp.R;
import com.khk.lmsapp.listener.ImageSelectListener;

import java.io.File;
import java.util.ArrayList;

public class LoadingImagesAdapter extends RecyclerView.Adapter<LoadingImagesAdapter.ViewHolder> {

    Context context;
    ArrayList<String> imageData;
    ImageSelectListener imageSelectListener;
    ArrayList<String> selectedImageList = new ArrayList<>();
    ArrayList<Uri> selectedImageUriList = new ArrayList<>();

    public LoadingImagesAdapter(ArrayList<String> imageData, Activity activity, ImageSelectListener listener) {
        this.imageData = imageData;
        this.context = activity;
        this.imageSelectListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_images_list, parent, false);
        return new ViewHolder(view);
    }

    private static final String TAG = "LoadingImagesAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String data = imageData.get(position);
        if (data != null){
            Glide.with(context)
                    .load(data)
                    .into(holder.image);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.selected_mark.getVisibility() == View.GONE){
                    holder.selected_mark.setVisibility(View.VISIBLE);
                    String selectedImage = imageData.get(holder.getAdapterPosition());
                    selectedImageList.add(selectedImage);

                    Uri uri = Uri.fromFile(new File(data));
                    selectedImageUriList.add(uri);
                }else {
                    holder.selected_mark.setVisibility(View.GONE);
                    String selectedImage = imageData.get(holder.getAdapterPosition());
                    selectedImageList.remove(selectedImage);

                    Uri uri = Uri.fromFile(new File(data));
                    selectedImageUriList.remove(uri);
                }
                imageSelectListener.onImageSelect(selectedImageList, selectedImageUriList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        FrameLayout selected_mark;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            selected_mark = itemView.findViewById(R.id.selected_mark);
            image = itemView.findViewById(R.id.single_image);
        }
    }
}
