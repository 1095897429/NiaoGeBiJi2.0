package com.qmkj.niaogebiji.module.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.event.ShowSearchEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-12
 * 描述:不加构造函数会出现问题
 */
public class BottomBarBehavior extends AppBarLayout.ScrollingViewBehavior {

    public BottomBarBehavior() {
    }

    public BottomBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {

        super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
        return true;

    }


    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {

        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);


    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {



        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);

        //TODO 11.14 在搜索栏不见的情况下，点击可刷新界面
        LinearLayout part =  coordinatorLayout.findViewById(R.id.search_part);
        Rect localRect = new Rect();
        boolean isVisible = part.getLocalVisibleRect(localRect);
        KLog.d("tag","搜索栏的状态 " + isVisible );
        //在看不见的情况下，去显示搜索图片
        if(!isVisible){
            EventBus.getDefault().post(new ShowSearchEvent("1"));
        }else{
            EventBus.getDefault().post(new ShowSearchEvent("0"));
        }
    }
}
