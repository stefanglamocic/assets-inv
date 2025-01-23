package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mr_proj.R;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.util.DialogUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FixedAssetsFragment extends BaseFragment<FixedAsset>
    implements AddEntityDialog.DialogListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fixed_assets, container, false);

        FloatingActionButton addBtn = root.findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this::onAdd);

        return root;
    }

    private void onAdd(View view) {
        AddEntityDialog dialog = new AddEntityDialog();
        dialog.setScanButtonListener(this::onOpenScanner);
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

        return new FixedAsset();
    }

    private void onOpenScanner(View view) {
        Toast.makeText(getContext(), "Open scanner", Toast.LENGTH_SHORT).show();
    }
}