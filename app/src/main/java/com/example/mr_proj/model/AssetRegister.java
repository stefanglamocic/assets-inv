package com.example.mr_proj.model;

import androidx.room.Entity;

@Entity(tableName = "asset_register")
public class AssetRegister extends DbEntity{
    private static final String DEFAULT_IMG = "ic_list";

    public String name;


    @Override
    public String getRowText() {
        return name;
    }

    @Override
    public String getRowImage() {
        return DEFAULT_IMG;
    }
}
