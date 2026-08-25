package com.example.myduet;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class RouteActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_route);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        WebView webViewMap = findViewById(R.id.webViewMap);
        WebSettings settings = webViewMap.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webViewMap.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("intent://") || url.startsWith("maps://") || url.startsWith("geo:"))) {
                    try {
                        android.content.Intent intent = android.content.Intent.parseUri(url, android.content.Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            view.getContext().startActivity(intent);
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, android.webkit.WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("intent://") || url.startsWith("maps://") || url.startsWith("geo:")) {
                    try {
                        android.content.Intent intent = android.content.Intent.parseUri(url, android.content.Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            view.getContext().startActivity(intent);
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "body, html { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; }\n" +
                "iframe { border: none; width: 100%; height: 100%; }\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3648.4348577366536!2d90.418188!3d24.017849!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3755db8be7754b2d%3A0x8d517c2f6d0f62d2!2sDhaka%20University%20of%20Engineering%20%26%20Technology%20(DUET)!5e0!3m2!1sen!2sbd!4v1780000000000!5m2!1sen!2sbd\"></iframe>\n" +
                "</body>\n" +
                "</html>";

        webViewMap.loadDataWithBaseURL("https://www.google.com", htmlContent, "text/html", "UTF-8", null);
    }
}
