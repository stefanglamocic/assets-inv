package com.example.mr_proj.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Entity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mr_proj.R;
import com.example.mr_proj.dao.IDAO;
import com.example.mr_proj.fragments.dialog.EditEntityDialog;
import com.example.mr_proj.fragments.dialog.RemoveEntityDialog;
import com.example.mr_proj.model.DbEntity;

import java.util.List;

public class ListAdapter<T extends DbEntity> extends RecyclerView.Adapter<ListAdapter.RowHolder>{
    private static final String IS_PREFIX = "/data/data";

    private final List<T> entities;
    private final IDAO<T> dao;
    private IRowClickListener<T> rowClickListener;


    public ListAdapter(List<T> entities, IDAO<T> dao) {
        this.entities = entities;
        this.dao = dao;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.list_row, parent, false);
        return new RowHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        T entity = entities.get(position);
        holder.rowText.setText(entity.getRowText());
        setRowImage(holder.rowImage, entity.getRowImage());

        if (rowClickListener != null)
            holder.itemView.setOnClickListener(v -> rowClickListener.onEntityClick(entity));
        holder.itemView.setOnLongClickListener(v -> onItemLongClick(v, entity));
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public void notifyAdapter(List<T> list) {
        int empSize = entities.size();
        if (empSize >= list.size())
            return;
        for (int i = empSize; i < list.size(); i++) {
            T e = list.get(i);
            if (!entities.contains(e)) {
                entities.add(e);
                notifyItemInserted(i);
            }
        }
    }

    private boolean onItemLongClick(View v, T entity) {
        PopupMenu menu = new PopupMenu(v.getContext(), v);
        menu.getMenuInflater().inflate(R.menu.list, menu.getMenu());

        menu.setOnMenuItemClickListener(item -> {
            DialogFragment dialog = null;

            int id = item.getItemId();
            if (id == R.id.edit) {
                dialog = new EditEntityDialog<>(entity);
            }
            else if (id == R.id.remove) {
                dialog = new RemoveEntityDialog<>(entity, dao);
            }
            assert dialog != null;
            dialog.show(((FragmentActivity)v.getContext()).getSupportFragmentManager() , "longClickDialog");
            return true;
        });

        menu.show();
        return true;
    }

    private void setRowImage(ImageView imageView, String imgPath) {
        if (imgPath == null)
            return;

        if (imgPath.startsWith(IS_PREFIX)) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            imageView.setImageBitmap(bitmap);
        }
        else {
            Context context = imageView.getContext().getApplicationContext();
            @SuppressLint("DiscouragedApi")
            int resId = context.getResources().getIdentifier(imgPath, "drawable", context.getPackageName());
            imageView.setImageResource(resId);
        }
    }

    public List<T> getEntities() { return entities; }

    public void setRowClickListener(IRowClickListener<T> rowClickListener) {
        this.rowClickListener = rowClickListener;
    }

    public IDAO<T> getDao() { return dao; }

    public static class RowHolder extends RecyclerView.ViewHolder {
        ImageView rowImage;
        TextView rowText;

        public RowHolder(@NonNull View itemView) {
            super(itemView);
            rowImage = itemView.findViewById(R.id.row_img);
            rowText = itemView.findViewById(R.id.row_text);
        }
    }

    public interface IRowClickListener<T extends DbEntity> {
        void onEntityClick(T entity);
    }
}
