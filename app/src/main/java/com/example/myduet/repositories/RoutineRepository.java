package com.example.myduet.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.myduet.models.RoutineClass;
import com.example.myduet.models.RoutineData;
import com.example.myduet.network.SupabaseConfig;
import com.example.myduet.utils.RoutineFileResolver;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoutineRepository {

    private static final String TAG = "RoutineRepository";
    private static final String SUPABASE_STORAGE_URL = SupabaseConfig.SUPABASE_URL + "/storage/v1/object/public/myduet/";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final OkHttpClient httpClient = new OkHttpClient();

    public interface RoutineSyncCallback {
        void onSyncComplete(boolean isUpdated, List<RoutineClass> routine);
    }

    public List<RoutineClass> getRoutine(Context context, String dept, String year, String section, String day) {
        String assetPath = RoutineFileResolver.INSTANCE.getAssetPath(dept, year, section);
        Log.d(TAG, "Loading routine from: " + assetPath);

        try {
            String json = loadJsonFromStorageOrAsset(context, assetPath);
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

    public void syncRoutineFromSupabase(Context context, String dept, String year, String section, String day, RoutineSyncCallback callback) {
        String relativePath = RoutineFileResolver.INSTANCE.getAssetPath(dept, year, section);
        String remoteUrl = SUPABASE_STORAGE_URL + relativePath;

        executor.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url(remoteUrl)
                        .header("Cache-Control", "no-cache")
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String freshJson = response.body().string();
                        RoutineData data = new Gson().fromJson(freshJson, RoutineData.class);
                        if (data != null && data.getDays() != null) {
                            String cachedJson = loadJsonFromStorage(context, relativePath);
                            boolean isNew = cachedJson == null || !cachedJson.trim().equals(freshJson.trim());
                            if (isNew) {
                                saveJsonToStorage(context, relativePath, freshJson);
                            }
                            List<RoutineClass> dayRoutine = data.getDays().get(day);
                            List<RoutineClass> result = dayRoutine != null ? dayRoutine : new ArrayList<>();
                            mainHandler.post(() -> {
                                if (callback != null) {
                                    callback.onSyncComplete(isNew, result);
                                }
                            });
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to sync routine from Supabase: " + remoteUrl, e);
            }

            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSyncComplete(false, getRoutine(context, dept, year, section, day));
                }
            });
        });
    }

    private String loadJsonFromStorageOrAsset(Context context, String path) {
        String fromStorage = loadJsonFromStorage(context, path);
        if (fromStorage != null) {
            return fromStorage;
        }

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

    private String loadJsonFromStorage(Context context, String path) {
        try {
            File localFile = new File(context.getFilesDir(), path);
            if (localFile.exists()) {
                FileInputStream fis = new FileInputStream(localFile);
                int size = fis.available();
                byte[] buffer = new byte[size];
                int read = fis.read(buffer);
                fis.close();
                if (read > 0) {
                    return new String(buffer, StandardCharsets.UTF_8);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Local file not read: " + path, e);
        }
        return null;
    }

    private void saveJsonToStorage(Context context, String path, String content) {
        try {
            File file = new File(context.getFilesDir(), path);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }
            Log.d(TAG, "Saved routine to internal storage: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error saving routine to internal storage: " + path, e);
        }
    }
}