package com.example.mr_proj.fragments.main;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mr_proj.R;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.fragments.dialog.EditEntityDialog;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.model.Location;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationsFragment extends BaseFragment<Location> implements AddEntityDialog.DialogListener{
    private ProgressBar loadingSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locations, container, false);
        FloatingActionButton addFAB = root.findViewById(R.id.add_location_btn);
        addFAB.setOnClickListener(this::onOpenDialog);
        loadingSpinner = root.findViewById(R.id.loading_spinner);

        return root;
    }

    private void onOpenDialog(View view) {
        AddEntityDialog dialog = new AddEntityDialog();
        dialog.setMapReadyListener(this::onMapReady);
        dialog.show(getChildFragmentManager(), "addLocation");
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    private void onMapReady() {
        loadingSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onAddPositiveClick(DialogFragment dialog) {
        LatLng coors = ((AddEntityDialog)dialog).getCurrentPosition();
        String city = null;
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(coors.latitude, coors.longitude, 1);
            if (addresses != null)
                city = addresses.get(0).getLocality();
        } catch (IOException e) {
            Log.println(Log.DEBUG, "Geocoder", "geocoder exception");
        }
        if (city == null)
            city = "Banja Luka";

        dialog.dismiss();
    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {
        EditEntityDialog<? extends DbEntity> editDialog = (EditEntityDialog<? extends DbEntity>) dialog;
        int locationId = editDialog.getEntityId();
        LatLng coors = editDialog.getCurrentPosition();

        //Location location = new Location(coors);
        //location.id = locationId;
    }
}