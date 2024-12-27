package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mr_proj.dao.IDAO;
import com.example.mr_proj.model.DbEntity;

public class EditEntityDialog<T extends DbEntity> extends AddEntityDialog<T> {
    private final T entity;

    public EditEntityDialog(T entity, IDAO<T> dao) {
        super(dao);
        this.entity = entity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder.create();
    }
}
