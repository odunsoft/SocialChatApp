package com.odunyazilim.socialcchatt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {


   private ImageView imageViewer;
   private String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageViewer=findViewById(R.id.image_viewer);
        imageUrl = getIntent().getStringExtra("url");

        Picasso.get().load(imageUrl).into(imageViewer);


    }
}
