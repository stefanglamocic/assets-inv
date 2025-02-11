package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.mr_proj.R;
import com.example.mr_proj.dao.FixedAssetDAO;
import com.example.mr_proj.dto.FixedAssetDetails;
import com.example.mr_proj.fragments.main.LocationsFragment;
import com.example.mr_proj.model.Location;
import com.example.mr_proj.util.Converters;
import com.example.mr_proj.util.DatabaseUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DetailsDialog<T> extends DialogFragment
        implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private final T entity;

    private TextView dialogTitleText;
    private ImageButton backButton;
    private ProgressBar detailsSpinner;
    private TextView detailsText;

    private AddEntityDialog.MapReadyListener mapReadyListener;
    private MapView mapView;
    private ViewSwitcher switcher;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public DetailsDialog(T entity) {
        this.entity = entity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String titleText = getString(R.string.details);
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogTitle = inflater.inflate(R.layout.header_dialog, null);
        backButton = dialogTitle.findViewById(R.id.dialog_back_btn);
        dialogTitleText = dialogTitle.findViewById(R.id.dialog_title);
        dialogTitleText.setText(titleText);

        View dialogView = inflater.inflate(R.layout.dialog_location, null);
        if (entity instanceof Location) {
            switcher = dialogView.findViewById(R.id.switcher);
            detailsSpinner = dialogView.findViewById(R.id.details_loading);
            detailsText = dialogView.findViewById(R.id.location_assets);
            backButton.setOnClickListener(this::onBack);
            initMapView(savedInstanceState, dialogView, R.id.map);
        }
        else if (entity instanceof FixedAssetDetails) {
            dialogView = inflater.inflate(R.layout.dialog_fixed_asset_details, null);
            initFADetailsDialog(dialogView);
            initMapView(savedInstanceState, dialogView, R.id.fad_map);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(dialogView)
                .setCustomTitle(dialogTitle)
                .setNeutralButton(R.string.ok, (dialog, which) -> dismiss())
                .create();
    }

    private void initFADetailsDialog(View dialogView) {
        FixedAssetDetails fad = (FixedAssetDetails) entity;

        TextView fadDate = dialogView.findViewById(R.id.fad_date);
        fadDate.setText(Converters.formatDate(fad.fixedAsset.creationDate));
        TextView fadName = dialogView.findViewById(R.id.fad_name);
        fadName.setText(fad.fixedAsset.name);
        TextView fadDesc = dialogView.findViewById(R.id.fad_description);
        fadDesc.setText(fad.fixedAsset.description);
        TextView fadPrice = dialogView.findViewById(R.id.fad_price);
        fadPrice.setText(String.valueOf(fad.fixedAsset.price));
        TextView fadBarcode = dialogView.findViewById(R.id.fad_barcode);
        fadBarcode.setText(String.valueOf(fad.fixedAsset.barCode));
        TextView fadEmployee = dialogView.findViewById(R.id.fad_employee);
        if (fad.employee != null)
            fadEmployee.setText(fad.employee.getRowText());

        ImageView fadImage = dialogView.findViewById(R.id.fad_image);
        if (fad.fixedAsset.image != null) {
            Glide
                    .with(requireContext())
                    .load(Uri.parse(fad.fixedAsset.image))
                    .into(fadImage);
        }
        else {
            fadImage.setVisibility(View.GONE);
        }
    }

    private void initMapView(Bundle savedInstanceState, View dialogView, int viewId) {
        mapReadyListener = (AddEntityDialog.MapReadyListener) getParentFragment();
        mapView = dialogView.findViewById(viewId);
        if (entity instanceof FixedAssetDetails
                && ((FixedAssetDetails) entity).location == null) {
            mapView.setVisibility(View.GONE);
            mapReadyListener.onMapReady();
            return;
        }
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapReadyListener.onMapReady();

        Location location = null;
        if (entity instanceof Location) {
            location = (Location) entity;
        }
        else if (entity instanceof FixedAssetDetails) {
            FixedAssetDetails fixedAssetDetails = (FixedAssetDetails) entity;
            location = fixedAssetDetails.location;
        }

        if (location == null)
            return;

        LatLng coors = new LatLng(location.latitude, location.longitude);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.addMarker(new MarkerOptions()
                .position(coors));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coors, 8f));

        if (getParentFragment() instanceof LocationsFragment) {
            googleMap.setOnMarkerClickListener(this::onMarkerClick);
        }
    }

    private boolean onMarkerClick(Marker marker) {
        switcher.showNext();
        String titleText = getString(R.string.assets_list);
        dialogTitleText.setText(titleText);
        backButton.setVisibility(View.VISIBLE);
        FixedAssetDAO dao = DatabaseUtil.getDbInstance(requireContext()).fixedAssetDAO();
        Location location = (Location) entity;

        Disposable d = dao
                .getByLocation(location.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    detailsSpinner.setVisibility(View.GONE);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        builder.append(i + 1)
                                .append(". ")
                                .append(list.get(i).getRowText())
                                .append(System.lineSeparator());
                    }
                    detailsText.setText(builder.toString());
                });
        disposables.add(d);

        return true;
    }

    private void onBack(View view) {
        String titleText = getString(R.string.details);
        switcher.showPrevious();
        backButton.setVisibility(View.GONE);
        dialogTitleText.setText(titleText);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        disposables.clear();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        disposables.clear();
    }
}
