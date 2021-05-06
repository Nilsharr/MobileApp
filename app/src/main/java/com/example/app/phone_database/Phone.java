package com.example.app.phone_database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "phone")
public class Phone implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    @ColumnInfo(name = "manufacturer")
    private String manufacturer;
    @NonNull
    @ColumnInfo(name = "model")
    private String model;
    @NonNull
    @ColumnInfo(name = "androidVersion")
    private String androidVersion;
    @NonNull
    @ColumnInfo(name = "webAddress")
    private String webAddress;


    public Phone(long id, @NonNull String manufacturer, @NonNull String model, @NonNull String androidVersion, @NonNull String webAddress) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.androidVersion = androidVersion;
        this.webAddress = webAddress;
    }

    @Ignore
    public Phone(@NonNull String manufacturer, @NonNull String model, @NonNull String androidVersion, @NonNull String webAddress) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.androidVersion = androidVersion;
        this.webAddress = webAddress;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(@NonNull String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @NonNull
    public String getModel() {
        return model;
    }

    public void setModel(@NonNull String model) {
        this.model = model;
    }

    @NonNull
    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(@NonNull String androidVersion) {
        this.androidVersion = androidVersion;
    }

    @NonNull
    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(@NonNull String webAddress) {
        this.webAddress = webAddress;
    }

    // implementing Parcelable interface
    protected Phone(Parcel in) {
        id = in.readLong();
        manufacturer = in.readString();
        model = in.readString();
        androidVersion = in.readString();
        webAddress = in.readString();
    }

    public static final Creator<Phone> CREATOR = new Creator<Phone>() {
        @Override
        public Phone createFromParcel(Parcel source) {
            return new Phone(source);
        }

        @Override
        public Phone[] newArray(int size) {
            return new Phone[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(manufacturer);
        dest.writeString(model);
        dest.writeString(androidVersion);
        dest.writeString(webAddress);
    }
}
