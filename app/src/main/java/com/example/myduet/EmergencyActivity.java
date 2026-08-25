package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.adapters.EmergencyCategoryAdapter;
import com.example.myduet.repositories.EmergencyRepository;
import com.google.android.material.appbar.MaterialToolbar;

public class EmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        LocaleHelper.styleAppBar(this, toolbar, "#444A72", "#444A72");

        RecyclerView rv = findViewById(R.id.rvEmergencyCategories);
        rv.setLayoutManager(new LinearLayoutManager(this));

        EmergencyRepository repo = new EmergencyRepository();
        EmergencyCategoryAdapter adapter = new EmergencyCategoryAdapter(repo.getCategories(), item -> {
            Intent intent;
            switch (item.getId()) {
                case "1":
                    intent = new Intent(this, MedicalActivity.class);
                    break;
                case "2":
                    intent = new Intent(this, SecurityActivity.class);
                    break;
                case "3":
                    intent = new Intent(this, ProctorActivity.class);
                    break;
                case "4":
                    intent = new Intent(this, TransportActivity.class);
                    break;
                case "5":
                    intent = new Intent(this, HallListActivity.class);
                    break;
                case "6":
                    intent = new Intent(this, ICTActivity.class);
                    break;
                case "7":
                    intent = new Intent(this, FacultyActivity.class);
                    break;
                default:
                    return;
            }
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }
}