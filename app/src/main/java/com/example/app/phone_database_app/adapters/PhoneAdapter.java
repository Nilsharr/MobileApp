package com.example.app.phone_database_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.phone_database_app.database.Phone;
import com.example.app.phone_database_app.selection_tracker.PhoneItemDetails;
import com.example.app.phone_database_app.selection_tracker.PhoneItemKeyProvider;

import java.util.List;


public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {
    private List<Phone> mPhoneList;

    private SelectionTracker<Long> mSelectionTracker;
    private final PhoneItemKeyProvider mPhoneItemKeyProvider;

    public PhoneAdapter(PhoneItemKeyProvider phoneItemKeyProvider) {
        mPhoneItemKeyProvider = phoneItemKeyProvider;
    }

    // stores information about a single item view
    public class PhoneViewHolder extends RecyclerView.ViewHolder {
        private final TextView phoneManufacturerInfo;
        private final TextView phoneModelInfo;
        final PhoneItemDetails phoneItemDetails;

        public PhoneViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneManufacturerInfo = itemView.findViewById(R.id.phoneManufacturerInfo);
            phoneModelInfo = itemView.findViewById(R.id.phoneModelInfo);
            phoneItemDetails = new PhoneItemDetails();
        }

        // filling row with data
        public void bindToPhoneViewHolder(int position, boolean isSelected) {
            phoneManufacturerInfo.setText(mPhoneList.get(position).getManufacturer());
            phoneModelInfo.setText(mPhoneList.get(position).getModel());
            phoneItemDetails.setId(mPhoneList.get(position).getId());
            phoneItemDetails.setPosition(position);
            itemView.setActivated(isSelected);
        }

        public PhoneItemDetails getPhoneItemDetails() {
            return phoneItemDetails;
        }
    }

    // creating row
    @NonNull
    @Override
    public PhoneAdapter.PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_phones_group, parent, false);
        return new PhoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneAdapter.PhoneViewHolder holder, int position) {
        boolean isSelected = false;
        if ((mSelectionTracker != null) && mSelectionTracker.isSelected(mPhoneList.get(position).getId())) {
            isSelected = true;
        }
        holder.bindToPhoneViewHolder(position, isSelected);
    }

    @Override
    public int getItemCount() {
        return mPhoneList == null ? 0 : mPhoneList.size();
    }

    public void setPhoneList(List<Phone> phoneList) {
        if (mSelectionTracker != null) {
            mSelectionTracker.clearSelection();
        }
        this.mPhoneList = phoneList;
        mPhoneItemKeyProvider.setPhones(phoneList);
        notifyDataSetChanged();
    }

    public void setSelectionTracker(SelectionTracker<Long> mSelectionTracker) {
        this.mSelectionTracker = mSelectionTracker;
    }

}
