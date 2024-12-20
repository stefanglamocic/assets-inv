package com.example.mr_proj.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fixed_asset")
public class FixedAsset implements DbEntity{
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public int barCode;
    public double price;
    public Long creationDate;
    public String image;

    @ColumnInfo(name = "location_id")
    public int locationId;

    @ColumnInfo(name = "employee_id")
    public int employeeId;

    @Override
    public String getRowText() {
        return name;
    }

    @Override
    public String getRowImage() {
        return image;
    }
}
