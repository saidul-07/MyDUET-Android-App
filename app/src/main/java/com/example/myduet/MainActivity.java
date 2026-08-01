package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        
        findViewById(R.id.cardNotices).setOnClickListener(v -> {
            Intent intent = new Intent(this, NoticeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardEmergency).setOnClickListener(v -> {
            Intent intent = new Intent(this, EmergencyActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardLibrary).setOnClickListener(v -> {
            Intent intent = new Intent(this, LibraryHomeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardRoutine).setOnClickListener(v -> {
            Intent intent = new Intent(this, RoutineHomeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardAdmission).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdmissionActivity.class);
            startActivity(intent);
        });
    }
}