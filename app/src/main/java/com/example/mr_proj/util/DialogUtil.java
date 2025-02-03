package com.example.mr_proj.util;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.mr_proj.model.DbEntity;

public class DialogUtil {
    public static String getFieldValue(DialogFragment dialog, int fieldId) {
        if (dialog.getDialog() == null)
            return null;

        View field = dialog.getDialog().findViewById(fieldId);
        if (field instanceof EditText) {
            return ((EditText) field).getText().toString().trim();
        }
        else if (field instanceof TextView) {
            return ((TextView) field).getText().toString().trim();
        }
        return null;
    }

    public static Object getObjectFromSpinner(DialogFragment dialog, int fieldId) {
        if (dialog.getDialog() == null)
            return null;

        Spinner spinner = dialog.getDialog().findViewById(fieldId);
        return spinner.getSelectedItem();
    }
}
