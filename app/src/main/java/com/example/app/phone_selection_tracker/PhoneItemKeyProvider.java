package com.example.app.phone_selection_tracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import com.example.app.phone_database.Phone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneItemKeyProvider extends ItemKeyProvider<Long> {

    private Map<Long, Integer> mKeyToPosition;
    private List<Phone> mPhoneList;

    public PhoneItemKeyProvider() {
        super(SCOPE_MAPPED);
        this.mPhoneList = null;
    }

    public void setPhones(List<Phone> phoneList) {
        this.mPhoneList = phoneList;
        mKeyToPosition = new HashMap<>(mPhoneList.size());
        for (int i = 0; i < mPhoneList.size(); ++i) {
            mKeyToPosition.put(mPhoneList.get(i).getId(), i);
        }
    }

    @Nullable
    @Override
    public Long getKey(int position) {
        return mPhoneList.get(position).getId();
    }

    @Override
    public int getPosition(@NonNull Long key) {
        return mKeyToPosition.get(key);
    }
}
