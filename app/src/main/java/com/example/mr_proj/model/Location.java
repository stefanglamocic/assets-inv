package com.example.mr_proj.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Location extends DbEntity{
    private static final String IMG = "ic_location_city";

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
