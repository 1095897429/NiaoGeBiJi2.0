package com.qmkj.niaogebiji.module.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.qmkj.niaogebiji.R;

import java.util.ArrayList;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:
 */
public class ViewPagerTitleFeather extends LinearLayout {

    //标题
    private String[] titles;
    //控件
    private ArrayList<TextView> textViews = new ArrayList<>();
    //点击事件
    private OnTextViewClick onTextViewClick;
    //字体
    private Typeface typeface;

    private DynamicLine dynamicLine;
    private ViewPager viewPager;
    private MyOnPageChangeListenerFeather onPageChangeListener;

    public ViewPagerTitleFeather(Context context) {
        this(context, null);
    }

    public ViewPagerTitleFeather(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTitleFeather(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    //初始化方法
    public void initData(String[] titles, ViewPager viewPager, int defaultIndex) {
        this.titles = titles;
        this.viewPager = viewPager;
        createDynamicLine();
        createTextViews(titles);
        setCurrentItem(defaultIndex);
        onPageChangeListener = new MyOnPageChangeListenerFeather(getContext(), viewPager, dynamicLine, this);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setCurrentItem(int index) {
        for (int i = 0; i < textViews.size(); i++) {
            if (i == index) {
                textViews.get(i).setTextColor(getResources().getColor(R.color.text_first_color));
                textViews.get(i).setTextSize(18);
                textViews.get(i).setTypeface(typeface);
            } else {
                textViews.get(i).setTextColor(getResources().getColor(R.color.text_news_tag_color));
                textViews.get(i).setTextSize(15);
                textViews.get(i).setTypeface(null);
            }
        }
    }

    private void createTextViews(String[] titles) {

        //创建RelativeLayout
        RelativeLayout rl = new RelativeLayout(getContext());
        LinearLayout.LayoutParams linearLayoutParams0 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.setLayoutParams(linearLayoutParams0);
        rl.setGravity(Gravity.CENTER);
        rl.addView(dynamicLine);


        LinearLayout textViewLl = new LinearLayout(getContext());
        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textViewLl.setLayoutParams(linearLayoutParams);

        textViewLl.setOrientation(HORIZONTAL);

        LinearLayout.LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);


        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/DIN-Bold.otf");

        for (int i = 0; i < titles.length; i++) {
            TextView textView = new TextView(getContext());
            textView.setText(titles[i]);
            textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
            textView.setTextSize(15);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            textView.setOnClickListener(onClickListener);
            textView.setTag(i);
            textViews.add(textView);
            textViewLl.addView(textView);
        }

        rl.addView(textViewLl);

        addView(rl);


    }

    private void createDynamicLine() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dynamicLine = new DynamicLine(getContext());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        dynamicLine.setLayoutParams(params);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewPager.removeOnPageChangeListener(onPageChangeListener);
    }

    //返回textview控件
    public ArrayList<TextView> getTextView() {
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
                    textViews.get(i).setTextSize(18);
                } else {
                    textViews.get(i).setTextColor(getResources().getColor(R.color.text_news_tag_color));
                    textViews.get(i).setTextSize(15);
                }
            }

            viewPager.setCurrentItem((int) v.getTag());
            if (onTextViewClick != null) {
                onTextViewClick.textViewClick((TextView) v, (int) v.getTag());
            }

            //TODO 10.15 设置延迟事件，避免多次触发太快
//            new Handler().postDelayed(() -> {
//                //设置viewpager的项目
//                viewPager.setCurrentItem((int) v.getTag());
//                if (onTextViewClick != null) {
//                    onTextViewClick.textViewClick((TextView) v, (int) v.getTag());
//                }
//            },300);


        }
    };

}
