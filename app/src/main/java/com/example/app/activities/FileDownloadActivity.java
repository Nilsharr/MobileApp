package com.example.app.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.utils.Utilities;

import java.util.Objects;

public class FileDownloadActivity extends AppCompatActivity {
    private EditText fileDownloadAddressInput;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Returning to previous activity
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        
        fileDownloadAddressInput = findViewById(R.id.fileDownloadAddressInput);
        // allowing multiline text and setting max lines to 3 (with done action button)
        fileDownloadAddressInput.setHorizontallyScrolling(false);
        // maybe 4
        fileDownloadAddressInput.setMaxLines(3);

        // creating home button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // listener set on screen that on screen touch clears focus of current input field and closes keyboard
        findViewById(R.id.fileDownloadLayout).setOnTouchListener(((view, event) -> {
            Utilities.clearFocusAndHideKeyboard(view);
            view.performClick();
            return false;
        }));

        // listener on done keyboard button that clears focus of current input field and closes keyboard
        fileDownloadAddressInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Utilities.clearFocusAndHideKeyboard(view);
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

