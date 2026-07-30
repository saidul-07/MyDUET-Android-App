package com.example.myduet.repositories;

import android.os.AsyncTask;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.models.Notice;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoticeRepository {

    private static final String TAG = "NoticeRepository";
    
    private static final Map<String, String> CATEGORY_URLS = new HashMap<String, String>() {{
        put("All Notices", "https://duet.ac.bd/notice/all-notices");
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

    public LiveData<List<Notice>> getNotices() {
        MutableLiveData<List<Notice>> data = new MutableLiveData<>();
        new FetchAllNoticesTask(data).execute();
        return data;
    }

    private static class FetchAllNoticesTask extends AsyncTask<Void, Void, List<Notice>> {
        private MutableLiveData<List<Notice>> liveData;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

        FetchAllNoticesTask(MutableLiveData<List<Notice>> liveData) {
            this.liveData = liveData;
        }

        @Override
        protected List<Notice> doInBackground(Void... voids) {
            List<Notice> allNotices = new ArrayList<>();
            
            for (Map.Entry<String, String> entry : CATEGORY_URLS.entrySet()) {
                String categoryName = entry.getKey();
                String url = entry.getValue();
                
                if (categoryName.equals("All Notices")) continue;

                try {
                    Log.d(TAG, "Fetching Official: " + categoryName);
                    Document doc = Jsoup.connect(url)
                            .timeout(20000)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                            .get();
                    
                    Elements rows = doc.select(".all-notice-content .row, article, .post-item, .notice-item");
                    
                    int index = 0;
                    for (Element row : rows) {
                        try {
                            Element link = row.select("a[href*=/notice/]").first();
                            if (link == null) continue;
                            
                            String title = link.text().trim();
                            String noticeUrl = link.attr("abs:href");

                            if (title.length() < 10 || isForbidden(title)) continue;

                            String date = extractDate(row);

                            // DYNAMIC DESCRIPTION EXTRACTION
                            // We look for any text content inside the notice row that isn't the title or date
                            String description = row.select(".notice-excerpt, .post-excerpt, p, .description").text().trim();
                            
                            // If the list page doesn't have an excerpt, we use a snippet of the title as a baseline
                            // Note: To avoid excessive network calls in an AsyncTask, we prioritize extracting
                            // available text from the list row first.
                            if (description.isEmpty() || description.length() < 10) {
                                description = "Announcement regarding " + title + ". Tap to read the full document on the official DUET website.";
                            }

                            // Clean and Format Description (150-200 chars max)
                            description = formatDescription(description);

                            allNotices.add(new Notice(
                                String.valueOf(allNotices.size() + 1),
                                categoryName,
                                title,
                                description,
                                date,
                                noticeUrl,
                                index++
                            ));
                        } catch (Exception e) {
                            Log.e(TAG, "Row parsing error", e);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Failed to fetch category: " + categoryName, e);
                }
            }

            List<Notice> uniqueNotices = deduplicate(allNotices);
            sortNoticesHierarchically(uniqueNotices);
            return uniqueNotices;
        }

        private String formatDescription(String text) {
            // Remove HTML-like strings if any, clean whitespace
            String cleaned = text.replaceAll("<[^>]*>", "").trim();
            cleaned = cleaned.replaceAll("\\s+", " ");
            
            // Limit to ~180 characters for a good 2-line preview
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

        private void sortNoticesHierarchically(List<Notice> list) {
            Collections.sort(list, (n1, n2) -> {
                try {
                    Date d1 = dateFormat.parse(n1.getDate());
                    Date d2 = dateFormat.parse(n2.getDate());
                    
                    int dateCompare = d2.compareTo(d1);
                    if (dateCompare != 0) return dateCompare;
                    return Integer.compare(n1.getOriginalIndex(), n2.getOriginalIndex());
                } catch (ParseException e) {
                    return 0;
                }
            });
        }

        private boolean isForbidden(String title) {
            for (String forbidden : FORBIDDEN_TITLES) {
                if (title.equalsIgnoreCase(forbidden)) return true;
            }
            return false;
        }

        private List<Notice> deduplicate(List<Notice> list) {
            List<Notice> result = new ArrayList<>();
            List<String> seen = new ArrayList<>();
            for (Notice n : list) {
                if (!seen.contains(n.getUrl())) {
                    result.add(n);
                    seen.add(n.getUrl());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Notice> notices) {
            liveData.setValue(notices);
        }
    }
}