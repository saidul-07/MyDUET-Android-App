package com.example.myduet.repositories;

import android.content.Context;
import com.example.myduet.models.AdmissionResult;
import com.example.myduet.utils.AdmissionResultJsonReader;
import java.util.List;

public class AdmissionResultRepository {
    private List<AdmissionResult> cachedResults;

    public AdmissionResultRepository(Context context) {
        try {
            cachedResults = AdmissionResultJsonReader.readResultsFromAssets(context, "results/admission_result_2025.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AdmissionResult getResultByRoll(int roll) {
        if (cachedResults == null) return null;
        for (AdmissionResult result : cachedResults) {
            if (result.getRoll() == roll) {
                return result;
            }
        }
        return null;
    }
}