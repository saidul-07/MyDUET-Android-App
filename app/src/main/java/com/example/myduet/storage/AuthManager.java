package com.example.myduet.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AuthManager {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_LOGGED_IN_EMAIL = "logged_in_email";
    private static final String KEY_REGISTERED_USERS = "registered_users";

    private final SharedPreferences prefs;
    private final Gson gson;

    public AuthManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Checks if the email format is valid (Gmail, duet.ac.bd, or any .edu/.edu.bd domain).
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String cleanEmail = email.trim().toLowerCase();
        
        // Simple regex check for general format first
        if (!cleanEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return false;
        }
        
        // Match specific domains
        return cleanEmail.endsWith("@gmail.com") 
                || cleanEmail.endsWith("@duet.ac.bd") 
                || cleanEmail.endsWith(".edu") 
                || cleanEmail.endsWith(".edu.bd");
    }

    /**
     * Registers a new user locally.
     * Returns true if registration is successful, false if the email is already registered.
     */
    public synchronized boolean registerUser(String email, String password) {
        if (email == null || password == null) return false;
        String cleanEmail = email.trim().toLowerCase();
        
        Map<String, String> users = getRegisteredUsers();
        if (users.containsKey(cleanEmail)) {
            return false; // Already registered
        }
        
        users.put(cleanEmail, password);
        saveRegisteredUsers(users);
        return true;
    }

    /**
     * Verifies user credentials.
     */
    public synchronized boolean verifyCredentials(String email, String password) {
        if (email == null || password == null) return false;
        String cleanEmail = email.trim().toLowerCase();
        
        Map<String, String> users = getRegisteredUsers();
        return users.containsKey(cleanEmail) && password.equals(users.get(cleanEmail));
    }

    /**
     * Starts a logged-in user session.
     */
    public void setLoggedIn(String email, boolean isLoggedIn) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        if (isLoggedIn && email != null) {
            editor.putString(KEY_LOGGED_IN_EMAIL, email.trim().toLowerCase());
        } else {
            editor.remove(KEY_LOGGED_IN_EMAIL);
        }
        editor.apply();
    }

    /**
     * Checks if a user is currently logged in.
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Gets the current logged-in user's email.
     */
    public String getLoggedInUser() {
        return prefs.getString(KEY_LOGGED_IN_EMAIL, "");
    }

    /**
     * Clears the current user session (Sign Out).
     */
    public void logout() {
        setLoggedIn(null, false);
    }

    /**
     * Checks if a user email is registered locally.
     */
    public synchronized boolean isUserRegistered(String email) {
        if (email == null) return false;
        String cleanEmail = email.trim().toLowerCase();
        Map<String, String> users = getRegisteredUsers();
        return users.containsKey(cleanEmail);
    }

    /**
     * Resets/updates the password of an existing registered user.
     * Returns true if successful, false if the email is not registered.
     */
    public synchronized boolean resetPassword(String email, String newPassword) {
        if (email == null || newPassword == null) return false;
        String cleanEmail = email.trim().toLowerCase();
        
        Map<String, String> users = getRegisteredUsers();
        if (!users.containsKey(cleanEmail)) {
            return false; // Not registered
        }
        
        users.put(cleanEmail, newPassword);
        saveRegisteredUsers(users);
        return true;
    }

    /**
     * Helper to load registered users from SharedPreferences.
     */
    private Map<String, String> getRegisteredUsers() {
        String json = prefs.getString(KEY_REGISTERED_USERS, null);
        if (json == null) {
            return new HashMap<>();
        }
        try {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> users = gson.fromJson(json, type);
            return users != null ? users : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Helper to save registered users to SharedPreferences.
     */
    private void saveRegisteredUsers(Map<String, String> users) {
        String json = gson.toJson(users);
        prefs.edit().putString(KEY_REGISTERED_USERS, json).apply();
    }

    /**
     * Saves user profile details.
     */
    public void saveUserProfile(String email, String name, String dept, String studentId, String batch) {
        if (email == null) return;
        String key = email.trim().toLowerCase();
        prefs.edit()
             .putString(key + "_profile_name", name)
             .putString(key + "_profile_dept", dept)
             .putString(key + "_profile_id", studentId)
             .putString(key + "_profile_batch", batch)
             .apply();
    }

    /**
     * Gets a user profile detail.
     */
    public String getUserProfileField(String email, String fieldName, String defaultValue) {
        if (email == null) return defaultValue;
        String key = email.trim().toLowerCase();
        return prefs.getString(key + "_profile_" + fieldName, defaultValue);
    }
}
