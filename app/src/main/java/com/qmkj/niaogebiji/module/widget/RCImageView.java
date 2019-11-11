package com.qmkj.niaogebiji.module.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Checkable;

import androidx.appcompat.widget.AppCompatImageView;

import com.gcssloop.widget.helper.RCAttrs;
import com.gcssloop.widget.helper.RCHelper;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class RCImageView extends AppCompatImageView implements Checkable, RCAttrs {


    //组合类，本质是它操作
    RCHelper mRCHelper;


    public RCImageView(Context context) {
        this(context, null);
    }

    public RCImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RCImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRCHelper = new RCHelper();
        //获取属性
        mRCHelper.initAttrs(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取尺寸后的长宽
        mRCHelper.onSizeChanged(this, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        mRCHelper.refreshRegion(this);
        if (mRCHelper.mClipBackground) {
            canvas.save();
            canvas.clipPath(mRCHelper.mClipPath);
            super.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //离屏绘制
        canvas.saveLayer(mRCHelper.mLayer, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
//        mRCHelper.onClipDraw(canvas);
        onClipLayout(canvas);
        canvas.restore();
    }

    // TODO: 2019-08-21 9.0圆角失效 https://github.com/GcsSloop/rclayout/issues/37
    public void onClipLayout(Canvas canvas){
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawPath(mRCHelper.mClipPath, mPaint);
        } else {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

            final Path path = new Path();
            path.addRect(0, 0, (int) mRCHelper.mLayer.width(), (int) mRCHelper.mLayer.height(), Path.Direction.CW);
            path.op(mRCHelper.mClipPath, Path.Op.DIFFERENCE);
            canvas.drawPath(path, mPaint);
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            refreshDrawableState();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            setPressed(false);
            refreshDrawableState();
        }
        if (!mRCHelper.mAreaRegion.contains((int) ev.getX(), (int) ev.getY())) {
            setPressed(false);
            refreshDrawableState();
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }


    //--- 公开接口 ----------------------------------------------------------------------------------

    @Override
    public void setClipBackground(boolean clipBackground) {
        mRCHelper.mClipBackground = clipBackground;
        invalidate();
    }

    @Override
    public void setRoundAsCircle(boolean roundAsCircle) {
        mRCHelper.mRoundAsCircle = roundAsCircle;
        invalidate();
    }

    @Override
    public void setRadius(int radius) {
        for (int i = 0; i < mRCHelper.radii.length; i++) {
            mRCHelper.radii[i] = radius;
        }
        invalidate();
    }

    @Override
    public void setTopLeftRadius(int topLeftRadius) {
        mRCHelper.radii[0] = topLeftRadius;
        mRCHelper.radii[1] = topLeftRadius;
        invalidate();
    }

    @Override
    public void setTopRightRadius(int topRightRadius) {
        mRCHelper.radii[2] = topRightRadius;
        mRCHelper.radii[3] = topRightRadius;
        invalidate();
    }

    @Override
    public void setBottomLeftRadius(int bottomLeftRadius) {
        mRCHelper.radii[4] = bottomLeftRadius;
        mRCHelper.radii[5] = bottomLeftRadius;
        invalidate();
    }

    @Override
    public void setBottomRightRadius(int bottomRightRadius) {
        mRCHelper.radii[6] = bottomRightRadius;
        mRCHelper.radii[7] = bottomRightRadius;
        invalidate();
    }

    @Override
    public void setStrokeWidth(int strokeWidth) {
        mRCHelper.mStrokeWidth = strokeWidth;
        invalidate();
    }

    @Override
    public void setStrokeColor(int strokeColor) {
        mRCHelper.mStrokeColor = strokeColor;
        invalidate();
    }

    @Override
    public boolean isClipBackground() {
        return mRCHelper.mClipBackground;
    }

    @Override
    public boolean isRoundAsCircle() {
        return mRCHelper.mRoundAsCircle;
    }

    @Override
    public float getTopLeftRadius() {
        return mRCHelper.radii[0];
    }

    @Override
    public float getTopRightRadius() {
        return mRCHelper.radii[2];
    }

    @Override
    public float getBottomLeftRadius() {
        return mRCHelper.radii[4];
    }

    @Override
    public float getBottomRightRadius() {
        return mRCHelper.radii[6];
    }

    @Override
    public int getStrokeWidth() {
        return mRCHelper.mStrokeWidth;
    }

    @Override
    public int getStrokeColor() {
        return mRCHelper.mStrokeColor;
    }


    //--- Selector 支持 ----------------------------------------------------------------------------

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mRCHelper.drawableStateChanged(this);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mRCHelper.mChecked != checked) {
            mRCHelper.mChecked = checked;
            refreshDrawableState();
            if (mRCHelper.mOnCheckedChangeListener != null) {
                mRCHelper.mOnCheckedChangeListener.onCheckedChanged(this, mRCHelper.mChecked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mRCHelper.mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mRCHelper.mChecked);
    }

    public void setOnCheckedChangeListener(RCHelper.OnCheckedChangeListener listener) {
        mRCHelper.mOnCheckedChangeListener = listener;
    }

}
