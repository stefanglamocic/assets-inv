package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mr_proj.R;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.model.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EditEntityDialog<T extends DbEntity> extends AddEntityDialog {
    private final T entity;

    public EditEntityDialog(T entity) {
        this.entity = entity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog dialog = (AlertDialog) super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.edit));
        dialog.setOnShowListener(di -> setControls(dialog));

        return dialog;
    }

    public int getEntityId() {
        return entity.id;
    }

    private void setControls(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> listener.onEditPositiveClick(EditEntityDialog.this));
        if (entity instanceof Employee) {
            setEmployeeControls(dialog);
        }
        else if (entity instanceof Location) {
            setCurrentLocation();
        }
        else if (entity instanceof FixedAsset) {
            setFixedAssetControls(dialog);
        }
    }

    private void setFixedAssetControls(AlertDialog dialog) {
        
    }

    private void setEmployeeControls(AlertDialog dialog) {
        Employee employee = (Employee)entity;
        EditText firstNameET = dialog.findViewById(R.id.firstName);
        EditText lastNameET = dialog.findViewById(R.id.lastName);
        firstNameET.setText(employee.firstName);
        lastNameET.setText(employee.lastName);
    }

    private void setCurrentLocation() {
        Location location = (Location) entity;
        currentPosition = new LatLng(location.latitude, location.longitude);
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(currentPosition));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 9.2f));
    }
}
