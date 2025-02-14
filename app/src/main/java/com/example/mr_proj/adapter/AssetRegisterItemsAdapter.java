package com.example.mr_proj.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mr_proj.R;
import com.example.mr_proj.dto.FixedAssetDetails;
import com.example.mr_proj.model.AppDatabase;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.model.FixedAsset;
import com.example.mr_proj.model.Location;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DialogUtil;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AssetRegisterItemsAdapter extends RecyclerView.Adapter<AssetRegisterItemsAdapter.ItemHolder> {
    private boolean editMode = true;

    private final List<FixedAssetDetails> fixedAssets;
    private final List<FixedAssetDetails> unwanted = new ArrayList<>();

    private final AppDatabase db;

    private final ActivityResultLauncher<ScanOptions> barcodeScannerLauncher;
    private int scannedItemPosition;
    private List<FixedAsset> spinnerAssets;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public AssetRegisterItemsAdapter(AppDatabase db,
                                     List<FixedAssetDetails> fixedAssets,
                                     ActivityResultLauncher<ScanOptions> barcodeScannerLauncher) {
        this.db = db;
        this.fixedAssets = fixedAssets;
        this.barcodeScannerLauncher = barcodeScannerLauncher;
    }

    public AssetRegisterItemsAdapter(AppDatabase db, ActivityResultLauncher<ScanOptions> barcodeScannerLauncher) {
        this(db, new ArrayList<>(), barcodeScannerLauncher);
        editMode = false;
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
        initSpinners(holder, position);
        holder.scanButton.setOnClickListener(v -> onOpenScanner(v, position));
        holder.removeButton.setOnClickListener(v -> removeItem(position));
    }

    private void onOpenScanner(View view, int position) {
        ScanOptions scanOptions = new ScanOptions();
        String notice = view.getContext().getString(R.string.scan_barcode);
        scanOptions.setPrompt(notice);
        scanOptions.setOrientationLocked(false);

        scannedItemPosition = position;
        barcodeScannerLauncher.launch(scanOptions);
    }

    public boolean setAssetByBarcode(long barcode) {
        FixedAssetDetails fad = fixedAssets.get(scannedItemPosition);
        fad.fixedAsset = findAssetByBarcode(barcode);
        notifyItemChanged(scannedItemPosition);
        return fad.fixedAsset != null;
    }

    private FixedAsset findAssetByBarcode(long barcode) {
        if (spinnerAssets == null)
            return null;
        for (FixedAsset fa : spinnerAssets) {
            if (fa != null && fa.barCode == barcode)
                return fa;
        }
        return null;
    }

    private void initSpinners(ItemHolder holder, int position) {
        FixedAssetDetails fixedAsset = fixedAssets.get(position);

        Single<List<FixedAsset>> func = db.fixedAssetDAO().getAllUnregistered();
        if (editMode) {
            func = db.fixedAssetDAO().getAllUnregistered(fixedAsset.fixedAsset.assetRegisterId);
        }

        Disposable fixedAssetsD = func
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    spinnerAssets = list;
                    list.add(0, null);
                    ArrayAdapter<FixedAsset> adapter = new DropdownListAdapter<>(
                            holder.fixedAssetSpinner.getContext(),
                            android.R.layout.simple_spinner_item,
                            list
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.fixedAssetSpinner.setAdapter(adapter);

                    if (fixedAsset.fixedAsset != null) {
                        Log.d("initSpinners", "id: " + fixedAsset.fixedAsset.id);
                        DialogUtil.setSpinnerItem(holder.fixedAssetSpinner, fixedAsset.fixedAsset.id);
                    }
                    else
                        DialogUtil.setSpinnerItem(holder.fixedAssetSpinner, null);
                });

        Disposable employeeD = DAOService.populateSpinner(db.employeeDAO(), holder.obligatedEmployeeSpinner,
                () -> {
                    if (fixedAsset.obligatedEmployee != null) {
                        DialogUtil.setSpinnerItem(holder.obligatedEmployeeSpinner, fixedAsset.obligatedEmployee.id);
                    }
                    else {
                        DialogUtil.setSpinnerItem(holder.obligatedEmployeeSpinner, null);
                    }
                });

        Disposable locationD = DAOService.populateSpinner(db.locationDAO(), holder.newLocationSpinner,
                () -> {
                    if (fixedAsset.newLocation != null)
                        DialogUtil.setSpinnerItem(holder.newLocationSpinner, fixedAsset.newLocation.id);
                    else
                        DialogUtil.setSpinnerItem(holder.newLocationSpinner, null);
                });

        setSpinnerEvents(holder, position);

        disposables.addAll(employeeD, locationD, fixedAssetsD);
    }

    private void setSpinnerEvents(ItemHolder holder, int listPosition) {
        AdapterView.OnItemSelectedListener fixedAssetSelected, employeeSelected, locationSelected;
        FixedAssetDetails fad = fixedAssets.get(listPosition);

        fixedAssetSelected = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fad.fixedAsset = (FixedAsset) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        employeeSelected = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fad.obligatedEmployee = (Employee) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        locationSelected = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fad.newLocation = (Location) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        holder.fixedAssetSpinner.setOnItemSelectedListener(fixedAssetSelected);
        holder.obligatedEmployeeSpinner.setOnItemSelectedListener(employeeSelected);
        holder.newLocationSpinner.setOnItemSelectedListener(locationSelected);
    }

    @Override
    public int getItemCount() {
        return fixedAssets.size();
    }

    private void removeItem(int position) {
        if (position >= 0 && position < fixedAssets.size()) {
            FixedAssetDetails fad = fixedAssets.remove(position);
            unwanted.add(fad);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, fixedAssets.size());
        }
    }

    public List<FixedAssetDetails> getFixedAssets() { return fixedAssets; }

    public List<FixedAssetDetails> getUnwanted() { return unwanted; }

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
