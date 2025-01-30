package com.example.mr_proj.fragments.main;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mr_proj.R;
import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.dao.FixedAssetDAO;
import com.example.mr_proj.dao.LocationDAO;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.fragments.dialog.RemoveEntityDialog;
import com.example.mr_proj.model.AppDatabase;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.model.Location;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DatabaseUtil;
import com.example.mr_proj.util.DialogUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import io.reactivex.rxjava3.disposables.Disposable;


public class FixedAssetsFragment extends BaseFragment<FixedAsset>
    implements AddEntityDialog.DialogListener,
        AddEntityDialog.FixedAssetsButtonsListener,
        RemoveEntityDialog.RemoveDialogListener {
    //dao
    private EmployeeDAO employeeDAO;
    private LocationDAO locationDAO;

    private final AddEntityDialog dialog = new AddEntityDialog();

    //activity result
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<Uri> takePhotoLauncher;

    private Uri photoUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fixed_assets, container, false);

        AppDatabase db = DatabaseUtil.getDbInstance(requireContext());
        employeeDAO = db.employeeDAO();
        locationDAO = db.locationDAO();
        FixedAssetDAO fixedAssetDAO = db.fixedAssetDAO();

        listAdapter = new ListAdapter<>(fixedAssetDAO, this);
        listAdapter.setRowClickListener(this::onItemSelect);
        Disposable d = DAOService.getEntities(listAdapter);
        disposables.add(d);

        RecyclerView fixedAssetsRecyclerView = root.findViewById(R.id.fixed_assets_list);
        fixedAssetsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fixedAssetsRecyclerView.setAdapter(listAdapter);

        barcodeLauncher = registerForActivityResult(new ScanContract(), dialog::setBarcodeField);
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), dialog::setMedia);
        takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), photoTaken -> {
            if (photoTaken)
                dialog.setMedia(photoUri);
        });

        FloatingActionButton addBtn = root.findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this::onAdd);

        return root;
    }

    private void onAdd(View view) {
        dialog.show(getChildFragmentManager(), "addFixedAsset");
    }

    @Override
    public void onAddPositiveClick(DialogFragment dialog) {

        dialog.dismiss();
    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {

        dialog.dismiss();
    }

    private FixedAsset extractFromFields(DialogFragment dialog) {
        long creationDate = System.currentTimeMillis();
        String name = DialogUtil.getFieldValue(dialog, R.id.fixed_asset_name);
        String desc = DialogUtil.getFieldValue(dialog, R.id.fixed_asset_desc);
        int barCode = Integer.parseInt(DialogUtil.getFieldValue(dialog, R.id.bar_code));
        double price = Double.parseDouble(DialogUtil.getFieldValue(dialog, R.id.price));
        Location location = (Location)DialogUtil.getObjectFromSpinner(dialog, R.id.locations_spinner);
        Employee employee = (Employee) DialogUtil.getObjectFromSpinner(dialog, R.id.employees_spinner);

        return new FixedAsset();
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) { //entity deletion
        RemoveEntityDialog<? extends DbEntity> removeDialog = (RemoveEntityDialog<? extends DbEntity>) dialog;
        int id = removeDialog.getEntityId();
        FixedAsset entity = new FixedAsset();
        entity.id = id;
        Disposable d = DAOService.deleteEntity(entity, listAdapter);
        disposables.add(d);
    }

    @Override
    public void onScannerOpen(View view) {
        ScanOptions scanOptions = new ScanOptions();
        String notice = getString(R.string.scan_barcode);
        scanOptions.setPrompt(notice);
        scanOptions.setOrientationLocked(false);
        barcodeLauncher.launch(scanOptions);
    }

    @Override
    public void onImagePickerOpen(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void onCameraOpen(View view) {
        //TODO: create image file and set to photoUri...
        takePhotoLauncher.launch(photoUri);
    }


    private void onItemSelect(FixedAsset fixedAsset) { //display fixed asset details

    }

    public EmployeeDAO getEmployeeDAO() {
        return employeeDAO;
    }

    public LocationDAO getLocationDAO() {
        return locationDAO;
    }
}