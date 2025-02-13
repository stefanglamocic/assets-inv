package com.example.mr_proj.dto;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mr_proj.model.AssetRegister;
import com.example.mr_proj.model.FixedAsset;

import java.util.List;

public class AssetRegisterDTO {
    @Embedded
    public AssetRegister assetRegister;
    @Relation(
            parentColumn = "id",
            entityColumn = "asset_register_id",
            entity = FixedAsset.class
    )
    public List<FixedAssetDetails> assetList;

    public AssetRegisterDTO() {}

    public AssetRegisterDTO(String name, List<FixedAssetDetails> assetList) {
        assetRegister = new AssetRegister();
        assetRegister.name = name;
        this.assetList = assetList;
    }

    @NonNull
    @Override
    public String toString() {
        return assetRegister.name + "=>" + assetList.toString();
    }
}
