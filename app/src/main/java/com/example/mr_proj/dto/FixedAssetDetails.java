package com.example.mr_proj.dto;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mr_proj.model.Employee;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.model.Location;

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
}
