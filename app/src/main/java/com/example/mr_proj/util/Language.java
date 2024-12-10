package com.example.mr_proj.util;

import androidx.annotation.NonNull;

import com.example.mr_proj.R;

public enum Language {
    SERBIAN("sr", R.id.radio_serbian),
    ENGLISH("en", R.id.radio_english);

    private final String language;
    private final int id;

    Language(String language, int id) {
        this.language = language;
        this.id = id;
    }

    public int getId() { return id; }

    public static Language getLanguage(String code) {
        if (code.equals(Language.ENGLISH.language)) {
            return Language.ENGLISH;
        }
        return Language.SERBIAN;
    }

    @NonNull
    @Override
    public String toString() {
        return language;
    }
}
