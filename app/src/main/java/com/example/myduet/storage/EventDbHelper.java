package com.example.myduet.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.myduet.models.Event;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class EventDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myduet_events.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_USERS = "users";

    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";

    public static final String KEY_EVENT_ID = "eventId";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CLUB_NAME = "clubName";
    public static final String KEY_ORGANIZER_NAME = "organizerName";
    public static final String KEY_BANNER_URL = "bannerUrl";
    public static final String KEY_EVENT_DATE = "eventDate";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_VENUE = "venue";
    public static final String KEY_REG_REQUIRED = "registrationRequired";
    public static final String KEY_REG_DEADLINE = "registrationDeadline";
    public static final String KEY_REG_URL = "registrationUrl";
    public static final String KEY_CONTACT_NAME = "contactName";
    public static final String KEY_CONTACT_EMAIL = "contactEmail";
    public static final String KEY_CONTACT_PHONE = "contactPhone";
    public static final String KEY_MAX_PARTICIPANTS = "maxParticipants";
    public static final String KEY_SOCIAL_MEDIA_URL = "socialMediaUrl";
    public static final String KEY_ADDITIONAL_INFO = "additionalInfo";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATED_BY = "createdBy";

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_PASSWORD_HASH = "passwordHash";
    public static final String KEY_ROLE = "role";
    public static final String KEY_DISPLAY_NAME = "displayName";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_USER_ID + " TEXT PRIMARY KEY,"
            + KEY_PASSWORD_HASH + " TEXT,"
            + KEY_ROLE + " TEXT,"
            + KEY_DISPLAY_NAME + " TEXT"
            + ")";

    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
            + KEY_EVENT_ID + " INTEGER PRIMARY KEY,"
            + KEY_TITLE + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_TYPE + " TEXT,"
            + KEY_CLUB_NAME + " TEXT,"
            + KEY_ORGANIZER_NAME + " TEXT,"
            + KEY_BANNER_URL + " TEXT,"
            + KEY_EVENT_DATE + " TEXT,"
            + KEY_START_TIME + " TEXT,"
            + KEY_END_TIME + " TEXT,"
            + KEY_VENUE + " TEXT,"
            + KEY_REG_REQUIRED + " INTEGER,"
            + KEY_REG_DEADLINE + " TEXT,"
            + KEY_REG_URL + " TEXT,"
            + KEY_CONTACT_NAME + " TEXT,"
            + KEY_CONTACT_EMAIL + " TEXT,"
            + KEY_CONTACT_PHONE + " TEXT,"
            + KEY_MAX_PARTICIPANTS + " INTEGER,"
            + KEY_SOCIAL_MEDIA_URL + " TEXT,"
            + KEY_ADDITIONAL_INFO + " TEXT,"
            + KEY_STATUS + " TEXT,"
            + KEY_CREATED_BY + " TEXT,"
            + KEY_CREATED_AT + " INTEGER,"
            + KEY_UPDATED_AT + " INTEGER"
            + ")";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_EVENTS);
        seedUsers(db);
        seedEvents(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public synchronized void replaceAllEvents(List<Event> events) {
        if (events == null) return;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_EVENTS, null, null);
            for (Event event : events) {
                ContentValues values = getEventContentValues(event);
                db.insertWithOnConflict(TABLE_EVENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public synchronized void upsertEvent(Event event) {
        if (event == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = getEventContentValues(event);
        db.insertWithOnConflict(TABLE_EVENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public synchronized void deleteEventById(int eventId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});
    }

    public List<Event> getAllEventsFromCache() {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " ORDER BY " + KEY_CREATED_AT + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToEvent(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private ContentValues getEventContentValues(Event event) {
        ContentValues values = new ContentValues();
        if (event.getEventId() > 0) {
            values.put(KEY_EVENT_ID, event.getEventId());
        }
        values.put(KEY_TITLE, event.getTitle());
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_TYPE, event.getType());
        values.put(KEY_CLUB_NAME, event.getClubName());
        values.put(KEY_ORGANIZER_NAME, event.getOrganizerName());
        values.put(KEY_BANNER_URL, event.getBannerUrl());
        values.put(KEY_EVENT_DATE, event.getEventDate());
        values.put(KEY_START_TIME, event.getStartTime());
        values.put(KEY_END_TIME, event.getEndTime());
        values.put(KEY_VENUE, event.getVenue());
        values.put(KEY_REG_REQUIRED, event.isRegistrationRequired() ? 1 : 0);
        values.put(KEY_REG_DEADLINE, event.getRegistrationDeadline());
        values.put(KEY_REG_URL, event.getRegistrationUrl());
        values.put(KEY_CONTACT_NAME, event.getContactName());
        values.put(KEY_CONTACT_EMAIL, event.getContactEmail());
        values.put(KEY_CONTACT_PHONE, event.getContactPhone());
        values.put(KEY_MAX_PARTICIPANTS, event.getMaxParticipants());
        values.put(KEY_SOCIAL_MEDIA_URL, event.getSocialMediaUrl());
        values.put(KEY_ADDITIONAL_INFO, event.getAdditionalInfo());
        values.put(KEY_STATUS, event.getStatus());
        values.put(KEY_CREATED_BY, event.getCreatedBy());
        values.put(KEY_CREATED_AT, event.getCreatedAt() > 0 ? event.getCreatedAt() : System.currentTimeMillis());
        values.put(KEY_UPDATED_AT, event.getUpdatedAt() > 0 ? event.getUpdatedAt() : System.currentTimeMillis());
        return values;
    }

    public static Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setEventId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EVENT_ID)));
        event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
        event.setType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)));
        event.setClubName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLUB_NAME)));
        event.setOrganizerName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ORGANIZER_NAME)));
        event.setBannerUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BANNER_URL)));
        event.setEventDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EVENT_DATE)));
        event.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_START_TIME)));
        event.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_END_TIME)));
        event.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(KEY_VENUE)));
        event.setRegistrationRequired(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_REG_REQUIRED)) == 1);
        event.setRegistrationDeadline(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REG_DEADLINE)));
        event.setRegistrationUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REG_URL)));
        event.setContactName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_NAME)));
        event.setContactEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_EMAIL)));
        event.setContactPhone(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_PHONE)));

        int maxPartCol = cursor.getColumnIndexOrThrow(KEY_MAX_PARTICIPANTS);
        if (!cursor.isNull(maxPartCol)) {
            event.setMaxParticipants(cursor.getInt(maxPartCol));
        }
        event.setSocialMediaUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SOCIAL_MEDIA_URL)));
        event.setAdditionalInfo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDITIONAL_INFO)));

        event.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));
        event.setCreatedBy(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
        event.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
        event.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT)));
        return event;
    }

    private void seedUsers(SQLiteDatabase db) {
        insertUser(db, "admin", "admin123", "ADMIN", "System Administrator");
        insertUser(db, "duet_auth", "duet123", "UNIVERSITY_AUTHORITY", "DUET Registrar Office");
        insertUser(db, "club_auth", "club123", "CLUB_AUTHORITY", "CSE Programming Club");
    }

    private void insertUser(SQLiteDatabase db, String userId, String password, String role, String displayName) {
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_PASSWORD_HASH, hashPassword(password));
        values.put(KEY_ROLE, role);
        values.put(KEY_DISPLAY_NAME, displayName);
        db.insert(TABLE_USERS, null, values);
    }

    private void seedEvents(SQLiteDatabase db) {
        insertEvent(db, 1,
                "DUET National Programming Contest",
                "Annual programming competition for university students from across Bangladesh. Join us to compete for the championship title!",
                "Club",
                "CSE Programming Club",
                "CSE Dept & Programming Club",
                "https://images.unsplash.com/photo-1515187029135-18ee286d815b?q=80&w=600&auto=format&fit=crop",
                "2026-09-15",
                "09:00",
                "17:00",
                "CSE Computer Lab 3 & Auditorium",
                true,
                "2026-09-14 23:59",
                "https://example.com/npc-register",
                "Prof. Dr. Raju Ahmed",
                "raju.ahmed@duet.ac.bd",
                "+8801713000001",
                200,
                "https://facebook.com/duet.cse.pc",
                "Participants should bring their ID cards.",
                "club_auth"
        );

        insertEvent(db, 2,
                "Seminar on Generative AI & Career Prospects",
                "Discover the latest breakthroughs in artificial intelligence and how to prepare for careers in machine learning.",
                "University",
                "",
                "ICT Cell, DUET",
                "https://images.unsplash.com/photo-1591453089816-0fbb971b454c?q=80&w=600&auto=format&fit=crop",
                "2026-09-20",
                "14:00",
                "16:00",
                "Central Auditorium, DUET",
                true,
                "2026-09-19 12:00",
                "https://example.com/ai-seminar-reg",
                "ICT Support Team",
                "ict@duet.ac.bd",
                "+8801711000009",
                500,
                "https://linkedin.com/school/duet-gazipur",
                "Open for all DUET departments.",
                "duet_auth"
        );
    }

    private void insertEvent(SQLiteDatabase db, int id, String title, String description, String type, 
                             String clubName, String organizerName, String bannerUrl, String date, 
                             String startTime, String endTime, String venue, boolean regRequired, 
                             String regDeadline, String regUrl, String contactName, String contactEmail, 
                             String contactPhone, Integer maxPart, String socialMedia, String addInfo, 
                             String createdBy) {
        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, id);
        values.put(KEY_TITLE, title);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_TYPE, type);
        values.put(KEY_CLUB_NAME, clubName);
        values.put(KEY_ORGANIZER_NAME, organizerName);
        values.put(KEY_BANNER_URL, bannerUrl);
        values.put(KEY_EVENT_DATE, date);
        values.put(KEY_START_TIME, startTime);
        values.put(KEY_END_TIME, endTime);
        values.put(KEY_VENUE, venue);
        values.put(KEY_REG_REQUIRED, regRequired ? 1 : 0);
        values.put(KEY_REG_DEADLINE, regDeadline);
        values.put(KEY_REG_URL, regUrl);
        values.put(KEY_CONTACT_NAME, contactName);
        values.put(KEY_CONTACT_EMAIL, contactEmail);
        values.put(KEY_CONTACT_PHONE, contactPhone);
        values.put(KEY_MAX_PARTICIPANTS, maxPart);
        values.put(KEY_SOCIAL_MEDIA_URL, socialMedia);
        values.put(KEY_ADDITIONAL_INFO, addInfo);
        values.put(KEY_STATUS, "Active");
        values.put(KEY_CREATED_BY, createdBy);
        values.put(KEY_CREATED_AT, System.currentTimeMillis());
        values.put(KEY_UPDATED_AT, System.currentTimeMillis());
        db.insertWithOnConflict(TABLE_EVENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Log.e("EventDbHelper", "Error hashing password", e);
            return password;
        }
    }
}
