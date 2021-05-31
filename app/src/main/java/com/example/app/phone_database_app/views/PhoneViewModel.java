package com.example.app.phone_database_app.views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.app.phone_database_app.database.Phone;
import com.example.app.phone_database_app.database.PhoneRepository;

import java.util.List;

public class PhoneViewModel extends AndroidViewModel {
    private final PhoneRepository mRepository;
    private final LiveData<List<Phone>> mAllPhones;

    public PhoneViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PhoneRepository(application);
        mAllPhones = mRepository.getAllPhones();
    }

    public LiveData<List<Phone>> getAllPhones() {
        return mAllPhones;
    }

    public void insert(Phone phone) {
        mRepository.insert(phone);
    }

    public void update(Phone phone) {
        mRepository.update(phone);
    }

    public void deletePhone(Phone phone) {
        mRepository.deletePhone(phone);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

}
