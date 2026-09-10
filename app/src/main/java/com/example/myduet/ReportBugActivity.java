package com.example.myduet;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ReportBugActivity extends AppCompatActivity {

    private static final String BUG_FORM_URL = "https://forms.gle/RpSCj1VT7ZSssPQb6";

    private WebView webViewBugReport;
    private LinearProgressIndicator progressBarBug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bug);

        MaterialToolbar toolbar = findViewById(R.id.toolbarReportBug);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        LocaleHelper.styleAppBar(this, toolbar, "#76C457", "#4A8C34");

        MaterialButton btnBackToApp = findViewById(R.id.btnBackToApp);
        btnBackToApp.setOnClickListener(v -> finish());

        progressBarBug = findViewById(R.id.progressBarBug);
        webViewBugReport = findViewById(R.id.webViewBugReport);

        setupWebView();
        webViewBugReport.loadUrl(BUG_FORM_URL);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webViewBugReport != null && webViewBugReport.canGoBack()) {
                    webViewBugReport.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    private void setupWebView() {
        WebSettings settings = webViewBugReport.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webViewBugReport.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBarBug != null) {
                    progressBarBug.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBarBug != null) {
                    progressBarBug.setVisibility(View.GONE);
                }
            }
        });

        webViewBugReport.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBarBug != null) {
                    progressBarBug.setProgressCompat(newProgress, true);
                    if (newProgress == 100) {
                        progressBarBug.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}