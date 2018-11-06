package com.txzh.walk.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    public FragmentManager fm;
    private List<Fragment> fragmentList;
    public int  mChildCount;

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        fragmentList = fragments;
        this.fm=fm;
    }

    @Override
    public Fragment getItem(int arg0) {

        return fragmentList.get(arg0);
    //    return fragmentList.get(arg0);
    }

    @Override
    public int getCount() {

        return fragmentList.size();
    }


    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }


}

