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
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.app.R;
import com.example.app.file_download_app.FileDownloadService;
import com.example.app.file_download_app.FileInfo;
import com.example.app.file_download_app.GetFileInfoTask;
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
    private FileInfo fileInfo;

    // https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4
    // https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4
    // https://effigis.com/wp-content/uploads/2015/02/Airbus_Pleiades_50cm_8bit_RGB_Yogyakarta.jpg
    private static final String DOWNLOAD_ADDRESS = "https://effigis.com/wp-content/uploads/2015/02/Airbus_Pleiades_50cm_8bit_RGB_Yogyakarta.jpg";

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

        fileDownloadInformationButton.setOnClickListener(view -> {
            if (Utilities.isNetworkConnected(view)) {
                new GetFileInfoTask(fileInfo -> {
                    if (fileInfo != null) {
                        this.fileInfo = fileInfo;
                        fileDownloadButton.setEnabled(true);
                        fileDownloadSizeValueLabel.setText(fileInfo.getFileSizeStringInMB());
                        fileDownloadTypeValueLabel.setText(fileInfo.getFileType());
                    } else {
                        Toast.makeText(this, getString(R.string.error_file_download_address_input_invalid), Toast.LENGTH_SHORT).show();
                    }
                }).execute(Utilities.formattedURLString(fileDownloadAddressInput.getText().toString()));
            } else {
                Toast.makeText(this, "no internet", Toast.LENGTH_SHORT).show();
            }
        });

        fileDownloadButton.setOnClickListener(view ->
        {
            if (Utilities.isNetworkConnected(view)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    FileDownloadService.startService(this, Utilities.formattedURLString(fileDownloadAddressInput.getText().toString()));
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    }
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            } else {
                Toast.makeText(this, "no internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fileInfo = intent.getParcelableExtra(FileDownloadService.SAVED_FILE);

            fileDownloadProgressValueLabel.setText(fileInfo.getDataDownloadedStringInMB());
            //Log.d("por", fileInfo.getDataDownloaded() * 100 + "   " + fileInfo.getFileSize() + "   " + 100 * (fileInfo.getDataDownloaded() / (double) fileInfo.getFileSize()));
            fileDownloadProgressBar.setProgress((int) (100 * (fileInfo.getDataDownloaded() / (double) fileInfo.getFileSize())));
        }
    };

    private boolean isLinkValid() {
        return Patterns.WEB_URL.matcher(fileDownloadAddressInput.getText().toString()).matches();
    }

    private void setButtonsEnabled() {
        fileDownloadInformationButton.setEnabled(isLinkValid());
        fileDownloadButton.setEnabled(isLinkValid() && !fileDownloadSizeValueLabel.getText().toString().equals(getString(R.string.label_no_data)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FileDownloadService.startService(this, Utilities.formattedURLString(fileDownloadAddressInput.getText().toString()));
            } else {
                Toast.makeText(this, "sadge", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // todo jezeli fileinfo istnieje ustawienie labeli
        // jezeli result = complete
        setButtonsEnabled();
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadReceiver, new IntentFilter(FileDownloadService.BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadReceiver);
    }

    @Override
    protected void onStop() {
        Log.d("halo", "olah");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //todo saving to file info
        outState.putParcelable("filetemp", fileInfo);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if ((fileInfo = savedInstanceState.getParcelable("filetemp")) != null) {
            fileDownloadSizeValueLabel.setText(fileInfo.getFileSizeStringInMB());
            fileDownloadTypeValueLabel.setText(fileInfo.getFileType());
            if (fileInfo.getResult() == FileInfo.DOWNLOAD_SUCCESSFUL) {
                fileDownloadProgressValueLabel.setText(fileInfo.getDataDownloadedStringInMB());
            }
        }
    }
}

