package com.example.mr_proj.model;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Objects;

public abstract class DbEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public abstract String getRowText();
    public abstract String getRowImage();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbEntity)) return false;
        DbEntity entity = (DbEntity) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @NonNull
    @Override
    public String toString() {
        return getRowText();
    }
}
