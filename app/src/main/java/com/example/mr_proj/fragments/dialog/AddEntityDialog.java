package com.example.mr_proj.fragments.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mr_proj.R;
import com.example.mr_proj.adapter.AssetRegisterItemsAdapter;
import com.example.mr_proj.dto.FixedAssetDetails;
import com.example.mr_proj.fragments.main.AssetRegistersFragment;
import com.example.mr_proj.fragments.main.EmployeesFragment;
import com.example.mr_proj.fragments.main.FixedAssetsFragment;
import com.example.mr_proj.fragments.main.LocationsFragment;
import com.example.mr_proj.model.AppDatabase;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DatabaseUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class AddEntityDialog extends DialogFragment
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    //listeners
    DialogListener listener;
    private FixedAssetsButtonsListener formButtonsListener;
    private MapReadyListener mapReadyListener;

    //map
    private MapView mapView;
    protected GoogleMap map;
    protected LatLng currentPosition = new LatLng(44.772182, 17.191000);

    //asset register
    private AssetRegisterItemsAdapter registerItemsAdapter;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment() +
                    " must implement " + DialogListener.class.getName());
        }
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fixed_asset_form, null);
        Fragment parentFragment = getParentFragment();

        if (parentFragment instanceof FixedAssetsFragment) {
            try {
                formButtonsListener = (FixedAssetsButtonsListener) parentFragment;
            } catch (ClassCastException e) {
                throw new ClassCastException(FixedAssetsFragment.class.getName() + " must implement " +
                        FixedAssetsButtonsListener.class.getName());
            }
            initFixedAssetsDialog(dialogView, parentFragment);
        }
        else if (parentFragment instanceof EmployeesFragment) {
            dialogView = inflater.inflate(R.layout.dialog_employee_form, null);
        }
        else if (parentFragment instanceof AssetRegistersFragment) {
            dialogView = inflater.inflate(R.layout.dialog_asset_register_form, null);
            initAssetsRegisterDialog(dialogView);
        }
        else if (parentFragment instanceof LocationsFragment) {
            try {
                mapReadyListener = (MapReadyListener) parentFragment;
            } catch (ClassCastException e) {
                throw new ClassCastException(LocationsFragment.class.getName() + " must implement " +
                        MapReadyListener.class.getName());
            }
            dialogView = inflater.inflate(R.layout.dialog_location, null);

            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            }
            mapView = dialogView.findViewById(R.id.map);
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);
        }

        builder.setView(dialogView)
                .setTitle(R.string.add)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, (dialog, id) -> dismiss());
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> listener.onAddPositiveClick(AddEntityDialog.this))
        );

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        if (mapView != null)
            mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        mapReadyListener.onMapReady();

        LatLng defaultLocation = new LatLng(44.772182, 17.191000);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.addMarker(new MarkerOptions()
                .position(defaultLocation));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 9.2f));

        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(latLng));
        currentPosition = latLng;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        compositeDisposable.clear();
        if (registerItemsAdapter != null)
            registerItemsAdapter.getDisposables().clear();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        compositeDisposable.clear();
        if (registerItemsAdapter != null)
            registerItemsAdapter.getDisposables().clear();
    }

    private void initAssetsRegisterDialog(View dialogView) {
        List<FixedAssetDetails> fixedAssets = new ArrayList<>();
        AppDatabase db = DatabaseUtil.getDbInstance(getContext());
        registerItemsAdapter = new AssetRegisterItemsAdapter(db, fixedAssets);

        RecyclerView assetItemsView = dialogView.findViewById(R.id.fixed_assets_container);
        assetItemsView.setLayoutManager(new LinearLayoutManager(getContext()));
        assetItemsView.setAdapter(registerItemsAdapter);

        Button addAssetItemBtn = dialogView.findViewById(R.id.add_fixed_asset);
        addAssetItemBtn.setOnClickListener(this::addFixedAssetItem);
    }

    private void addFixedAssetItem(View view) {
        List<FixedAssetDetails> fixedAssets = registerItemsAdapter.getFixedAssets();
        fixedAssets.add(new FixedAssetDetails());
        registerItemsAdapter.notifyItemInserted(fixedAssets.size() - 1);
    }

    private void initFixedAssetsDialog(View dialogView, Fragment parentFragment) {
        ImageButton scanButton = dialogView.findViewById(R.id.scan_bar_code);
        scanButton.setOnClickListener(formButtonsListener::onScannerOpen);
        ImageButton addImageButton = dialogView.findViewById(R.id.add_image);
        addImageButton.setOnClickListener(formButtonsListener::onImagePickerOpen);
        ImageButton cameraButton = dialogView.findViewById(R.id.take_photo);
        cameraButton.setOnClickListener(formButtonsListener::onCameraOpen);
        FloatingActionButton cancelFAB = dialogView.findViewById(R.id.cancel_image);
        cancelFAB.setOnClickListener(this::cancelImage);

        FixedAssetsFragment fragment = (FixedAssetsFragment) parentFragment;
        Spinner locationsSpinner = dialogView.findViewById(R.id.locations_spinner);
        Disposable ld = DAOService.populateSpinner(fragment.getLocationDAO(), locationsSpinner);
        compositeDisposable.add(ld);
        Spinner employeesSpinner = dialogView.findViewById(R.id.employees_spinner);
        Disposable ed = DAOService.populateSpinner(fragment.getEmployeeDAO(), employeesSpinner);
        compositeDisposable.add(ed);
    }

    private void cancelImage(View view) {
        View rootView = view.getRootView();
        String cancelString = getString(R.string.no_image);

        TextView imagePath = rootView.findViewById(R.id.fixed_asset_image);
        imagePath.setText(cancelString);
        imagePath.setVisibility(View.VISIBLE);

        ImageView image = rootView.findViewById(R.id.fixed_asset_image_preview);
        image.setVisibility(View.GONE);

        view.setVisibility(View.GONE);
    }

    public LatLng getCurrentPosition() { return currentPosition; }

    public interface DialogListener {
        void onAddPositiveClick(DialogFragment dialog);
        void onEditPositiveClick(DialogFragment dialog);
    }

    public interface MapReadyListener {
        void onMapReady();
    }

    public interface FixedAssetsButtonsListener {
        void onScannerOpen(View view);
        void onImagePickerOpen(View view);
        void onCameraOpen(View view);
    }
}
