package com.example.mr_proj.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.dao.FixedAssetDAO;
import com.example.mr_proj.dao.LocationDAO;
import com.example.mr_proj.util.Converters;

@Database(entities = {FixedAsset.class, Employee.class, Location.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract FixedAssetDAO fixedAssetDAO();
    public abstract EmployeeDAO employeeDAO();
    public abstract LocationDAO locationDAO();
}
