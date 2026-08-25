package com.example.myduet.repositories;

import android.content.Context;
import com.example.myduet.models.SeatPlan;
import com.example.myduet.utils.JsonUtils;
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
import java.util.List;

public class SeatPlanRepository {
    private static final String REMOTE_SEAT_PLAN_URL = "https://raw.githubusercontent.com/saidul-07/MyDUET-Android-App/main/app/src/main/assets/seat_plan/latest_seat_plan.json";

    private List<SeatPlan> cachedSeatPlans;

    public SeatPlanRepository(Context context) {
        loadSeatPlans(context);
        fetchLatestSeatPlansAsync(context);
    }

    private void loadSeatPlans(Context context) {
        try {
            File cacheFile = new File(context.getCacheDir(), "latest_seat_plan.json");
            if (cacheFile.exists()) {
                try (InputStreamReader reader = new InputStreamReader(new FileInputStream(cacheFile))) {
                    Type listType = new TypeToken<List<SeatPlan>>() {}.getType();
                    cachedSeatPlans = new Gson().fromJson(reader, listType);
                }
            }
            if (cachedSeatPlans == null || cachedSeatPlans.isEmpty()) {
                cachedSeatPlans = JsonUtils.loadSeatPlanFromAssets(context, "seat_plan/2026/seat_plan_2026.json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchLatestSeatPlansAsync(Context context) {
        new Thread(() -> {
            try {
                URL url = new URL(REMOTE_SEAT_PLAN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                
                if (conn.getResponseCode() == 200) {
                    File tempFile = new File(context.getCacheDir(), "temp_seat_plan.json");
                    try (InputStream in = conn.getInputStream();
                         FileOutputStream out = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                    
                    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(tempFile))) {
                        Type listType = new TypeToken<List<SeatPlan>>() {}.getType();
                        List<SeatPlan> test = new Gson().fromJson(reader, listType);
                        if (test != null && !test.isEmpty()) {
                            File cacheFile = new File(context.getCacheDir(), "latest_seat_plan.json");
                            if (tempFile.renameTo(cacheFile)) {
                                cachedSeatPlans = test;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public SeatPlan searchSeatPlan(int roll) {
        if (cachedSeatPlans == null) return null;
        
        for (SeatPlan plan : cachedSeatPlans) {
            if (plan.getRoll() == roll) {
                plan.setSearchedRoll(roll);
                plan.setCandidateName(plan.getName());
                plan.setCandidateFatherName(plan.getFatherName());
                return plan;
            }
        }
        return null;
    }
}