package com.qmkj.niaogebiji.module.widget.tab3;

import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;

import java.util.ArrayList;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-10-10
 * 描述:页面切换监听器 可共用一套
 */
public class MyOnPageChangeListener3V3 implements ViewPager.OnPageChangeListener {


    private ViewPager pager;

    private ViewPagerTitleSlide3V3 viewPagerTitle;

    private DynamicLineNoRadiu3 dynamicLine;

    private ArrayList<View> textViews;

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
        lastPosition = pager.getCurrentItem();
        viewPagerTitle.setCurrentItem(position);


        if (textViews.get(position) instanceof ImageView) {
            dynamicLine.updateView(0, 0);
            ImageView imageView = (ImageView) textViews.get(position);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            lp.width = SizeUtils.dp2px(50);
//            lp.height = SizeUtils.dp2px(20);
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            imageView.setLayoutParams(lp);
            imageView.setImageResource(R.mipmap.icon_first_hot);
        } else {
            if (position == textViews.size() - 1) {
                dynamicLine.updateView(SizeUtils.dp2px(16) + lastPosition * everyLength + dis, SizeUtils.dp2px(16) + dis + lineWidth + (position) * everyLength);
            } else {
                dynamicLine.updateView(lastPosition * everyLength + dis, dis + lineWidth + (position) * everyLength);

                ImageView imageView = (ImageView) textViews.get(textViews.size() - 1);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                lp.width = SizeUtils.dp2px(50);
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                imageView.setImageResource(R.mipmap.icon_first_hot_default);
                imageView.setLayoutParams(lp);
            }


        }

    }


    @Override
    public void onPageScrollStateChanged(int state) {

//        if (state == SCROLL_STATE_SETTLING) {
//            KLog.d("tag","onPageScrollStateChanged");
//            lastPosition = pager.getCurrentItem();
//        }

    }



    public MyOnPageChangeListener3V3(Context context, ViewPager viewPager, DynamicLineNoRadiu3 dynamicLine, ViewPagerTitleSlide3V3 viewPagerTitle, int defaultIndex) {
        this.pager = viewPager;
        this.viewPagerTitle = viewPagerTitle;
        this.dynamicLine = dynamicLine;
        textViews = viewPagerTitle.getTextView();
        pagerCount = textViews.size();
        screenWidth = getScreenWidth((Activity)context) - SizeUtils.dp2px(16 + 28 + 16  );
//        screenWidth = ScreenUtils.getScreenWidth();
        //得到第一个选中文本的大小
        lineWidth = (int)getTextViewLength((TextView) textViews.get(defaultIndex));
        everyLength = screenWidth / pagerCount;
//        KLog.d("tag","每个控件均分长度为  " + everyLength);
        everyLength = SizeUtils.dp2px(50);
        //默认绘制
        dynamicLine.updateView(lastPosition * everyLength + dis,dis + lineWidth + (defaultIndex)*everyLength );

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
