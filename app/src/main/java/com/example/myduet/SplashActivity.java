package com.example.myduet;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class SplashActivity extends AppCompatActivity {

    private int activeDotIndex = 0;
    private final Handler dotHandler = new Handler(Looper.getMainLooper());
    private Runnable dotRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find views
        MaterialCardView logoContainer = findViewById(R.id.logo_container);
        View dotsContainer = findViewById(R.id.loading_dots_container);
        TextView appNameText = findViewById(R.id.app_name_text);
        TextView versionText = findViewById(R.id.version_text);

        // Staggered entry animations
        
        // 1. Logo container animation: Fade in + Scale (starts immediately, duration 800ms)
        logoContainer.setAlpha(0f);
        logoContainer.setScaleX(0.6f);
        logoContainer.setScaleY(0.6f);
        logoContainer.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // 2. Loading Dots container: Fade in (starts at 200ms, duration 600ms)
        dotsContainer.setAlpha(0f);
        dotsContainer.animate()
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(200)
                .start();

        // 3. App Name Text (MyDUET): Fade Up (starts at 400ms, duration 700ms)
        appNameText.setAlpha(0f);
        appNameText.setTranslationY(40f);
        appNameText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(400)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // 4. Version Text (Version 1.0.0): Fade Up (starts at 550ms, duration 700ms)
        versionText.setAlpha(0f);
        versionText.setTranslationY(40f);
        versionText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(550)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Set up continuous loading indicator animation
        setupDotAnimation();

        // Automatic navigation to MainActivity after 2.2 seconds (2200ms)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 2200);
    }

    private void setupDotAnimation() {
        final View[] dots = {
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3)
        };
        
        final int activeColor = Color.parseColor("#444A72");
        final int inactiveColor = Color.parseColor("#DCE0EE");

        dotRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    View dot = dots[i];
                    if (dot == null) continue;
                    
                    boolean isActive = (i == activeDotIndex);
                    float targetScale = isActive ? 1.2f : 1.0f;
                    float targetAlpha = isActive ? 1.0f : 0.4f;
                    int targetColor = isActive ? activeColor : inactiveColor;

                    dot.animate()
                            .scaleX(targetScale)
                            .scaleY(targetScale)
                            .alpha(targetAlpha)
                            .setDuration(300)
                            .start();

                    dot.setBackgroundTintList(ColorStateList.valueOf(targetColor));
                }

                activeDotIndex = (activeDotIndex + 1) % 3;
                dotHandler.postDelayed(this, 400); // Sequence switches every 400 milliseconds
            }
        };

        // Start dot animation loop
        dotHandler.post(dotRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove handler callbacks to prevent leaks
        if (dotHandler != null && dotRunnable != null) {
            dotHandler.removeCallbacks(dotRunnable);
        }
    }
}