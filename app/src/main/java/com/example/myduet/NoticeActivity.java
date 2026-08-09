package com.example.myduet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.myduet.adapters.NoticeAdapter;
import com.example.myduet.models.Notice;
import com.example.myduet.viewmodels.NoticeViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

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
    }

    private void initViews() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyState = findViewById(R.id.emptyState);
        errorState = findViewById(R.id.errorState);

        findViewById(R.id.btnRetry).setOnClickListener(v -> viewModel.loadNotices());
        findViewById(R.id.btnRefresh).setOnClickListener(v -> {
            swipeRefresh.setRefreshing(true);
            viewModel.loadNotices();
        });

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadNotices());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        LocaleHelper.styleAppBar(this, toolbar, "#005FB0", "#004F90");

        android.widget.ImageView btnRefresh = findViewById(R.id.btnRefresh);
        if (btnRefresh != null) {
            btnRefresh.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvNotices = findViewById(R.id.rvNotices);
        rvNotices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoticeAdapter();
        adapter.setOnNoticeClickListener(notice -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            String url = notice.getUrl();
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
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(NoticeViewModel.class);
        viewModel.getNotices().observe(this, notices -> {
            swipeRefresh.setRefreshing(false);
            if (notices == null || notices.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                adapter.setNotices(notices);
            } else {
                emptyState.setVisibility(View.GONE);
                errorState.setVisibility(View.GONE);
                adapter.setNotices(notices);
            }
        });
    }
}