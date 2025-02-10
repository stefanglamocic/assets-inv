package com.example.mr_proj.util;

import android.content.Context;

import androidx.room.Room;

import com.example.mr_proj.model.AppDatabase;

public class DatabaseUtil {
    private static AppDatabase db;

    private DatabaseUtil() {}

    public static synchronized AppDatabase getDbInstance(Context context) {
        if (db == null)
            db = Room.databaseBuilder(context, AppDatabase.class, "mrdb")
                    .addMigrations(AppDatabase.MIGRATION_1_2,
                            AppDatabase.MIGRATION_2_3)
                    .build();
        return db;
    }
}
