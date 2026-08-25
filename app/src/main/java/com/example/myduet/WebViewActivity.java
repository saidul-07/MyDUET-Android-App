package com.example.myduet;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myduet.databinding.ActivityWebviewBinding;

public class WebViewActivity extends AppCompatActivity {

    private ActivityWebviewBinding binding;
    private String originalUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#444A72"));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        originalUrl = url;
        if (url != null && url.contains("docs.google.com/gview")) {
            int index = url.indexOf("url=");
            if (index != -1) {
                originalUrl = url.substring(index + 4);
            }
        }

        if (title != null && !title.isEmpty()) {
            binding.toolbarWebView.setTitle(title);
        }

        binding.toolbarWebView.setNavigationOnClickListener(v -> finish());

        // Inflate action menu programmatically for downloading/opening externally
        binding.toolbarWebView.getMenu().add(0, 1, 0, "Download")
                .setIcon(R.drawable.ic_download)
                .setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS);

        binding.toolbarWebView.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                if (originalUrl != null && !originalUrl.isEmpty()) {
                    try {
                        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(originalUrl));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
            return false;
        });

        setupWebView();

        if (url != null && !url.isEmpty()) {
            binding.webView.loadUrl(url);
        }
    }

    private void setupWebView() {
        WebSettings settings = binding.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, android.webkit.WebResourceRequest request) {
                String url = request.getUrl().toString();
                return handleCustomUri(view, url);
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleCustomUri(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.progressBarWeb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.progressBarWeb.setVisibility(View.GONE);
                if (url != null && url.contains("service-and-utility-contact")) {
                    view.evaluateJavascript(
                        "(function() { " +
                        "   var style = document.createElement('style'); " +
                        "   style.type = 'text/css'; " +
                        "   style.innerHTML = '#header, .page-header { display: none !important; }'; " +
                        "   document.head.appendChild(style); " +
                        "})()",
                        null
                    );
                }
            }
        });

        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                binding.progressBarWeb.setProgress(newProgress);
                if (newProgress == 100) {
                    binding.progressBarWeb.setVisibility(View.GONE);
                } else {
                    binding.progressBarWeb.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        String currentUrl = binding.webView.getUrl();
        if (currentUrl != null && (currentUrl.contains("google.com/maps") || currentUrl.contains("maps.google"))) {
            super.onBackPressed();
        } else if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private boolean handleCustomUri(WebView view, String url) {
        if (url == null) return false;

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return false;
        }

        try {
            android.content.Intent intent = android.content.Intent.parseUri(url, android.content.Intent.URI_INTENT_SCHEME);
            if (intent != null) {
                if (getPackageManager().resolveActivity(intent, 0) != null) {
                    startActivity(intent);
                } else {
                    String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                    if (fallbackUrl != null) {
                        view.loadUrl(fallbackUrl);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (binding.webView != null) {
            binding.webView.loadUrl("about:blank");
            binding.webView.destroy();
        }
        super.onDestroy();
    }
}
