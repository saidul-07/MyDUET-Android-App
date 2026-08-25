package com.example.myduet;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

        // Setup Video Card click listener to open inside in-app WebView
        findViewById(R.id.card_video).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, WebViewActivity.class);
            intent.putExtra("url", "https://www.youtube.com/watch?v=4C2FFKkU5IQ&t=274s");
            intent.putExtra("title", "Campus Video Tour");
            startActivity(intent);
        });

        // Bind interactive full-screen previews to all images
        setupImageZoom(findViewById(R.id.img_campus_1), R.drawable.gallery_campus_1);
        setupImageZoom(findViewById(R.id.img_campus_2), R.drawable.gallery_campus_2);
        setupImageZoom(findViewById(R.id.img_campus_3), R.drawable.gallery_campus_3);
        setupImageZoom(findViewById(R.id.img_campus_4), R.drawable.gallery_campus_4);
    }

    private void setupImageZoom(ImageView imageView, int drawableRes) {
        imageView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            ImageView fullImageView = new ImageView(this);
            fullImageView.setImageResource(drawableRes);
            fullImageView.setAdjustViewBounds(true);
            fullImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fullImageView.setBackgroundColor(android.graphics.Color.BLACK);
            
            AlertDialog dialog = builder.setView(fullImageView).create();
            fullImageView.setOnClickListener(v2 -> dialog.dismiss());
            dialog.show();
        });
    }
}
