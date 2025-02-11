package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mr_proj.R;
import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.AssetRegisterDAO;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.fragments.dialog.RemoveEntityDialog;
import com.example.mr_proj.model.AssetRegister;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DatabaseUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.rxjava3.disposables.Disposable;

public class AssetRegistersFragment extends BaseFragment<AssetRegister>
    implements AddEntityDialog.DialogListener,
        RemoveEntityDialog.RemoveDialogListener{

    private final AddEntityDialog addDialog = new AddEntityDialog();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_asset_registers, container, false);

        AssetRegisterDAO assetRegisterDAO = DatabaseUtil.getDbInstance(requireContext()).assetRegisterDAO();

        listAdapter = new ListAdapter<>(assetRegisterDAO, this);
        listAdapter.setRowClickListener(this::onItemClick);

        Disposable d = DAOService.getEntities(listAdapter);
        disposables.add(d);

        RecyclerView assetRegistersRecyclerView = root.findViewById(R.id.asset_registers_list);
        assetRegistersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assetRegistersRecyclerView.setAdapter(listAdapter);

        FloatingActionButton addFAB = root.findViewById(R.id.add_asset_register);
        addFAB.setOnClickListener(this::onOpenAddDialog);

        return root;
    }

    private void onOpenAddDialog(View view) {
    }

    private void onItemClick(AssetRegister assetRegister) {
    }

    @Override
    public void onAddPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onPositiveClick(DialogFragment dialog) { //delete item

    }
}