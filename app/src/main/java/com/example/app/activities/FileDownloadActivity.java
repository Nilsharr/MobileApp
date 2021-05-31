package com.example.app.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.utils.Utilities;

import java.util.Objects;

public class FileDownloadActivity extends AppCompatActivity {
    private EditText fileDownloadAddressInput;
    private Button fileDownloadInformationButton;
    private TextView fileDownloadSizeValueLabel;
    private TextView fileDownloadTypeValueLabel;
    private Button fileDownloadButton;
    private TextView fileDownloadProgressValueLabel;
    private ProgressBar fileDownloadProgressBar;

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

        fileDownloadInformationButton = findViewById(R.id.fileDownloadInformationButton);
        fileDownloadSizeValueLabel = findViewById(R.id.fileDownloadSizeValueLabel);
        fileDownloadTypeValueLabel = findViewById(R.id.fileDownloadTypeValueLabel);
        fileDownloadButton = findViewById(R.id.fileDownloadButton);
        fileDownloadProgressValueLabel = findViewById(R.id.fileDownloadProgressValueLabel);
        fileDownloadProgressBar = findViewById(R.id.fileDownloadProgressBar);

        //fileDownloadSizeValueLabel.setText("qwefdgrty");
        //fileDownloadTypeValueLabel.setText("qwerty");
        //fileDownloadProgressValueLabel.setText("qwty");

        fileDownloadAddressInput = findViewById(R.id.fileDownloadAddressInput);
        // allowing multiline text and setting max lines to 3 (with done action button)
        fileDownloadAddressInput.setHorizontallyScrolling(false);
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

        /*
        String urlAddress = fileDownloadAddressInput.getText().toString();
            if (Patterns.WEB_URL.matcher(urlAddress).matches()) {
                if (!urlAddress.startsWith("https://") && !urlAddress.startsWith("http://")) {
                    urlAddress = "https://" + urlAddress;
                }
         */

        fileDownloadAddressInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (!isLinkValid()) {
                    fileDownloadAddressInput.setError(getString(R.string.error_file_download_address_input_invalid));
                    setButtonsEnabled(false);
                } else {
                    setButtonsEnabled(true);
                }
            }
        });

        fileDownloadInformationButton.setOnClickListener(view -> {
            if (isLinkValid()) {
                Toast.makeText(this, "xd", Toast.LENGTH_SHORT).show();
            } else {
                setButtonsEnabled(false);
            }
        });

        fileDownloadButton.setOnClickListener(view -> {
            if (isLinkValid()) {
                Toast.makeText(this, "xddd", Toast.LENGTH_SHORT).show();
            } else {
                setButtonsEnabled(false);
            }
        });
    }

    private boolean isLinkValid() {
        return Patterns.WEB_URL.matcher(fileDownloadAddressInput.getText().toString()).matches();
    }

    private void setButtonsEnabled(boolean enabled) {
        fileDownloadInformationButton.setEnabled(enabled);
        fileDownloadButton.setEnabled(enabled);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setButtonsEnabled(isLinkValid());
    }
}

