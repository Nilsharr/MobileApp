package com.example.app.file_download_app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.app.utils.Utilities;

import java.util.Locale;

public class FileInfo implements Parcelable {

    public static final int DOWNLOAD_SUCCESSFUL = 0;
    public static final int DOWNLOAD_IN_PROGRESS = 1;
    public static final int DOWNLOAD_ERROR = 2;

    private int size;
    private String type;
    private int dataDownloaded;
    private int result = DOWNLOAD_IN_PROGRESS;

    public FileInfo(int size, String type) {
        this.size = size;
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getFileSizeInMBString() {
        return String.format(Locale.getDefault(), "%.2f MB", Utilities.bytesToMegaBytes(size));
    }

    public String getDataDownloadedInMBString() {
        return String.format(Locale.getDefault(), "%.2f MB", Utilities.bytesToMegaBytes(dataDownloaded));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.size);
        dest.writeString(this.type);
        dest.writeInt(this.dataDownloaded);
        dest.writeInt(this.result);
    }

    protected FileInfo(Parcel in) {
        this.size = in.readInt();
        this.type = in.readString();
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
