package com.example.myduet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

public class LocaleHelper {

    public static void applyLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyDUET_Prefs", Context.MODE_PRIVATE);
        String lang = prefs.getString("selected_language", "English");
        String langCode = lang.contains("Bangla") ? "bn" : "en";
        
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        boolean darkMode = prefs.getBoolean("dark_mode_enabled", false);
        AppCompatDelegate.setDefaultNightMode(
            darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static void styleAppBar(android.app.Activity activity, com.google.android.material.appbar.MaterialToolbar toolbar, String colorHex, String statusBarColorHex) {
        if (toolbar == null) return;
        int bgColor = android.graphics.Color.parseColor(colorHex);
        int statusBarColor = android.graphics.Color.parseColor(statusBarColorHex);

        toolbar.setBackgroundColor(bgColor);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        toolbar.setNavigationIconTint(android.graphics.Color.WHITE);

        // Find AppBarLayout and style it
        android.view.ViewParent parent = toolbar.getParent();
        if (parent instanceof com.google.android.material.appbar.AppBarLayout) {
            com.google.android.material.appbar.AppBarLayout appBar = (com.google.android.material.appbar.AppBarLayout) parent;
            appBar.setBackgroundColor(bgColor);
            appBar.setElevation(4f * activity.getResources().getDisplayMetrics().density);
            appBar.setLiftOnScroll(false);
        } else if (parent instanceof android.view.View && ((android.view.View) parent).getParent() instanceof com.google.android.material.appbar.AppBarLayout) {
            com.google.android.material.appbar.AppBarLayout appBar = (com.google.android.material.appbar.AppBarLayout) ((android.view.View) parent).getParent();
            appBar.setBackgroundColor(bgColor);
            appBar.setElevation(4f * activity.getResources().getDisplayMetrics().density);
            appBar.setLiftOnScroll(false);
        }

        // Set status bar color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(statusBarColor);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                    activity.getWindow().getDecorView().getSystemUiVisibility() & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }
    }
}
