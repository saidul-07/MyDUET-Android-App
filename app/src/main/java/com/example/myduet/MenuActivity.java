package com.example.myduet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_menu);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_calendar) {
                Intent intent = new Intent(this, CalenderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            return true;
        });

        RecyclerView rvMenuServices = findViewById(R.id.rvMenuServices);
        rvMenuServices.setLayoutManager(new LinearLayoutManager(this));

        List<MenuServiceAdapter.MenuServiceItem> serviceItems = new ArrayList<>();
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_history), R.drawable.ic_monument));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_bus), R.drawable.ic_bus));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_route), R.drawable.ic_route));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_gallery), R.drawable.ic_gallery));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_settings), R.drawable.ic_settings));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_faq), R.drawable.ic_faq));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_contacts), R.drawable.ic_contacts));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_about), R.drawable.ic_info));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_privacy), R.drawable.ic_shield));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_update), R.drawable.ic_update));
        serviceItems.add(new MenuServiceAdapter.MenuServiceItem(getString(R.string.menu_bug), R.drawable.ic_bug));

        MenuServiceAdapter adapter = new MenuServiceAdapter(serviceItems, item -> {
            String title = item.title;
            if (title.equals(getString(R.string.menu_history))) {
                openWebView("History of DUET", "https://www.duet.ac.bd/about/history");
            } else if (title.equals(getString(R.string.menu_bus))) {
                openWebView("Bus Information", "https://www.duet.ac.bd/page/transportation");
            } else if (title.equals(getString(R.string.menu_route))) {
                showWayToCampusDialog();
            } else if (title.equals(getString(R.string.menu_gallery))) {
                openWebView("Image Gallery", "https://www.duet.ac.bd/gallery");
            } else if (title.equals(getString(R.string.menu_settings))) {
                showSettingsDialog();
            } else if (title.equals(getString(R.string.menu_faq))) {
                showFaqDialog();
            } else if (title.equals(getString(R.string.menu_contacts))) {
                startActivity(new Intent(this, EmergencyActivity.class));
            } else if (title.equals(getString(R.string.menu_about))) {
                showAboutUsDialog();
            } else if (title.equals(getString(R.string.menu_privacy))) {
                showPrivacyPolicyDialog();
            } else if (title.equals(getString(R.string.menu_update))) {
                checkForUpdates();
            } else if (title.equals(getString(R.string.menu_bug))) {
                Toast.makeText(this, "Thank you for reporting. Our developers will look into this.", Toast.LENGTH_LONG).show();
            }
        });
        rvMenuServices.setAdapter(adapter);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void openWebView(String title, String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        builder.setView(dialogView);

        MaterialSwitch swNotifications = dialogView.findViewById(R.id.swNotifications);
        MaterialSwitch swDarkMode = dialogView.findViewById(R.id.swDarkMode);
        TextView tvLanguageValue = dialogView.findViewById(R.id.tvLanguageValue);
        Button btnClearCache = dialogView.findViewById(R.id.btnClearCache);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        SharedPreferences prefs = getSharedPreferences("MyDUET_Prefs", MODE_PRIVATE);
        swNotifications.setChecked(prefs.getBoolean("notifications_enabled", true));
        swDarkMode.setChecked(prefs.getBoolean("dark_mode_enabled", false));
        tvLanguageValue.setText(prefs.getString("selected_language", "English"));

        AlertDialog dialog = builder.create();

        swNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Notifications enabled" : "Notifications disabled", Toast.LENGTH_SHORT).show();
            if (isChecked) {
                buttonView.postDelayed(() -> {
                    triggerNoticeNotification();
                }, 3000);
            }
        });

        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode_enabled", isChecked).apply();
            Toast.makeText(this, "Dark theme toggled", Toast.LENGTH_SHORT).show();
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                isChecked ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            );
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialogView.findViewById(R.id.layoutLanguage).setOnClickListener(v -> {
            String[] languages = {"English", "Bangla (বাংলা)"};
            new AlertDialog.Builder(this)
                .setTitle("Choose Language")
                .setItems(languages, (dialogInterface, which) -> {
                    String selection = languages[which];
                    prefs.edit().putString("selected_language", selection).apply();
                    tvLanguageValue.setText(selection);
                    Toast.makeText(this, "Language set to " + selection, Toast.LENGTH_SHORT).show();
                    
                    LocaleHelper.applyLocale(this);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
        });

        btnClearCache.setOnClickListener(v -> {
            try {
                File dir = getCacheDir();
                deleteDir(dir);
                Toast.makeText(this, "Cache cleared successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void showFaqDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Frequently Asked Questions")
            .setMessage("Q: How can I contact a teacher?\nA: Go to the home page or Menu, select 'Directory', choose a department, and click on the call/email icons on the teacher's profile card.\n\n" +
                        "Q: Where is the class routine?\nA: Use the Weekly Routine option on the Home screen to view routines.\n\n" +
                        "Q: How to check the exam seat plan?\nA: Click on the Seat Plan icon on the home dashboard and enter your Roll Number.")
            .setPositiveButton("Close", null)
            .show();
    }

    private void showAboutUsDialog() {
        new AlertDialog.Builder(this)
            .setTitle("About MyDUET")
            .setMessage("MyDUET is the official companion app designed for students, faculty, and visitors of Dhaka University of Engineering & Technology, Gazipur.\n\n" +
                        "Developed to offer seamless access to directories, transport info, academic calendars, seat plans, and admission notices in real-time.\n\n" +
                        "Version: 1.0.0\n© 2026 DUET Gazipur")
            .setPositiveButton("OK", null)
            .show();
    }

    private void showPrivacyPolicyDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Privacy Policy")
            .setMessage("We value your privacy. The MyDUET application:\n\n" +
                        "1. Does not collect personal tracking data.\n" +
                        "2. Handles external data requests securely.\n" +
                        "3. Stores user preferences (like settings and language) locally on your device.\n\n" +
                        "For details, contact DUET ICT Cell.")
            .setPositiveButton("Accept", null)
            .show();
    }

    private void checkForUpdates() {
        Toast.makeText(this, "Checking for updates...", Toast.LENGTH_SHORT).show();
        findViewById(R.id.rvMenuServices).postDelayed(() -> {
            new AlertDialog.Builder(this)
                .setTitle("App Update")
                .setMessage("Your application is fully up to date!\n\nCurrent Version: 1.0.0 (Latest)")
                .setPositiveButton("OK", null)
                .show();
        }, 1200);
    }

    private void showWayToCampusDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Way to Go to Campus (Dhaka to DUET)")
            .setMessage("Dhaka University of Engineering & Technology (DUET) is situated in Gazipur, about 40 km north of Dhaka city.\n\n" +
                        "Here are the instructions on how to travel from Dhaka to DUET:\n\n" +
                        "1. By Bus (Recommended):\n" +
                        "• Route: Take any Gazipur-bound bus (e.g., Gazipur Paribahan, Balaka, VIP, Anabil) from Mohakhali Bus Terminal, Gulistan, or Farmgate.\n" +
                        "• Destination: Get off at Joydebpur Chowrasta or Shibbari Mor.\n" +
                        "• Last Mile: From Chowrasta/Shibbari, take a local auto-rickshaw (Easy bike) or rickshaw directly to Shimultoli Road, DUET Campus (approx. 10-15 mins).\n\n" +
                        "2. By Train:\n" +
                        "• Route: Board any Mymensingh/North-Bengal bound train from Kamalapur Railway Station or Dhaka Airport Station.\n" +
                        "• Destination: Get off at Joydebpur Junction (Gazipur Railway Station).\n" +
                        "• Last Mile: Take an auto-rickshaw or rickshaw from Joydebpur Station directly to DUET (approx. 2.5 km).\n\n" +
                        "3. By Car / Taxi:\n" +
                        "• Route: Drive northwards on the Dhaka-Mymensingh Highway (N5).\n" +
                        "• Directions: Cross Gazipur Chowrasta and turn right towards Shibbari Mor. From Shibbari, follow Shimultoli Road north to reach DUET Campus.")
            .setPositiveButton("Close", null)
            .setNeutralButton("View on Google Maps", (dialog, which) -> {
                openWebView("Campus Direction", "https://www.google.com/maps?ll=24.017849,90.418188&z=15&t=m&hl=en-US&gl=US&mapclient=embed&cid=10183962064863318546");
            })
            .show();
    }

    private void triggerNoticeNotification() {
        try {
            String channelId = "MyDUET_Notices";
            String channelName = "DUET Notice Alerts";
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId, channelName, android.app.NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationManager.createNotificationChannel(channel);
            }

            android.content.Intent intent = new android.content.Intent(this, NoticeActivity.class);
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );

            androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.duet_official_logo)
                .setContentTitle("New Notice Published")
                .setContentText("Academic notice updated. Tap to read official circular.")
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

            notificationManager.notify(1, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
