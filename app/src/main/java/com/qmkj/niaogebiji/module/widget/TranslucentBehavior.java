package com.qmkj.niaogebiji.module.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.module.event.ShowTopAuthorEvent;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-20
 * 描述:
 */
public class TranslucentBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {

    /**标题栏的高度*/
    private int mToolbarHeight = 0;

    public TranslucentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        return dependency instanceof TextView;
    }

    /**
     * 必须要加上  layout_anchor，对方也要layout_collapseMode才能使用
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout child, View dependency) {

        // 初始化高度
        if (mToolbarHeight == 0) {
            //为了更慢的 -- 减去30是偏移量
            KLog.d("tag","child.getBottom() " + child.getBottom());
//            mToolbarHeight = parent.getBottom() - SizeUtils.dp2px(25 + 44);
//            mToolbarHeight = (child.getBottom()) * 2;
            mToolbarHeight = SizeUtils.dp2px(216f);
        }
        //
        //计算toolbar从开始移动到最后的百分比
        float percent = dependency.getY() / mToolbarHeight;

        //百分大于1，直接赋值为1
        if (percent >= 1) {
            percent = 1f;
            EventBus.getDefault().post(new ShowTopAuthorEvent("1"));
        }else{
            EventBus.getDefault().post(new ShowTopAuthorEvent("0"));
        }

        // 计算alpha通道值
        float alpha = percent * 255;


        //设置背景颜色
//        child.setBackgroundColor(Color.argb((int) alpha, 63, 81, 181));
        //白色
        child.setBackgroundColor(Color.argb((int) alpha,  255,255,255));


        return true;
    }
}