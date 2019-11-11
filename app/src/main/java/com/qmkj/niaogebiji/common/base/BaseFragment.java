package com.qmkj.niaogebiji.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qmkj.niaogebiji.R;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 */
public abstract class BaseFragment extends Fragment {
    public static final String TAG = "BaseFragment";
    //上下文
    protected Context mContext;
    //ButterKnife
    private Unbinder mUnbinder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.e(TAG,getClass().getSimpleName() + " -- onCreate");
        mContext = getActivity();
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
        KLog.e(TAG,getClass().getSimpleName() +" -- onViewCreated");
        initView();
        initData();
    }



    @Override
    public void onDestroy() {
        KLog.e(TAG,getClass().getSimpleName() +" -- onDestroy");
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



    //*************************************** 实现方法*************************************

    public void initData() {
        KLog.e(TAG,getClass().getSimpleName() + " -- initData");
    }

    //*************************************** 实现方法*************************************

    //*************************************** 抽象方法 *************************************


    protected abstract int getLayoutId();

    protected abstract void initView();

    //*************************************** 抽象方法 *************************************

    //*************************************** eventbus实现*************************************

    /** 需要接收事件 重写该方法 并返回true */
    protected boolean regEvent(){
        return false;
    }


    //*************************************** eventbus实现*************************************

    //*************************************** 界面实现*************************************


}
