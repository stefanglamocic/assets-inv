package com.example.mr_proj.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.mr_proj.R;
import com.example.mr_proj.fragments.main.EmployeesFragment;
import com.example.mr_proj.fragments.main.FixedAssetsFragment;

public class AddEntityDialog extends DialogFragment {
    DialogListener listener;
    private View.OnClickListener scanButtonListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment() +
                    " must implement DialogListener!");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_fixed_asset_form, null);

        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FixedAssetsFragment) {
            ImageButton scanButton = dialogView.findViewById(R.id.scan_bar_code);
            scanButton.setOnClickListener(scanButtonListener);
        }
        else if (parentFragment instanceof EmployeesFragment) {
            dialogView = inflater.inflate(R.layout.dialog_employee_form, null);
        }

        builder.setView(dialogView)
                .setTitle(R.string.add)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, (dialog, id) -> dismiss());
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> listener.onAddPositiveClick(AddEntityDialog.this))
        );

        return dialog;
    }

    public void setScanButtonListener(View.OnClickListener scanButtonListener) {
        this.scanButtonListener = scanButtonListener;
    }

    public interface DialogListener {
        void onAddPositiveClick(DialogFragment dialog);
        void onEditPositiveClick(DialogFragment dialog);
    }
}
