package com.example.mr_proj.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "fixed_asset",
        foreignKeys = {
                @ForeignKey(
                        entity = Location.class,
                        parentColumns = "id",
                        childColumns = "location_id",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "employee_id",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = AssetRegister.class,
                        parentColumns = "id",
                        childColumns = "asset_register_id",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "obligated_employee_id",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Location.class,
                        parentColumns = "id",
                        childColumns = "new_location_id",
                        onDelete = ForeignKey.SET_NULL
                )
        })
public class FixedAsset extends DbEntity{
    private static final String DEFAULT_IMG = "ic_object";

    public String name;
    public String description;
    public long barCode;
    public double price;
    public Long creationDate;
    public String image;

    @ColumnInfo(name = "location_id")
    public Integer locationId;

    @ColumnInfo(name = "employee_id")
    public Integer employeeId;

    @ColumnInfo(name = "asset_register_id")
    public Integer assetRegisterId;

    @ColumnInfo(name = "obligated_employee_id")
    public Integer obligatedEmployeeId;

    @ColumnInfo(name = "new_location_id")
    public Integer newLocationId;

    public FixedAsset() {
        super();
    }

    public FixedAsset(String name, String description, long barCode, double price, String image, Integer locationId, Integer employeeId) {
        this.name = name;
        this.description = description;
        this.barCode = barCode;
        this.price = price;
        this.image = image;
        this.locationId = locationId;
        this.employeeId = employeeId;
    }

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
