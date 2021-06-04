package com.example.app.file_download_app;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class DownloadFileFromURL extends IntentService {

    public DownloadFileFromURL(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
