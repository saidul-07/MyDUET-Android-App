package com.example.myduet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.adapters.MedicalContactAdapter;
import com.example.myduet.models.EmergencyContact;
import com.example.myduet.repositories.EmergencyRepository;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public class MedicalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_services);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvContacts);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<EmergencyContact> contacts = new EmergencyRepository().getMedicalContacts();
        MedicalContactAdapter adapter = new MedicalContactAdapter(contacts, new MedicalContactAdapter.OnItemClickListener() {
            @Override
            public void onCallClick(EmergencyContact contact) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contact.getPhone()));
                startActivity(intent);
            }

            @Override
            public void onDetailsClick(EmergencyContact contact) {
                Intent intent = new Intent(MedicalActivity.this, ContactDetailsActivity.class);
                intent.putExtra("name", contact.getName());
                intent.putExtra("person", contact.getPersonName());
                intent.putExtra("assistant", contact.getAssistantName());
                intent.putExtra("phone", contact.getPhone());
                intent.putExtra("email", contact.getEmail());
                intent.putExtra("location", contact.getLocation());
                intent.putExtra("hours", contact.getHours());
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);
    }
}