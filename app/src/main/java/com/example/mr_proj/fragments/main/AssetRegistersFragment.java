package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mr_proj.R;
import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.AssetRegisterDAO;
import com.example.mr_proj.dto.AssetRegisterDTO;
import com.example.mr_proj.dto.FixedAssetDetails;
import com.example.mr_proj.exception.EmptyFieldException;
import com.example.mr_proj.exception.FieldNotUniqueException;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.fragments.dialog.DetailsDialog;
import com.example.mr_proj.fragments.dialog.EditEntityDialog;
import com.example.mr_proj.fragments.dialog.RemoveEntityDialog;
import com.example.mr_proj.model.AppDatabase;
import com.example.mr_proj.model.AssetRegister;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DatabaseUtil;
import com.example.mr_proj.util.DialogUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
        addDialog.show(getChildFragmentManager(), "addAssetRegister");
    }

    private void onItemClick(AssetRegister assetRegister) {
        AppDatabase db = DatabaseUtil.getDbInstance(getContext());
        Disposable d = db.assetRegisterDAO()
                .getById(assetRegister.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dto -> {
                    DetailsDialog<AssetRegisterDTO> detailsDialog = new DetailsDialog<>(dto);
                    detailsDialog.show(getChildFragmentManager(), "assetRegisterDetails");
                });

        disposables.add(d);
    }

    @Override
    public void onAddPositiveClick(DialogFragment dialog) {
        try {
            String name = getAssetRegisterName(dialog);
            List<FixedAssetDetails> registerItems = getAssetRegisterItems(dialog);

            AssetRegisterDTO assetRegister = new AssetRegisterDTO(name, registerItems);
            Disposable d;
            if (registerItems.isEmpty()) {
                d = DAOService.insertEntity(assetRegister.assetRegister, listAdapter);
            }
            else {
                AppDatabase db = DatabaseUtil.getDbInstance(getContext());
                d = DAOService.insertAssetRegister(db, assetRegister);
            }
            disposables.add(d);

        } catch (EmptyFieldException e) {
            Toast.makeText(getContext(), R.string.fields_empty, Toast.LENGTH_LONG).show();
            return;
        }
        catch (FieldNotUniqueException e) {
            Toast.makeText(getContext(), R.string.item_present, Toast.LENGTH_LONG).show();
            return;
        }

        dialog.dismiss();
    }

    private String getAssetRegisterName(DialogFragment dialog) throws RuntimeException{
        String name = DialogUtil.getFieldValue(dialog, R.id.asset_register_name);
        if (name == null || name.trim().isEmpty())
            throw new EmptyFieldException();
        return name;
    }

    private List<FixedAssetDetails> getAssetRegisterItems(DialogFragment dialog) throws RuntimeException{
        AddEntityDialog addDialog = (AddEntityDialog) dialog;
        List<FixedAssetDetails> fixedAssetList = addDialog.getRegisterItemsAdapter().getFixedAssets();
        for (FixedAssetDetails fa : fixedAssetList) {
            if (fa.fixedAsset == null || fa.obligatedEmployee == null || fa.newLocation == null) {
                throw new EmptyFieldException();
            }
        }
        Set<FixedAssetDetails> assetRegisterItems = new HashSet<>(fixedAssetList);
        if (assetRegisterItems.size() != fixedAssetList.size())
            throw new FieldNotUniqueException();

        return fixedAssetList;
    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {
        EditEntityDialog<? extends DbEntity> editDialog = (EditEntityDialog<? extends DbEntity>) dialog;
        try {
            String name = getAssetRegisterName(dialog);
            List<FixedAssetDetails> registerItems = getAssetRegisterItems(dialog);

            AssetRegisterDTO assetRegister = new AssetRegisterDTO(name, registerItems);
            assetRegister.assetRegister.id = editDialog.getEntityId();
            List<FixedAssetDetails> unwanted = editDialog.getRegisterItemsAdapter().getUnwanted();

            AppDatabase db = DatabaseUtil.getDbInstance(getContext());
            Disposable d = Completable.mergeArray(
                    db.assetRegisterDAO().update(assetRegister.assetRegister),
                    DAOService.updateMultipleAssets(db, assetRegister),
                    DAOService.clearAssetObligations(db, unwanted)
            )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            disposables.add(d);

        } catch (EmptyFieldException e) {
            Toast.makeText(getContext(), R.string.fields_empty, Toast.LENGTH_LONG).show();
            return;
        }
        catch (FieldNotUniqueException e) {
            Toast.makeText(getContext(), R.string.item_present, Toast.LENGTH_LONG).show();
            return;
        }

        dialog.dismiss();
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) { //delete item
        RemoveEntityDialog<? extends DbEntity> removeDialog = (RemoveEntityDialog<? extends DbEntity>) dialog;
        AssetRegister assetRegister = new AssetRegister();
        assetRegister.id = removeDialog.getEntityId();
        Disposable d = DAOService.deleteEntity(assetRegister, listAdapter);
        disposables.add(d);
    }
}