package com.example.myduet.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.models.Event;
import com.example.myduet.models.User;
import com.example.myduet.repositories.EventRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventViewModel extends AndroidViewModel {

    private final EventRepository repository;
    private final MutableLiveData<List<Event>> filteredEvents = new MutableLiveData<>();
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>(null);
    private List<Event> allEvents = new ArrayList<>();
    
    private String currentCategory = "All"; // All, Upcoming, Ongoing, University, Clubs
    private String currentSearchQuery = "";

    public EventViewModel(@NonNull Application application) {
        super(application);
        this.repository = new EventRepository(application.getApplicationContext());
        loadEvents();
    }

    public void loadEvents() {
        repository.getAllEvents().observeForever(events -> {
            if (events != null) {
                this.allEvents = events;
                applyFilter();
            }
        });
    }

    public LiveData<List<Event>> getEvents() {
        return filteredEvents;
    }

    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    public void setCategory(String category) {
        this.currentCategory = category;
        applyFilter();
    }

    public void setSearchQuery(String query) {
        this.currentSearchQuery = query.toLowerCase().trim();
        applyFilter();
    }

    public LiveData<List<Event>> getMyEvents(String userId) {
        return repository.getEventsByAuthor(userId);
    }

    public boolean login(String userId, String password) {
        boolean authenticated = repository.authenticate(userId, password);
        if (authenticated) {
            User user = repository.getUser(userId);
            loggedInUser.setValue(user);
            return true;
        }
        return false;
    }

    public void logout() {
        loggedInUser.setValue(null);
    }

    public void createEvent(Event event, Runnable callback) {
        repository.insertEvent(event, () -> {
            loadEvents();
            if (callback != null) callback.run();
        });
    }

    public void updateEvent(Event event, Runnable callback) {
        repository.updateEvent(event, () -> {
            loadEvents();
            if (callback != null) callback.run();
        });
    }

    public void cancelEvent(int eventId, Runnable callback) {
        repository.cancelEvent(eventId, () -> {
            loadEvents();
            if (callback != null) callback.run();
        });
    }

    public void deleteEvent(int eventId, Runnable callback) {
        repository.deleteEvent(eventId, () -> {
            loadEvents();
            if (callback != null) callback.run();
        });
    }

    private void applyFilter() {
        List<Event> result = new ArrayList<>();
        for (Event event : allEvents) {
            String status = calculateEventStatus(event);

            // Filter by Category
            boolean matchesCategory = false;
            switch (currentCategory) {
                case "All":
                    matchesCategory = true;
                    break;
                case "Upcoming":
                    matchesCategory = "Upcoming".equals(status);
                    break;
                case "Ongoing":
                    matchesCategory = "Ongoing".equals(status);
                    break;
                case "University":
                    matchesCategory = "University".equalsIgnoreCase(event.getType());
                    break;
                case "Clubs":
                    matchesCategory = "Club".equalsIgnoreCase(event.getType());
                    break;
            }

            // Filter by Search Query
            boolean matchesSearch = currentSearchQuery.isEmpty()
                    || (event.getTitle() != null && event.getTitle().toLowerCase().contains(currentSearchQuery))
                    || (event.getClubName() != null && event.getClubName().toLowerCase().contains(currentSearchQuery))
                    || (event.getOrganizerName() != null && event.getOrganizerName().toLowerCase().contains(currentSearchQuery))
                    || (event.getVenue() != null && event.getVenue().toLowerCase().contains(currentSearchQuery));

            if (matchesCategory && matchesSearch) {
                result.add(event);
            }
        }
        filteredEvents.setValue(result);
    }

    /**
     * Dynamically calculates current status from event date and times.
     */
    public static String calculateEventStatus(Event event) {
        if ("Cancelled".equalsIgnoreCase(event.getStatus())) {
            return "Cancelled";
        }

        Date now = new Date();
        Date start = parseDateTime(event.getEventDate(), event.getStartTime());
        Date end = parseDateTime(event.getEventDate(), event.getEndTime());

        if (start != null && now.before(start)) {
            return "Upcoming";
        } else if (start != null && end != null && now.after(start) && now.before(end)) {
            return "Ongoing";
        } else if (end != null && now.after(end)) {
            return "Completed";
        }
        return "Upcoming"; // Fallback
    }

    /**
     * Determines whether registration is open, closed, or not required.
     */
    public static boolean isRegistrationClosed(Event event) {
        if (!event.isRegistrationRequired()) {
            return false;
        }
        String deadlineStr = event.getRegistrationDeadline();
        if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
            return false;
        }

        Date now = new Date();
        Date deadline = parseDeadline(deadlineStr);
        return deadline != null && now.after(deadline);
    }

    private static Date parseDateTime(String dateStr, String timeStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        if (timeStr == null || timeStr.trim().isEmpty()) timeStr = "00:00";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            return sdf.parse(dateStr.trim() + " " + timeStr.trim());
        } catch (Exception e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                return sdf.parse(dateStr.trim());
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private static Date parseDeadline(String deadlineStr) {
        if (deadlineStr == null || deadlineStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            return sdf.parse(deadlineStr.trim());
        } catch (Exception e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                return sdf.parse(deadlineStr.trim());
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
