package com.example.mr_proj.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mr_proj.model.FixedAsset;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

//ako treba vratiti DTO
//https://developer.android.com/training/data-storage/room/accessing-data#return-subset

@Dao
public interface FixedAssetDAO extends IDAO<FixedAsset>{
    @Insert
    Single<Long> insert(FixedAsset asset);

    @Query("SELECT * FROM fixed_asset")
    Flowable<List<FixedAsset>> getAll();

    @Update
    Completable update(FixedAsset asset);

    @Delete
    Completable delete(FixedAsset asset);

    @Query("SELECT * FROM fixed_asset WHERE name LIKE :query OR description LIKE :query")
    Flowable<List<FixedAsset>> search(String query);
}
