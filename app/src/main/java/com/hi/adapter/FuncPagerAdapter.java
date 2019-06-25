package com.hi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.hi.fragment.BaseFragment;

import java.util.List;

/**
 * @描述 聊天界面底部功能页面适配器
 */
public class FuncPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments;

    public FuncPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
