package com.example.mr_proj.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mr_proj.fragments.main.AssetRegistersFragment;
import com.example.mr_proj.fragments.main.EmployeesFragment;
import com.example.mr_proj.fragments.main.FixedAssetsFragment;
import com.example.mr_proj.fragments.main.LocationsFragment;

public class PagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 4;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new EmployeesFragment();
            case 2:
                return new LocationsFragment();
            case 3:
                return new AssetRegistersFragment();
            default:
                return new FixedAssetsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
