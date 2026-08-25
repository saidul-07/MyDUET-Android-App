package com.example.myduet.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.models.NoticeEntity;
import com.example.myduet.repositories.NoticeRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoticeViewModel extends AndroidViewModel {
    private final NoticeRepository repository;
    private final MediatorLiveData<List<NoticeEntity>> filteredNotices = new MediatorLiveData<>();
    
    private final MutableLiveData<String> category = new MutableLiveData<>("All Notices");
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> sortOrder = new MutableLiveData<>("newest");
    
    private final MutableLiveData<Boolean> isSyncing = new MutableLiveData<>(false);
    private final MutableLiveData<String> syncErrorMessage = new MutableLiveData<>("");

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    public NoticeViewModel(@NonNull Application application) {
        super(application);
        repository = new NoticeRepository(application);
        
        filteredNotices.addSource(repository.getCachedNotices(), notices -> applyFilters());
        filteredNotices.addSource(category, cat -> applyFilters());
        filteredNotices.addSource(searchQuery, q -> applyFilters());
        filteredNotices.addSource(sortOrder, order -> applyFilters());
        
        refreshNotices();
    }

    public LiveData<List<NoticeEntity>> getNotices() {
        return filteredNotices;
    }

    public LiveData<Boolean> getIsSyncing() {
        return isSyncing;
    }

    public LiveData<String> getSyncErrorMessage() {
        return syncErrorMessage;
    }

    public long getLastSyncTime() {
        return repository.getLastSyncTime();
    }

    public void setCategory(String newCategory) {
        category.setValue(newCategory);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query.toLowerCase().trim());
    }

    public void setSortOrder(String order) {
        sortOrder.setValue(order);
    }

    public void refreshNotices() {
        isSyncing.setValue(true);
        syncErrorMessage.setValue("");
        repository.syncNoticesAsync().observeForever(success -> {
            isSyncing.postValue(false);
            if (success == null || !success) {
                List<NoticeEntity> cached = repository.getCachedNotices().getValue();
                if (cached == null || cached.isEmpty()) {
                    syncErrorMessage.postValue("No notices available. Connect to the internet to download notices.");
                } else {
                    syncErrorMessage.postValue("Unable to check for new notices. Showing cached notices.");
                }
            }
        });
    }

    private void applyFilters() {
        List<NoticeEntity> rawNotices = repository.getCachedNotices().getValue();
        if (rawNotices == null) {
            filteredNotices.setValue(new ArrayList<>());
            return;
        }

        String cat = category.getValue();
        String query = searchQuery.getValue();
        String order = sortOrder.getValue();

        List<NoticeEntity> result = new ArrayList<>();
        for (NoticeEntity notice : rawNotices) {
            boolean matchesCategory = "All Notices".equalsIgnoreCase(cat) || 
                                     cat == null || 
                                     notice.getCategory().equalsIgnoreCase(cat);
            
            boolean matchesSearch = query == null || query.isEmpty() ||
                    notice.getTitle().toLowerCase().contains(query) ||
                    notice.getDescription().toLowerCase().contains(query);

            if (matchesCategory && matchesSearch) {
                result.add(notice);
            }
        }

        Collections.sort(result, (n1, n2) -> {
            try {
                Date d1 = dateFormat.parse(n1.getPublishDate());
                Date d2 = dateFormat.parse(n2.getPublishDate());
                if (d1 == null || d2 == null) return 0;
                
                int dateCompare;
                if ("newest".equals(order)) {
                    dateCompare = d2.compareTo(d1);
                } else {
                    dateCompare = d1.compareTo(d2);
                }
                
                if (dateCompare != 0) return dateCompare;
                return Integer.compare(n1.getOriginalIndex(), n2.getOriginalIndex());
            } catch (ParseException e) {
                return 0;
            }
        });

        filteredNotices.setValue(result);
    }
}