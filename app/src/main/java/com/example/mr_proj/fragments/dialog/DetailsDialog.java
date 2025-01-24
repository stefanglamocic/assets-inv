package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mr_proj.R;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.model.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailsDialog<T extends DbEntity> extends DialogFragment
        implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private final T entity;

    private AddEntityDialog.MapReadyListener mapReadyListener;
    private MapView mapView;

    public DetailsDialog(T entity) {
        this.entity = entity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String titleText = getString(R.string.details);
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_location, null);
        if (entity instanceof Location) {
            mapReadyListener = (AddEntityDialog.MapReadyListener) getParentFragment();
            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            }
            mapView = dialogView.findViewById(R.id.map);
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(dialogView)
                .setTitle(titleText)
                .setNeutralButton(R.string.ok, (dialog, which) -> dismiss())
                .create();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapReadyListener.onMapReady();
        Location location = (Location) entity;
        LatLng coors = new LatLng(location.latitude, location.longitude);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.addMarker(new MarkerOptions()
                .position(coors));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coors, 8f));
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
}
