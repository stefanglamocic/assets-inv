package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mr_proj.R;
import com.example.mr_proj.model.DbEntity;

public class RemoveEntityDialog<T extends DbEntity> extends DialogFragment {
    private final T entity;
    private RemoveDialogListener listener;

    public RemoveEntityDialog(T entity) {
        this.entity = entity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (RemoveDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment() + " needs to implement RemoveDialogListener!");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String actionText = getString(R.string.remove);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(actionText + " " + entity.getRowText() + "?")
                .setPositiveButton(R.string.confirm, (dialog, id) ->
                        listener.onPositiveClick(RemoveEntityDialog.this))
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});
        return builder.create();
    }

    public int getEntityId() { return entity.id; }

    public interface RemoveDialogListener {
        void onPositiveClick(DialogFragment dialog);
    }
}
