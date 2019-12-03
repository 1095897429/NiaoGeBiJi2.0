package com.qmkj.niaogebiji.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.qmkj.niaogebiji.R;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-29
 * 描述:
 */
public class MyLinearLayout extends LinearLayout {

    private Context mContext;

    public MyLinearLayout(Context context) {
        this(context, null);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    private void init() {
        setOrientation(HORIZONTAL);
    }


    private ViewPager mViewPager;
    View root;
    LinearLayout ll;
    String [] myTitls;

    private MyListent onPageChangeListener;

    //初始化方法
    public void initData(String [] titles, ViewPager viewPager, int defaultIndex){
        mViewPager = viewPager;
        myTitls = titles;
        createDynamicLine();
        root = LayoutInflater.from(mContext).inflate(R.layout.tesstttsts,null);
        ll = root.findViewById(R.id.ll);
        addView(root);

        setCurrentItem(defaultIndex);
        onPageChangeListener = new MyListent();
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private DynamicLine dynamicLine;
    private void createDynamicLine() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dynamicLine = new DynamicLine(getContext());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        dynamicLine.setLayoutParams(params);
    }

    public void setCurrentItem(int index) {


    }


    class MyListent implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
