package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity {

    public static class CalendarEvent {
        public String title;
        public String startDate;
        public String endDate;
        public int duration;

        public CalendarEvent(String title, String startDate, String endDate, int duration) {
            this.title = title;
            this.startDate = startDate;
            this.endDate = endDate;
            this.duration = duration;
        }
    }

    private final List<CalendarEvent> allEvents = new ArrayList<>();
    private final List<CalendarEvent> filteredEvents = new ArrayList<>();
    private final List<CalendarDayAdapter.CalendarDay> dayList = new ArrayList<>();

    private Calendar currentDisplayCalendar;
    private int selectedDay = 1;

    private TextView tvMonthYear;
    private CalendarDayAdapter dayAdapter;
    private CalendarEventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        loadCalendarEvents();

        currentDisplayCalendar = Calendar.getInstance();
        selectedDay = currentDisplayCalendar.get(Calendar.DAY_OF_MONTH);
        currentDisplayCalendar.set(Calendar.DAY_OF_MONTH, 1);

        tvMonthYear = findViewById(R.id.tvMonthYear);
        ImageButton btnPrevMonth = findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = findViewById(R.id.btnNextMonth);
        RecyclerView rvCalendarDays = findViewById(R.id.rvCalendarDays);
        RecyclerView rvCalendarEvents = findViewById(R.id.rvCalendarEvents);

        rvCalendarDays.setLayoutManager(new GridLayoutManager(this, 7));
        dayAdapter = new CalendarDayAdapter(dayList, day -> {
            selectedDay = day;
            updateCalendarGrid();
        });
        rvCalendarDays.setAdapter(dayAdapter);

        rvCalendarEvents.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new CalendarEventAdapter(filteredEvents);
        rvCalendarEvents.setAdapter(eventAdapter);

        btnPrevMonth.setOnClickListener(v -> {
            currentDisplayCalendar.add(Calendar.MONTH, -1);
            selectedDay = 1;
            updateCalendarState();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentDisplayCalendar.add(Calendar.MONTH, 1);
            selectedDay = 1;
            updateCalendarState();
        });

        updateCalendarState();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_calendar);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_menu) {
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                return true;
            }
            return true;
        });

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        LocaleHelper.styleAppBar(this, toolbar, "#444A72", "#444A72");
    }

    private void loadCalendarEvents() {
        try {
            InputStream is = getAssets().open("calender/academic_calendar.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.optJSONObject(i);
                if (obj != null) {
                    allEvents.add(new CalendarEvent(
                        obj.optString("title"),
                        obj.optString("startDate"),
                        obj.optString("endDate"),
                        obj.optInt("duration")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCalendarState() {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(monthYearFormat.format(currentDisplayCalendar.getTime()));

        filteredEvents.clear();
        int year = currentDisplayCalendar.get(Calendar.YEAR);
        int month = currentDisplayCalendar.get(Calendar.MONTH);
        
        String monthPattern = String.format(Locale.US, "-%02d-", month + 1);
        String yearPattern = String.format(Locale.US, "%04d-", year);

        for (CalendarEvent event : allEvents) {
            if (event.startDate.startsWith(yearPattern) && event.startDate.contains(monthPattern)) {
                filteredEvents.add(event);
            }
        }
        eventAdapter.notifyDataSetChanged();

        updateCalendarGrid();
    }

    private void updateCalendarGrid() {
        dayList.clear();

        Calendar tempCal = (Calendar) currentDisplayCalendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
        int offset = firstDayOfWeek - 1;

        int maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < offset; i++) {
            dayList.add(new CalendarDayAdapter.CalendarDay(0, false, false));
        }

        int year = currentDisplayCalendar.get(Calendar.YEAR);
        int month = currentDisplayCalendar.get(Calendar.MONTH);

        for (int day = 1; day <= maxDays; day++) {
            boolean isSelected = (day == selectedDay);
            boolean isHoliday = checkIsHoliday(year, month, day);
            dayList.add(new CalendarDayAdapter.CalendarDay(day, isSelected, isHoliday));
        }

        dayAdapter.notifyDataSetChanged();
    }

    private boolean checkIsHoliday(int year, int month, int day) {
        String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        for (CalendarEvent event : allEvents) {
            if (dateStr.compareTo(event.startDate) >= 0 && dateStr.compareTo(event.endDate) <= 0) {
                return true;
            }
        }
        return false;
    }
}
