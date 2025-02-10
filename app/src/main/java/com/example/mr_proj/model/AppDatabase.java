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

@Database(entities = {FixedAsset.class, Employee.class, Location.class, AssetRegister.class}, version = 3)
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

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
            supportSQLiteDatabase.execSQL("ALTER TABLE fixed_asset ADD COLUMN asset_register_id INTEGER");
            supportSQLiteDatabase.execSQL("ALTER TABLE fixed_asset ADD COLUMN obligated_employee_id INTEGER");
            supportSQLiteDatabase.execSQL("ALTER TABLE fixed_asset ADD COLUMN new_location_id INTEGER");

            supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS asset_register (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT)");
        }
    };
}
