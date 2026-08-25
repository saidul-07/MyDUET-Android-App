package com.example.myduet.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.myduet.models.NoticeEntity;
import java.util.List;

@Dao
public interface NoticeDao {
    @Query("SELECT * FROM notices")
    LiveData<List<NoticeEntity>> getAllNoticesLiveData();

    @Query("SELECT * FROM notices")
    List<NoticeEntity> getAllNoticesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotices(List<NoticeEntity> notices);

    @Query("SELECT id FROM notices")
    List<String> getAllNoticeIdsSync();

    @Query("DELETE FROM notices")
    void deleteAll();
}
