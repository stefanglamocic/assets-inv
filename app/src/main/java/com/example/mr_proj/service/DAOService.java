package com.example.mr_proj.service;

import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.model.DbEntity;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DAOService {
    public static <T  extends DbEntity> Disposable getEntities(ListAdapter<T> listAdapter) {
        return listAdapter.getDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listAdapter::notifyAdapter);
    }

    public static <T extends DbEntity> Disposable insertEntity(T entity, ListAdapter<T> listAdapter) {
        return listAdapter.getDao()
                .insert(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    listAdapter.getEntities().add(entity);
                    listAdapter.notifyItemInserted(listAdapter.getItemCount() - 1);
                });
    }

    public static <T extends DbEntity> Disposable updateEntity(T entity, ListAdapter<T> listAdapter) {
        return listAdapter.getDao()
                .update(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    int position = listAdapter.getEntities().indexOf(entity);
                    listAdapter.getEntities().set(position, entity);
                    listAdapter.notifyItemChanged(position);
                });
    }

    public static <T extends DbEntity> Disposable deleteEntity(T entity, ListAdapter<T> listAdapter) {
        return listAdapter.getDao()
                .delete(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    List<T> entities = listAdapter.getEntities();
                    int position = entities.indexOf(entity);
                    entities.remove(position);
                    listAdapter.notifyItemRemoved(position);
                });
    }
}
