package com.example.mr_proj.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mr_proj.fragments.main.AssetRegistersFragment;
import com.example.mr_proj.fragments.main.EmployeesFragment;
import com.example.mr_proj.fragments.main.FixedAssetsFragment;
import com.example.mr_proj.fragments.main.LocationsFragment;

import java.util.HashMap;
import java.util.Map;

public class PagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 4;

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new EmployeesFragment();
                break;
            case 2:
                fragment = new LocationsFragment();
                break;
            case 3:
                fragment = new AssetRegistersFragment();
                break;
            default:
                fragment = new FixedAssetsFragment();
        }
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    public Fragment getFragmentAt(int position) {
        return fragmentMap.get(position);
    }
}
