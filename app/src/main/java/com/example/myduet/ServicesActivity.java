package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class ServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#444A72"));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }
        setContentView(R.layout.activity_services);

        findViewById(R.id.card_ugr).setOnClickListener(v -> openWebView("UGR", "https://ugr.duetbd.org/"));
        findViewById(R.id.card_pgr).setOnClickListener(v -> openWebView("PGR", "https://pgr.duetbd.org/"));
        findViewById(R.id.card_elearning).setOnClickListener(v -> openWebView("E-Learning", "https://elp.duetbd.org/"));
        findViewById(R.id.card_question_bank).setOnClickListener(v -> openWebView("Question Bank", "https://drive.google.com/drive/folders/1A0IgdJphb4l2I6Jltub4cnUqJB5IYZZP"));
        findViewById(R.id.card_clearance).setOnClickListener(v -> openWebView("Clearance System", "https://clearance.duet.ac.bd/login"));
        findViewById(R.id.card_payroll).setOnClickListener(v -> openWebView("Payroll", "https://www.duetpayroll.org/accounts/profile/salaryLists/"));
        findViewById(R.id.card_utility).setOnClickListener(v -> openWebView("Utility Contact", "https://www.duet.ac.bd/service-and-utility-contact"));
        findViewById(R.id.card_vehicle).setOnClickListener(v -> openWebView("Vehicle Requisition", "https://www.duetpayroll.org/accounts/profile/transportrequisitions/"));

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
}
