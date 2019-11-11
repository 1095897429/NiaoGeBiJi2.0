package com.qmkj.niaogebiji.module.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:欢迎页适配
 */
public class ViewPagerAdapter extends PagerAdapter {


    private List<View> viewList;

    public ViewPagerAdapter(List<View> viewList){
        this.viewList=viewList;
    }

    @Override
    public int getCount() {
        if(viewList!=null){
            return viewList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;
    }

    //初始化节点
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    //销毁节点
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }


}
