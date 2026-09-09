package com.example.myduet.repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.models.AuthRequest;
import com.example.myduet.models.AuthResponse;
import com.example.myduet.models.Event;
import com.example.myduet.models.Profile;
import com.example.myduet.models.User;
import com.example.myduet.network.SupabaseApiService;
import com.example.myduet.network.SupabaseClient;
import com.example.myduet.network.SupabaseConfig;
import com.example.myduet.network.SupabaseRealtimeClient;
import com.example.myduet.storage.EventDbHelper;
import com.example.myduet.storage.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository implements SupabaseRealtimeClient.OnRealtimeEventListener {

    private static final String TAG = "EventRepository";

    private final Context context;
    private final EventDbHelper dbHelper;
    private final SessionManager sessionManager;
    private final SupabaseApiService apiService;
    private final SupabaseRealtimeClient realtimeClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSyncing = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRealtimeConnected = new MutableLiveData<>(false);

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(String message);
    }

    public EventRepository(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new EventDbHelper(this.context);
        this.sessionManager = new SessionManager(this.context);
        this.apiService = SupabaseClient.getApiService(this.context);
        this.realtimeClient = new SupabaseRealtimeClient();
        this.realtimeClient.setListener(this);
    }

    public LiveData<Boolean> getIsSyncing() {
        return isSyncing;
    }

    public LiveData<Boolean> getIsRealtimeConnected() {
        return isRealtimeConnected;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void startRealtimeSync() {
        realtimeClient.start();
    }

    public void stopRealtimeSync() {
        realtimeClient.stop();
    }

    @Override
    public void onEventChange(String eventType, String rawPayload) {
        Log.d(TAG, "Realtime update received from Supabase. Refreshing events...");
        syncEventsFromCloud(null);
    }

    @Override
    public void onConnectionStateChange(boolean isConnected) {
        isRealtimeConnected.postValue(isConnected);
    }

    public LiveData<List<Event>> getAllEvents() {
        executor.execute(() -> {
            List<Event> cached = dbHelper.getAllEventsFromCache();
            allEventsLiveData.postValue(cached);
        });

        syncEventsFromCloud(null);
        return allEventsLiveData;
    }

    public void syncEventsFromCloud(Runnable onComplete) {
        if (!SupabaseConfig.isConfigured()) {
            if (onComplete != null) mainHandler.post(onComplete);
            return;
        }

        isSyncing.postValue(true);
        apiService.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                isSyncing.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> cloudEvents = response.body();
                    executor.execute(() -> {
                        dbHelper.replaceAllEvents(cloudEvents);
                        allEventsLiveData.postValue(cloudEvents);
                        if (onComplete != null) {
                            mainHandler.post(onComplete);
                        }
                    });
                } else {
                    if (onComplete != null) mainHandler.post(onComplete);
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                isSyncing.postValue(false);
                Log.w(TAG, "Failed to sync events from Supabase: " + t.getMessage());
                if (onComplete != null) mainHandler.post(onComplete);
            }
        });
    }

    public LiveData<List<Event>> getEventsByAuthor(String userId) {
        MutableLiveData<List<Event>> authorEvents = new MutableLiveData<>();
        executor.execute(() -> {
            List<Event> list = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(
                    EventDbHelper.TABLE_EVENTS,
                    null,
                    EventDbHelper.KEY_CREATED_BY + " = ?",
                    new String[]{userId},
                    null, null,
                    EventDbHelper.KEY_CREATED_AT + " DESC"
            );

            if (cursor.moveToFirst()) {
                do {
                    list.add(EventDbHelper.cursorToEvent(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            authorEvents.postValue(list);
        });
        return authorEvents;
    }

    public void authenticate(String userIdOrEmail, String password, AuthCallback callback) {
        if (SupabaseConfig.isConfigured()) {
            String email = userIdOrEmail.contains("@") ? userIdOrEmail : userIdOrEmail + "@duet.ac.bd";
            AuthRequest req = new AuthRequest(email, password);

            apiService.login(req).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse auth = response.body();
                        String uid = auth.getUser() != null ? auth.getUser().getId() : "";
                        fetchProfile(uid, auth.getAccessToken(), auth.getRefreshToken(), userIdOrEmail, callback);
                    } else {
                        authenticateLocally(userIdOrEmail, password, callback);
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    authenticateLocally(userIdOrEmail, password, callback);
                }
            });
        } else {
            authenticateLocally(userIdOrEmail, password, callback);
        }
    }

    private void fetchProfile(String uid, String accessToken, String refreshToken, String fallbackUserId, AuthCallback callback) {
        // Try to fetch by user_id or id
        apiService.getProfileByUserId("eq." + fallbackUserId).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profile profile = response.body().get(0);
                    sessionManager.saveSession(accessToken, refreshToken, profile);
                    SupabaseClient.resetClient();
                    callback.onSuccess(new User(profile.getUserId(), profile.getRole(), profile.getDisplayName()));
                } else {
                    // Fallback to query by UUID
                    apiService.getProfileById("eq." + uid).enqueue(new Callback<List<Profile>>() {
                        @Override
                        public void onResponse(Call<List<Profile>> call2, Response<List<Profile>> response2) {
                            Profile profile;
                            if (response2.isSuccessful() && response2.body() != null && !response2.body().isEmpty()) {
                                profile = response2.body().get(0);
                            } else {
                                profile = new Profile(uid, fallbackUserId + "@duet.ac.bd", fallbackUserId, "CLUB_AUTHORITY", fallbackUserId);
                            }
                            sessionManager.saveSession(accessToken, refreshToken, profile);
                            SupabaseClient.resetClient();
                            callback.onSuccess(new User(profile.getUserId(), profile.getRole(), profile.getDisplayName()));
                        }

                        @Override
                        public void onFailure(Call<List<Profile>> call2, Throwable t) {
                            Profile profile = new Profile(uid, fallbackUserId + "@duet.ac.bd", fallbackUserId, "CLUB_AUTHORITY", fallbackUserId);
                            sessionManager.saveSession(accessToken, refreshToken, profile);
                            SupabaseClient.resetClient();
                            callback.onSuccess(new User(profile.getUserId(), profile.getRole(), profile.getDisplayName()));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Profile profile = new Profile(uid, fallbackUserId + "@duet.ac.bd", fallbackUserId, "CLUB_AUTHORITY", fallbackUserId);
                sessionManager.saveSession(accessToken, refreshToken, profile);
                SupabaseClient.resetClient();
                callback.onSuccess(new User(profile.getUserId(), profile.getRole(), profile.getDisplayName()));
            }
        });
    }

    public void refreshUserProfile(User currentUser, AuthCallback callback) {
        if (!SupabaseConfig.isConfigured() || currentUser == null) return;

        apiService.getProfileByUserId("eq." + currentUser.getUserId()).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profile p = response.body().get(0);
                    sessionManager.saveSession(sessionManager.getAccessToken(), "", p);
                    User updated = new User(p.getUserId(), p.getRole(), p.getDisplayName());
                    if (callback != null) mainHandler.post(() -> callback.onSuccess(updated));
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Log.w(TAG, "Profile refresh failed: " + t.getMessage());
            }
        });
    }

    private void authenticateLocally(String userId, String password, AuthCallback callback) {
        executor.execute(() -> {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String hashedPassword = EventDbHelper.hashPassword(password);

            Cursor cursor = db.query(
                    EventDbHelper.TABLE_USERS,
                    new String[]{EventDbHelper.KEY_USER_ID, EventDbHelper.KEY_ROLE, EventDbHelper.KEY_DISPLAY_NAME},
                    EventDbHelper.KEY_USER_ID + " = ? AND " + EventDbHelper.KEY_PASSWORD_HASH + " = ?",
                    new String[]{userId, hashedPassword},
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                cursor.close();
                sessionManager.saveLocalSession(user);
                mainHandler.post(() -> callback.onSuccess(user));
            } else {
                cursor.close();
                mainHandler.post(() -> callback.onError("Invalid credentials. Please check your User ID / Email and password."));
            }
        });
    }

    public void logout() {
        sessionManager.clearSession();
        SupabaseClient.resetClient();
    }

    public User getCachedUser() {
        return sessionManager.getLoggedInUser();
    }

    public void insertEvent(Event event, ActionCallback callback) {
        if (event.getEventId() <= 0) {
            event.setEventId(null);
        }
        if (event.getCreatedAt() <= 0) {
            event.setCreatedAt(System.currentTimeMillis());
        }
        if (event.getUpdatedAt() <= 0) {
            event.setUpdatedAt(System.currentTimeMillis());
        }

        // Local cache first
        executor.execute(() -> {
            dbHelper.upsertEvent(event);
            allEventsLiveData.postValue(dbHelper.getAllEventsFromCache());
        });

        if (SupabaseConfig.isConfigured()) {
            apiService.createEvent("return=representation", event).enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Event created = response.body().get(0);
                        executor.execute(() -> {
                            dbHelper.upsertEvent(created);
                            allEventsLiveData.postValue(dbHelper.getAllEventsFromCache());
                        });
                        Log.d(TAG, "Event inserted into Supabase successfully with ID: " + created.getEventId());
                    } else {
                        Log.w(TAG, "Supabase insert error: code=" + response.code() + ", error=" + response.message());
                    }
                    if (callback != null) callback.onSuccess();
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    Log.w(TAG, "Cloud create failed, saved locally: " + t.getMessage());
                    if (callback != null) callback.onSuccess();
                }
            });
        } else {
            if (callback != null) mainHandler.post(callback::onSuccess);
        }
    }

    public void updateEvent(Event event, ActionCallback callback) {
        event.setUpdatedAt(System.currentTimeMillis());
        executor.execute(() -> {
            dbHelper.upsertEvent(event);
            allEventsLiveData.postValue(dbHelper.getAllEventsFromCache());
        });

        if (SupabaseConfig.isConfigured()) {
            apiService.updateEvent("eq." + event.getEventId(), "return=representation", event).enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    if (callback != null) callback.onSuccess();
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    Log.w(TAG, "Cloud update failed, saved locally: " + t.getMessage());
                    if (callback != null) callback.onSuccess();
                }
            });
        } else {
            if (callback != null) mainHandler.post(callback::onSuccess);
        }
    }

    public void cancelEvent(int eventId, ActionCallback callback) {
        executor.execute(() -> {
            List<Event> list = dbHelper.getAllEventsFromCache();
            for (Event e : list) {
                if (e.getEventId() == eventId) {
                    e.setStatus("Cancelled");
                    dbHelper.upsertEvent(e);
                    break;
                }
            }
            allEventsLiveData.postValue(dbHelper.getAllEventsFromCache());
        });

        if (SupabaseConfig.isConfigured()) {
            Map<String, Object> fields = new HashMap<>();
            fields.put("status", "Cancelled");
            fields.put("updated_at", System.currentTimeMillis());

            apiService.updateEventPartial("eq." + eventId, "return=representation", fields).enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    if (callback != null) callback.onSuccess();
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    if (callback != null) callback.onSuccess();
                }
            });
        } else {
            if (callback != null) mainHandler.post(callback::onSuccess);
        }
    }

    public void deleteEvent(int eventId, ActionCallback callback) {
        executor.execute(() -> {
            dbHelper.deleteEventById(eventId);
            allEventsLiveData.postValue(dbHelper.getAllEventsFromCache());
        });

        if (SupabaseConfig.isConfigured()) {
            apiService.deleteEvent("eq." + eventId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (callback != null) callback.onSuccess();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if (callback != null) callback.onSuccess();
                }
            });
        } else {
            if (callback != null) mainHandler.post(callback::onSuccess);
        }
    }
}