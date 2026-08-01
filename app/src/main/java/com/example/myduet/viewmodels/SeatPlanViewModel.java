package com.example.myduet.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.models.SeatPlan;
import com.example.myduet.repositories.SeatPlanRepository;

public class SeatPlanViewModel extends AndroidViewModel {

    private final SeatPlanRepository repository;
    private final MutableLiveData<SeatPlan> seatPlanResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SeatPlanViewModel(@NonNull Application application) {
        super(application);
        repository = new SeatPlanRepository(application);
    }

    public LiveData<SeatPlan> getSeatPlanResult() {
        return seatPlanResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void search(String rollStr) {
        if (rollStr == null || rollStr.trim().isEmpty()) {
            errorMessage.setValue("Please enter your admission roll number.");
            return;
        }

        int roll;
        try {
            roll = Integer.parseInt(rollStr.trim());
        } catch (NumberFormatException e) {
            errorMessage.setValue("Invalid roll number.");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);
        seatPlanResult.setValue(null);

        // Simulate a small delay for loading effect
        new android.os.Handler().postDelayed(() -> {
            SeatPlan result = repository.searchSeatPlan(roll);
            isLoading.setValue(false);
            if (result != null) {
                seatPlanResult.setValue(result);
            } else {
                errorMessage.setValue("Seat Plan Not Found");
            }
        }, 800);
    }
}