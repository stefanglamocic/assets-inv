package com.example.mr_proj.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mr_proj.model.Location;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface LocationDAO extends IDAO<Location> {
    @Insert
    Single<Long> insert(Location location);

    @Update
    Completable update(Location location);

    @Delete
    Completable delete(Location location);

    @Query("SELECT * FROM location")
    Flowable<List<Location>> getAll();

    @Query("SELECT * FROM location WHERE city LIKE :query")
    Flowable<List<Location>> search(String query);
}
