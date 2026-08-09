package com.example.myduet.repositories;

import android.content.Context;
import com.example.myduet.models.AdmissionResult;
import com.example.myduet.utils.AdmissionResultJsonReader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdmissionResultRepository {
    private static final String REMOTE_URL = "https://raw.githubusercontent.com/saidul-07/MyDUET-Android-App/main/app/src/main/assets/results/latest_admission_result.json";
    private List<AdmissionResult> cachedResults;

    public AdmissionResultRepository(Context context) {
        loadResults(context);
        fetchLatestResultsAsync(context);
    }

    private void loadResults(Context context) {
        try {
            File cacheFile = new File(context.getCacheDir(), "latest_admission_result.json");
            if (cacheFile.exists()) {
                try (InputStreamReader reader = new InputStreamReader(new FileInputStream(cacheFile))) {
                    Type listType = new TypeToken<List<AdmissionResult>>() {}.getType();
                    cachedResults = new Gson().fromJson(reader, listType);
                }
            }
            if (cachedResults == null || cachedResults.isEmpty()) {
                cachedResults = AdmissionResultJsonReader.readResultsFromAssets(context, "results/2026/admission_result_2026.json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchLatestResultsAsync(Context context) {
        new Thread(() -> {
            try {
                URL url = new URL(REMOTE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                
                if (conn.getResponseCode() == 200) {
                    File tempFile = new File(context.getCacheDir(), "temp_admission_result.json");
                    try (InputStream in = conn.getInputStream();
                         FileOutputStream out = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                    
                    // Validate JSON list before caching
                    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(tempFile))) {
                        Type listType = new TypeToken<List<AdmissionResult>>() {}.getType();
                        List<AdmissionResult> test = new Gson().fromJson(reader, listType);
                        if (test != null && !test.isEmpty()) {
                            File cacheFile = new File(context.getCacheDir(), "latest_admission_result.json");
                            if (tempFile.renameTo(cacheFile)) {
                                cachedResults = test;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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