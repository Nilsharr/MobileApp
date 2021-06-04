package com.example.app.file_download_app;

import android.os.Parcel;
import android.os.Parcelable;

public class FileInfo /*implements Parcelable*/ {
    private String fileSize;
    private String fileType;
    private String downloadProgress;

    public FileInfo() {
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(String downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

}
