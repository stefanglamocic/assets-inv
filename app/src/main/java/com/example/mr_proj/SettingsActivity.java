package com.example.mr_proj;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.LocaleListCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;

import com.example.mr_proj.util.DataStoreUtil;
import com.example.mr_proj.util.Language;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class SettingsActivity extends AppCompatActivity {
    private volatile Language lang;
    private RxDataStore<Preferences> dataStore;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataStore = DataStoreUtil.getDataStoreInstance(getApplicationContext());

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RadioGroup languagesGroup = findViewById(R.id.rg_languages);
        languagesGroup.setOnCheckedChangeListener(this::onLanguageChange);

        Disposable d = DataStoreUtil.readPreference(dataStore, DataStoreUtil.LANG_KEY,
                data -> setLang(Language.getLanguage(data), languagesGroup),
                err -> setLang(Language.SERBIAN, languagesGroup));
        disposables.add(d);
    }


    private void setLang(Language lang, RadioGroup group) {
        this.lang = lang;
        group.check(lang.getId());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Disposable d = DataStoreUtil.writePreference(dataStore, DataStoreUtil.LANG_KEY, lang.toString());
        disposables.add(d);
        disposables.clear();

        super.onDestroy();
    }

    private void onLanguageChange(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_serbian) {
            lang = Language.SERBIAN;

        }
        else if (checkedId == R.id.radio_english) {
            lang = Language.ENGLISH;
        }
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(lang.toString());
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
}