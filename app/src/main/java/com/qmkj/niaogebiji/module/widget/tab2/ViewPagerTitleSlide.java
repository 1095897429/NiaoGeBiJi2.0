package com.qmkj.niaogebiji.module.widget.tab2;

import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.widget.tab1.DynamicLineNoRadiu;
import com.qmkj.niaogebiji.module.widget.tab1.MyOnPageChangeListener;

import java.util.ArrayList;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-10-10
 * 描述: 固定标题的指示器 -- 可滑动
 */
public class ViewPagerTitleSlide extends HorizontalScrollView {

    //标题
    private String[] titles;
    //控件
    private ArrayList<TextView> textViews = new ArrayList<>();
    //点击事件
    private OnTextViewClick onTextViewClick;
    //字体
    private Typeface typeface;

    private DynamicLineNoRadiu dynamicLine;
    private ViewPager viewPager;
    private MyOnPageChangeListenerSlide onPageChangeListener;
    private Context mContext;


    private LinearLayout mLayout;

    private  LinearLayout textViewLl;

    public ViewPagerTitleSlide(Context context) {
        this(context, null);
    }

    public ViewPagerTitleSlide(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTitleSlide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    // 当某一项被选中时会回调onPageSelected方法，在这里我们可以让该PagerIndicator自动滚动：这里主要涉及到移动距离的计算
    public void changePager(final int last) {
        final TextView lastChild = (TextView) textViewLl.getChildAt(last);
        performItemClick(lastChild);
    }



    private void init() {
        // 设置横向滚动条不显示
        setHorizontalScrollBarEnabled(false);

        // 添加一个LinearLayout作为默认的子View
        mLayout = new LinearLayout(mContext);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams liLayoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mLayout, liLayoutParams);

    }


    //初始化方法
    public void initData(String [] titles,ViewPager viewPager,int defaultIndex){
        this.titles = titles;
        this.viewPager = viewPager;
        createDynamicLine();
        createTextViews(titles);
        setCurrentItem(defaultIndex);
        onPageChangeListener = new MyOnPageChangeListenerSlide(getContext(), viewPager, dynamicLine, this,defaultIndex);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setCurrentItem(int index) {
        for (int i = 0; i < textViews.size(); i++) {
            if (i == index) {
                textViews.get(i).setTextColor(getResources().getColor(R.color.text_first_color));
                textViews.get(i).setTextSize(22);
                textViews.get(i).setTypeface(typeface);
            } else {
                textViews.get(i).setTextColor(getResources().getColor(R.color.text_news_tag_color));
                textViews.get(i).setTextSize(16);
                textViews.get(i).setTypeface(null);
            }
        }
    }

    private void createTextViews(String[] titles) {

        //创建RelativeLayout
        RelativeLayout rl = new RelativeLayout(getContext());
        rl.setGravity(Gravity.CENTER);
        LayoutParams linearLayoutParams0 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.setLayoutParams(linearLayoutParams0);
        rl.addView(dynamicLine);


        textViewLl  = new LinearLayout(getContext());
        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textViewLl.setLayoutParams(linearLayoutParams);

        textViewLl.setOrientation(LinearLayout.HORIZONTAL);
        // 给与一定的宽度
        LayoutParams params = new LayoutParams(SizeUtils.dp2px(60) ,ViewGroup.LayoutParams.MATCH_PARENT);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/DIN-Bold.otf");

        for (int i = 0; i < titles.length; i++) {
            TextView textView = new TextView(getContext());
            textView.setText(titles[i]);
            textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
            textView.setTextSize(16);
            textView.setId(i);
//            if(i == 1){
//                textView.setBackgroundColor(getResources().getColor(R.color.yellow));
//            }if(i == 2){
//                textView.setBackgroundColor(getResources().getColor(R.color.roseEnd));
//            }

            textView.setLayoutParams(params);
            textView.setGravity(Gravity.BOTTOM|Gravity.LEFT);
            textView.setOnClickListener(onClickListener);
            textView.setTag(i);
            textViews.add(textView);
            textViewLl.addView(textView);
        }

        rl.addView(textViewLl);

        mLayout.addView(rl);

    }

    private void createDynamicLine() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dynamicLine = new DynamicLineNoRadiu(getContext());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        dynamicLine.setLayoutParams(params);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewPager.removeOnPageChangeListener(onPageChangeListener);
    }

    //返回textview控件
    public ArrayList<TextView> getTextView(){
        return textViews;
    }


    public interface OnTextViewClick {
        void textViewClick(TextView textView, int index);
    }

    public void setOnTextViewClickListener(OnTextViewClick onTextViewClick) {
        this.onTextViewClick = onTextViewClick;
    }

    //点击自定义的接口，然后会调用👇的onClick方法
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < textViews.size(); i++) {
                if (i == (int) v.getTag()) {
                    textViews.get(i).setTextColor(getResources().getColor(R.color.text_first_color));
                    textViews.get(i).setTextSize(22);
                } else {
                    textViews.get(i).setTextColor(getResources().getColor(R.color.text_news_tag_color));
                    textViews.get(i).setTextSize(16);
                }
            }

            viewPager.setCurrentItem((int) v.getTag());
            if (onTextViewClick != null) {
                onTextViewClick.textViewClick((TextView) v, (int) v.getTag());
            }

            performItemClick(v);

        }
    };


    private void performItemClick(View view) {
        //------get Display's Width--------
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;

        int scrollX = (view.getLeft() - (screenWidth / 2)) + (view.getWidth() / 2);

        //smooth scrolling horizontalScrollView
        smoothScrollTo(scrollX, 0);
    }

}
