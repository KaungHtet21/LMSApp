package com.khk.lmsapp.listener;

import android.net.Uri;

import java.util.ArrayList;

public interface ImageSelectListener {
    void onImageSelect(ArrayList<String> selectedImagesList, ArrayList<Uri> selectedImageUriList);
}
