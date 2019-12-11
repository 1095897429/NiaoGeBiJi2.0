package com.qmkj.niaogebiji.module.widget.tab3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.widget.tab1.DynamicLineNoRadiu;
import com.socks.library.KLog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-10-10
 * 描述: 固定标题的指示器 -- 可滑动
 */
public class ViewPagerTitleSlide3 extends LinearLayout {

    //标题
    private String[] titles;
    //控件
    private ArrayList<View> textViews = new ArrayList<>();
    //点击事件
    private OnTextViewClick onTextViewClick;
    //字体
    private Typeface typeface;

    private DynamicLineNoRadiu3 dynamicLine;
    private ViewPager viewPager;
    private MyOnPageChangeListener3 onPageChangeListener;
    private Context mContext;

    private int myDefault;


    private  LinearLayout textViewLl;

    public ViewPagerTitleSlide3(Context context) {
        this(context, null);
    }

    public ViewPagerTitleSlide3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTitleSlide3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }






    private void init() {

    }


    //初始化方法
    public void initData(String [] titles,ViewPager viewPager,int defaultIndex){
        this.titles = titles;
        this.viewPager = viewPager;
        myDefault = defaultIndex;
        createDynamicLine();
        createTextViews(titles);

        setLineMargin();

        setCurrentItem(defaultIndex);
        onPageChangeListener = new MyOnPageChangeListener3(getContext(), viewPager, dynamicLine, this,defaultIndex);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void setLineMargin() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dynamicLine.getLayoutParams();
        int space = SizeUtils.dp2px(44) / 2 ;
        TextView temp = (TextView) textViews.get(myDefault);
        float fonts = getTextViewLength(temp) / 6;
        float result = space + fonts;
        params.setMargins(0, (int) result,0,0);
        dynamicLine.setLayoutParams(params);
    }

    public void setCurrentItem(int index) {

        for (int i = 0; i < textViews.size(); i++) {
            if (i == index) {

                if(textViews.get(i) instanceof TextView){
                    TextView textView = (TextView) textViews.get(i);
                    textView.setTextColor(getResources().getColor(R.color.text_first_color));
                    textView.setTextSize(18);
                    textView.setTypeface(typeface);
                }


                if(textViews.get(i) instanceof ImageView){
                    ImageView imageView = (ImageView) textViews.get(i);
                    KLog.d("tag","图片选总");
                }

//                TextView textView = (TextView) textViews.get(i);
//                textView.setTextColor(getResources().getColor(R.color.text_first_color));
//                textView.setTextSize(22);
//                textView.setTypeface(typeface);

            } else {

//                TextView textView = (TextView) textViews.get(i);
//                textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
//                textView.setTextSize(16);
//                textView.setTypeface(null);

                if(textViews.get(i) instanceof TextView){
                    TextView textView = (TextView) textViews.get(i);
                    textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
                    textView.setTextSize(16);
                    textView.setTypeface(null);
                }

                if(textViews.get(i) instanceof ImageView){
                    ImageView imageView = (ImageView) textViews.get(i);
//                    KLog.d("tag","图片没选中选总");
                }

            }
        }
    }

    private void createTextViews(String[] titles) {
        //创建RelativeLayout
        RelativeLayout rl = new RelativeLayout(getContext());
        LayoutParams linearLayoutParams0 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl.setLayoutParams(linearLayoutParams0);
        rl.addView(dynamicLine);


        textViewLl  = new LinearLayout(getContext());
        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        linearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textViewLl.setLayoutParams(linearLayoutParams);

        textViewLl.setOrientation(LinearLayout.HORIZONTAL);

        textViewLl.setGravity(Gravity.CENTER|Gravity.LEFT);

        // 给与一定的宽度
        LayoutParams params = new LayoutParams(SizeUtils.dp2px(60) ,ViewGroup.LayoutParams.WRAP_CONTENT);

        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/DIN-Bold.otf");

        for (int i = 0; i < titles.length; i++) {

            if(i == titles.length - 1){
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.mipmap.icon_first_hot);
                imageView.setLayoutParams(params);
                imageView.setTag(i);
                imageView.setOnClickListener(onClickListener);
                textViewLl.addView(imageView);
                textViews.add(imageView);
                break;
            }

            TextView textView = new TextView(getContext());
            textView.setText(titles[i]);
            textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
            textView.setTextSize(16);
            textView.setId(i);

            if(i == 4){
                textView.setBackgroundColor(getResources().getColor(R.color.green2));
            }

            textView.setLayoutParams(params);
            textView.setOnClickListener(onClickListener);
            textView.setTag(i);
            textViews.add(textView);
            textViewLl.addView(textView);

        }

        rl.addView(textViewLl);

        addView(rl);

    }

    private void createDynamicLine() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        dynamicLine = new DynamicLineNoRadiu3(getContext());
        dynamicLine.setLayoutParams(params);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(null != viewPager){
            viewPager.removeOnPageChangeListener(onPageChangeListener);
        }

    }

    //返回textview控件
    public ArrayList<View> getTextView(){
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

                    if(textViews.get(i) instanceof TextView){
                        TextView textView = (TextView) textViews.get(i);
                        textView.setTextColor(getResources().getColor(R.color.text_first_color));
                        textView.setTextSize(18);
                        textView.setTypeface(null);
                    }

                    if(textViews.get(i) instanceof ImageView){
                        ImageView imageView = (ImageView) textViews.get(i);
//                        LinearLayout.LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
//                        lp.height = SizeUtils.dp2px(44);
//                        lp.gravity = Gravity.CENTER | Gravity.LEFT;
//                        imageView.setLayoutParams(lp);
                    }

                } else {

                    if(textViews.get(i) instanceof TextView){
                        TextView textView = (TextView) textViews.get(i);
                        textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
                        textView.setTextSize(16);
                    }

                    if(textViews.get(i) instanceof ImageView){
                        ImageView imageView = (ImageView) textViews.get(i);
                        LinearLayout.LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
                        lp.height = LayoutParams.WRAP_CONTENT;
                        imageView.setLayoutParams(lp);
                    }
                }
            }

            viewPager.setCurrentItem((int) v.getTag());
            if (onTextViewClick != null) {
                onTextViewClick.textViewClick((TextView) v, (int) v.getTag());
            }

        }
    };

    public  float getTextViewLength(TextView textView) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        return paint.measureText(textView.getText().toString());
    }

}
