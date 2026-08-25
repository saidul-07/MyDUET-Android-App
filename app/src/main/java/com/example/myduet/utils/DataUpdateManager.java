package com.example.myduet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataUpdateManager {
    private static final String TAG = "DataUpdateManager";
    private static final String PREFS_NAME = "duet_update_prefs";
    private static final String KEY_VERSION = "data_version";
    private static final String BASE_URL = "https://raw.githubusercontent.com/saidul-07/MyDUET-Android-App/main/updates/";

    private static DataUpdateManager instance;
    private final Context context;

    private DataUpdateManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized DataUpdateManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataUpdateManager(context);
        }
        return instance;
    }

    public interface UpdateCallback {
        void onProgress(String status);
        void onSuccess(boolean updated);
        void onFailure(String error);
    }

    private static class Manifest {
        int version;
    }

    public int getLocalVersion() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_VERSION, 0);
    }

    private void saveLocalVersion(int version) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_VERSION, version).apply();
    }

    public void checkForUpdates(UpdateCallback callback) {
        new Thread(() -> {
            try {
                callback.onProgress("Checking for updates...");
                URL url = new URL(BASE_URL + "update_manifest.json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    callback.onFailure("Failed to connect to update server.");
                    return;
                }

                InputStream is = conn.getInputStream();
                Manifest manifest = new Gson().fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), Manifest.class);
                is.close();

                if (manifest == null) {
                    callback.onFailure("Invalid update manifest.");
                    return;
                }

                int localVersion = getLocalVersion();
                Log.d(TAG, "Local version: " + localVersion + ", Remote version: " + manifest.version);

                if (manifest.version > localVersion) {
                    callback.onProgress("Downloading updates (" + manifest.version + ")...");
                    downloadAndApplyUpdate(manifest.version, callback);
                } else {
                    callback.onSuccess(false); // No update needed
                }
            } catch (Exception e) {
                Log.e(TAG, "Update check failed", e);
                callback.onFailure("Update check failed: " + e.getLocalizedMessage());
            }
        }).start();
    }

    private void downloadAndApplyUpdate(int remoteVersion, UpdateCallback callback) {
        File tempZip = new File(context.getCacheDir(), "duet_updates.zip");
        if (tempZip.exists()) {
            tempZip.delete();
        }

        try {
            URL url = new URL(BASE_URL + "duet_updates.zip");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                callback.onFailure("Failed to download update package.");
                return;
            }

            try (InputStream is = new BufferedInputStream(conn.getInputStream());
                 FileOutputStream fos = new FileOutputStream(tempZip)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }

            callback.onProgress("Applying updates...");
            unzip(tempZip, context.getFilesDir());

            // Save new local version
            saveLocalVersion(remoteVersion);
            callback.onSuccess(true);
        } catch (Exception e) {
            Log.e(TAG, "Failed to apply updates", e);
            callback.onFailure("Failed to apply updates: " + e.getLocalizedMessage());
        } finally {
            if (tempZip.exists()) {
                tempZip.delete();
            }
        }
    }

    private void unzip(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                File filePath = new File(destDir, entry.getName());
                if (!entry.isDirectory()) {
                    File parent = filePath.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zipIn.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                } else {
                    filePath.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }
}
