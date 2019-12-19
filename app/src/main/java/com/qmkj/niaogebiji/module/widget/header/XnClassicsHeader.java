package com.qmkj.niaogebiji.module.widget.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.qmkj.niaogebiji.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.socks.library.KLog;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-06
 * 描述:
 */
public class XnClassicsHeader extends LinearLayout implements RefreshHeader {

    private LottieAnimationView lottie;
    private ImageView mImageView;
    RelativeLayout item;
    int color;

    private AnimationDrawable animationDrawable;

    public XnClassicsHeader(Context context) {
        super(context);
        initView(context);
    }

    public XnClassicsHeader(Context context, @DrawableRes int resid) {
        super(context);
        this.color = resid;
        initView(context);
    }

    public XnClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context);
    }

    public XnClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        View v = LayoutInflater.from(context).inflate(R.layout.ref_view_head, null);
        item = v.findViewById(R.id.ref_view_head_item);
        mImageView = v.findViewById(R.id.image);

        lottie = v.findViewById(R.id.lottieAnimationView);

        addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        item.setBackgroundResource(color);


        //帧动画
        mImageView.setImageResource(R.drawable.splash_animation3);
        animationDrawable = (AnimationDrawable) mImageView.getDrawable();


    }

    @Override
    @NonNull
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
//            mProgressDrawable.start();//开始动画
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        return 500;//延迟500毫秒之后再弹回
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
//                KLog.d("tag","刷新None");
                if(animationDrawable != null && animationDrawable.isRunning()){
                    animationDrawable.stop();
                }
                break;
            case PullDownToRefresh:
//                    mHeaderText.setText("下拉开始刷新");
//                     mProgressView.setVisibility(VISIBLE);//隐藏动画
//                    mArrowView.animate().rotation(0);//还原箭头方向
//                mImageView.setVisibility(VISIBLE);
//                lottie.setVisibility(GONE);
//                KLog.d("tag","下拉开始刷新");

                break;
            case Refreshing:
//                    mHeaderText.setText("正在刷新");
//                    mProgressView.setVisibility(VISIBLE);//显示加载动画
//                    mArrowView.setVisibility(GONE);//隐藏箭头
//                mImageView.setVisibility(GONE);
//                lottie.setVisibility(VISIBLE);
//                KLog.d("tag","正在刷新");

               if(animationDrawable != null ){
                   animationDrawable.start();
               }

//                lottie.setImageAssetsFolder("images");
//                lottie.setAnimation("images/new_fresh.json");
//                //硬件加速，解决lottie卡顿问题
//                lottie.playAnimation();

                break;
            case ReleaseToRefresh:
//                    mHeaderText.setText("释放立即刷新");
//                    mArrowView.animate().rotation(180);//显示箭头改为朝上
                break;
            case RefreshFinish:
//                    mHeaderText.setText("刷新结束");
//                KLog.d("tag","刷新结束");
                break;
                default:
        }
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }


    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void setPrimaryColors(@ColorInt int... colors) {
    }


}
