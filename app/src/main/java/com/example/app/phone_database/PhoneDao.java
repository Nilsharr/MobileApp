package com.example.app.phone_database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PhoneDao {
    @Query("SELECT * FROM phone ORDER BY manufacturer")
    LiveData<List<Phone>> getPhonesList();

    @Insert
    void insertPhone(Phone phone);

    @Update
    void updatePhone(Phone phone);

    @Delete
    void deletePhone(Phone phone);

    @Query("DELETE FROM phone")
    void deleteAll();

}
