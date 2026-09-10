package com.example.myduet;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myduet.adapters.AboutMemberAdapter;
import com.example.myduet.databinding.ActivityAboutUsBinding;
import com.example.myduet.models.AboutMember;
import com.example.myduet.network.SupabaseConfig;

import java.util.ArrayList;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity {

    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                        getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }

        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        binding.toolbarAboutUs.setNavigationOnClickListener(v -> finish());

        // Dynamic Versioning
        String versionName = "1.0.0";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ignored) {}

        binding.tvCurrentVersion.setText("Current Version: " + versionName);
        binding.tvLatestVersion.setText("Latest Version: " + versionName);
        binding.tvReleaseDate.setText("Release Date: 2026-09-10");

        setupMembersList();
    }

    private void setupMembersList() {
        List<AboutMember> members = new ArrayList<>();

        // Supabase Storage bucket URL for profile photos (myduet bucket)
        String supabaseStorageBase = SupabaseConfig.SUPABASE_URL + "/storage/v1/object/public/myduet/";

        // 2026 Team Header
        members.add(new AboutMember("2026"));

        // 1. Md Sayedul Islam
        members.add(new AboutMember(
                "2026",
                "Md Sayedul Islam",
                "App Role: Backend developer",
                "CSE 22 Series",
                supabaseStorageBase + "sayedul_islam.jpg"
        ));

        // 2. Md Alamin
        members.add(new AboutMember(
                "2026",
                "Md Alamin",
                "App Role: UI Designer",
                "CSE 22 Series",
                supabaseStorageBase + "alamin.jpg"
        ));

        // 3. Md Musabbir
        members.add(new AboutMember(
                "2026",
                "Md Musabbir",
                "App Role: Data Entry Operator",
                "CSE 22 Series",
                supabaseStorageBase + "musabbir.jpg"
        ));

        binding.rvAboutMembers.setLayoutManager(new LinearLayoutManager(this));
        AboutMemberAdapter adapter = new AboutMemberAdapter(members);
        binding.rvAboutMembers.setAdapter(adapter);
    }
}