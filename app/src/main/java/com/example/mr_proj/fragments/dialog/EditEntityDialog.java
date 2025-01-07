package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mr_proj.R;
import com.example.mr_proj.dao.IDAO;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.model.Employee;

public class EditEntityDialog<T extends DbEntity> extends AddEntityDialog<T> {
    private final T entity;

    public EditEntityDialog(T entity) {
        this.entity = entity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.edit));
        dialog.setOnShowListener(di -> setControls(dialog));

        return dialog;
    }

    private void setControls(Dialog dialog) {
        if (entity instanceof Employee) {
            Employee employee = (Employee)entity;
            EditText firstNameET = dialog.findViewById(R.id.firstName);
            EditText lastNameET = dialog.findViewById(R.id.lastName);
            firstNameET.setText(employee.firstName);
            lastNameET.setText(employee.lastName);
        }
    }
}
