package com.qmkj.niaogebiji.module.widget;

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
 * 创建时间 2019-11-12
 * 描述:
 */
public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {


    private ViewPager pager;

    private ViewPagerTitle viewPagerTitle;

    private DynamicLine dynamicLine;

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


    int leftWidth = 0;


    @Override
    public void onPageSelected(int position) {
//        KLog.d("tag","onPageSelected");
        lastPosition = pager.getCurrentItem();
        viewPagerTitle.setCurrentItem(position);

        //向左
//        if(lastPosition > position){
//            int temp;
//            temp =  (int)getTextViewLength(textViews.get(lastPosition));
//            dynamicLine.updateView(leftWidth,leftWidth + temp);
//            leftWidth = leftWidth - temp;
//        }else{

//        }


        int x =  textViews.get(position).getLeft() ;
//        KLog.d("tag",x + "");
        dynamicLine.updateView(x, (int)getTextViewLength(textViews.get(position)) + x);

//        if(lastPosition == 0){
//            temp =  (int)getTextViewLength(textViews.get(lastPosition));
//            dynamicLine.updateView(leftWidth,leftWidth + temp);
//        }else if(lastPosition == 1){
//            temp =  (int)getTextViewLength(textViews.get(lastPosition));
//            dynamicLine.updateView(leftWidth,temp + leftWidth);
//        }else if(lastPosition == 2){
//            temp =  (int)getTextViewLength(textViews.get(lastPosition));
//            dynamicLine.updateView(leftWidth,temp + leftWidth);
//        }



//        dynamicLine.updateView(lastPosition * everyLength + dis,dis + lineWidth + (position )*everyLength);

    }


    @Override
    public void onPageScrollStateChanged(int state) {

//        if (state == SCROLL_STATE_SETTLING) {
//            KLog.d("tag","onPageScrollStateChanged");
//            lastPosition = pager.getCurrentItem();
//        }

    }



    public MyOnPageChangeListener(Context context, ViewPager viewPager, DynamicLine dynamicLine, ViewPagerTitle viewPagerTitle) {
        this.pager = viewPager;
        this.viewPagerTitle = viewPagerTitle;
        this.dynamicLine = dynamicLine;
        textViews = viewPagerTitle.getTextView();
        pagerCount = textViews.size();
        //默认是屏幕的宽，但是有时右边有路障
        screenWidth = getScreenWidth((Activity) context) - SizeUtils.dp2px(16f + 28f + 16f);
        //文本的宽度
        lineWidth = (int)getTextViewLength(textViews.get(0));

//        for (int i = 0; i < textViews.size(); i++) {
//            lineWidth += (int)getTextViewLength(textViews.get(i));
//        }
//
//        int lastSpace = screenWidth - lineWidth;
//        dis = lastSpace / pagerCount;

        everyLength = screenWidth / pagerCount;
        dis = (everyLength - lineWidth) / 2;
//        KLog.d("tag","左右边距 " + dis);

        //默认绘制
//        dynamicLine.updateView(lastPosition * everyLength + dis,dis + lineWidth + (0)*everyLength);

        //画第二个 思路，得到第一个盒子的宽度
        float x =  textViews.get(0).getX();
//        KLog.d("tag",x + "");
        dynamicLine.updateView(x, (int)getTextViewLength(textViews.get(0)) + x);

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
