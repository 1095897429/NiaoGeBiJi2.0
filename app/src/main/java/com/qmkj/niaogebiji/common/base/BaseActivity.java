package com.qmkj.niaogebiji.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
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
        setContentView(R.layout.activity_base);
        ((ViewGroup)findViewById(R.id.fl_content)).addView(getLayoutInflater().inflate(getLayoutId(),null));


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

    /** 子类接受事件 重写该方法 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioEvent(AudioEvent event){
        KLog.d("tag","AudioEvent");
        part_audio.setVisibility(View.VISIBLE);
        initMediaPlayer();
        initListener();
        //加载音频资源
        try {
            mMediaPlayer.setDataSource(getAudioLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 准备播放（异步）
        mMediaPlayer.prepareAsync();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //主要是用于监听进度值的改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //监听用户开始拖动进度条的时候
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //监听用户结束拖动进度条的时候
                int dest = seekBar.getProgress();
                mMediaPlayer.seekTo(dest);

            }
        });

        RxView.clicks(pause)
                //每1秒中只处理第一个元素
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    play.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.GONE);
                    close.setVisibility(View.VISIBLE);

                    play();
                });


        RxView.clicks(play)
                //每1秒中只处理第一个元素
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                    close.setVisibility(View.GONE);

                    play();

                });


        RxView.clicks(close)
                //每1秒中只处理第一个元素
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {

                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        mMediaPlayer.reset();
                    }
                    part_audio.setVisibility(View.GONE);

                });

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

    /** --------------------------------- 设置 app 字体不随系统字体设置改变  ---------------------------------*/


    String getAudioLocation = "https://sharefs.yun.kugou.com/201911121833/b21ec0e226573a3056d3fed" +
            "3ddc62f66/G001/M01/0D/17/QQ0DAFSOoouAWmrPADzKcq0p8H8687.mp3";


    private MediaPlayer mMediaPlayer;
    private boolean isplay;
    private Mythred mythred;


    @BindView(R.id.pause)
    ImageView pause;

    @BindView(R.id.play)
    ImageView play;

    @BindView(R.id.close)
    ImageView close;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.seekbar)
    SeekBar seekbar;

    @BindView(R.id.audio_title)
    TextView audio_title;



    @BindView(R.id.part_audio)
    RelativeLayout part_audio;

    // 初始化MediaPlayer
    private void initMediaPlayer() {
        if (mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
        // 设置音量，参数分别表示左右声道声音大小，取值范围为0~1
        mMediaPlayer.setVolume(0.5f, 0.5f);
        // 设置是否循环播放
        mMediaPlayer.setLooping(false);
    }


    private void initListener() {
        //播放出错监听
        mMediaPlayer.setOnErrorListener((mediaPlayer, what, extra) -> {
            Log.d("tag", "OnError - Error code: " + what + " Extra code: " + extra);
            return false;
        });
        //播放完成监听
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            KLog.d("tag","播放完成监听");
            isplay = false;
            mythred = null;
        });
        //网络流媒体缓冲监听
        mMediaPlayer.setOnBufferingUpdateListener((mediaPlayer, i) -> {
            // i 0~100
            Log.d("tag:", "缓存进度" + i + "%");
        });

        //准备Prepared完成监听
        mMediaPlayer.setOnPreparedListener(mediaPlayer -> {
            //获得音乐总时长
            int lengthoftime = mediaPlayer.getDuration();
            KLog.d("tag","获得音乐总时长 " + lengthoftime);
            seekbar.setMax(lengthoftime);
            time.setText(timeParse(lengthoftime));
        });

        //进度调整完成SeekComplete监听，主要是配合seekTo(int)方法
        mMediaPlayer.setOnSeekCompleteListener(mediaPlayer -> {
            KLog.d("tag");
        });
    }




    @Override
    public void onStop() {
        super.onStop();
        // 释放mediaPlayer
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    class Mythred extends Thread {
        @Override
        public void run() {
            super.run();
            while (seekbar.getProgress() <= seekbar.getMax()) {
                //设置进度条的进度
                //得到当前音乐的播放位置
                int currentPosition = mMediaPlayer.getCurrentPosition();
                Log.i("tag", "currentPosition " + currentPosition);
                seekbar.setProgress(currentPosition);
                //让进度条每一秒向前移动
                SystemClock.sleep(1000);

                if (!isplay) {
                    break;

                }

            }

        }
    }

    private void play() {
        if (mMediaPlayer.isPlaying()){
            isplay = false;
            mMediaPlayer.pause();
            //当停止播放时线程也停止了(这样也可以减少占用的内存)
            mythred = null;

        }else {
            isplay = true;
            mMediaPlayer.start();
            mythred = new Mythred();
            mythred.start();
        }
        setMarquee(audio_title);
    }

    /**
     * Android 音乐播放器应用里，读出的音乐时长为 long 类型以毫秒数为单位，例如：将 234736 转化为分钟和秒应为 03:55 （包含四舍五入）
     * @param duration 音乐时长
     * @return
     */
    public static String timeParse(long duration) {
        String time = "" ;
        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;
        long second = Math.round((float)seconds/1000) ;
        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+":" ;
        if( second < 10 ){
            time += "0" ;
        }
        time += second ;
        return time ;
    }



    private void setMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }

}
