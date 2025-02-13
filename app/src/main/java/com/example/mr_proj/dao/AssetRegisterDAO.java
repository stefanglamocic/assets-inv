package com.example.mr_proj.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.mr_proj.dto.AssetRegisterDTO;
import com.example.mr_proj.model.AssetRegister;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface AssetRegisterDAO extends IDAO<AssetRegister>{
    @Query("SELECT * FROM asset_register")
    Flowable<List<AssetRegister>> getAll();

    @Insert
    Single<Long> insert(AssetRegister assetRegister);

    @Update
    Completable update(AssetRegister assetRegister);

    @Delete
    Completable delete(AssetRegister assetRegister);

    @Query("SELECT * FROM asset_register WHERE name LIKE :query")
    Flowable<List<AssetRegister>> search(String query);

    @Transaction
    @Query("SELECT * FROM asset_register AS ar " +
            "LEFT JOIN fixed_asset AS fa ON ar.id = fa.asset_register_id " +
            "LEFT JOIN employee ON fa.obligated_employee_id = employee.id " +
            "LEFT JOIN location ON fa.new_location_id = location.id " +
            "WHERE ar.id = :id")
    Single<AssetRegisterDTO> getById(int id);
}
