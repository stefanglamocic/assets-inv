package com.example.mr_proj.model;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.dao.FixedAssetDAO;
import com.example.mr_proj.dao.LocationDAO;
import com.example.mr_proj.util.Converters;

@Database(entities = {FixedAsset.class, Employee.class, Location.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract FixedAssetDAO fixedAssetDAO();
    public abstract EmployeeDAO employeeDAO();
    public abstract LocationDAO locationDAO();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
            supportSQLiteDatabase.execSQL("ALTER TABLE location ADD COLUMN latitude REAL NOT NULL DEFAULT 0");
            supportSQLiteDatabase.execSQL("ALTER TABLE location ADD COLUMN longitude REAL NOT NULL DEFAULT 0");
        }
    };
}
