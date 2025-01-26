package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mr_proj.R;
import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.dao.LocationDAO;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.model.AppDatabase;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.util.DatabaseUtil;
import com.example.mr_proj.util.DialogUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FixedAssetsFragment extends BaseFragment<FixedAsset>
    implements AddEntityDialog.DialogListener, AddEntityDialog.FixedAssetsButtonsListener {
    //dao
    private EmployeeDAO employeeDAO;
    private LocationDAO locationDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fixed_assets, container, false);

        AppDatabase db = DatabaseUtil.getDbInstance(requireContext());
        employeeDAO = db.employeeDAO();
        locationDAO = db.locationDAO();

        FloatingActionButton addBtn = root.findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this::onAdd);

        return root;
    }

    private void onAdd(View view) {
        AddEntityDialog dialog = new AddEntityDialog();
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

        return new FixedAsset();
    }

    @Override
    public void onScannerOpen(View view) {

    }

    @Override
    public void onImagePickerOpen(View view) {

    }

    @Override
    public void onCameraOpen(View view) {

    }

    public EmployeeDAO getEmployeeDAO() {
        return employeeDAO;
    }

    public LocationDAO getLocationDAO() {
        return locationDAO;
    }
}