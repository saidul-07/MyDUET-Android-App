package com.example.myduet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ContactDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String person = intent.getStringExtra("person");
        String assistant = intent.getStringExtra("assistant");
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String location = intent.getStringExtra("location");
        String hours = intent.getStringExtra("hours");

        if (name != null && name.equals("Medical Center")) {
            setContentView(R.layout.activity_medical_details);
        } else if (name != null && name.equals("Ambulance Contact")) {
            setContentView(R.layout.activity_ambulance_details);
        } else {
            setContentView(R.layout.activity_contact_details);
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        toolbar.setTitle(name);

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvPerson = findViewById(R.id.tvDetailPerson);
        TextView tvAssistant = findViewById(R.id.tvDetailAssistant);
        TextView tvPhone = findViewById(R.id.tvDetailPhone);
        TextView tvEmail = findViewById(R.id.tvDetailEmail);
        TextView tvLocation = findViewById(R.id.tvDetailLocation);
        TextView tvHours = findViewById(R.id.tvDetailHours);

        if (tvTitle != null) tvTitle.setText(name);
        if (tvPerson != null) tvPerson.setText(person);
        if (tvAssistant != null) {
            if (assistant != null && !assistant.isEmpty()) {
                tvAssistant.setText(assistant);
                tvAssistant.setVisibility(View.VISIBLE);
            } else {
                tvAssistant.setVisibility(View.GONE);
            }
        }
        if (tvPhone != null) tvPhone.setText(phone);
        
        if (tvEmail != null) {
            if (email != null && !email.isEmpty()) {
                tvEmail.setText(email);
            } else {
                View layout = findViewById(R.id.layoutEmail);
                if (layout != null) layout.setVisibility(View.GONE);
            }
        }

        if (tvLocation != null) {
            if (location != null && !location.isEmpty()) {
                tvLocation.setText(location);
            } else {
                View layout = findViewById(R.id.layoutLocation);
                if (layout != null) layout.setVisibility(View.GONE);
            }
        }

        if (tvHours != null) {
            if (hours != null && !hours.isEmpty()) {
                tvHours.setText(hours);
            } else {
                View layout = findViewById(R.id.layoutHours);
                if (layout != null) layout.setVisibility(View.GONE);
            }
        }

        findViewById(R.id.btnCall).setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + phone));
            startActivity(dialIntent);
        });

        View btnEmail = findViewById(R.id.btnEmail);
        if (btnEmail != null) {
            if (email != null && !email.isEmpty()) {
                btnEmail.setOnClickListener(v -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + email));
                    startActivity(emailIntent);
                });
            } else {
                btnEmail.setVisibility(View.GONE);
            }
        }

        View btnLocation = findViewById(R.id.btnLocation);
        if (btnLocation != null) {
            if (location != null && !location.isEmpty()) {
                btnLocation.setOnClickListener(v -> {
                    Toast.makeText(this, "Opening map for: " + location, Toast.LENGTH_SHORT).show();
                });
            } else {
                btnLocation.setVisibility(View.GONE);
            }
        }
    }
}