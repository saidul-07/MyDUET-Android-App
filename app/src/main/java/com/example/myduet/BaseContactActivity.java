package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.adapters.ContactAdapter;
import com.example.myduet.models.EmergencyContact;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public abstract class BaseContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getToolbarTitle());
        toolbar.setNavigationOnClickListener(v -> finish());

        String bgColor = "#005FB0";
        String statusBarColor = "#004F90";
        String activityName = this.getClass().getSimpleName();
        if (activityName.contains("Medical")) {
            bgColor = "#D32F2F";
            statusBarColor = "#B71C1C";
        } else if (activityName.contains("Security")) {
            bgColor = "#1565C0";
            statusBarColor = "#0D47A1";
        } else if (activityName.contains("Proctor")) {
            bgColor = "#7B1FA2";
            statusBarColor = "#4A148C";
        } else if (activityName.contains("Transport")) {
            bgColor = "#2E7D32";
            statusBarColor = "#1B5E20";
        } else if (activityName.contains("ICT")) {
            bgColor = "#00838F";
            statusBarColor = "#006064";
        } else if (activityName.contains("Office")) {
            bgColor = "#1E88E5";
            statusBarColor = "#1565C0";
        } else if (activityName.contains("Institute")) {
            bgColor = "#2E7D32";
            statusBarColor = "#1B5E20";
        } else if (activityName.contains("ResearchCenter")) {
            bgColor = "#7B1FA2";
            statusBarColor = "#4A148C";
        }
        LocaleHelper.styleAppBar(this, toolbar, bgColor, statusBarColor);

        RecyclerView rv = findViewById(R.id.rvContacts);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ContactAdapter adapter = new ContactAdapter(getContacts(), contact -> {
            Intent intent = new Intent(this, ContactDetailsActivity.class);
            // Passing individual fields since we don't have Parcelable yet for simplicity
            intent.putExtra("name", contact.getName());
            intent.putExtra("person", contact.getPersonName());
            intent.putExtra("assistant", contact.getAssistantName());
            intent.putExtra("phone", contact.getPhone());
            intent.putExtra("email", contact.getEmail());
            intent.putExtra("location", contact.getLocation());
            intent.putExtra("hours", contact.getHours());
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }

    protected abstract String getToolbarTitle();
    protected abstract List<EmergencyContact> getContacts();
}