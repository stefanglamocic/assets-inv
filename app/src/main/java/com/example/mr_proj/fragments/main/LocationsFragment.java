package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mr_proj.R;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class LocationsFragment extends Fragment implements AddEntityDialog.DialogListener{
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

    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {

    }
}