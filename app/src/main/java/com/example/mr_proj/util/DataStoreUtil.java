package com.example.mr_proj.util;


import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DataStoreUtil {
    public static final String LANG_KEY = "lang";
    private static final Object LOCK = new Object();

    private static RxDataStore<Preferences> dataStore;

    public static RxDataStore<Preferences> getDataStoreInstance(Context context) {
        if (dataStore == null || dataStore.isDisposed()) {
            synchronized (LOCK) {
                dataStore = new RxPreferenceDataStoreBuilder(context, "settings").build();
            }
        }
        return dataStore;
    }

    public static void disposeDataStore() {
        if (dataStore != null && !dataStore.isDisposed())
            dataStore.dispose();
    }

    public static void writePreference(RxDataStore<Preferences> dataStore, String key, String value) {
        Preferences.Key<String> KEY = PreferencesKeys.stringKey(key);

        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
            mutablePrefs.set(KEY, value);
            return Single.just(mutablePrefs);
        });
    }

    public static Disposable readPreference(RxDataStore<Preferences> dataStore, String key,
                                            Consumer<String> onSuccess, Consumer<Throwable> onError) {
        Preferences.Key<String> KEY = PreferencesKeys.stringKey(key);
        return dataStore
                .data()
                .map(prefs -> prefs.get(KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError);
    }
}
