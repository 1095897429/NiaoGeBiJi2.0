package com.qmkj.niaogebiji.module.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:固定标题的指示器
 */
public class DynamicLine extends View {

    //横坐标
    private float startX, stopX;

    //画笔
    private Paint paint;

    private Scroller scroller;

    private RectF rectF = new RectF(startX, 0, stopX, 0);

    public DynamicLine(Context context) {
        this(context,null);
    }

    public DynamicLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DynamicLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.yellow));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(20, MeasureSpec.getMode(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getScreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        rectF.set(startX,0,stopX, SizeUtils.dp2px(10));
        canvas.drawRoundRect(rectF,SizeUtils.dp2px(5),SizeUtils.dp2px(5),paint);
    }

    //更新View，传入坐标
    public void updateView(float startX,float stopX){
        this.startX = startX;
        this.stopX = stopX;
        invalidate();
    }
}
