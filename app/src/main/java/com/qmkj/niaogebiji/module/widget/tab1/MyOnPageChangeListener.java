package com.qmkj.niaogebiji.module.widget.tab1;

import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.socks.library.KLog;

import java.util.ArrayList;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-10-10
 * 描述:页面切换监听器 可共用一套
 */
public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {


    private ViewPager pager;

    private ViewPagerTitle viewPagerTitle;

    private DynamicLineNoRadiu dynamicLine;

    private ArrayList<TextView> textViews;

    private int screenWidth;

    private int pagerCount;
    //线的宽度
    private int lineWidth;
    //每个盒子宽度
    private int everyLength;
    //盒子中左右边距
    private int dis;
    //当前position
    private int lastPosition;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

//        if(lastPosition > position){
//            KLog.d("tag","1111");
//            dynamicLine.updateView((position + positionOffset) * everyLength + dis, (lastPosition + 1) * everyLength - dis);
//        }else{
//            KLog.d("tag","2222 lastPosition " + lastPosition + " " + "position " + position);
//            if(positionOffset > 0.5f){
//                positionOffset = 0.5f;
//            }
//            dynamicLine.updateView(lastPosition * everyLength + dis,dis + lineWidth + (position + positionOffset * 2 )*everyLength);
//        }

    }

    @Override
    public void onPageSelected(int position) {
        KLog.d("tag","onPageSelected");
        lastPosition = pager.getCurrentItem();
        viewPagerTitle.setCurrentItem(position);
        dynamicLine.updateView(lastPosition * everyLength + dis,dis + lineWidth + (position )*everyLength);

    }


    @Override
    public void onPageScrollStateChanged(int state) {

//        if (state == SCROLL_STATE_SETTLING) {
//            KLog.d("tag","onPageScrollStateChanged");
//            lastPosition = pager.getCurrentItem();
//        }

    }



    public MyOnPageChangeListener(Context context, ViewPager viewPager, DynamicLineNoRadiu dynamicLine, ViewPagerTitle viewPagerTitle, int defaultIndex) {
        this.pager = viewPager;
        this.viewPagerTitle = viewPagerTitle;
        this.dynamicLine = dynamicLine;
        textViews = viewPagerTitle.getTextView();
        pagerCount = textViews.size();
        screenWidth = SizeUtils.dp2px(120);
        //得到第一个文本的大小
        lineWidth = (int)getTextViewLength(textViews.get(defaultIndex));
        everyLength = screenWidth / pagerCount;
        dis = (everyLength - lineWidth) / 2;
        dis = 0;
        KLog.d("tag","每个控件均分长度为  " + everyLength);

        //默认绘制
        dynamicLine.updateView(lastPosition * everyLength + dis,dis + lineWidth + (0)*everyLength );

    }

    public int getScreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public  float getTextViewLength(TextView textView) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        return paint.measureText(textView.getText().toString());
    }

}
