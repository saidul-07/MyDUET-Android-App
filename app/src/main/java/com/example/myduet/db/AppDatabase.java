package com.example.myduet.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.myduet.models.NoticeEntity;

@Database(entities = {NoticeEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract NoticeDao noticeDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "myduet_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
