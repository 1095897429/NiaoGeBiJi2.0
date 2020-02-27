package com.qmkj.niaogebiji.common.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.module.activity.HomeActivity;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-8
 * 描述:视频的基类
 * 关键点：1.service控制mediaplay播放状态
 *         2.切换资源
 *         3.让seekbar只能在播放时移动
 *         4.有底部栏的需加上底部栏，没有的不加
 *
 *
 *
 * 1.自动播放添加监听
 * 2.手动播放添加
 * 3.切换界面添加监听
 * 4.移动seekbar添加监听
 *
 *
 * 1.全局变量isAudaioShow 判断是否 显示音频播放控件
 * 2.全局变量 mMyBinder 控制音频的播放
 * 3.控件点击事件 保存到全局变量中
 * 3.每次界面进入时，都给这个控件设置了点击事件，因此在退出上个界面时，再点击控件还是有效果
 *
 *
 * 全局发送布局
 */
public abstract class BaseActivity extends AppCompatActivity {

    //上下文
    protected Context mContext;
    //ButterKnife
    private Unbinder mUnbinder;
    //全局常量
    private static int currentProgress;
    private static int maxProgress;
    private static long audiotime;
    public static boolean isAudaioShow;
    public static long  currenttime;


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

        //加入Activity管理器中
        BaseApp.getApplication().getActivityManage().addActivity(this);
        //注册事件
        if(regEvent()){
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.activity_base);

        ((ViewGroup)findViewById(R.id.fl_content)).addView(getLayoutInflater().inflate(getLayoutId(),null));

        //初始化ButterKnife
        mUnbinder = ButterKnife.bind(this);

        //设置事件
        initAudioEvent();

        //设置发送事件
        initSendEvent();

        initFirstData();

    }


    @BindView(R.id.ll_circle_send)
    LinearLayout ll_circle_send;

    @BindView(R.id.rl_sending)
    RelativeLayout rl_sending;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.rl_send_ok)
    RelativeLayout rl_send_ok;

    @BindView(R.id.rl_send_fail)
    RelativeLayout rl_send_fail;


    private static int currentSendProgress;
    private static int maxSendProgress;

    private void initSendEvent() {

    }

    //布局找到的时候设置数据
    public void initFirstData() {

    }


    @SuppressLint("CheckResult")
    protected  void initAudioEvent(){
        //主要用于不在播放时，不可移动seekbar
        seekbar.setEnabled(true);

        //详情
        RxView.clicks(toDetail)
                //每1秒中只处理第一个元素
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    if(!TextUtils.isEmpty(newID)){
                        UIHelper.toNewsDetailActivity(this,newID);
                    }
                });

        //之前是正在播放，现在是暂停
        RxView.clicks(pause)
                //每1秒中只处理第一个元素
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    MobclickAgentUtils.onEvent(UmengEvent.index_player_pause_2_0_0);

                    //主要用于不在播放时，不可移动seekbar
                    seekbar.setEnabled(true);
                    if(HomeActivity.mMyBinder != null){
                        HomeActivity.mMyBinder.pauseMusic();
                        pause();
                    }
                });


        //之前是暂停，现在是正在播放
        RxView.clicks(play)
                //每1秒中只处理第一个元素
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    MobclickAgentUtils.onEvent(UmengEvent.index_player_play_2_0_0);

                    //主要用于不在播放时，不可移动seekbar
                    seekbar.setEnabled(true);
                    HomeActivity.mMyBinder.playMusic();
                    HomeActivity.mMediaService.setOnProgressListener(progress -> {
                        if(seekbar != null){
                            runOnUiThread(() -> {
                                timeParse(time,progress);
                                seekbar.setProgress(progress);
                                currentProgress = progress;
                            });
                        }

                    });
                    play();
                });


        RxView.clicks(close)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    MobclickAgentUtils.onEvent(UmengEvent.index_player_close_2_0_0);

                    part_audio.setVisibility(View.GONE);
                    isAudaioShow = false;
                    seekbar.setEnabled(true);
                    currentProgress = 0;
                    if(seekbar != null){
                       seekbar.setProgress(0);
                    }
                    if(HomeActivity.mMyBinder != null){
                        HomeActivity.mMyBinder.closeMedia();
                    }

                });


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
                KLog.d("tag","当前进度是  " + seekBar.getProgress());
                int dest = seekBar.getProgress();
                currentProgress = dest;
                runOnUiThread(() -> {
                    seekBar.setProgress(currentProgress);
                    timeParse(time,dest);
                });
                if(HomeActivity.mMyBinder != null){
                    HomeActivity.mMyBinder.seekToPositon(dest);
                }
            }
        });

    }


    public static void timeParse(TextView timeView,long duration) {
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
        KLog.e("tag"," 时间是 " + time);
        if(timeView != null){
            timeView.setText(time);
        }
        currenttime = duration;
    }


    String newID;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioEvent(AudioEvent event){
        String url = event.getUrl();
        newID = event.getNewId();
        KLog.e("tag","播放视频的路径是 " + url  + " 播放名称 " + event.getTitle());
        if(TextUtils.isEmpty(event.getUrl())){
            ToastUtils.showShort("播放资源有误~");
            return;
        }
        part_audio.setVisibility(View.VISIBLE);
        audio_title.setText("今日早报｜" + event.getTitle());
        isAudaioShow = true;


        //先暂停
        HomeActivity.mMyBinder.pauseMusic();

        //有资源时 先关闭之前资源
        HomeActivity.mMyBinder.closeMedia();

        //有资源时 重新准备新资源
        HomeActivity.mMediaService.prepare(url);


        HomeActivity.mMediaService.setOnCloseListener(() -> {
            if(play != null){
                play.setVisibility(View.VISIBLE);
            }
            if(close != null){
                close.setVisibility(View.VISIBLE);
            }
            if(pause != null){
                pause.setVisibility(View.GONE);
            }
            if(seekbar != null){
                seekbar.setProgress(0);
            }

        });

        HomeActivity.mMediaService.setOnEndListener(() -> {

            if(play != null){
                play.setVisibility(View.VISIBLE);
            }
            if(close != null){
                close.setVisibility(View.VISIBLE);
            }
            if(pause != null){
                pause.setVisibility(View.GONE);
            }

        });


        //准备完成回调 重置默认值
        HomeActivity.mMediaService.setOnStartListener(totalLength -> {
            currentProgress = 0;
            maxProgress = totalLength;
            audiotime = totalLength;

            if(seekbar != null){
                seekbar.setMax(totalLength);
                seekbar.setProgress(currentProgress);
            }
            //控件 恢复默认值
            if(play != null){
                play.setVisibility(View.VISIBLE);
            }
            if(close != null){
                close.setVisibility(View.VISIBLE);
            }
            if(pause != null){
                pause.setVisibility(View.GONE);
            }
            //👇是自动播放
            play();
            HomeActivity.mMyBinder.playMusic();
            HomeActivity.mMediaService.setOnProgressListener(progress -> {
                if(seekbar != null){

                    runOnUiThread(() -> {
                        timeParse(time,progress);
                        seekbar.setProgress(progress);
                        currentProgress = progress;

                    });
                }

            });

        });

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
//        KLog.d("tag","onAttachedToWindow");
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
    public void initData() {}
    protected abstract int getLayoutId();
    protected abstract void initView();
    //*************************************** eventbus实现*************************************

    /** 需要接收事件 重写该方法 并返回true */
    protected boolean regEvent(){
        return false;
    }


    //*************************************** eventbus实现*************************************


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


    /** ---------------------------------  播放  ---------------------------------*/

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
    public RelativeLayout part_audio;

    @BindView(R.id.toDetail)
    ImageView toDetail;



    @Override
    public void onStop() {
        super.onStop();
    }



    @Override
    protected void onResume() {

        super.onResume();
        MobclickAgent.onResume(this);

        //存在 + view显示
        if(HomeActivity.mMyBinder != null && isAudaioShow){
            part_audio.setVisibility(View.VISIBLE);

            if(getClass().getSimpleName().equals("HomeActivity")
                || getClass().getSimpleName().equals("NewsDetailActivity") ){
                //设置view的距离
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) part_audio.getLayoutParams();
                lp.setMargins(SizeUtils.dp2px(16f),0,SizeUtils.dp2px(16f),SizeUtils.dp2px(34f + 49f));
                part_audio.setLayoutParams(lp);
            }else{
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) part_audio.getLayoutParams();
                lp.setMargins(SizeUtils.dp2px(16f),0,SizeUtils.dp2px(16f),SizeUtils.dp2px(34f));
                part_audio.setLayoutParams(lp);
            }

            //全局 设置界面进度
            seekbar.setMax(maxProgress);
            seekbar.setProgress(currentProgress);
            //设置当时时间
            time.setText(timeParse(currenttime) + "");
            //视频正在播放
            if( HomeActivity.mMyBinder.isPlaying()){
                HomeActivity.mMyBinder.playMusic();
                play.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }else{
                play.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }

            HomeActivity.mMediaService.setOnProgressListener(progress -> {
                if(seekbar != null){
                    runOnUiThread(() -> {
                        timeParse(time,progress);
                        seekbar.setProgress(progress);
                        currentProgress = progress;
                    });
                }

            });


            HomeActivity.mMediaService.setOnEndListener(() -> {
                if(play != null){
                    play.setVisibility(View.VISIBLE);
                }
                if(close != null){
                    close.setVisibility(View.VISIBLE);
                }
                if(pause != null){
                    pause.setVisibility(View.GONE);
                }

            });
        }else{
            part_audio.setVisibility(View.GONE);
        }
    }

    private void play() {
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        close.setVisibility(View.GONE);
        setMarquee(audio_title);
    }

    private void pause() {
        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);
        close.setVisibility(View.VISIBLE);
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




    /** --------------------------------- 退出动画  ---------------------------------*/
    protected void finishWithAnim(int inAnim,int outAnim){
        finish();
        overridePendingTransition(inAnim,outAnim);
    }


    protected void changePriaseStatus() {

    }



    /** --------------------------------- 分享 链接  ---------------------------------*/
    // 左边文字，右边图片 -- link形式
    protected void shareWxCircleByWeb(WxShareBean bean) {
        if (this == null){
            return;
        }
        if(null != bean){
            String sharepic = bean.getPic();
            String shareurl = bean.getShare_url();
            String title = bean.getShare_title();
            String summary = bean.getShare_summary();
            SHARE_MEDIA platform;
            platform = SHARE_MEDIA.WEIXIN_CIRCLE;
            UMImage thumb;
            if (TextUtils.isEmpty(sharepic)) {
                thumb = new UMImage(this, R.mipmap.icon_fenxiang);
            } else {
                thumb = new UMImage(this, sharepic);
            }
            UMWeb web = new UMWeb(shareurl);
            //标题
            web.setTitle(title);
            //缩略图
            web.setThumb(thumb);
            //描述
            web.setDescription(summary);
            //传入平台
            new ShareAction(this)
                    .setPlatform(platform)
                    .withMedia(web)
                    .share();
        }
    }


    //分享微信（web) 链接
    protected void shareWxByWeb(WxShareBean bean) {
        if(null == this){
            return;
        }
        if(null != bean){
            String sharepic = bean.getPic();
            String shareurl = bean.getShare_url();
            String title = bean.getShare_title();
            String summary = bean.getShare_summary();
            SHARE_MEDIA platform;
            platform = SHARE_MEDIA.WEIXIN;
            UMImage thumb;
            if (TextUtils.isEmpty(sharepic)) {
                thumb = new UMImage(this, R.mipmap.icon_fenxiang);
            } else {
                thumb = new UMImage(this, sharepic);
            }
            UMWeb web = new UMWeb(shareurl);
            //标题
            web.setTitle(title);
            //缩略图
            web.setThumb(thumb);
            //描述
            web.setDescription(summary);
            //传入平台
            new ShareAction(this)
                    .setPlatform(platform)
                    .withMedia(web)
                    .share();
        }

    }


    // 分享微信（pic)
    public void shareWxByPic(Bitmap bitmap) {
        if (this == null){
            return;
        }

        SHARE_MEDIA platform;
        platform = SHARE_MEDIA.WEIXIN;
        UMImage image = new UMImage(this, bitmap);
        UMImage  thumb = new UMImage(this, bitmap);
        image.setThumb(thumb);

        //传入平台
        new ShareAction(this)
                .withText("哈哈")
                .setPlatform(platform)
                .withMedia(image)
                .share();
    }


    // 分享微信圈（pic)
    public void shareWxCircleByPic(Bitmap bitmap) {
        if (this == null){
            return;
        }

        SHARE_MEDIA platform;
        platform = SHARE_MEDIA.WEIXIN_CIRCLE;
        UMImage  image = new UMImage(this, bitmap);

        //传入平台
        new ShareAction(this)
                .withText("哈哈")
                .setPlatform(platform)
                .withMedia(image)
                .share();
    }


    //检查权限
    protected boolean hasPermissions(@NonNull Context context,
                                         @Size(min = 1) @NonNull String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w("tag", "hasPermissions: API version < M, returning true by default");
            return true;
        }

        if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /** ---------------------------------  评论  ---------------------------------*/
    protected void setEmpty(BaseQuickAdapter adapter){
        //不需要可以配置加载更多
        adapter.disableLoadMoreIfNotFullPage();
        //TODO 预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        adapter.setPreLoadNumber(2);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.activity_empty,null);
        adapter.setEmptyView(emptyView);
        ((TextView)emptyView.findViewById(R.id.tv_empty)).setText("成为第一个评论者");
    }



    public interface OnAudioListener {
        void onAudio();
    }

    /**
     * 完成结束回调
     */
    private OnAudioListener mOnAudioListener;

    public void setOnAudioListener(OnAudioListener onAudioListener) {
        mOnAudioListener = onAudioListener;
    }
}
