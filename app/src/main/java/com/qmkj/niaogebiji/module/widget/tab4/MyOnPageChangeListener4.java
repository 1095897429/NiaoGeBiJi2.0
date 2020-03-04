package com.qmkj.niaogebiji.module.widget.tab4;

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
import com.qmkj.niaogebiji.module.widget.tab3.DynamicLineNoRadiu3;
import com.qmkj.niaogebiji.module.widget.tab3.ViewPagerTitleSlide3;
import com.socks.library.KLog;

import java.util.ArrayList;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-10-10
 * 描述:页面切换监听器 可共用一套
 */
public class MyOnPageChangeListener4 implements ViewPager.OnPageChangeListener {


    private ViewPager pager;

    private ViewPagerTitleSlide4 viewPagerTitle;

    private DynamicLineNoRadiu4 dynamicLine;

    private ArrayList<View> textViews;

    private int screenWidth;

    private int pagerCount;
    //线的宽度
    private int lineWidth;
    //每个盒子宽度
    private float everyLength;
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
        KLog.d("tag","onPageSelected 界面初始化会进入");
        lastPosition = pager.getCurrentItem();
        viewPagerTitle.setCurrentItem(position);



        if(textViews.get(position) instanceof TextView){
            //当前文本
            TextView temp1 = (TextView) textViews.get(position);
            KLog.d("tag","当前文本是 " + temp1.getText().toString());
//            KLog.d("tag","getLeft1 " + temp1.getPaddingLeft());
            KLog.d("tag","getLeft2 " + temp1.getLeft());

            dynamicLine.updateView(temp1.getLeft(),temp1.getRight());
        }


        if(textViews.get(position) instanceof ImageView){
            dynamicLine.updateView(0,0);
            ImageView imageView = (ImageView) textViews.get(position);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            lp.height = SizeUtils.dp2px(15);
//            lp.width =  SizeUtils.dp2px(60);
            lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.setMargins(0,0,0,10);
            imageView.setLayoutParams(lp);
            imageView.setImageResource(R.mipmap.icon_first_hot);
        } else{

            //处理图片
            ImageView imageView = (ImageView) textViews.get(textViews.size() - 1);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            lp.height = SizeUtils.dp2px(15);
//            lp.width =  SizeUtils.dp2px(60);
            lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.setMargins(0,0,0,10);
            imageView.setImageResource(R.mipmap.icon_first_hot_default);
            imageView.setLayoutParams(lp);
        }



    }


    @Override
    public void onPageScrollStateChanged(int state) {

//        if (state == SCROLL_STATE_SETTLING) {
//            KLog.d("tag","onPageScrollStateChanged");
//            lastPosition = pager.getCurrentItem();
//        }

    }



    public MyOnPageChangeListener4(Context context, ViewPager viewPager, DynamicLineNoRadiu4 dynamicLine, ViewPagerTitleSlide4 viewPagerTitle, int defaultIndex) {
        this.pager = viewPager;
        this.viewPagerTitle = viewPagerTitle;
        this.dynamicLine = dynamicLine;
        textViews = viewPagerTitle.getTextView();
        pagerCount = textViews.size();
        screenWidth = getScreenWidth((Activity)context) - SizeUtils.dp2px( 28 + 16  + 28);
//        screenWidth = ScreenUtils.getScreenWidth();
        //得到第一个选中文本的大小
        lineWidth = (int)getTextViewLength((TextView) textViews.get(defaultIndex));
        everyLength = screenWidth / pagerCount;
//        KLog.d("tag","每个控件均分长度为  " + everyLength);
        everyLength = LinearLayout.LayoutParams.WRAP_CONTENT;
        //默认绘制

        //第一个文本
        TextView temp1 = (TextView) textViews.get(defaultIndex - 1);
        float fonts1 = getTextViewLength(temp1) ;
        //第二个文本
        TextView temp = (TextView) textViews.get(defaultIndex);
        float fonts = getTextViewLength(temp) ;

        //第一个左边距 + 文字宽 + 有边距
        float space = SizeUtils.dp2px(16)*2  + fonts1 ;

        everyLength = space + fonts;

//        dynamicLine.updateView(lastPosition * everyLength + dis,dis + lineWidth + (defaultIndex)*everyLength );

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
