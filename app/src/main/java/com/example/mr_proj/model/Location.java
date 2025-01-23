package com.example.mr_proj.model;

import androidx.room.Entity;

import com.google.android.gms.maps.model.LatLng;

@Entity
public class Location extends DbEntity{
    private static final String IMG = "ic_location_city";

    public Location() {}

    public Location(LatLng location) {
        latitude = location.latitude;
        longitude = location.longitude;
    }

    public Location(String city, LatLng location) {
        this(location);
        this.city = city;
    }

    public String city;
    public double latitude;
    public double longitude;

    @Override
    public String getRowText() {
        return city;
    }

    @Override
    public String getRowImage() {
        return IMG;
    }
}
