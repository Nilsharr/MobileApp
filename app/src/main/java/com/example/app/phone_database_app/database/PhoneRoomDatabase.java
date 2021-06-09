package com.example.app.phone_database_app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Phone.class}, version = 1, exportSchema = false)
public abstract class PhoneRoomDatabase extends RoomDatabase {
    private static final String DB_NAME = "phone_db";
    private static final int NUMBER_OF_THREADS = 4;

    public abstract PhoneDao phoneDao();

    private static volatile PhoneRoomDatabase instance;

    public static PhoneRoomDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (PhoneRoomDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), PhoneRoomDatabase.class, DB_NAME).addCallback(sRoomDatabaseCallback).fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }

    // to run queries in the background
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> instance.phoneDao());
        }
    };

}
