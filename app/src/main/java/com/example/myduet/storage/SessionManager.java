package com.example.myduet.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.myduet.models.Profile;
import com.example.myduet.models.User;
import com.google.gson.Gson;

public class SessionManager {

    private static final String PREF_NAME = "myduet_auth_pref";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences preferences;
    private final Gson gson = new Gson();

    public SessionManager(Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String accessToken, String refreshToken, Profile profile) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        if (profile != null) {
            editor.putString(KEY_USER_ID, profile.getUserId());
            editor.putString(KEY_USER_ROLE, profile.getRole());
            editor.putString(KEY_DISPLAY_NAME, profile.getDisplayName());
            editor.putString(KEY_EMAIL, profile.getEmail());
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void saveLocalSession(User user) {
        SharedPreferences.Editor editor = preferences.edit();
        if (user != null) {
            editor.putString(KEY_USER_ID, user.getUserId());
            editor.putString(KEY_USER_ROLE, user.getRole());
            editor.putString(KEY_DISPLAY_NAME, user.getName());
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getAccessToken() {
        return preferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getLoggedInUser() {
        if (!isLoggedIn()) return null;
        String userId = preferences.getString(KEY_USER_ID, "");
        String role = preferences.getString(KEY_USER_ROLE, "CLUB_AUTHORITY");
        String name = preferences.getString(KEY_DISPLAY_NAME, "Authority");
        return new User(userId, role, name);
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}

