package com.example.mr_proj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.LocaleListCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.room.Room;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mr_proj.adapter.PagerAdapter;
import com.example.mr_proj.model.AppDatabase;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.util.DataStoreUtil;
import com.example.mr_proj.util.Language;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private final List<Disposable> disposables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RxDataStore<Preferences> dataStore = DataStoreUtil.getDataStoreInstance(getApplicationContext());
        Disposable disposable = DataStoreUtil.readPreference(dataStore, DataStoreUtil.LANG_KEY,
                s -> {
                    if (s == null) {
                        setFallbackLang();
                    }
                }, err -> setFallbackLang());
        disposables.add(disposable);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mrdb").build();
//        Disposable d = db.employeeDAO().getAll()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(list -> Log.println(Log.DEBUG, "Main activity", list.toString()));
//        disposables.add(d);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager2 viewPager = findViewById(R.id.pager);
        FragmentStateAdapter pagerAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        attachTabs(tabLayout, viewPager);
    }

    @Override
    protected void onDestroy() {
        DataStoreUtil.disposeDataStore();
        for (Disposable d : disposables)
            d.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attachTabs(TabLayout tabLayout, ViewPager2 viewPager) {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.fixed_assets);
                    break;
                case 1:
                    tab.setText(R.string.employees);
                    break;
                case 2:
                    tab.setText(R.string.locations);
                    break;
                case 3:
                    tab.setText(R.string.asset_registers);
                    break;
            }
        }).attach();
    }

    private void setFallbackLang() {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(
                Language.SERBIAN.toString());
        runOnUiThread(() -> AppCompatDelegate.setApplicationLocales(appLocale));
    }
}