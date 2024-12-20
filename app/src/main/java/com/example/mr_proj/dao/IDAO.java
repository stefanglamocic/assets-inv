package com.example.mr_proj.dao;

import com.example.mr_proj.model.DbEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public interface IDAO<T extends DbEntity> {
    Flowable<List<T>> getAll();
}
