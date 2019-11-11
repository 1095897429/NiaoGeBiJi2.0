package com.qmkj.niaogebiji.common.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

//import com.qmkj.niaogebiji.module.widget.XnClassicsHeader;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-03
 * 描述:视频的懒加载基类
 */
public abstract class BaseLazyFragment extends Fragment {


    public static final String TAG = "BaseLazyFragment";
    //上下文
    protected Context mContext;
    //ButterKnife
    private Unbinder mUnbinder;
    //错误布局
    private ViewStub emptyView;
    //fragment是否可见
    protected boolean isVisible;
    //控件是否已经初始化完成
    protected boolean isPrepared;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        KLog.e(TAG,getClass().getSimpleName() + " -- onAttach");
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        KLog.e(TAG,getClass().getSimpleName() + " -- onCreateView");
        View view = getLayoutInflater().inflate(getLayoutId(),null);
        mUnbinder = ButterKnife.bind(this,view);
        if(regEvent()){
            EventBus.getDefault().register(this);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KLog.e(TAG,getClass().getSimpleName() + " -- onViewCreated");
        initView();
        finishCreateView();
        initData();
    }


    @Override
    public void onDestroy() {
        KLog.e(TAG,getClass().getSimpleName() + " -- onDestroy");
        super.onDestroy();
        //解绑
        if(null != mUnbinder){
            mUnbinder.unbind();
            mUnbinder = null;
        }
        //解绑
        if (regEvent()) {
            EventBus.getDefault().unregister(this);
        }
    }


    /** 默认加载数据 */
    protected void initData() {
        KLog.e(TAG,getClass().getSimpleName() + " -- initData");
    }

    /** 第一次都会走这里，不过后续通过Fragment是否可见判断 */
    public void finishCreateView() {
        isPrepared = true;
        lazyLoad();
    }

    protected void onInvisible() {

    }

    protected void onVisible() {
        lazyLoad();
    }


    /** 懒加载 */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
        lazyLoadData();
        isPrepared = false;
    }


    protected void lazyLoadData() {
        KLog.e(TAG,getClass().getSimpleName() + " -- 懒加载数据开始");
    }



    /** 需要接收事件 重写该方法 并返回true */
    protected boolean regEvent(){
        return false;
    }


    protected abstract int getLayoutId();

    protected abstract void initView();



}
