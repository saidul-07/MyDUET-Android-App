package com.example.myduet;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myduet.network.SupabaseConfig;
import com.google.android.material.appbar.MaterialToolbar;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#444A72"));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }
        setContentView(R.layout.activity_gallery);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        LocaleHelper.styleAppBar(this, toolbar, "#76C457", "#4A8C34");

        // Setup Video Card click listener to open inside in-app WebView
        findViewById(R.id.card_video).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, WebViewActivity.class);
            intent.putExtra("url", "https://www.youtube.com/watch?v=4C2FFKkU5IQ&t=274s");
            intent.putExtra("title", "Campus Video Tour");
            startActivity(intent);
        });

        // Supabase Storage Public Base URL
        String supabaseStorageBase = SupabaseConfig.SUPABASE_URL + "/storage/v1/object/public/myduet/";

        String imgUrl1 = supabaseStorageBase + "gallery_campus_1.jpg";
        String imgUrl2 = supabaseStorageBase + "gallery_campus_2.jpg";
        String imgUrl3 = supabaseStorageBase + "gallery_campus_3.jpg";
        String imgUrl4 = supabaseStorageBase + "gallery_campus_4.jpg";

        ImageView ivVideoThumb = findViewById(R.id.iv_video_thumb);
        ImageView img1 = findViewById(R.id.img_campus_1);
        ImageView img2 = findViewById(R.id.img_campus_2);
        ImageView img3 = findViewById(R.id.img_campus_3);
        ImageView img4 = findViewById(R.id.img_campus_4);

        loadImage(ivVideoThumb, imgUrl1, R.drawable.gallery_campus_1);
        loadImage(img1, imgUrl1, R.drawable.gallery_campus_1);
        loadImage(img2, imgUrl2, R.drawable.gallery_campus_2);
        loadImage(img3, imgUrl3, R.drawable.gallery_campus_3);
        loadImage(img4, imgUrl4, R.drawable.gallery_campus_4);

        // Bind interactive full-screen previews to all images
        setupImageZoom(img1, imgUrl1, R.drawable.gallery_campus_1);
        setupImageZoom(img2, imgUrl2, R.drawable.gallery_campus_2);
        setupImageZoom(img3, imgUrl3, R.drawable.gallery_campus_3);
        setupImageZoom(img4, imgUrl4, R.drawable.gallery_campus_4);
    }

    private void loadImage(ImageView imageView, String url, int fallbackRes) {
        if (imageView == null) return;
        Glide.with(this)
                .load(url)
                .placeholder(fallbackRes)
                .error(fallbackRes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView);
    }

    private void setupImageZoom(ImageView imageView, String url, int fallbackRes) {
        if (imageView == null) return;
        imageView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            ImageView fullImageView = new ImageView(this);
            fullImageView.setAdjustViewBounds(true);
            fullImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fullImageView.setBackgroundColor(android.graphics.Color.BLACK);

            Glide.with(this)
                    .load(url)
                    .placeholder(fallbackRes)
                    .error(fallbackRes)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(fullImageView);

            AlertDialog dialog = builder.setView(fullImageView).create();
            fullImageView.setOnClickListener(v2 -> dialog.dismiss());
            dialog.show();
        });
    }
}
