package com.lebogang.zapperdisplay.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntryDao {

    @Query("SELECT * FROM entries")
    LiveData<List<DataEntry>> loadAllEntries();

    @Insert
    void insertEntry(DataEntry dataEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(DataEntry dataEntry);

    @Delete
    void deleteEntry(DataEntry dataEntry);

    @Query("SELECT * FROM entries WHERE id = :id")
    LiveData<DataEntry> loadEntryById(int id);
}
