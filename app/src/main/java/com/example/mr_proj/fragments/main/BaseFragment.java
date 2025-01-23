package com.example.mr_proj.fragments.main;

import androidx.fragment.app.Fragment;

import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.service.DAOService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseFragment<T extends DbEntity> extends Fragment
    implements ListAdapter.Filterable{
    final List<Disposable> disposables = new ArrayList<>();
    ListAdapter<T> listAdapter;

    @Override
    public void filter(String query) {
        String searchQuery = "%" + query.trim() + "%";
        Disposable d = DAOService.searchEntities(searchQuery, listAdapter);
        disposables.add(d);
    }

    @Override
    public void onDestroy() {
        for (Disposable d : disposables) {
            if (d != null && !d.isDisposed())
                d.dispose();
        }

        super.onDestroy();
    }


}
