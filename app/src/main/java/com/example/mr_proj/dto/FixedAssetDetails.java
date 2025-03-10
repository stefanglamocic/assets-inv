package com.example.mr_proj.dto;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mr_proj.model.AssetRegister;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.model.Location;

import java.util.Objects;

public class FixedAssetDetails {
    @Embedded
    public FixedAsset fixedAsset;
    @Relation(
            parentColumn = "location_id",
            entityColumn = "id"
    )
    public Location location;
    @Relation(
            parentColumn = "employee_id",
            entityColumn = "id"
    )
    public Employee employee;
    @Relation(
            parentColumn = "asset_register_id",
            entityColumn = "id"
    )
    public AssetRegister assetRegister;
    @Relation(
            parentColumn = "obligated_employee_id",
            entityColumn = "id",
            entity = Employee.class
    )
    public Employee obligatedEmployee;
    @Relation(
            parentColumn = "new_location_id",
            entityColumn = "id",
            entity = Location.class
    )
    public Location newLocation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixedAssetDetails)) return false;
        FixedAssetDetails that = (FixedAssetDetails) o;
        return Objects.equals(fixedAsset, that.fixedAsset);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fixedAsset);
    }

    @Override
    public String toString() {
        return "FixedAssetDetails{" +
                "fixedAsset=" + fixedAsset +
                ", location=" + location +
                ", employee=" + employee +
                ", assetRegister=" + assetRegister +
                ", obligatedEmployee=" + obligatedEmployee +
                ", newLocation=" + newLocation +
                '}';
    }
}
