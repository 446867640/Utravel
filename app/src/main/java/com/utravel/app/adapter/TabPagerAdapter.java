package com.utravel.app.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.utravel.app.delegates.LatterDelegate;

import java.util.ArrayList;
import java.util.List;

public class TabPagerAdapter extends FragmentPagerAdapter {
    private List<String> TAB_TITLES;
    private List<LatterDelegate> DELEGATES = new ArrayList<>();

    public TabPagerAdapter(FragmentManager fm, List<String> tab_titles, List<LatterDelegate> delegates) {
        super(fm);
        this.TAB_TITLES = tab_titles;
        this.DELEGATES = delegates;
    }

    @Override
    public Fragment getItem(int position) { return DELEGATES.get(position); }
    @Override
    public int getCount() { return TAB_TITLES.size(); }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { return TAB_TITLES.get(position); }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);//可以避免Fragment的销毁过程
    }
}
