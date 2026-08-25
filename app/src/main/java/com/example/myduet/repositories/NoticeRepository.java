package com.example.myduet.repositories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.db.AppDatabase;
import com.example.myduet.db.NoticeDao;
import com.example.myduet.models.NoticeEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoticeRepository {
    private static final String TAG = "NoticeRepository";
    
    private static final Map<String, String> CATEGORY_URLS = new HashMap<String, String>() {{
        put("Academic Notices", "https://duet.ac.bd/notice/academic-notices");
        put("Admission Notices", "https://duet.ac.bd/notice/admission-notices");
        put("Career Notices", "https://duet.ac.bd/notice/career-notices");
        put("NOC/GO Notices", "https://duet.ac.bd/notice/nocgo-notices");
        put("Tender Notices", "https://duet.ac.bd/notice/tender-notices");
        put("Others", "https://duet.ac.bd/notice/others");
    }};

    private static final List<String> FORBIDDEN_TITLES = Arrays.asList(
            "All Notices", "Academic Notices", "Admission Notices", 
            "Career Notices", "NOC/GO Notices", "Tender Notices", "Others",
            "Read More", "View All", "Download", "Home", "Notice"
    );

    private final NoticeDao noticeDao;
    private final LiveData<List<NoticeEntity>> cachedNotices;
    private final Context context;

    public NoticeRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(context);
        this.noticeDao = db.noticeDao();
        this.cachedNotices = noticeDao.getAllNoticesLiveData();
    }

    public LiveData<List<NoticeEntity>> getCachedNotices() {
        return cachedNotices;
    }

    public List<NoticeEntity> getCachedNoticesSync() {
        return noticeDao.getAllNoticesSync();
    }

    public long getLastSyncTime() {
        return context.getSharedPreferences("NoticePrefs", Context.MODE_PRIVATE)
                .getLong("last_sync_time", 0);
    }

    /**
     * Performs a synchronous background synchronization.
     * @return true if sync succeeded and data was fetched/written, false otherwise.
     */
    public boolean syncNoticesSync() {
        if (!isNetworkAvailable()) {
            Log.d(TAG, "Sync failed: network unavailable");
            return false;
        }

        List<NoticeEntity> allFetched = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (Map.Entry<String, String> entry : CATEGORY_URLS.entrySet()) {
            String categoryName = entry.getKey();
            String url = entry.getValue();

            try {
                Log.d(TAG, "Syncing category: " + categoryName);
                Document doc = Jsoup.connect(url)
                        .timeout(20000)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .get();

                Elements rows = doc.select(".all-notice-content .row, article, .post-item, .notice-item");

                int index = 0;
                for (Element row : rows) {
                    try {
                        Element link = row.select("h6 a").first();
                        if (link == null) {
                            link = row.select("a[href*=/notice/]").first();
                        }
                        if (link == null) continue;

                        String title = link.text().trim();
                        String noticeUrl = link.attr("abs:href").trim();

                        if (title.length() < 10 || isForbidden(title)) continue;

                        String date = extractDate(row);
                        String description = row.select(".notice-excerpt, .post-excerpt, p, .description").text().trim();

                        if (description.isEmpty() || description.length() < 10) {
                            description = "Announcement regarding " + title + ". Tap to read the full document on the official DUET website.";
                        }

                        description = formatDescription(description);
                        String id = generateNoticeId(noticeUrl);

                        allFetched.add(new NoticeEntity(
                                id,
                                title,
                                description,
                                noticeUrl, // pdfUrl defaults to noticeUrl page
                                date,
                                categoryName,
                                "", // thumbnail
                                now,
                                noticeUrl,
                                index++
                        ));
                    } catch (Exception e) {
                        Log.e(TAG, "Row parsing error in category " + categoryName, e);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Network connection error for category " + categoryName, e);
                return false;
            }
        }

        if (!allFetched.isEmpty()) {
            List<NoticeEntity> uniqueNotices = deduplicate(allFetched);
            
            // Check for new notices to send notification
            List<String> existingIds = noticeDao.getAllNoticeIdsSync();
            boolean isFirstLoad = (existingIds == null || existingIds.isEmpty());
            
            List<NoticeEntity> newNotices = new ArrayList<>();
            for (NoticeEntity fetched : uniqueNotices) {
                if (existingIds == null || !existingIds.contains(fetched.getId())) {
                    newNotices.add(fetched);
                }
            }

            noticeDao.insertNotices(uniqueNotices);
            
            context.getSharedPreferences("NoticePrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("last_sync_time", now)
                    .apply();

            // Send notification if enabled and it's not the first load
            android.content.SharedPreferences prefs = context.getSharedPreferences("MyDUET_Prefs", Context.MODE_PRIVATE);
            boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);
            if (notificationsEnabled && !isFirstLoad && !newNotices.isEmpty()) {
                sendNoticeNotification(newNotices);
            }
            
            return true;
        }

        return false;
    }

    /**
     * Performs an asynchronous background synchronization.
     */
    public LiveData<Boolean> syncNoticesAsync() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        new Thread(() -> {
            boolean success = syncNoticesSync();
            result.postValue(success);
        }).start();
        return result;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
    }

    private String generateNoticeId(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return String.valueOf(url.hashCode());
        }
    }

    private String formatDescription(String text) {
        String cleaned = text.replaceAll("<[^>]*>", "").trim();
        cleaned = cleaned.replaceAll("\\s+", " ");
        if (cleaned.length() > 180) {
            return cleaned.substring(0, 177) + "...";
        }
        return cleaned;
    }

    private String extractDate(Element element) {
        String day = element.select(".day").text().trim();
        String month = element.select(".month").text().trim();
        String year = element.select(".year").text().trim();

        if (!day.isEmpty() && !month.isEmpty() && !year.isEmpty()) {
            return day + " " + month + " " + year;
        }

        Pattern p = Pattern.compile("(\\d{1,2})\\s+([A-Z]{3,})\\s+(\\d{4})");
        Matcher m = p.matcher(element.text());
        if (m.find()) return m.group(0);

        return "01 JAN 2000";
    }

    private boolean isForbidden(String title) {
        for (String forbidden : FORBIDDEN_TITLES) {
            if (title.equalsIgnoreCase(forbidden)) return true;
        }
        return false;
    }

    private List<NoticeEntity> deduplicate(List<NoticeEntity> list) {
        List<NoticeEntity> result = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        for (NoticeEntity n : list) {
            if (!seen.contains(n.getSourceUrl())) {
                result.add(n);
                seen.add(n.getSourceUrl());
            }
        }
        return result;
    }

    private void sendNoticeNotification(List<NoticeEntity> newNotices) {
        if (newNotices == null || newNotices.isEmpty()) return;
        try {
            String channelId = "MyDUET_Notices";
            String channelName = "DUET Notice Alerts";
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) 
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationChannel channel = new android.app.NotificationChannel(
                        channelId, channelName, android.app.NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationManager.createNotificationChannel(channel);
            }

            android.content.Intent intent = new android.content.Intent(context, com.example.myduet.NoticeActivity.class);
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                    context, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );

            String title = "New Notice Published";
            String text;
            if (newNotices.size() == 1) {
                text = newNotices.get(0).getTitle();
            } else {
                text = newNotices.size() + " new notices published: " + newNotices.get(0).getTitle() + " and others.";
            }

            androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(com.example.myduet.R.drawable.duet_official_logo)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(new androidx.core.app.NotificationCompat.BigTextStyle().bigText(text))
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(101, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Failed to send notification", e);
        }
    }
}