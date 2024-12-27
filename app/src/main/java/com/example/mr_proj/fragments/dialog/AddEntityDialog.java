package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mr_proj.dao.IDAO;
import com.example.mr_proj.model.DbEntity;

public class AddEntityDialog<T extends DbEntity> extends DialogFragment {
    protected final IDAO<T> dao;

    public AddEntityDialog(IDAO<T> dao) {
        this.dao = dao;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder.create();
    }
}
