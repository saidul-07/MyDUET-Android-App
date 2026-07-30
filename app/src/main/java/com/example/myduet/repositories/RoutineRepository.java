package com.example.myduet.repositories;

import android.content.Context;
import android.util.Log;
import com.example.myduet.models.RoutineClass;
import com.example.myduet.models.RoutineData;
import com.example.myduet.utils.RoutineFileResolver;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RoutineRepository {

    private static final String TAG = "RoutineRepository";

    public List<RoutineClass> getRoutine(Context context, String dept, String year, String section, String day) {
        String assetPath = RoutineFileResolver.INSTANCE.getAssetPath(dept, year, section);
        Log.d(TAG, "Loading routine from: " + assetPath);

        try {
            String json = loadJsonFromAsset(context, assetPath);
            if (json == null) return new ArrayList<>();

            RoutineData data = new Gson().fromJson(json, RoutineData.class);
            if (data != null && data.getDays() != null) {
                List<RoutineClass> dayRoutine = data.getDays().get(day);
                return dayRoutine != null ? dayRoutine : new ArrayList<>();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON: " + assetPath, e);
        }
        return new ArrayList<>();
    }

    private String loadJsonFromAsset(Context context, String path) {
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();
            if (read > 0) {
                return new String(buffer, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            Log.e(TAG, "Asset file not found: " + path);
        }
        return null;
    }
}