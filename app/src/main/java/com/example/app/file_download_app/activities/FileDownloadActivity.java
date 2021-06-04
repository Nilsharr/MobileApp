package com.example.app.file_download_app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.app.file_download_app.GetFileInfo;
import com.example.app.utils.Constants;
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

    // https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4
    // https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4
    private static final String DOWNLOAD_ADDRESS = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4";

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

        fileDownloadAddressInput = findViewById(R.id.fileDownloadAddressInput);
        // allowing multiline text and setting max lines to 3 (with done action button)
        fileDownloadAddressInput.setHorizontallyScrolling(false);
        fileDownloadAddressInput.setMaxLines(3);


        fileDownloadAddressInput.setText(DOWNLOAD_ADDRESS);

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

        fileDownloadAddressInput.addTextChangedListener(new TextWatcher() {
            final Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;

            @Override
            public void afterTextChanged(Editable s) {
                // Delaying validation of input field for 1 sec after each written letter
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> {
                    if (!isLinkValid()) {
                        fileDownloadAddressInput.setError(getString(R.string.error_file_download_address_input_invalid));
                    }
                };
                handler.postDelayed(workRunnable, 1000);
                setButtonsEnabled();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        fileDownloadInformationButton.setOnClickListener(view ->
                new GetFileInfo(fileInfo -> {
                    if (fileInfo != null) {
                        fileDownloadButton.setEnabled(true);
                        fileDownloadSizeValueLabel.setText(fileInfo.getFileSize());
                        fileDownloadTypeValueLabel.setText(fileInfo.getFileType());
                    } else {
                        Toast.makeText(this, getString(R.string.error_file_download_address_input_invalid), Toast.LENGTH_SHORT).show();
                    }
                }).execute(Utilities.formattedURLString(fileDownloadAddressInput.getText().toString())));

        fileDownloadButton.setOnClickListener(view ->
        {
            Toast.makeText(this, "xddd", Toast.LENGTH_SHORT).show();
            //sendBroadcast(new Intent().putExtra("INFO", "info"));
            //fileDownloadProgressValueLabel.setText("50/100");
            //fileDownloadProgressBar.setProgress(50);
        });

    }

    private boolean isLinkValid() {
        return Patterns.WEB_URL.matcher(fileDownloadAddressInput.getText().toString()).matches();
    }

    private void setButtonsEnabled() {
        fileDownloadInformationButton.setEnabled(isLinkValid());
        fileDownloadButton.setEnabled(isLinkValid() && !fileDownloadSizeValueLabel.getText().toString().equals(getString(R.string.label_no_data)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setButtonsEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.SAVED_FILE_DOWNLOAD_SIZE_VALUE_LABEL, fileDownloadSizeValueLabel.getText().toString());
        outState.putString(Constants.SAVED_FILE_DOWNLOAD_TYPE_VALUE_LABEL, fileDownloadTypeValueLabel.getText().toString());
        //outState.putString(Constants.SAVED_FILE_DOWNLOAD_PROGRESS_VALUE_LABEL, fileDownloadProgressValueLabel.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileDownloadSizeValueLabel.setText(savedInstanceState.getString(Constants.SAVED_FILE_DOWNLOAD_SIZE_VALUE_LABEL));
        fileDownloadTypeValueLabel.setText(savedInstanceState.getString(Constants.SAVED_FILE_DOWNLOAD_TYPE_VALUE_LABEL));
        //fileDownloadProgressValueLabel.setText(savedInstanceState.getString(Constants.SAVED_FILE_DOWNLOAD_PROGRESS_VALUE_LABEL));
    }
}

