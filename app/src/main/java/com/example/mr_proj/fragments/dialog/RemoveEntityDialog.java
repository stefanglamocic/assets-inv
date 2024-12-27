package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mr_proj.R;
import com.example.mr_proj.dao.IDAO;
import com.example.mr_proj.model.DbEntity;

public class RemoveEntityDialog<T extends DbEntity> extends DialogFragment {
    private final T entity;
    private final IDAO<T> dao;

    public RemoveEntityDialog(T entity, IDAO<T> dao) {
        this.entity = entity;
        this.dao = dao;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String actionText = getString(R.string.remove);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(actionText + " " + entity.getRowText() + "?")
                .setPositiveButton(R.string.confirm, (dialog, id) -> {})
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});
        return builder.create();
    }
}
