package com.example.myduet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myduet.models.Notice;
import com.example.myduet.repositories.NoticeRepository;
import java.util.ArrayList;
import java.util.List;

public class NoticeViewModel extends ViewModel {
    private NoticeRepository repository;
    private MutableLiveData<List<Notice>> filteredNotices = new MutableLiveData<>();
    private List<Notice> allNotices = new ArrayList<>();
    private String currentCategory = "All Notices";
    private String currentSearch = "";

    public NoticeViewModel() {
        repository = new NoticeRepository();
        loadNotices();
    }

    public void loadNotices() {
        repository.getNotices().observeForever(notices -> {
            allNotices = notices;
            applyFilter();
        });
    }

    public LiveData<List<Notice>> getNotices() {
        return filteredNotices;
    }

    public void setCategory(String category) {
        this.currentCategory = category;
        applyFilter();
    }

    public void setSearchQuery(String query) {
        this.currentSearch = query.toLowerCase().trim();
        applyFilter();
    }

    private void applyFilter() {
        List<Notice> result = new ArrayList<>();
        for (Notice notice : allNotices) {
            // Exact match for category filtering using the official names
            boolean matchesCategory = currentCategory.equals("All Notices") || notice.getCategory().equals(currentCategory);
            boolean matchesSearch = currentSearch.isEmpty() || notice.getTitle().toLowerCase().contains(currentSearch);
            
            if (matchesCategory && matchesSearch) {
                result.add(notice);
            }
        }
        filteredNotices.setValue(result);
    }
}