package com.example.mr_proj.util;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converters {

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long time) {
        return time == null ? null : new Date(time);
    }

    public static String formatDate(Date date) {
        if (date == null)
            return null;
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return f.format(date);
    }
}
