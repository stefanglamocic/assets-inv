package com.example.mr_proj.model;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mr_proj.dao.AssetRegisterDAO;
import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.dao.FixedAssetDAO;
import com.example.mr_proj.dao.LocationDAO;
import com.example.mr_proj.util.Converters;

@Database(entities = {FixedAsset.class, Employee.class, Location.class, AssetRegister.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract FixedAssetDAO fixedAssetDAO();
    public abstract EmployeeDAO employeeDAO();
    public abstract LocationDAO locationDAO();
    public abstract AssetRegisterDAO assetRegisterDAO();

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

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
            supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS fixed_asset_temp (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "barCode INTEGER NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "creationDate INTEGER, " +
                    "image TEXT, " +
                    "location_id INTEGER, " +
                    "employee_id INTEGER, " +
                    "asset_register_id INTEGER, " +
                    "obligated_employee_id INTEGER, " +
                    "new_location_id INTEGER, " +
                    "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE SET NULL, " +
                    "FOREIGN KEY (employee_id) REFERENCES Employee(id) ON DELETE SET NULL, " +
                    "FOREIGN KEY (asset_register_id) REFERENCES asset_register(id) ON DELETE SET NULL, " +
                    "FOREIGN KEY (obligated_employee_id) REFERENCES Employee(id) ON DELETE SET NULL, " +
                    "FOREIGN KEY (new_location_id) REFERENCES Location(id) ON DELETE SET NULL)");

            supportSQLiteDatabase.execSQL("INSERT INTO fixed_asset_temp(id, name, description, barCode, " +
                    "price, creationDate, image, location_id, employee_id, asset_register_id, " +
                    "obligated_employee_id, new_location_id) " +
                    "SELECT id, name, description, barCode, price, creationDate, image, location_id, " +
                    "employee_id, asset_register_id, obligated_employee_id, new_location_id " +
                    "FROM fixed_asset");

            supportSQLiteDatabase.execSQL("DROP TABLE fixed_asset");
            supportSQLiteDatabase.execSQL("ALTER TABLE fixed_asset_temp RENAME TO fixed_asset");
        }
    };
}
