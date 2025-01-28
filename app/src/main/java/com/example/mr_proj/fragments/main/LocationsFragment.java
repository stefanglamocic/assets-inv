package com.example.mr_proj.fragments.main;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mr_proj.R;
import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.LocationDAO;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.fragments.dialog.DetailsDialog;
import com.example.mr_proj.fragments.dialog.EditEntityDialog;
import com.example.mr_proj.fragments.dialog.RemoveEntityDialog;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.model.Location;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DatabaseUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.disposables.Disposable;


public class LocationsFragment extends BaseFragment<Location>
        implements AddEntityDialog.DialogListener,
        AddEntityDialog.MapReadyListener,
        RemoveEntityDialog.RemoveDialogListener {
    private ProgressBar loadingSpinner;
    private final AddEntityDialog dialog = new AddEntityDialog();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locations, container, false);
        FloatingActionButton addFAB = root.findViewById(R.id.add_location_btn);
        addFAB.setOnClickListener(this::onOpenDialog);
        loadingSpinner = root.findViewById(R.id.loading_spinner);

        LocationDAO dao = DatabaseUtil.getDbInstance(root.getContext()).locationDAO();
        listAdapter = new ListAdapter<>(dao, this);
        listAdapter.setRowClickListener(this::onItemClick);
        Disposable d = DAOService.getEntities(listAdapter);
        disposables.add(d);

        RecyclerView locationsRecyclerView = root.findViewById(R.id.locations_list);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        locationsRecyclerView.setAdapter(listAdapter);

        return root;
    }

    private void onOpenDialog(View view) {
        loadingSpinner.setVisibility(View.VISIBLE);
        dialog.show(getChildFragmentManager(), "addLocation");
    }

    private void onItemClick(Location location) {
        loadingSpinner.setVisibility(View.VISIBLE);
        DetailsDialog<Location> dialog = new DetailsDialog<>(location);
        dialog.show(getChildFragmentManager(), "locationDetails");
    }

    @Override
    public void onMapReady() {
        loadingSpinner.setVisibility(View.GONE);
    }

    private String getCityName(LatLng coors) {
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
        return city;
    }

    @Override
    public void onAddPositiveClick(DialogFragment dialog) {
        LatLng coors = ((AddEntityDialog)dialog).getCurrentPosition();
        String city = getCityName(coors);
        Location location = new Location(city, coors);
        Disposable d = DAOService.insertEntity(location, listAdapter);
        disposables.add(d);

        dialog.dismiss();
    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {
        EditEntityDialog<? extends DbEntity> editDialog = (EditEntityDialog<? extends DbEntity>) dialog;
        int locationId = editDialog.getEntityId();
        LatLng coors = editDialog.getCurrentPosition();
        String city = getCityName(coors);

        Location location = new Location(city, coors);
        location.id = locationId;
        Disposable d = DAOService.updateEntity(location, listAdapter);
        disposables.add(d);

        dialog.dismiss();
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) {
        RemoveEntityDialog<? extends DbEntity> removeDialog = (RemoveEntityDialog<? extends DbEntity>) dialog;
        Location location = new Location();
        location.id = removeDialog.getEntityId();
        Disposable d = DAOService.deleteEntity(location, listAdapter);
        disposables.add(d);
    }
}