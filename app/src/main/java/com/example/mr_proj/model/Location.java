package com.example.mr_proj.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location implements DbEntity{
    private static final String IMG = "ic_location_city";

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String city;

    @Override
    public String getRowText() {
        return city;
    }

    @Override
    public String getRowImage() {
        return IMG;
    }
}
