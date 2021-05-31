package com.example.app.phone_database_app.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PhoneRepository {
    private final PhoneDao mPhoneDao;
    private final LiveData<List<Phone>> mAllPhones;

    public PhoneRepository(Application application) {
        PhoneRoomDatabase phoneRoomDatabase = PhoneRoomDatabase.getDatabase(application);
        mPhoneDao = phoneRoomDatabase.phoneDao();
        mAllPhones = mPhoneDao.getPhonesList();
    }

    public LiveData<List<Phone>> getAllPhones() {
        return mAllPhones;
    }

    // performed in separate thread
    public void insert(Phone phone) {
        PhoneRoomDatabase.databaseWriteExecutor.execute(() -> mPhoneDao.insertPhone(phone));
    }

    public void update(Phone phone) {
        PhoneRoomDatabase.databaseWriteExecutor.execute(() -> mPhoneDao.updatePhone(phone));
    }

    public void deletePhone(Phone phone) {
        PhoneRoomDatabase.databaseWriteExecutor.execute(() -> mPhoneDao.deletePhone(phone));
    }

    public void deleteAll() {
        PhoneRoomDatabase.databaseWriteExecutor.execute(mPhoneDao::deleteAll);
    }

}
