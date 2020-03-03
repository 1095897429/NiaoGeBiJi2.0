package com.qmkj.niaogebiji.module.widget.tab4;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.widget.tab3.DynamicLineNoRadiu3;
import com.qmkj.niaogebiji.module.widget.tab3.MyOnPageChangeListener3;
import com.socks.library.KLog;

import java.util.ArrayList;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020.3.3
 * 描述: 固定标题的指示器 -- 可滑动
 */
public class ViewPagerTitleSlide4 extends LinearLayout {

    //标题
    private String[] titles;
    //控件
    private ArrayList<View> textViews = new ArrayList<>();
    //点击事件
    private OnTextViewClick onTextViewClick;
    //字体
    private Typeface typeface;

    private DynamicLineNoRadiu4 dynamicLine;
    private ViewPager viewPager;
    private MyOnPageChangeListener4 onPageChangeListener;
    private Context mContext;

    private int myDefault;


    private  LinearLayout textViewLl;

    public ViewPagerTitleSlide4(Context context) {
        this(context, null);
    }

    public ViewPagerTitleSlide4(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTitleSlide4(Context context, AttributeSet attrs, int defStyleAttr) {
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

//        setLineMargin();

        setCurrentItem(defaultIndex);
        //添加传递进来vp的滑动事件
        onPageChangeListener = new MyOnPageChangeListener4(getContext(), viewPager, dynamicLine, this,defaultIndex);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
//        rl.setBackgroundColor(R.color.blue);
        LayoutParams linearLayoutParams0 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl.setLayoutParams(linearLayoutParams0);
        rl.addView(dynamicLine);


        textViewLl  = new LinearLayout(getContext());
        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        textViewLl.setLayoutParams(linearLayoutParams);

//        textViewLl.setBackgroundColor(R.color.greenStart);

        textViewLl.setOrientation(LinearLayout.HORIZONTAL);
        //设置布局中的textView居左中间
//        textViewLl.setGravity(Gravity.BOTTOM);

        // 给与一定的宽度
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);

        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/DIN-Bold.otf");

        for (int i = 0; i < titles.length; i++) {

            if(i == titles.length - 1){
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.mipmap.icon_first_hot_default);
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


//            if(i == 0){
//                textView.setBackgroundColor(getResources().getColor(R.color.green2));
//
//            }else if(i == 1){
//                textView.setBackgroundColor(getResources().getColor(R.color.roseEnd));
//            }

            if(i == 0){
                params.setMargins(SizeUtils.dp2px(16),0,SizeUtils.dp2px(16),0);
            }else{
                params.setMargins(0,0,SizeUtils.dp2px(16),0);
            }
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.BOTTOM);
            textView.setOnClickListener(onClickListener);
            textView.setTag(i);
            //集合
            textViews.add(textView);
            textViewLl.addView(textView);

        }

        rl.addView(textViewLl);

        addView(rl);

    }

    private void createDynamicLine() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        dynamicLine = new DynamicLineNoRadiu4(getContext());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
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

//                    if(textViews.get(i) instanceof ImageView){
//                        ImageView imageView = (ImageView) textViews.get(i);
//                        LinearLayout.LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
//                        lp.height = LayoutParams.WRAP_CONTENT;
//                        lp.gravity = Gravity.CENTER | Gravity.LEFT;
//                        imageView.setLayoutParams(lp);
//                    }

                } else {

                    if(textViews.get(i) instanceof TextView){
                        TextView textView = (TextView) textViews.get(i);
                        textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
                        textView.setTextSize(16);
                    }

//                    if(textViews.get(i) instanceof ImageView){
//                        ImageView imageView = (ImageView) textViews.get(i);
//                        LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
//                        lp.height = LayoutParams.WRAP_CONTENT;
//                        lp.width = SizeUtils.dp2px(50);
//                        imageView.setImageResource(R.mipmap.icon_first_hot_default);
//                        imageView.setLayoutParams(lp);
//                    }
                }
            }

            viewPager.setCurrentItem((int) v.getTag());
            if (onTextViewClick != null) {
                onTextViewClick.textViewClick((TextView) v, (int) v.getTag());
            }

        }
    };

    //获取宽的长度
    public  float getTextViewLength(TextView textView) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        return paint.measureText(textView.getText().toString());
    }

}
