package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.view.ViewGroup;
import com.example.myduet.repositories.EmergencyRepository;
import com.example.myduet.models.EmergencyContact;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private androidx.viewpager2.widget.ViewPager2 viewPagerCarousel;
    private android.widget.LinearLayout dotsContainer;
    private final android.os.Handler sliderHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable sliderRunnable;

    private final List<SearchItem> databaseIndex = java.util.Collections.synchronizedList(new ArrayList<>());
    private final List<AdmissionResult> admissionResultsList = java.util.Collections.synchronizedList(new ArrayList<>());
    private final List<SeatPlan> seatPlansList = java.util.Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#444A72"));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }
        initializeSearchIndex();
        setupNoticeSyncWork();
        setupDataSyncWork();

        viewPagerCarousel = findViewById(R.id.viewPagerCarousel);
        dotsContainer = findViewById(R.id.dotsContainer);
        CarouselAdapter carouselAdapter = new CarouselAdapter();
        viewPagerCarousel.setAdapter(carouselAdapter);

        int dotCount = carouselAdapter.getItemCount();
        android.view.View[] dots = new android.view.View[dotCount];
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
            (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
            (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics())
        );
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < dotCount; i++) {
            dots[i] = new android.view.View(this);
            dots[i].setLayoutParams(params);
            dots[i].setBackgroundResource(R.drawable.bg_circle);
            dots[i].setAlpha(i == 0 ? 1.0f : 0.4f);
            dots[i].setScaleX(i == 0 ? 1.2f : 1.0f);
            dots[i].setScaleY(i == 0 ? 1.2f : 1.0f);
            dots[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
            dotsContainer.addView(dots[i]);
        }

        viewPagerCarousel.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotCount; i++) {
                    dots[i].setAlpha(i == position ? 1.0f : 0.4f);
                    dots[i].setScaleX(i == position ? 1.2f : 1.0f);
                    dots[i].setScaleY(i == position ? 1.2f : 1.0f);
                }
            }
        });

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPagerCarousel.getCurrentItem();
                int nextItem = (currentItem + 1) % dotCount;
                viewPagerCarousel.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000);
            }
        };

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_calendar) {
                Intent intent = new Intent(this, CalenderActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_menu) {
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                return true;
            }
            return true;
        });
        
        findViewById(R.id.cardNotices).setOnClickListener(v -> {
            Intent intent = new Intent(this, NoticeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardEmergency).setOnClickListener(v -> {
            Intent intent = new Intent(this, EmergencyActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardRoutine).setOnClickListener(v -> {
            Intent intent = new Intent(this, RoutineHomeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardAdmission).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdmissionActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardTeachers).setOnClickListener(v -> {
            Intent intent = new Intent(this, TeachersActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardEvents).setOnClickListener(v -> {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardServices).setOnClickListener(v -> {
            Intent intent = new Intent(this, ServicesActivity.class);
            startActivity(intent);
        });

        // Notification Handler
        findViewById(R.id.btnNotification).setOnClickListener(v -> showNotificationsDialog());
    }



    private void showSearchDialog() {
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        final android.widget.LinearLayout rootLayout = new android.widget.LinearLayout(this);
        rootLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        rootLayout.setPadding(padding, padding, padding, padding);

        final TextInputLayout tilSearch = new TextInputLayout(this);
        tilSearch.setHint("Search MyDUET...");
        tilSearch.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        tilSearch.setStartIconDrawable(android.R.drawable.ic_menu_search);
        
        final TextInputEditText etSearch = new TextInputEditText(tilSearch.getContext());
        etSearch.setSingleLine(true);
        etSearch.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH);
        tilSearch.addView(etSearch);
        rootLayout.addView(tilSearch);

        final android.widget.LinearLayout resultsContainer = new android.widget.LinearLayout(this);
        resultsContainer.setOrientation(android.widget.LinearLayout.VERTICAL);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        resultsContainer.setLayoutParams(params);
        
        rootLayout.addView(resultsContainer);
        scrollView.addView(rootLayout);

        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(scrollView)
            .create();

        class Renderer {
            void render(List<SearchItem> items) {
                resultsContainer.removeAllViews();
                if (items.isEmpty()) {
                    android.widget.TextView tvNoResults = new android.widget.TextView(MainActivity.this);
                    tvNoResults.setText("No matches found.");
                    tvNoResults.setGravity(android.view.Gravity.CENTER);
                    tvNoResults.setPadding(0, 32, 0, 32);
                    tvNoResults.setTextColor(0xFF757575);
                    resultsContainer.addView(tvNoResults);
                    return;
                }

                int count = 0;
                for (final SearchItem item : items) {
                    com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(MainActivity.this);
                    card.setClickable(true);
                    card.setFocusable(true);
                    card.setUseCompatPadding(true);
                    card.setRadius(12 * getResources().getDisplayMetrics().density);
                    card.setCardElevation(2 * getResources().getDisplayMetrics().density);
                    card.setStrokeColor(0xFFEEEEEE);
                    card.setStrokeWidth(1);

                    android.widget.LinearLayout cardContent = new android.widget.LinearLayout(MainActivity.this);
                    cardContent.setOrientation(android.widget.LinearLayout.VERTICAL);
                    int cardPadding = (int) (12 * getResources().getDisplayMetrics().density);
                    cardContent.setPadding(cardPadding, cardPadding, cardPadding, cardPadding);
                    cardContent.setBackgroundResource(android.R.drawable.list_selector_background);

                    android.widget.TextView tvBadge = new android.widget.TextView(MainActivity.this);
                    tvBadge.setText(item.category);
                    tvBadge.setTextSize(10);
                    tvBadge.setAllCaps(true);
                    tvBadge.setTextColor(0xFF007ACC);
                    cardContent.addView(tvBadge);

                    android.widget.TextView tvTitle = new android.widget.TextView(MainActivity.this);
                    tvTitle.setText(item.title);
                    tvTitle.setTextSize(16);
                    tvTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                    tvTitle.setTextColor(0xFF212121);
                    android.widget.LinearLayout.LayoutParams titleParams = new android.widget.LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    titleParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
                    tvTitle.setLayoutParams(titleParams);
                    cardContent.addView(tvTitle);

                    android.widget.TextView tvDesc = new android.widget.TextView(MainActivity.this);
                    tvDesc.setText(item.description);
                    tvDesc.setTextSize(13);
                    tvDesc.setTextColor(0xFF757575);
                    android.widget.LinearLayout.LayoutParams descParams = new android.widget.LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    descParams.topMargin = (int) (2 * getResources().getDisplayMetrics().density);
                    tvDesc.setLayoutParams(descParams);
                    cardContent.addView(tvDesc);

                    card.addView(cardContent);
                    final Runnable originalAction = item.action;
                    card.setOnClickListener(v -> {
                        if (originalAction != null) {
                            originalAction.run();
                        }
                        dialog.dismiss();
                    });

                    resultsContainer.addView(card);
                    count++;
                    if (count >= 40) break;
                }
            }
        }
        final Renderer renderer = new Renderer();

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                List<SearchItem> matches = new ArrayList<>();

                if (query.isEmpty()) {
                    resultsContainer.removeAllViews();
                    return;
                }

                // 1. Search in-memory general index
                for (SearchItem item : databaseIndex) {
                    if (item.title.toLowerCase().contains(query) || 
                        item.description.toLowerCase().contains(query) || 
                        item.category.toLowerCase().contains(query) || 
                        item.keywords.contains(query)) {
                        matches.add(item);
                    }
                }

                // 2. Search Admission Seat Plans by Roll Number
                if (query.matches("\\d+")) {
                    int roll = Integer.parseInt(query);
                    for (SeatPlan sp : seatPlansList) {
                        if (roll == sp.roll) {
                            String title = (sp.name != null && !sp.name.trim().isEmpty()) ? "Seat Plan: " + sp.name : "Seat Plan: Room " + sp.room + " (" + sp.building + ")";
                            String desc = "Roll " + roll + " -> Room " + sp.room + " (" + sp.building + ")";
                            
                            matches.add(new SearchItem(
                                title,
                                desc,
                                "SEAT PLAN",
                                "",
                                () -> {
                                    StringBuilder msg = new StringBuilder();
                                    if (sp.name != null && !sp.name.trim().isEmpty()) {
                                        msg.append("Name: ").append(sp.name).append("\n");
                                    }
                                    if (sp.fatherName != null && !sp.fatherName.trim().isEmpty()) {
                                        msg.append("Father's Name: ").append(sp.fatherName).append("\n\n");
                                    }
                                    msg.append("Your Roll: ").append(roll).append("\n");
                                    msg.append("Department: ").append(sp.department).append("\n\n");
                                    msg.append("Building: ").append(sp.building).append("\n");
                                    msg.append("Room: ").append(sp.room).append("\n\n");
                                    msg.append("Exam Date: ").append(sp.date).append("\n");
                                    msg.append("Shift: ").append(sp.shift);

                                    new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Seat Plan Details")
                                        .setMessage(msg.toString())
                                        .setPositiveButton("Close", null)
                                        .show();
                                }
                            ));
                        }
                    }
                }

                // 3. Search Admission Candidates & Results (if query length >= 3)
                if (query.length() >= 3) {
                    int resultCount = 0;
                    for (AdmissionResult res : admissionResultsList) {
                        if (String.valueOf(res.roll).contains(query) || res.name.toLowerCase().contains(query)) {
                            String title = "Result: " + res.name + " (" + res.roll + ")";
                            String desc = "Dept: " + res.department + " • Status: " + res.status;
                            matches.add(new SearchItem(title, desc, "ADMISSION RESULT", "", () -> {
                                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Admission Result Details")
                                    .setMessage("Candidate: " + res.name + "\nRoll: " + res.roll + "\nFather's Name: " + res.fatherName + "\n\nDepartment: " + res.department + "\nStatus: " + res.status)
                                    .setPositiveButton("Close", null)
                                    .show();
                            }));
                            resultCount++;
                            if (resultCount >= 25) break;
                        }
                    }
                }

                renderer.render(matches);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etSearch.setText("");
        dialog.show();
    }

    private void initializeSearchIndex() {
        new Thread(() -> {
            databaseIndex.add(new SearchItem("Class Routine", "View academic class schedules", "ACADEMIC", "routine schedule class exam timeline lecture", () -> {
                startActivity(new Intent(MainActivity.this, RoutineHomeActivity.class));
            }));

            // Index Events from SQLite Database
            try {
                com.example.myduet.storage.EventDbHelper eventDb = new com.example.myduet.storage.EventDbHelper(MainActivity.this);
                android.database.sqlite.SQLiteDatabase db = eventDb.getReadableDatabase();
                android.database.Cursor cursor = db.rawQuery("SELECT * FROM " + com.example.myduet.storage.EventDbHelper.TABLE_EVENTS, null);
                if (cursor.moveToFirst()) {
                    do {
                        final String title = cursor.getString(cursor.getColumnIndexOrThrow(com.example.myduet.storage.EventDbHelper.KEY_TITLE));
                        final String desc = cursor.getString(cursor.getColumnIndexOrThrow(com.example.myduet.storage.EventDbHelper.KEY_DESCRIPTION));
                        final String organizer = cursor.getString(cursor.getColumnIndexOrThrow(com.example.myduet.storage.EventDbHelper.KEY_ORGANIZER_NAME));
                        final String venue = cursor.getString(cursor.getColumnIndexOrThrow(com.example.myduet.storage.EventDbHelper.KEY_VENUE));
                        
                        String keywords = title + " " + organizer + " " + venue + " event campus university club";
                        
                        databaseIndex.add(new SearchItem(
                            title,
                            "Organizer: " + organizer + " • Venue: " + venue,
                            "EVENT",
                            keywords,
                            () -> {
                                Intent intent = new Intent(MainActivity.this, com.example.myduet.EventsActivity.class);
                                startActivity(intent);
                            }
                        ));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            databaseIndex.add(new SearchItem("Admission Notice", "DUET admission circulars and announcements", "ADMISSION", "admission notice exam result 2024 2025 circular seat plan", () -> {
                startActivity(new Intent(MainActivity.this, AdmissionActivity.class));
            }));
            databaseIndex.add(new SearchItem("Admission Result", "Search admission results by Roll number", "ADMISSION", "result admission merit selection marks", () -> {
                startActivity(new Intent(MainActivity.this, AdmissionActivity.class));
            }));
            databaseIndex.add(new SearchItem("Academic Notices", "Latest announcements from DUET authorities", "NOTICES", "notice announcements board news updates", () -> {
                startActivity(new Intent(MainActivity.this, NoticeActivity.class));
            }));
            databaseIndex.add(new SearchItem("Library Catalog", "Browse books and resources in central library", "LIBRARY", "library book search borrow read", () -> {
                startActivity(new Intent(MainActivity.this, LibraryHomeActivity.class));
            }));
            databaseIndex.add(new SearchItem("Emergency Helpline", "Campus security, medical, and proctor services", "EMERGENCY", "emergency help security police transport ict ambulance medical", () -> {
                startActivity(new Intent(MainActivity.this, EmergencyActivity.class));
            }));

            class ContactLauncher {
                void launch(EmergencyContact contact) {
                    Intent intent = new Intent(MainActivity.this, ContactDetailsActivity.class);
                    intent.putExtra("name", contact.getName());
                    intent.putExtra("person", contact.getPersonName());
                    intent.putExtra("assistant", contact.getAssistantName());
                    intent.putExtra("phone", contact.getPhone());
                    intent.putExtra("email", contact.getEmail());
                    intent.putExtra("location", contact.getLocation());
                    intent.putExtra("hours", contact.getHours());
                    startActivity(intent);
                }
            }
            final ContactLauncher launcher = new ContactLauncher();

            EmergencyRepository repo = new EmergencyRepository();
            for (EmergencyContact contact : repo.getHallContacts()) {
                databaseIndex.add(new SearchItem(
                    contact.getName() + " Info", 
                    "Provost: " + contact.getPersonName(), 
                    "STAFF & HALLS", 
                    "hall provost stah " + contact.getName() + " " + contact.getPersonName() + " " + contact.getEmail(),
                    () -> launcher.launch(contact)
                ));
            }
            for (EmergencyContact contact : repo.getMedicalContacts()) {
                databaseIndex.add(new SearchItem(
                    contact.getName(), 
                    contact.getHours(), 
                    "MEDICAL", 
                    "medical center hospital ambulance doctor call health " + contact.getName() + " " + contact.getPersonName(),
                    () -> launcher.launch(contact)
                ));
            }
            for (EmergencyContact contact : repo.getSecurityContacts()) {
                databaseIndex.add(new SearchItem(
                    contact.getName(), 
                    contact.getHours(), 
                    "SECURITY", 
                    "security gate safety control police guard " + contact.getName() + " " + contact.getPersonName(),
                    () -> launcher.launch(contact)
                ));
            }
            for (EmergencyContact contact : repo.getProctorContacts()) {
                databaseIndex.add(new SearchItem(
                    contact.getName(), 
                    "Student discipline office", 
                    "PROCTOR", 
                    "proctor administration rules discipline campus " + contact.getName() + " " + contact.getPersonName(),
                    () -> launcher.launch(contact)
                ));
            }
            for (EmergencyContact contact : repo.getTransportContacts()) {
                databaseIndex.add(new SearchItem(
                    contact.getName(), 
                    contact.getHours(), 
                    "TRANSPORT", 
                    "bus transport driver schedule garage vehicle " + contact.getName() + " " + contact.getPersonName(),
                    () -> launcher.launch(contact)
                ));
            }
            for (EmergencyContact contact : repo.getIctContacts()) {
                databaseIndex.add(new SearchItem(
                    contact.getName(), 
                    "Wifi, Network, and ICT Support", 
                    "ICT CELL", 
                    "ict network wifi internet support router computer website mail " + contact.getName() + " " + contact.getPersonName(),
                    () -> launcher.launch(contact)
                ));
            }

            indexRoutines(databaseIndex);
            loadSeatPlans();
            loadAdmissionResults();

        }).start();
    }

    private void indexRoutines(List<SearchItem> database) {
        try {
            String[] depts = getAssets().list("routines");
            if (depts != null) {
                for (String dept : depts) {
                    String[] files = getAssets().list("routines/" + dept);
                    if (files != null) {
                        for (String file : files) {
                            if (file.endsWith(".json")) {
                                indexRoutineFile("routines/" + dept + "/" + file, database);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indexRoutineFile(String path, List<SearchItem> database) {
        try {
            java.io.InputStream is = getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, "UTF-8");
            org.json.JSONObject obj = new org.json.JSONObject(jsonStr);
            final String dept = obj.optString("department");
            final String year = obj.optString("year");
            final String sec = obj.optString("section");

            org.json.JSONObject days = obj.optJSONObject("days");
            if (days != null) {
                java.util.Iterator<String> keys = days.keys();
                while (keys.hasNext()) {
                    final String day = keys.next();
                    org.json.JSONArray classes = days.optJSONArray(day);
                    if (classes != null) {
                        for (int i = 0; i < classes.length(); i++) {
                            org.json.JSONObject cls = classes.optJSONObject(i);
                            if (cls != null) {
                                final String code = cls.optString("courseCode");
                                final String name = cls.optString("courseName");
                                final String type = cls.optString("type");
                                final String time = cls.optString("time");
                                final String room = cls.optString("room");

                                String title = code + " - " + name;
                                String desc = dept + " " + year + " Sec " + sec + " (" + day + ", " + time + " in " + room + ")";
                                String keywords = code + " " + name + " " + dept + " " + year + " " + sec + " " + day + " " + room + " routine class schedule";
                                
                                database.add(new SearchItem(title, desc, "ROUTINE", keywords, () -> {
                                    new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Class Routine Details")
                                        .setMessage("Course: " + name + "\nCode: " + code + "\nType: " + type + "\n\nDepartment: " + dept + "\nYear: " + year + " (Section " + sec + ")" + "\n\nSchedule: " + day + ", " + time + "\nRoom: " + room)
                                        .setPositiveButton("Close", null)
                                        .show();
                                }));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSeatPlans() {
        try {
            java.io.InputStream is;
            java.io.File cacheFile = new java.io.File(getCacheDir(), "latest_seat_plan.json");
            if (cacheFile.exists()) {
                is = new java.io.FileInputStream(cacheFile);
            } else {
                is = getAssets().open("seat_plan/2026/seat_plan_2026.json");
            }
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, "UTF-8");
            org.json.JSONArray arr = new org.json.JSONArray(jsonStr);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.optJSONObject(i);
                if (obj != null) {
                    SeatPlan sp = new SeatPlan();
                    sp.roll = obj.optInt("roll");
                    sp.name = obj.optString("name");
                    sp.fatherName = obj.optString("fatherName");
                    sp.building = obj.optString("building");
                    sp.room = obj.optString("room");
                    sp.department = obj.optString("department");
                    sp.date = obj.optString("examDate");
                    sp.shift = obj.optString("shift");
                    seatPlansList.add(sp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAdmissionResults() {
        try {
            java.io.InputStream is;
            java.io.File cacheFile = new java.io.File(getCacheDir(), "latest_admission_result.json");
            if (cacheFile.exists()) {
                is = new java.io.FileInputStream(cacheFile);
            } else {
                is = getAssets().open("results/2026/admission_result_2026.json");
            }
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, "UTF-8");
            org.json.JSONArray arr = new org.json.JSONArray(jsonStr);
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.optJSONObject(i);
                if (obj != null) {
                    AdmissionResult res = new AdmissionResult();
                    res.roll = obj.optInt("roll");
                    res.name = obj.optString("name");
                    res.fatherName = obj.optString("fatherName");
                    res.department = obj.optString("department");
                    res.status = obj.optString("status");
                    admissionResultsList.add(res);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class SearchItem {
        String title;
        String description;
        String category;
        String keywords;
        Runnable action;

        SearchItem(String title, String description, String category, String keywords, Runnable action) {
            this.title = title;
            this.description = description;
            this.category = category;
            this.keywords = keywords.toLowerCase();
            this.action = action;
        }
    }

    private static class AdmissionResult {
        int roll;
        String name;
        String fatherName;
        String department;
        String status;
    }

    private static class SeatPlan {
        int roll;
        String name;
        String fatherName;
        String building;
        String room;
        String department;
        String date;
        String shift;
    }

    private void showNotificationsDialog() {
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        class NotificationItem {
            String title;
            String message;
            String time;
            Runnable action;

            NotificationItem(String title, String message, String time, Runnable action) {
                this.title = title;
                this.message = message;
                this.time = time;
                this.action = action;
            }
        }

        final List<NotificationItem> notifications = new ArrayList<>();
        notifications.add(new NotificationItem(
            "Academic notice updated",
            "Official circular on DUET academic council decisions and class timetables. Tap to view.",
            "2 hours ago",
            () -> startActivity(new Intent(MainActivity.this, NoticeActivity.class))
        ));
        notifications.add(new NotificationItem(
            "Class Routine Active",
            "Routine for current session is loaded and searchable in the app. Check class timings.",
            "1 day ago",
            () -> startActivity(new Intent(MainActivity.this, RoutineHomeActivity.class))
        ));
        notifications.add(new NotificationItem(
            "Seat Plan Release",
            "Admission seat plans have been parsed. Enter your roll number in search bar to see room allocation.",
            "3 days ago",
            () -> showSearchDialog()
        ));
        notifications.add(new NotificationItem(
            "Welcome to MyDUET",
            "Explore campus resources, library, and routines all in one place.",
            "Just now",
            () -> {}
        ));

        for (NotificationItem item : notifications) {
            com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(this);
            card.setClickable(true);
            card.setFocusable(true);
            card.setUseCompatPadding(true);
            card.setRadius(12 * getResources().getDisplayMetrics().density);
            card.setCardElevation(2 * getResources().getDisplayMetrics().density);
            card.setStrokeColor(0xFFEEEEEE);
            card.setStrokeWidth(1);

            android.widget.LinearLayout content = new android.widget.LinearLayout(this);
            content.setOrientation(android.widget.LinearLayout.VERTICAL);
            int cardPadding = (int) (12 * getResources().getDisplayMetrics().density);
            content.setPadding(cardPadding, cardPadding, cardPadding, cardPadding);
            content.setBackgroundResource(android.R.drawable.list_selector_background);

            android.widget.RelativeLayout titleBar = new android.widget.RelativeLayout(this);
            
            android.widget.TextView tvTitle = new android.widget.TextView(this);
            tvTitle.setText(item.title);
            tvTitle.setTextSize(15);
            tvTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            tvTitle.setTextColor(0xFF212121);
            android.widget.RelativeLayout.LayoutParams titleParams = new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            titleParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_START);
            tvTitle.setLayoutParams(titleParams);
            titleBar.addView(tvTitle);

            android.widget.TextView tvTime = new android.widget.TextView(this);
            tvTime.setText(item.time);
            tvTime.setTextSize(11);
            tvTime.setTextColor(0xFF757575);
            android.widget.RelativeLayout.LayoutParams timeParams = new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            timeParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
            timeParams.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
            tvTime.setLayoutParams(timeParams);
            titleBar.addView(tvTime);

            content.addView(titleBar);

            android.widget.TextView tvMsg = new android.widget.TextView(this);
            tvMsg.setText(item.message);
            tvMsg.setTextSize(13);
            tvMsg.setTextColor(0xFF555555);
            android.widget.LinearLayout.LayoutParams msgParams = new android.widget.LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            msgParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
            tvMsg.setLayoutParams(msgParams);
            content.addView(tvMsg);

            card.addView(content);
            layout.addView(card);
        }

        scrollView.addView(layout);

        final androidx.appcompat.app.AlertDialog notificationDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Recent Notifications")
            .setView(scrollView)
            .setPositiveButton("Close", null)
            .create();


        for (int i = 0; i < layout.getChildCount(); i++) {
            final android.view.View card = layout.getChildAt(i);
            final int index = i;
            card.setOnClickListener(v -> {
                notificationDialog.dismiss();
                notifications.get(index).action.run();
            });
        }

        notificationDialog.show();
    }

    private void setupNoticeSyncWork() {
        try {
            androidx.work.Constraints constraints = new androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build();

            androidx.work.PeriodicWorkRequest syncRequest =
                    new androidx.work.PeriodicWorkRequest.Builder(
                            com.example.myduet.workers.NoticeSyncWorker.class,
                            6, java.util.concurrent.TimeUnit.HOURS)
                            .setConstraints(constraints)
                            .build();

            androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "NoticeSyncWork",
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDataSyncWork() {
        try {
            androidx.work.Constraints constraints = new androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build();

            androidx.work.PeriodicWorkRequest syncRequest =
                    new androidx.work.PeriodicWorkRequest.Builder(
                            com.example.myduet.workers.DataSyncWorker.class,
                            24, java.util.concurrent.TimeUnit.HOURS)
                            .setConstraints(constraints)
                            .build();

            androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "DataSyncWork",
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    static class CarouselAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
        private final int[] images = {
            R.drawable.duet_gate,
            R.drawable.duet_campus,
            R.drawable.duet_towers
        };

        @androidx.annotation.NonNull
        @Override
        public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
            android.widget.ImageView imageView = new android.widget.ImageView(parent.getContext());
            imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
            holder.imageView.setImageResource(images[position]);
        }

        @Override
        public int getItemCount() {
            return images.length;
        }

        static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            android.widget.ImageView imageView;
            ViewHolder(android.widget.ImageView itemView) {
                super(itemView);
                imageView = itemView;
            }
        }
    }
}
