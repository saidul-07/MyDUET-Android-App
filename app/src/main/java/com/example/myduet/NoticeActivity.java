package com.example.myduet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.myduet.adapters.NoticeAdapter;
import com.example.myduet.models.NoticeEntity;
import com.example.myduet.viewmodels.NoticeViewModel;
import com.example.myduet.workers.NoticeSyncWorker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NoticeActivity extends AppCompatActivity {

    private NoticeViewModel viewModel;
    private NoticeAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout emptyState, errorState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupCategoryFilter();
        setupSearch();
        setupViewModel();
        setupWorkManager();
    }

    private void initViews() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyState = findViewById(R.id.emptyState);
        errorState = findViewById(R.id.errorState);

        findViewById(R.id.btnRetry).setOnClickListener(v -> viewModel.refreshNotices());


        swipeRefresh.setOnRefreshListener(() -> viewModel.refreshNotices());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        LocaleHelper.styleAppBar(this, toolbar, "#444A72", "#444A72");

        ImageView btnRefresh = findViewById(R.id.btnRefresh);
        if (btnRefresh != null) {
            btnRefresh.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
            btnRefresh.setOnClickListener(v -> {
                swipeRefresh.setRefreshing(true);
                viewModel.refreshNotices();
            });
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvNotices = findViewById(R.id.rvNotices);
        rvNotices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoticeAdapter();
        adapter.setOnNoticeClickListener(notice -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            String url = notice.getPdfUrl();
            if (url != null && (url.toLowerCase().endsWith(".pdf") || url.toLowerCase().contains(".pdf"))) {
                url = "https://docs.google.com/gview?embedded=true&url=" + url;
            }
            intent.putExtra("url", url);
            intent.putExtra("title", notice.getTitle());
            startActivity(intent);
        });
        rvNotices.setAdapter(adapter);
    }

    private void setupCategoryFilter() {
        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = findViewById(checkedIds.get(0));
                viewModel.setCategory(chip.getText().toString());
            }
        });
    }

    private void setupSearch() {
        SearchBar searchBar = findViewById(R.id.search_bar);
        SearchView searchView = findViewById(R.id.search_view);

        searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            String query = searchView.getText().toString();
            searchBar.setText(query);
            viewModel.setSearchQuery(query);
            searchView.hide();
            return false;
        });

        searchView.getEditText().addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(NoticeViewModel.class);
        
        viewModel.getNotices().observe(this, notices -> {
            Boolean isSyncing = viewModel.getIsSyncing().getValue();
            boolean syncing = (isSyncing != null && isSyncing);

            findViewById(R.id.skeletonLayout).setVisibility(View.GONE);

            if (notices == null || notices.isEmpty()) {
                if (!syncing) {
                    emptyState.setVisibility(View.VISIBLE);
                }
                adapter.setNotices(notices);
            } else {
                emptyState.setVisibility(View.GONE);
                errorState.setVisibility(View.GONE);
                adapter.setNotices(notices);
            }
            updateOfflineBanner();
        });

        viewModel.getIsSyncing().observe(this, syncing -> {
            if (syncing) {
                List<NoticeEntity> current = viewModel.getNotices().getValue();
                if (current == null || current.isEmpty()) {
                    findViewById(R.id.skeletonLayout).setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                    errorState.setVisibility(View.GONE);
                }
                swipeRefresh.setRefreshing(true);
            } else {
                swipeRefresh.setRefreshing(false);
                updateOfflineBanner();
            }
        });

        viewModel.getSyncErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Snackbar.make(findViewById(R.id.rvNotices), errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupWorkManager() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncRequest =
                new PeriodicWorkRequest.Builder(NoticeSyncWorker.class, 6, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "NoticeSyncWork",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest);
    }

    private void updateOfflineBanner() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(network);
                    isConnected = caps != null && (
                            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                }
            } else {
                NetworkInfo activeInfo = cm.getActiveNetworkInfo();
                isConnected = activeInfo != null && activeInfo.isConnected();
            }
        }

        View banner = findViewById(R.id.cardOfflineBanner);
        TextView tvLastSyncTime = findViewById(R.id.tvLastSyncTime);

        if (!isConnected) {
            banner.setVisibility(View.VISIBLE);
            long lastSync = viewModel.getLastSyncTime();
            if (lastSync != 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.ENGLISH);
                tvLastSyncTime.setText("Last updated: " + sdf.format(new Date(lastSync)));
            } else {
                tvLastSyncTime.setText("Connect to the internet to download notices.");
            }
        } else {
            banner.setVisibility(View.GONE);
        }
    }
}