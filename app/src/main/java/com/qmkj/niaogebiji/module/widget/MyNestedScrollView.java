package com.qmkj.niaogebiji.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.widget.NestedScrollView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:
 */
public class MyNestedScrollView extends NestedScrollView {
    private String TAG = MyNestedScrollView.class.getSimpleName();
    final int MAX_SCROLL_LENGTH = 400;
    /**
     * 该控件滑动的高度，高于这个高度后交给子滑动控件
     */
    int mParentScrollHeight ;
    int mScrollY ;
    public MyNestedScrollView(Context context) {
        super(context);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMyScrollHeight(int scrollLength) {
        this.mParentScrollHeight = scrollLength;
    }




    /**
     * 子控件告诉父控件 开始滑动了
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     * 如果有就返回true
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);

        if (mScrollY < mParentScrollHeight) {
            consumed[0] = dx;
            consumed[1] = dy;
            scrollBy(0, dy);

        }

//            KLog.d("tag","dx " + dx + " dy "+ dy +  " " + consumed[0]  + " " + consumed[1] + " scrollY " + mScrollY);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollY = t;
        if (scrollViewListener != null) {
            scrollViewListener.onScroll(mScrollY);
        }
    }


    private ScrollViewListener scrollViewListener = null;
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }


    public interface ScrollViewListener {

        void onScroll(int scrollY);

    }



    private boolean isNeedScroll = true;

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                return isNeedScroll;
//        }
//        return super.onInterceptTouchEvent(ev);
//    }

    /*
    改方法用来处理NestedScrollView是否拦截滑动事件
     */
    public void setNeedScroll(boolean isNeedScroll) {
        this.isNeedScroll = isNeedScroll;
    }


}
