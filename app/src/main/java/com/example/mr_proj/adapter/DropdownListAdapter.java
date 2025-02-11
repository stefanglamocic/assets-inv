package com.example.mr_proj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mr_proj.R;
import com.example.mr_proj.model.DbEntity;

import java.util.List;
import java.util.Objects;

public class DropdownListAdapter<T extends DbEntity> extends ArrayAdapter<T> {
    public DropdownListAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TextView textItem = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            String text = getContext().getString(R.string.choose_option);
            textItem.setText(text);
            return textItem;
        }

        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            View hiddenView = new View(getContext());
            hiddenView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            hiddenView.setVisibility(View.GONE);
            return hiddenView;
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TextView view = (TextView) inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        view.setText(Objects.requireNonNull(getItem(position)).toString());
        return view;
    }
}
