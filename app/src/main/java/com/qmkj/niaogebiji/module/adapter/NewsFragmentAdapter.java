package com.qmkj.niaogebiji.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.qmkj.niaogebiji.R;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:
 */
public class NewsFragmentAdapter extends FragmentStatePagerAdapter {

    //存放Fragment的集合
    private List<Fragment> mFragmentList;
    //存储对应的标题
    private List<String> mTitles;

    private Context mContext;

    public NewsFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.mTitles = titles;
    }

    public NewsFragmentAdapter(Context context,FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
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


    /** 自定义TabView2 */
    public View getTabView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_header, null);
        TextView textView=view.findViewById(R.id.tv_header);
        textView.setText(mTitles.get(position));
        return view;
    }

}
