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

public class AddEntityDialog extends DialogFragment implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    DialogListener listener;
    private View.OnClickListener scanButtonListener;
    private MapView mapView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment() +
                    " must implement DialogListener!");
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
            ImageButton scanButton = dialogView.findViewById(R.id.scan_bar_code);
            scanButton.setOnClickListener(scanButtonListener);
        }
        else if (parentFragment instanceof EmployeesFragment) {
            dialogView = inflater.inflate(R.layout.dialog_employee_form, null);
        }
        else if (parentFragment instanceof LocationsFragment) {
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

    public void setScanButtonListener(View.OnClickListener scanButtonListener) {
        this.scanButtonListener = scanButtonListener;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng defaultLocation = new LatLng(44.772182, 17.191000);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.addMarker(new MarkerOptions()
                .position(defaultLocation));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 9.2f));
    }

    public interface DialogListener {
        void onAddPositiveClick(DialogFragment dialog);
        void onEditPositiveClick(DialogFragment dialog);
    }
}
