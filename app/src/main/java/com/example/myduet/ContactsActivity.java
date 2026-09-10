package com.example.myduet;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myduet.databinding.ActivityContactsBinding;

public class ContactsActivity extends AppCompatActivity {

    private ActivityContactsBinding binding;
    private static final String DUET_PHONE = "+880249274034";
    private static final String DUET_EMAIL = "registrar@duet.ac.bd";
    private static final String DUET_WEB = "https://www.duet.ac.bd";

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

        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        // Setup Toolbar & System Color
        LocaleHelper.styleAppBar(this, binding.toolbarContacts, "#76C457", "#4A8C34");
        binding.toolbarContacts.setNavigationOnClickListener(v -> finish());

        // Call Actions
        View.OnClickListener callAction = v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + DUET_PHONE));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Could not open dialer", Toast.LENGTH_SHORT).show();
            }
        };
        binding.btnCallNow.setOnClickListener(callAction);
        binding.layoutPhone.setOnClickListener(callAction);

        // Email Actions
        View.OnClickListener emailAction = v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + DUET_EMAIL));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry to DUET Registrar");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Could not open email client", Toast.LENGTH_SHORT).show();
            }
        };
        binding.btnSendEmail.setOnClickListener(emailAction);
        binding.layoutEmail.setOnClickListener(emailAction);

        // Web Action
        binding.layoutWeb.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", DUET_WEB);
                intent.putExtra("title", "DUET Official Website");
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DUET_WEB));
                    startActivity(intent);
                } catch (Exception ignored) {}
            }
        });
    }
}