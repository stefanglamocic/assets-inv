package com.example.mr_proj.service;

import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.IDAO;
import com.example.mr_proj.model.DbEntity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DAOService {
    public static <T extends IDAO<U>, U extends DbEntity> Disposable getEntities(T dao, ListAdapter<U> listAdapter) {
        return dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listAdapter::notifyAdapter);
    }
}
