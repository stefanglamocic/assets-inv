package com.example.mr_proj.fragments.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mr_proj.R;
import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.dao.FixedAssetDAO;
import com.example.mr_proj.dao.LocationDAO;
import com.example.mr_proj.dto.FixedAssetDetails;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.fragments.dialog.DetailsDialog;
import com.example.mr_proj.fragments.dialog.EditEntityDialog;
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
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FixedAssetsFragment extends BaseFragment<FixedAsset>
    implements AddEntityDialog.DialogListener,
        AddEntityDialog.FixedAssetsButtonsListener,
        RemoveEntityDialog.RemoveDialogListener,
        AddEntityDialog.MapReadyListener{
    private static final String ALBUM_NAME = "fixed_assets";

    //dao
    private FixedAssetDAO fixedAssetDAO;
    private EmployeeDAO employeeDAO;
    private LocationDAO locationDAO;

    private final AddEntityDialog dialog = new AddEntityDialog();
    private ProgressBar loadingSpinner;

    //activity result
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    private Uri photoUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fixed_assets, container, false);

        AppDatabase db = DatabaseUtil.getDbInstance(requireContext());
        employeeDAO = db.employeeDAO();
        locationDAO = db.locationDAO();
        fixedAssetDAO = db.fixedAssetDAO();

        listAdapter = new ListAdapter<>(fixedAssetDAO, this);
        listAdapter.setRowClickListener(this::onItemSelect);
        Disposable d = DAOService.getEntities(listAdapter);
        disposables.add(d);

        RecyclerView fixedAssetsRecyclerView = root.findViewById(R.id.fixed_assets_list);
        fixedAssetsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fixedAssetsRecyclerView.setAdapter(listAdapter);

        loadingSpinner = root.findViewById(R.id.fa_loading);

        barcodeLauncher = registerForActivityResult(new ScanContract(), this::setBarcodeField);
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::setMedia);
        cameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                this::checkCameraPermission);
        takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), photoTaken -> {
            if (photoTaken)
                setMedia(photoUri);
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
        FixedAsset newAsset = extractFromFields(dialog);
        if (newAsset == null) {
            Toast.makeText(getContext(), R.string.form_notice, Toast.LENGTH_SHORT).show();
            return;
        }
        newAsset.creationDate = System.currentTimeMillis();
        Disposable d = DAOService.insertEntity(newAsset, listAdapter);
        disposables.add(d);

        dialog.dismiss();
    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {
        EditEntityDialog<? extends DbEntity> editDialog = (EditEntityDialog<? extends DbEntity>) dialog;
        FixedAsset oldFixedAsset = (FixedAsset) editDialog.getEntity();
        FixedAsset newFixedAsset = extractFromFields(dialog);
        if (newFixedAsset == null) {
            Toast.makeText(getContext(), R.string.form_notice, Toast.LENGTH_SHORT).show();
            return;
        }

        newFixedAsset.id = oldFixedAsset.id;
        newFixedAsset.creationDate = oldFixedAsset.creationDate;
        Disposable d = DAOService.updateEntity(newFixedAsset, listAdapter);
        disposables.add(d);
        dialog.dismiss();
    }

    private FixedAsset extractFromFields(DialogFragment dialog) {
        String name = DialogUtil.getFieldValue(dialog, R.id.fixed_asset_name);
        String desc = DialogUtil.getFieldValue(dialog, R.id.fixed_asset_desc);
        int barCode;
        double price;
        try {
            barCode = Integer.parseInt(DialogUtil.getFieldValue(dialog, R.id.bar_code));
            price = Double.parseDouble(DialogUtil.getFieldValue(dialog, R.id.price));
        } catch (RuntimeException e) {
            return null;
        }

        Location location = (Location)DialogUtil.getObjectFromSpinner(dialog, R.id.locations_spinner);
        Employee employee = (Employee) DialogUtil.getObjectFromSpinner(dialog, R.id.employees_spinner);
        String image = DialogUtil.getFieldValue(dialog, R.id.fixed_asset_image);
        if (getString(R.string.no_image).equals(image)) //if the image is not selected in the dialog form
            image = null;

        int locationId = (location == null) ? 0 : location.id;
        int employeeId = (employee == null) ? 0 : employee.id;

        return new FixedAsset(name, desc, barCode, price, image, locationId, employeeId);
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.CAMERA)) {
            showInContextUI();
        }
        else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void showInContextUI() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.camera_permission_title)
                .setMessage(R.string.camera_permission_message)
                .setPositiveButton(R.string.ok,
                        (d, which) -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA))
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .show();
    }

    private void checkCameraPermission(Boolean isGranted) {
        if (isGranted) {
            openCamera();
        }
        else {
            Toast.makeText(requireContext(), R.string.camera_permission_notice, Toast.LENGTH_LONG).show();
        }
    }

    private void openCamera() {
        File photoDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), ALBUM_NAME);
        if (!photoDir.exists() && !photoDir.mkdirs()) {
            Log.d("FixedAssetsFragment", photoDir.getAbsolutePath() + " couldn't be created.");
        }
        File imageFile = new File(photoDir, "FA_" + System.currentTimeMillis() + ".jpg");
        photoUri = FileProvider.getUriForFile(requireContext(), "com.example.mr_proj.fileprovider", imageFile);
        takePhotoLauncher.launch(photoUri);
    }

    private void setMedia(Uri uri) {
        if (uri == null)
            return;

        Dialog currentDialog = getCurrentDialog().getDialog();

        if (currentDialog != null) {
            TextView imagePath = currentDialog.findViewById(R.id.fixed_asset_image);
            imagePath.setText(uri.toString());
            imagePath.setVisibility(View.GONE);

            ImageView imagePreview = currentDialog.findViewById(R.id.fixed_asset_image_preview);
            imagePreview.setVisibility(View.VISIBLE);

            FloatingActionButton cancelFAB = currentDialog.findViewById(R.id.cancel_image);
            cancelFAB.setVisibility(View.VISIBLE);

            requireActivity().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Glide
                    .with(requireContext())
                    .load(uri)
                    .into(imagePreview);
        }
    }

    private void setBarcodeField(ScanIntentResult result) {
        if (result == null)
            return;

        Dialog currentDialog = getCurrentDialog().getDialog();

        String barcode = result.getContents();
        if (currentDialog != null) {
            EditText field = currentDialog.findViewById(R.id.bar_code);
            field.setText(barcode);
        }
        else {
            Toast.makeText(requireContext(), "Scanning error", Toast.LENGTH_SHORT).show();
        }
    }

    private DialogFragment getCurrentDialog() {
        DialogFragment editDialog = (DialogFragment) getChildFragmentManager().findFragmentByTag("longClickDialog");
        DialogFragment addDialog = (DialogFragment) getChildFragmentManager().findFragmentByTag("addFixedAsset");

        return addDialog != null ? addDialog : editDialog;
    }

    @Override
    public void onMapReady() {
        loadingSpinner.setVisibility(View.GONE);
    }

    private void onItemSelect(FixedAsset fixedAsset) { //display fixed asset details
        loadingSpinner.setVisibility(View.VISIBLE);

        Disposable d = fixedAssetDAO.getById(fixedAsset.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(faDetails -> {
                    DetailsDialog<FixedAssetDetails> detailsDialog = new DetailsDialog<>(faDetails);
                    detailsDialog.show(getChildFragmentManager(), "fixedAssetDetails");
                });
        disposables.add(d);
    }

    public EmployeeDAO getEmployeeDAO() {
        return employeeDAO;
    }

    public LocationDAO getLocationDAO() {
        return locationDAO;
    }
}