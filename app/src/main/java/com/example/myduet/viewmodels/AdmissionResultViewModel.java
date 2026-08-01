package com.example.myduet.viewmodels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.models.AdmissionResult;
import com.example.myduet.repositories.AdmissionResultRepository;

public class AdmissionResultViewModel extends AndroidViewModel {

    private final AdmissionResultRepository repository;
    private final MutableLiveData<AdmissionResult> searchResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AdmissionResultViewModel(@NonNull Application application) {
        super(application);
        repository = new AdmissionResultRepository(application);
    }

    public LiveData<AdmissionResult> getSearchResult() { return searchResult; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }

    public void search(String rollStr) {
        if (rollStr == null || rollStr.trim().isEmpty()) {
            error.setValue("Please enter your admission roll number.");
            return;
        }

        int roll;
        try {
            roll = Integer.parseInt(rollStr.trim());
        } catch (NumberFormatException e) {
            error.setValue("Invalid roll number.");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);
        searchResult.setValue(null);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AdmissionResult result = repository.getResultByRoll(roll);
            isLoading.setValue(false);
            if (result != null) {
                searchResult.setValue(result);
            } else {
                error.setValue("Result Not Found");
            }
        }, 1000);
    }
}