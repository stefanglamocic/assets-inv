package com.example.mr_proj.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.mr_proj.dto.FixedAssetDetails;
import com.example.mr_proj.model.FixedAsset;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

//if I need to return a DTO
//https://developer.android.com/training/data-storage/room/accessing-data#return-subset

@Dao
public interface FixedAssetDAO extends IDAO<FixedAsset>{
    @Insert
    Single<Long> insert(FixedAsset asset);

    @Query("SELECT * FROM fixed_asset")
    Flowable<List<FixedAsset>> getAll();

    @Update
    Completable update(FixedAsset asset);

    @Query("UPDATE fixed_asset SET asset_register_id = :registerId " +
            "WHERE id IN (:fixedAssetIds)")
    Completable update(List<Integer> fixedAssetIds, int registerId);

    @Query("UPDATE fixed_asset SET asset_register_id = :assetRegisterId, " +
            "obligated_employee_id = :employeeId, " +
            "new_location_id = :locationId " +
            "WHERE id = :fixedAssetId")
    Completable update(int fixedAssetId, Integer assetRegisterId, Integer employeeId, Integer locationId);

    @Delete
    Completable delete(FixedAsset asset);

    @Query("SELECT * FROM fixed_asset WHERE name LIKE :query OR description LIKE :query")
    Flowable<List<FixedAsset>> search(String query);

    @Transaction
    @Query("SELECT * FROM fixed_asset AS fa " +
            "LEFT JOIN location AS l ON fa.location_id = l.id " +
            "LEFT JOIN employee AS e ON fa.employee_id = e.id " +
            "WHERE fa.id = :id")
    Single<FixedAssetDetails> getById(int id);

    @Transaction
    @Query("SELECT * FROM fixed_asset " +
            "WHERE fixed_asset.location_id = :id")
    Single<List<FixedAsset>> getByLocation(int id);

    @Transaction
    @Query("SELECT * FROM fixed_asset AS fa " +
            "LEFT JOIN location AS l ON fa.location_id = l.id " +
            "LEFT JOIN employee AS e ON fa.employee_id = e.id " +
            "WHERE fa.barCode = :barcode")
    Single<FixedAssetDetails> getByBarcode(long barcode);

    @Transaction
    @Query("SELECT fa.* FROM fixed_asset AS fa " +
            "LEFT JOIN asset_register AS ar ON fa.asset_register_id = ar.id " +
            "WHERE fa.asset_register_id IS NULL")
    Single<List<FixedAsset>> getAllUnregistered();

    @Transaction
    @Query("SELECT * FROM fixed_asset AS fa " +
            "WHERE fa.asset_register_id =:id OR fa.asset_register_id IS NULL")
    Single<List<FixedAsset>> getAllUnregistered(int id);

    @Transaction
    @Query("SELECT * FROM fixed_asset AS fa " +
            "LEFT JOIN employee AS e ON fa.obligated_employee_id = e.id " +
            "LEFT JOIN location AS l ON fa.new_location_id = l.id " +
            "WHERE fa.asset_register_id = :id")
    Single<List<FixedAssetDetails>> getAllByRegister(int id);
}
