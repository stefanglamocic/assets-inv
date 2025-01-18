package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mr_proj.R;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class LocationsFragment extends Fragment implements AddEntityDialog.DialogListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locations, container, false);
        FloatingActionButton addFAB = root.findViewById(R.id.add_location_btn);
        addFAB.setOnClickListener(this::onOpenDialog);

        return root;
    }

    private void onOpenDialog(View view) {
        DialogFragment fragment = new AddEntityDialog();
        fragment.show(getChildFragmentManager(), "addLocation");
    }

    @Override
    public void onAddPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {

    }
}