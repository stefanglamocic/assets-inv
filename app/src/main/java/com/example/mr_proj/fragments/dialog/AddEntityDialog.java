package com.example.mr_proj.fragments.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.mr_proj.R;
import com.example.mr_proj.fragments.main.EmployeesFragment;
import com.example.mr_proj.fragments.main.FixedAssetsFragment;
import com.example.mr_proj.fragments.main.LocationsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

    private void initFixedAssetsDialog(View dialogView) {
        ImageButton scanButton = dialogView.findViewById(R.id.scan_bar_code);
        scanButton.setOnClickListener(formButtonsListener::onScannerOpen);
        ImageButton addImageButton = dialogView.findViewById(R.id.add_image);
        addImageButton.setOnClickListener(formButtonsListener::onImagePickerOpen);
        ImageButton cameraButton = dialogView.findViewById(R.id.take_photo);
        cameraButton.setOnClickListener(formButtonsListener::onCameraOpen);
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
            initFixedAssetsDialog(dialogView);
        }
        else if (parentFragment instanceof EmployeesFragment) {
            dialogView = inflater.inflate(R.layout.dialog_employee_form, null);
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
