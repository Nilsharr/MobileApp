package com.example.app.phone_database_app.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.phone_database_app.adapters.PhoneAdapter;
import com.example.app.phone_database_app.database.Phone;
import com.example.app.phone_database_app.views.PhoneViewModel;
import com.example.app.phone_database_app.selection_tracker.PhoneItemDetailsLookup;
import com.example.app.phone_database_app.selection_tracker.PhoneItemKeyProvider;

import java.util.List;
import java.util.Objects;


public class PhonesDatabaseBrowsingActivity extends AppCompatActivity {

    private static final int ADD_PHONE_ACTIVITY_REQUEST_CODE = 2;
    private static final int EDIT_PHONE_ACTIVITY_REQUEST_CODE = 3;

    public static final String PHONE_OBJECT_FROM_ACTIVITY_RESULT = "com.example.app.phone_database_app.activities.phoneObjectFromActivityResult";
    public static final String INTENT_PHONE_TO_EDIT = "com.example.app.phone_database_app.activities.intentPhoneToEdit";

    private PhoneViewModel mPhoneViewModel;
    private PhoneAdapter mPhoneAdapter;
    private SelectionTracker<Long> mSelectionTracker;
    private PhoneItemKeyProvider mPhoneItemKeyProvider;

    // Creating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.phone_browsing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int itemId = item.getItemId();
        // Returning to previous activity
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        // Adding phone to database
        if (itemId == R.id.addPhoneToDatabaseMenuButton) {
            startActivityForResult(new Intent(PhonesDatabaseBrowsingActivity.this, PhonesDatabaseFormActivity.class), ADD_PHONE_ACTIVITY_REQUEST_CODE);
            return true;
        }
        // Deleting phone from database
        else if (itemId == R.id.deletePhoneFromDatabaseMenuButton) {
            deleteSelected();
            Toast.makeText(this, getString(R.string.message_item_deleted), Toast.LENGTH_SHORT).show();
            return true;
        }
        // Editing existing phone
        else if (itemId == R.id.editPhoneInDatabaseMenuButton) {
            Selection<Long> selection = mSelectionTracker.getSelection();
            List<Phone> phoneList = mPhoneViewModel.getAllPhones().getValue();
            if (phoneList != null) {
                Phone phone = null;
                for (long phoneId : selection) {
                    phone = phoneList.get(mPhoneItemKeyProvider.getPosition(phoneId));
                }
                Intent intent = new Intent(PhonesDatabaseBrowsingActivity.this, PhonesDatabaseFormActivity.class);
                intent.putExtra(INTENT_PHONE_TO_EDIT, phone);
                startActivityForResult(intent, EDIT_PHONE_ACTIVITY_REQUEST_CODE);
            }
            return true;
        }
        // Deleting all phones in database
        else if (itemId == R.id.deleteAllPhonesFromDatabaseMenuButton) {
            mPhoneViewModel.deleteAll();
            Toast.makeText(this, getString(R.string.message_item_deleted_all), Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Changing menu layout based on database or selected item in recyclerview
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // showing "no data" message if db is empty
        findViewById(R.id.phoneListEmptyInfo).setVisibility(mPhoneAdapter.getItemCount() > 0 ? View.INVISIBLE : View.VISIBLE);
        // showing edit menu icon if only one item is selected
        menu.findItem(R.id.editPhoneInDatabaseMenuButton).setVisible(mSelectionTracker.getSelection().size() == 1);
        // showing delete menu icon if there is at least one selected item
        menu.findItem(R.id.deletePhoneFromDatabaseMenuButton).setVisible(mSelectionTracker.hasSelection());
        // showing delete all menu icon if there is at least one item in db
        menu.findItem(R.id.deleteAllPhonesFromDatabaseMenuButton).setVisible(mPhoneAdapter.getItemCount() > 0);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phones_database_browsing);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // creating recyclerview
        RecyclerView phoneRecyclerView = findViewById(R.id.phoneList);
        // setting key provider
        mPhoneItemKeyProvider = new PhoneItemKeyProvider();
        // setting adapter
        mPhoneAdapter = new PhoneAdapter(mPhoneItemKeyProvider);
        phoneRecyclerView.setAdapter(mPhoneAdapter);
        // building selection tracker
        mSelectionTracker = new SelectionTracker.Builder<>("phone-selection", phoneRecyclerView, mPhoneItemKeyProvider,
                new PhoneItemDetailsLookup(phoneRecyclerView), StorageStrategy.createLongStorage()).build();
        // setting selection tracker in adapter
        mPhoneAdapter.setSelectionTracker(mSelectionTracker);
        // setting layout manager to linear
        phoneRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // dividing items with divider lines
        phoneRecyclerView.addItemDecoration(new DividerItemDecoration(phoneRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mPhoneViewModel = new ViewModelProvider(this).get(PhoneViewModel.class);

        mPhoneViewModel.getAllPhones().observe(this, phones -> {
            mPhoneAdapter.setPhoneList(phones);
            // refreshing menu state
            invalidateOptionsMenu();
        });

        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                // refreshing menu state on item selection
                invalidateOptionsMenu();
                super.onSelectionChanged();
            }

            @Override
            public void onSelectionRestored() {
                // refreshing menu state
                invalidateOptionsMenu();
                super.onSelectionRestored();
            }

            @Override
            public void onItemStateChanged(@NonNull Long key, boolean selected) {
                super.onItemStateChanged(key, selected);
            }
        });

        // allowing to delete item on swipe
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // deletes swiped item
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                List<Phone> phoneList = mPhoneViewModel.getAllPhones().getValue();
                if (phoneList != null) {
                    mPhoneViewModel.deletePhone(phoneList.get(viewHolder.getAdapterPosition()));
                    Toast.makeText(PhonesDatabaseBrowsingActivity.this, getString(R.string.message_item_deleted), Toast.LENGTH_SHORT).show();
                }
            }

            // adding background and icon on swipe
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                // so background is behind the rounded corners of itemView
                int backgroundCornerOffset = 20;

                // setting icon and color
                Drawable icon = ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_menu_delete);
                ColorDrawable background = new ColorDrawable(Color.RED);
                if (icon != null) {
                    // calculating bounds
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    // Swiping to the right
                    if (dX > 0) {
                        // calculating bounds
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + icon.getIntrinsicWidth();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        background.setBounds(itemView.getLeft(), itemView.getTop(),
                                itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                    }
                    // Swiping to the left
                    else if (dX < 0) {
                        // calculating bounds
                        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                                itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    }
                    // view is unSwiped
                    else {
                        background.setBounds(0, 0, 0, 0);
                    }
                    background.draw(c);
                    icon.draw(c);
                }
            }
        }).attachToRecyclerView(phoneRecyclerView);
    }

    // deletes selected items
    private void deleteSelected() {
        Selection<Long> selection = mSelectionTracker.getSelection();
        int phonePosition;
        List<Phone> phoneList = mPhoneViewModel.getAllPhones().getValue();
        if (phoneList != null) {
            for (long phoneId : selection) {
                phonePosition = mPhoneItemKeyProvider.getPosition(phoneId);
                mPhoneViewModel.deletePhone(phoneList.get(phonePosition));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectionTracker.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectionTracker.onRestoreInstanceState(savedInstanceState);
    }

    // adds phone to db or edits phone based on request code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PHONE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Phone phone = data.getExtras().getParcelable(PHONE_OBJECT_FROM_ACTIVITY_RESULT);
                mPhoneViewModel.insert(phone);
            }
        }
        if (requestCode == EDIT_PHONE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Phone phone = data.getExtras().getParcelable(PHONE_OBJECT_FROM_ACTIVITY_RESULT);
                mPhoneViewModel.update(phone);
            }
        }
    }
}
