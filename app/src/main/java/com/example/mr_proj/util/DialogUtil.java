package com.example.mr_proj.util;

import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class DialogUtil {
    public static String getFieldValue(DialogFragment dialog, int fieldId) {
        if (dialog.getDialog() == null)
            return null;

        View field = dialog.getDialog().findViewById(fieldId);
        if (field instanceof EditText) {
            return ((EditText) field).getText().toString().trim();
        }
        return null;
    }
}
