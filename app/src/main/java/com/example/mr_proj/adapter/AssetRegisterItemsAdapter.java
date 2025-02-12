package com.example.mr_proj.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mr_proj.R;
import com.example.mr_proj.dto.FixedAssetDetails;
import com.example.mr_proj.model.AppDatabase;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AssetRegisterItemsAdapter extends RecyclerView.Adapter<AssetRegisterItemsAdapter.ItemHolder> {
    private final List<FixedAssetDetails> fixedAssets;

    private final AppDatabase db;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public AssetRegisterItemsAdapter(AppDatabase db, List<FixedAssetDetails> fixedAssets) {
        this.db = db;
        this.fixedAssets = fixedAssets;
    }

    public AssetRegisterItemsAdapter(AppDatabase db) {
        this(db, new ArrayList<>());
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.asset_register_item_view, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        FixedAssetDetails fixedAsset = fixedAssets.get(position);
        initSpinners(holder, position);
        //holder.scanButton set on click event when spinners are initialized
        holder.removeButton.setOnClickListener(v -> removeItem(position));
    }

    private void initSpinners(ItemHolder holder, int position) {
        FixedAssetDetails fixedAsset = fixedAssets.get(position);

        Disposable fixedAssetsD = db
                .fixedAssetDAO()
                .getAllUnregistered()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    List<FixedAsset> filteredList = filterFixedAssetSpinnerList(list);
                    filteredList.add(0, null);
                    ArrayAdapter<FixedAsset> adapter = new DropdownListAdapter<>(
                            holder.fixedAssetSpinner.getContext(),
                            android.R.layout.simple_spinner_item,
                            filteredList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.fixedAssetSpinner.setAdapter(adapter);
                });

        Disposable employeeD = DAOService.populateSpinner(db.employeeDAO(), holder.obligatedEmployeeSpinner);
        if (fixedAsset.obligatedEmployee != null)
            DialogUtil.setSpinnerItem(holder.obligatedEmployeeSpinner, fixedAsset.obligatedEmployee.id);
        else
            DialogUtil.setSpinnerItem(holder.obligatedEmployeeSpinner, null);

        Disposable locationD = DAOService.populateSpinner(db.locationDAO(), holder.newLocationSpinner);
        if (fixedAsset.newLocation != null)
            DialogUtil.setSpinnerItem(holder.newLocationSpinner, fixedAsset.newLocation.id);
        else
            DialogUtil.setSpinnerItem(holder.newLocationSpinner, null);

        disposables.addAll(employeeD, locationD, fixedAssetsD);
    }

    private List<FixedAsset> filterFixedAssetSpinnerList(List<FixedAsset> spinnerList) {
        List<FixedAsset> currentList = fixedAssets
                .stream()
                .map(e -> e.fixedAsset)
                .collect(Collectors.toList());

        return spinnerList
                .stream()
                .filter(e -> !currentList.contains(e))
                .collect(Collectors.toList());
    }

    @Override
    public int getItemCount() {
        return fixedAssets.size();
    }

    private void removeItem(int position) {
        if (position >= 0 && position < fixedAssets.size()) {
            fixedAssets.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, fixedAssets.size());
        }
    }

    public List<FixedAssetDetails> getFixedAssets() { return fixedAssets; }

    public CompositeDisposable getDisposables() { return disposables; }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        ImageButton scanButton;
        Spinner fixedAssetSpinner;
        Spinner obligatedEmployeeSpinner;
        Spinner newLocationSpinner;
        ImageButton removeButton;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            scanButton = itemView.findViewById(R.id.ari_scan);
            fixedAssetSpinner = itemView.findViewById(R.id.ari_spinner);
            obligatedEmployeeSpinner = itemView.findViewById(R.id.fa_employee_spinner);
            newLocationSpinner = itemView.findViewById(R.id.fa_location_spinner);
            removeButton = itemView.findViewById(R.id.ari_remove);
        }
    }
}
