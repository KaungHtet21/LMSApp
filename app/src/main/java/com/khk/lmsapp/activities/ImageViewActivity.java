package com.khk.lmsapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.khk.lmsapp.R;

public class ImageViewActivity extends AppCompatActivity {

    ImageView back, image;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        image = findViewById(R.id.image_viewer);
        url = getIntent().getStringExtra("url");
        Glide.with(this)
                .load(url)
                .into(image);
    }
}