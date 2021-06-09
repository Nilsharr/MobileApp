package com.example.app.file_download_app.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.app.R;
import com.example.app.file_download_app.services.FileDownloadService;
import com.example.app.file_download_app.models.FileInfo;
import com.example.app.file_download_app.services.GetFileInfoTask;
import com.example.app.utils.Utilities;

import java.util.Objects;

public class FileDownloadActivity extends AppCompatActivity {

    private static final String SAVED_FILE_INFO_STATE = "com.example.app.file_download_app.activities.savedFileInfoState";
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 4;

    private EditText fileDownloadAddressInput;
    private Button fileDownloadInformationButton;
    private TextView fileDownloadSizeValueLabel;
    private TextView fileDownloadTypeValueLabel;
    private Button fileDownloadButton;
    private TextView fileDownloadProgressValueLabel;
    private ProgressBar fileDownloadProgressBar;
    private FileInfo fileInfo;

    // Returning to main menu on back arrow click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        // allowing multiline text and setting max lines to 3
        fileDownloadAddressInput.setHorizontallyScrolling(false);
        fileDownloadAddressInput.setMaxLines(3);

        // temporary url address
        // https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4
        // https://effigis.com/wp-content/uploads/2015/02/Airbus_Pleiades_50cm_8bit_RGB_Yogyakarta.jpg
        final String DOWNLOAD_ADDRESS = "https://effigis.com/wp-content/uploads/2015/02/Airbus_Pleiades_50cm_8bit_RGB_Yogyakarta.jpg";
        fileDownloadAddressInput.setText(DOWNLOAD_ADDRESS);

        // creating home button (back arrow)
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // listener set on screen that on screen touch clears focus of current input field and closes keyboard
        findViewById(R.id.fileDownloadConstraintLayout).setOnTouchListener(((view, event) -> {
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

        // listener on text input
        // validates after 1 sec has passed after user entered letter
        // (time refreshes on next letter)
        fileDownloadAddressInput.addTextChangedListener(new TextWatcher() {
            final Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;

            @Override
            public void afterTextChanged(Editable s) {
                // Delaying validation of input field for 1 sec after each written letter
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> {
                    // validation
                    if (!Utilities.isURLStringValid(fileDownloadAddressInput.getText().toString())) {
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

        // checks if device is connected to internet
        // if it isn't connected appropriate message is shown
        // if it is connected async task is started that
        // returns information about file size and type
        fileDownloadInformationButton.setOnClickListener(view -> {
            if (Utilities.isNetworkConnected(view)) {
                new GetFileInfoTask(fileInfo -> {
                    if (fileInfo != null) {
                        this.fileInfo = fileInfo;
                        fileDownloadButton.setEnabled(true);
                        fileDownloadSizeValueLabel.setText(fileInfo.getFileSizeInMBString());
                        fileDownloadTypeValueLabel.setText(fileInfo.getType());
                    } else {
                        Toast.makeText(this, getString(R.string.error_file_download_address_input_invalid), Toast.LENGTH_SHORT).show();
                    }
                }).execute(Utilities.formattedURLString(fileDownloadAddressInput.getText().toString()));
            } else {
                Toast.makeText(this, getString(R.string.information_no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

        // checks if device is connected to internet
        // if it isn't connected appropriate message is shown
        // if it is connected checks if user has given permission to write to external storage
        // if permission was given download starts
        // if it's first time clicking this button user gets asked for permission
        fileDownloadButton.setOnClickListener(view ->
        {
            if (Utilities.isNetworkConnected(view)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    FileDownloadService.startService(this, Utilities.formattedURLString(fileDownloadAddressInput.getText().toString()));
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            } else {
                Toast.makeText(this, getString(R.string.information_no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // enables or disables buttons based on entered address validity
    private void setButtonsEnabled() {
        fileDownloadInformationButton.setEnabled(Utilities.isURLStringValid(fileDownloadAddressInput.getText().toString()));
        fileDownloadButton.setEnabled(Utilities.isURLStringValid(fileDownloadAddressInput.getText().toString()) && !fileDownloadSizeValueLabel.getText().toString().equals(getString(R.string.label_no_data)));
    }

    // if user agreed to give asked permission
    // service starts that downloads file from given url address
    // else toast message explains tha file cannot be downloaded without permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FileDownloadService.startService(this, Utilities.formattedURLString(fileDownloadAddressInput.getText().toString()));
            } else {
                Toast.makeText(this, getString(R.string.message_cannot_download_without_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // when broadcast from service is received
    // if there was problem downloading file, progress is cleared
    // else label and progressbar are updated with received information
    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fileInfo = intent.getParcelableExtra(FileDownloadService.FILE_DOWNLOAD_INFO);
            if (fileInfo.getResult() == FileInfo.DOWNLOAD_ERROR) {
                fileDownloadProgressValueLabel.setText(getString(R.string.label_no_data));
                fileDownloadProgressBar.setProgress(0);
                Toast.makeText(FileDownloadActivity.this, getString(R.string.message_error_while_downloading_file), Toast.LENGTH_SHORT).show();
            } else {
                fileDownloadProgressValueLabel.setText(fileInfo.getDataDownloadedInMBString());
                fileDownloadProgressBar.setProgress(Utilities.percentOfNumber(fileInfo.getDataDownloaded(), fileInfo.getSize()));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setButtonsEnabled();
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadReceiver, new IntentFilter(FileDownloadService.FILE_DOWNLOAD_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadReceiver);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_FILE_INFO_STATE, fileInfo);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if ((fileInfo = savedInstanceState.getParcelable(SAVED_FILE_INFO_STATE)) != null) {
            fileDownloadSizeValueLabel.setText(fileInfo.getFileSizeInMBString());
            fileDownloadTypeValueLabel.setText(fileInfo.getType());
            if (fileInfo.getResult() == FileInfo.DOWNLOAD_SUCCESSFUL) {
                fileDownloadProgressValueLabel.setText(fileInfo.getDataDownloadedInMBString());
            }
        }
    }
}

