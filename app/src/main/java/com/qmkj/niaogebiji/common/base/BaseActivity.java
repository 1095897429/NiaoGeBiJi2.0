package com.qmkj.niaogebiji.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-8
 * 描述:视频的基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    //上下文
    protected Context mContext;
    //ButterKnife
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (setStatusTopTextLightColor()) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setStatusBarDarkMode(true, this);
            } else{
                //使用默认的白色系,这里需要用此方式切换
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getStatusTopColor());
        }

        mContext = this;
        //设置布局
//        setContentView(R.layout.activity_base);
//        ((ViewGroup)findViewById(R.id.fl_content)).addView(getLayoutInflater().inflate(getLayoutId(),null));
        setContentView(getLayoutId());



        //初始化ButterKnife
        mUnbinder = ButterKnife.bind(this);
        //加入Activity管理器中
        BaseApp.getApplication().getActivityManage().addActivity(this);
        //注册事件
        if(regEvent()){
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 是否设置状态栏字体颜色light
     */
    public boolean setStatusTopTextLightColor() {
        return true;
    }

    public void setStatusBarDarkMode(boolean darkmode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取状态栏字体颜色
     */
    public int getStatusTopColor() {
        return Color.TRANSPARENT;
    }




    /** 当view被附着在窗体window时触发 */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
        initData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将Activity从管理器移除
        BaseApp.getApplication().getActivityManage().removeActivity(this);
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


    //*************************************** 普通方法 *************************************
    public void initData() {

    }


    //*************************************** 普通方法 *************************************

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


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }



    /** --------------------------------- 设置 app 字体不随系统字体设置改变  ---------------------------------*/

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res != null) {
            Configuration config = res.getConfiguration();
            if (config != null && config.fontScale != 1.0f) {
                config.fontScale = 1.0f;
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
        return res;
    }
}
