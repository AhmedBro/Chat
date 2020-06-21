package com.example.chatapp.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class fragmentPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragmentslist = new ArrayList<>();
    ArrayList<String> fragmenttitle = new ArrayList<>();

    public fragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentslist.get(position);
    }

    @Override
    public int getCount() {
        return fragmentslist.size();
    }

    public void showFragments(Fragment fragment , String title)
    {

        fragmentslist.add(fragment) ;
        fragmenttitle.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmenttitle.get(position);
    }
}
