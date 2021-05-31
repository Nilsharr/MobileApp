package com.example.app.phone_database_app.selection_tracker;

import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class PhoneItemDetails extends ItemDetailsLookup.ItemDetails<Long> {
    private int position;
    private long id;

    @Override
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public Long getSelectionKey() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean inSelectionHotspot(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public boolean inDragRegion(@NonNull MotionEvent e) {
        return true;
    }
}
