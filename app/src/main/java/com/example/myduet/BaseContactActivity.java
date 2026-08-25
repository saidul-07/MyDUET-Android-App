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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getToolbarTitle());
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        String bgColor = "#444A72";
        String activityName = this.getClass().getSimpleName();
        if (activityName.contains("Medical")) {
            bgColor = "#7DD6C8";
        } else if (activityName.contains("Transport") || activityName.contains("ICT") || activityName.contains("Institute") || activityName.contains("ResearchCenter")) {
            bgColor = "#088BB3";
        }
        String statusBarColor = bgColor;
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