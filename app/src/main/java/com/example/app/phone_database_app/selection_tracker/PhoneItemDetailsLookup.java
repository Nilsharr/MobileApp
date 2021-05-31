package com.example.app.phone_database_app.selection_tracker;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.phone_database_app.adapters.PhoneAdapter;

public class PhoneItemDetailsLookup extends ItemDetailsLookup<Long> {
    private final RecyclerView mRecyclerView;

    public PhoneItemDetailsLookup(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(view);
            if (viewHolder instanceof PhoneAdapter.PhoneViewHolder) {
                return ((PhoneAdapter.PhoneViewHolder) viewHolder).getPhoneItemDetails();
            }
        }
        return null;
    }
}
