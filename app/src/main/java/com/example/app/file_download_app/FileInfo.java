package com.example.app.file_download_app;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.app.utils.Utilities;

import java.util.Locale;

public class FileInfo implements Parcelable {

    public static final int DOWNLOAD_SUCCESSFUL = 0;
    public static final int DOWNLOAD_IN_PROGRESS = 1;
    public static final int DOWNLOAD_ERROR = 2;

    private int fileSize;
    private String fileType;
    private int dataDownloaded;
    private int result = DOWNLOAD_IN_PROGRESS;


    public FileInfo() {
    }

    public FileInfo(int fileSize, String fileType) {
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getDataDownloaded() {
        return dataDownloaded;
    }

    public void setDataDownloaded(int dataDownloaded) {
        this.dataDownloaded = dataDownloaded;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getFileSizeStringInMB() {
        return String.format(Locale.getDefault(), "%.2f MB", Utilities.bytesToMegaBytes(fileSize));
    }

    public String getDataDownloadedStringInMB() {
        return String.format(Locale.getDefault(), "%.2f MB", Utilities.bytesToMegaBytes(dataDownloaded));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.fileSize);
        dest.writeString(this.fileType);
        dest.writeInt(this.dataDownloaded);
        dest.writeInt(this.result);
    }

    public void readFromParcel(Parcel source) {
        this.fileSize = source.readInt();
        this.fileType = source.readString();
        this.dataDownloaded = source.readInt();
        this.result = source.readInt();
    }

    protected FileInfo(Parcel in) {
        this.fileSize = in.readInt();
        this.fileType = in.readString();
        this.dataDownloaded = in.readInt();
        this.result = in.readInt();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
