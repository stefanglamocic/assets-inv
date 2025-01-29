package com.example.mr_proj.service;

import android.annotation.SuppressLint;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.IDAO;
import com.example.mr_proj.model.DbEntity;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DAOService {
    private static Disposable searchDisposable;

    public static <T  extends DbEntity> Disposable getEntities(ListAdapter<T> listAdapter) {
        return listAdapter.getDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listAdapter::notifyAdapter);
    }

    public static <T extends DbEntity> Disposable populateSpinner(IDAO<T> dao, Spinner spinner) {
        return dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    ArrayAdapter<T> adapter = new ArrayAdapter<>(
                            spinner.getContext(),
                            android.R.layout.simple_spinner_item,
                            list
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                });
    }

    public static <T extends DbEntity> Disposable insertEntity(T entity, ListAdapter<T> listAdapter) {
        return listAdapter.getDao()
                .insert(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    entity.id = id.intValue();
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

    @SuppressLint("NotifyDataSetChanged")
    public static <T extends DbEntity> Disposable searchEntities(String query, ListAdapter<T> listAdapter) {
        if (searchDisposable != null && !searchDisposable.isDisposed())
            searchDisposable.dispose();

        searchDisposable = listAdapter.getDao()
                .search(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    listAdapter.getEntities().clear();
                    listAdapter.getEntities().addAll(list);
                    listAdapter.notifyDataSetChanged();
                });

        return searchDisposable;
    }
}
