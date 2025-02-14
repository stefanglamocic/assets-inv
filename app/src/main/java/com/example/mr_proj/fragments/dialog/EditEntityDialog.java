package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.mr_proj.R;
import com.example.mr_proj.adapter.AssetRegisterItemsAdapter;
import com.example.mr_proj.dto.AssetRegisterDTO;
import com.example.mr_proj.model.AppDatabase;
import com.example.mr_proj.model.AssetRegister;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.model.Location;
import com.example.mr_proj.util.DatabaseUtil;
import com.example.mr_proj.util.DialogUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

    public T getEntity() { return entity; }

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
        else if (entity instanceof AssetRegister) {
            setAssetRegisterControls(dialog);
        }
    }

    @Override
    protected void initAssetsRegisterDialog(View dialogView) {
        initRegisterItemsAdapter(dialogView);
    }

    @Override
    protected void initRegisterItemsAdapter(View dialogView) {
        ActivityResultLauncher<ScanOptions> barcodeScannerLauncher =
                registerForActivityResult(new ScanContract(), this::barcodeScanned);
        AppDatabase db = DatabaseUtil.getDbInstance(getContext());

        Disposable d =
                db.fixedAssetDAO()
                .getAllByRegister(getEntityId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    registerItemsAdapter = new AssetRegisterItemsAdapter(db, list, barcodeScannerLauncher);
                    initAssetRegisterViews(dialogView);
                });
        compositeDisposable.add(d);
    }

    private void setAssetRegisterControls(AlertDialog dialog) {
        AssetRegister assetRegister = (AssetRegister) entity;

        EditText arName = dialog.findViewById(R.id.asset_register_name);
        arName.setText(assetRegister.name);
    }

    private void setFixedAssetControls(AlertDialog dialog) {
        FixedAsset fixedAsset = (FixedAsset) entity;

        EditText faName = dialog.findViewById(R.id.fixed_asset_name);
        faName.setText(fixedAsset.name);
        EditText faPrice = dialog.findViewById(R.id.price);
        faPrice.setText(String.valueOf(fixedAsset.price));
        EditText faDesc = dialog.findViewById(R.id.fixed_asset_desc);
        faDesc.setText(fixedAsset.description);
        EditText faBarCode = dialog.findViewById(R.id.bar_code);
        faBarCode.setText(String.valueOf(fixedAsset.barCode));
        //image related stuff
        TextView faImagePath = dialog.findViewById(R.id.fixed_asset_image);
        ImageView faImagePreview = dialog.findViewById(R.id.fixed_asset_image_preview);
        FloatingActionButton cancelFab = dialog.findViewById(R.id.cancel_image);

        if (fixedAsset.image == null) {
            faImagePath.setText(R.string.no_image);
            faImagePath.setVisibility(View.VISIBLE);
            faImagePreview.setVisibility(View.GONE);
            cancelFab.setVisibility(View.GONE);
        }
        else {
            faImagePath.setText(fixedAsset.image);
            faImagePath.setVisibility(View.GONE);
            faImagePreview.setVisibility(View.VISIBLE);
            cancelFab.setVisibility(View.VISIBLE);

            Glide
                    .with(requireContext())
                    .load(Uri.parse(fixedAsset.image))
                    .into(faImagePreview);
        }
        //spinners
        Spinner faLocation = dialog.findViewById(R.id.locations_spinner);
        DialogUtil.setSpinnerItem(faLocation, fixedAsset.locationId);
        Spinner faEmployee = dialog.findViewById(R.id.employees_spinner);
        DialogUtil.setSpinnerItem(faEmployee, fixedAsset.employeeId);
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
