package com.example.mr_proj.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "fixed_asset")
public class FixedAsset extends DbEntity{
    private static final String DEFAULT_IMG = "ic_object";

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
        if (image == null)
            return DEFAULT_IMG;
        return image;
    }
}
