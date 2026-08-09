package com.example.myduet.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.models.Event;
import com.example.myduet.models.User;
import com.example.myduet.storage.EventDbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {

    private final EventDbHelper dbHelper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public EventRepository(Context context) {
        this.dbHelper = new EventDbHelper(context);
    }

    /**
     * Authenticates an authority user.
     */
    public boolean authenticate(String userId, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String hashedPassword = EventDbHelper.hashPassword(password);

        Cursor cursor = db.query(
                EventDbHelper.TABLE_USERS,
                new String[]{EventDbHelper.KEY_USER_ID},
                EventDbHelper.KEY_USER_ID + " = ? AND " + EventDbHelper.KEY_PASSWORD_HASH + " = ?",
                new String[]{userId, hashedPassword},
                null, null, null
        );

        boolean authenticated = cursor.getCount() > 0;
        cursor.close();
        return authenticated;
    }

    /**
     * Retrieves user profile details.
     */
    public User getUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                EventDbHelper.TABLE_USERS,
                new String[]{EventDbHelper.KEY_USER_ID, EventDbHelper.KEY_ROLE, EventDbHelper.KEY_DISPLAY_NAME},
                EventDbHelper.KEY_USER_ID + " = ?",
                new String[]{userId},
                null, null, null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
        }
        cursor.close();
        return user;
    }

    /**
     * Fetches all events from the database.
     */
    public LiveData<List<Event>> getAllEvents() {
        MutableLiveData<List<Event>> data = new MutableLiveData<>();
        executor.execute(() -> {
            List<Event> list = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + EventDbHelper.TABLE_EVENTS + " ORDER BY " + EventDbHelper.KEY_CREATED_AT + " DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    list.add(cursorToEvent(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            data.postValue(list);
        });
        return data;
    }

    /**
     * Fetches events created by a specific user.
     */
    public LiveData<List<Event>> getEventsByAuthor(String userId) {
        MutableLiveData<List<Event>> data = new MutableLiveData<>();
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
                    list.add(cursorToEvent(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            data.postValue(list);
        });
        return data;
    }

    /**
     * Inserts a new event.
     */
    public void insertEvent(Event event, Runnable callback) {
        executor.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = getEventContentValues(event);
            values.put(EventDbHelper.KEY_CREATED_AT, System.currentTimeMillis());
            values.put(EventDbHelper.KEY_UPDATED_AT, System.currentTimeMillis());
            db.insert(EventDbHelper.TABLE_EVENTS, null, values);
            if (callback != null) {
                callback.run();
            }
        });
    }

    /**
     * Updates an existing event.
     */
    public void updateEvent(Event event, Runnable callback) {
        executor.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = getEventContentValues(event);
            values.put(EventDbHelper.KEY_UPDATED_AT, System.currentTimeMillis());
            db.update(
                    EventDbHelper.TABLE_EVENTS,
                    values,
                    EventDbHelper.KEY_EVENT_ID + " = ?",
                    new String[]{String.valueOf(event.getEventId())}
            );
            if (callback != null) {
                callback.run();
            }
        });
    }

    /**
     * Cancels an event by updating its status to "Cancelled".
     */
    public void cancelEvent(int eventId, Runnable callback) {
        executor.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(EventDbHelper.KEY_STATUS, "Cancelled");
            values.put(EventDbHelper.KEY_UPDATED_AT, System.currentTimeMillis());
            db.update(
                    EventDbHelper.TABLE_EVENTS,
                    values,
                    EventDbHelper.KEY_EVENT_ID + " = ?",
                    new String[]{String.valueOf(eventId)}
            );
            if (callback != null) {
                callback.run();
            }
        });
    }

    /**
     * Deletes an event.
     */
    public void deleteEvent(int eventId, Runnable callback) {
        executor.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(
                    EventDbHelper.TABLE_EVENTS,
                    EventDbHelper.KEY_EVENT_ID + " = ?",
                    new String[]{String.valueOf(eventId)}
            );
            if (callback != null) {
                callback.run();
            }
        });
    }

    // Helper to map cursor row to Event object
    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setEventId(cursor.getInt(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_EVENT_ID)));
        event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_TITLE)));
        event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_DESCRIPTION)));
        event.setType(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_TYPE)));
        event.setClubName(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_CLUB_NAME)));
        event.setOrganizerName(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_ORGANIZER_NAME)));
        event.setBannerUrl(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_BANNER_URL)));
        event.setEventDate(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_EVENT_DATE)));
        event.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_START_TIME)));
        event.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_END_TIME)));
        event.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_VENUE)));
        event.setRegistrationRequired(cursor.getInt(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_REG_REQUIRED)) == 1);
        event.setRegistrationDeadline(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_REG_DEADLINE)));
        event.setRegistrationUrl(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_REG_URL)));
        event.setContactName(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_CONTACT_NAME)));
        event.setContactEmail(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_CONTACT_EMAIL)));
        event.setContactPhone(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_CONTACT_PHONE)));

        // optional columns
        int maxPartCol = cursor.getColumnIndexOrThrow(EventDbHelper.KEY_MAX_PARTICIPANTS);
        if (!cursor.isNull(maxPartCol)) {
            event.setMaxParticipants(cursor.getInt(maxPartCol));
        }
        event.setSocialMediaUrl(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_SOCIAL_MEDIA_URL)));
        event.setAdditionalInfo(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_ADDITIONAL_INFO)));

        event.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_STATUS)));
        event.setCreatedBy(cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_CREATED_BY)));
        event.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_CREATED_AT)));
        event.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(EventDbHelper.KEY_UPDATED_AT)));
        return event;
    }

    // Helper to wrap Event in ContentValues
    private ContentValues getEventContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventDbHelper.KEY_TITLE, event.getTitle());
        values.put(EventDbHelper.KEY_DESCRIPTION, event.getDescription());
        values.put(EventDbHelper.KEY_TYPE, event.getType());
        values.put(EventDbHelper.KEY_CLUB_NAME, event.getClubName());
        values.put(EventDbHelper.KEY_ORGANIZER_NAME, event.getOrganizerName());
        values.put(EventDbHelper.KEY_BANNER_URL, event.getBannerUrl());
        values.put(EventDbHelper.KEY_EVENT_DATE, event.getEventDate());
        values.put(EventDbHelper.KEY_START_TIME, event.getStartTime());
        values.put(EventDbHelper.KEY_END_TIME, event.getEndTime());
        values.put(EventDbHelper.KEY_VENUE, event.getVenue());
        values.put(EventDbHelper.KEY_REG_REQUIRED, event.isRegistrationRequired() ? 1 : 0);
        values.put(EventDbHelper.KEY_REG_DEADLINE, event.getRegistrationDeadline());
        values.put(EventDbHelper.KEY_REG_URL, event.getRegistrationUrl());
        values.put(EventDbHelper.KEY_CONTACT_NAME, event.getContactName());
        values.put(EventDbHelper.KEY_CONTACT_EMAIL, event.getContactEmail());
        values.put(EventDbHelper.KEY_CONTACT_PHONE, event.getContactPhone());
        values.put(EventDbHelper.KEY_MAX_PARTICIPANTS, event.getMaxParticipants());
        values.put(EventDbHelper.KEY_SOCIAL_MEDIA_URL, event.getSocialMediaUrl());
        values.put(EventDbHelper.KEY_ADDITIONAL_INFO, event.getAdditionalInfo());
        values.put(EventDbHelper.KEY_STATUS, event.getStatus());
        values.put(EventDbHelper.KEY_CREATED_BY, event.getCreatedBy());
        return values;
    }
}
