package com.qmkj.niaogebiji.module.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:首页下方适配器
 */
public class FirstFragmentAdapter extends FragmentStatePagerAdapter {

    //存放Fragment的集合
    private List<Fragment> mFragmentList;
    //存储对应的标题
    private List<String> mTitles;

    private Context mContext;

    public FirstFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.mTitles = titles;
    }

    public FirstFragmentAdapter(Context context,FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
        super(fm);
        this.mContext = context;
        this.mFragmentList = fragmentList;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
