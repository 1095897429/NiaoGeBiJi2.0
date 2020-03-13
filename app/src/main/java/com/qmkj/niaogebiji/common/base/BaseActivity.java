package com.qmkj.niaogebiji.common.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import android.view.animation.LinearInterpolator;
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

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.ProfessionAutherDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.service.MediaService;
import com.qmkj.niaogebiji.common.service.SendService;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.HomeActivityV2;
import com.qmkj.niaogebiji.module.bean.QINiuTokenBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.qmkj.niaogebiji.module.event.SendEvent;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xzh.imagepicker.bean.MediaFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
 * 4.发送事件 1 --- 开始播放 2 -- 播放回传进度 3 --- 界面显示进度 4 --- 在onResume继续播放 5 --- 在onResume继续监听进度 6
 *
 * 全局发送布局
 */
public abstract class BaseActivity extends AppCompatActivity  {

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

        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
    }

    private MsgReceiver msgReceiver;
    private Intent mIntent;
    /**
     * 广播接收器
     * @author len
     *
     */
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // ① 发送错误
            int error = intent.getIntExtra("error", 0);
            if(1 == error){
                ll_circle_send.setVisibility(View.VISIBLE);
                rl_sending.setVisibility(View.GONE);
                rl_send_fail.setVisibility(View.VISIBLE);
                sendStatus = isSendFail;
                return;
            }

            // ① 纯文本发送成功
            int no_pic = intent.getIntExtra("no_pic", 0);
            if(1 == no_pic){
                ll_circle_send.setVisibility(View.VISIBLE);
                progressBar.setMax(maxSendProgress);
                setAnimation(progressBar);
                sendStatus = isSendOk;
                KLog.e("tag","当前呈现的类名是 "+  BaseActivity.this.getClass().getSimpleName() + "  2222没有图片  ");
                return;
            }

            // ② 图片后台发送成功
            int pic = intent.getIntExtra("pic", 0);
            if(1 == pic){
                showNumView();
                sendStatus = isSendOk;
                KLog.e("tag","当前呈现的类名是 "+  BaseActivity.this.getClass().getSimpleName() + "  2222有图片  ");
                return;
            }



            // ③ 图片上传拿到进度，更新UI
            int progress = intent.getIntExtra("progress", 0);
            //设置进度条的最大值  和 正在进行的进度
            if(progressBar != null){
                ll_circle_send.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                progressBar.setMax(maxSendProgress);
                sendStatus = isSending;
            }

        }

    }


    public void showNumView(){
        if( rl_sending != null){
            rl_sending.setVisibility(View.GONE);
        }

        if(rl_send_ok != null){
            rl_send_ok.setVisibility(View.VISIBLE);
        }

        Random rand = new Random();
        int temp = rand.nextInt(5000) + 5000;
        if(send_num != null){
            send_num.setText("发布成功！已推荐给 " + temp +"位同行营销圈同行");
        }

        new Handler().postDelayed(() -> {
            hideState();
        },2000);


        //发送事件去更新
        EventBus.getDefault().post(new SendOkCircleEvent());

        removeTempMsg();
        cleanData();
}




    @BindView(R.id.ll_circle_send)
    public LinearLayout ll_circle_send;

    @BindView(R.id.rl_sending)
    public RelativeLayout rl_sending;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    @BindView(R.id.rl_send_ok)
    public RelativeLayout rl_send_ok;

    @BindView(R.id.rl_send_fail)
    public RelativeLayout rl_send_fail;

    @BindView(R.id.send_num)
    public TextView send_num;

    @BindView(R.id.icon_send_cancel)
    public ImageView icon_send_cancel;

    @BindView(R.id.toReSend)
    public TextView toReSend;




    @SuppressLint("CheckResult")
    private void initSendEvent() {

        //发送取消
        RxView.clicks(icon_send_cancel)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    hideState();
                    HomeActivityV2.mService.cancelRequest();

                });

        //重新发送
        RxView.clicks(toReSend)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                   hideState();
                   HomeActivityV2.mService.sendRequest();
                });

    }

    Intent sendService;
    private TempMsgBean mTempMsgBean;


    public void toSendBlog(TempMsgBean tempMsgBean){
        mTempMsgBean = tempMsgBean;

        toRetrunBack();

        //启动服务
        mIntent = new Intent(this,SendService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mTempMsgBean",mTempMsgBean);
        mIntent.putExtras(bundle);
        startService(mIntent);

    }

    private static SendService msgService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("tag", "Service与Activity已连接");
            msgService = ((SendService.MsgBinder) service).getService();

            msgService.setTempMsgBean(mTempMsgBean);


            msgService.changeData();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //这个方法是让界面退出时，重新获取的界面时 有所需要的数据
    public void toRetrunBack(){

    }


    /** --------------------------------- 发布帖子中  ---------------------------------v*/
    //文件路径
    private String data;
    private String key;
    private boolean isCancelled;
    //七牛上传图片完成计数
    private int uploadTaskCount;
    //图片选择器临时返回数据
    private List<MediaFile> mediaFiles = new ArrayList<>();
    private List<MediaFile> pathList = new ArrayList<>();
    //动态配图，多图链接之间用英文逗号隔开
    private StringBuilder picbycomma = new StringBuilder();
    //界面传递过来的
    private String resultPic = "";
    //构建七牛
    private StringBuilder qiniuPic = new StringBuilder();
    private String lashPic = "";
    String blog = "";
    String blog_images = "";
    String blog_link = "";
    String blog_link_title = "";
    //0原创 1转发
    int blog_type = 0;
    //被转发动态ID，原创为0
    int blog_pid = 0;
    //转发时是否同时评论动态，1是 0否
    int blog_is_comment = 0;
    //文章Id
    int article_id;
    //文字标题
    String article_title = "";
    //文字图片
    String article_image= "";
    //话题id
    String topic_id = "";

    private void changeData() {

        mediaFiles = mTempMsgBean.getImgPath();
        pathList = mTempMsgBean.getImgPath2();
        blog_link  = mTempMsgBean.getLinkurl();
        blog_link_title =  mTempMsgBean.getLinkTitle();

        topic_id = mTempMsgBean.getTopicId();
        //TODO 12.21发现一张图片多次提交 需重新赋值
        picbycomma = new StringBuilder();
        qiniuPic = new StringBuilder();

        if(mediaFiles != null && !mediaFiles.isEmpty()){
            for (int i = 0; i < mediaFiles.size(); i++) {
                picbycomma.append(mediaFiles.get(i).getPath()).append(",");
            }
        }

        if(pathList != null && !pathList.isEmpty()){
            for (int i = 0; i < pathList.size(); i++) {
                picbycomma.append(pathList.get(i).getPath()).append(",");
            }
        }

        //有图则 赋值 resultPic
        if(!TextUtils.isEmpty(picbycomma.toString())){
            KLog.d("tag","picbycomma 值 是：" + picbycomma.toString());
            resultPic =  picbycomma.substring(0,picbycomma.length()  - 1);
            KLog.d("tag","去掉最后一个,显示的值：" + resultPic);
        }else{
            resultPic = "";
        }


        //内容
        blog  = mTempMsgBean.getContent();


        //① 如果没有图片，显示属性动画
        KLog.d("tag","resultPic " + resultPic);
        if(TextUtils.isEmpty(resultPic)){
            isTextALl = true;
            toRetrunBack();
            createBlog();
        }else{
            isTextALl = false;
            mExecutorService = Executors.newFixedThreadPool(2);
            getUploadToken();
        }
    }


    String qiniuToken;
    private void getUploadToken() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getUploadToken(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<HttpResponse<QINiuTokenBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<QINiuTokenBean> response) {
                        qiniuToken = response.getReturn_data().getToken();
                        if(!TextUtils.isEmpty(qiniuToken)){
                            if(!TextUtils.isEmpty(resultPic)){
                                uploadPicToQiNiu();
                            }
                        }
                    }
                });
    }


    UploadManager uploadManager;
    LinkedList<String> linkedList;
    //返回图片所有的集合
    List<String>  tempList;
    private int tempProgress;
    private ExecutorService mExecutorService;

    private void uploadPicToQiNiu() {
        com.qiniu.android.storage.Configuration config = new com.qiniu.android.storage.Configuration.Builder()
                // 分片上传时，每片的大小。 默认256K
                .chunkSize(512 * 1024)
                // 启用分片上传阀值。默认512K
                .putThreshhold(1024 * 1024)
                // 链接超时。默认10秒
                .connectTimeout(10)
                // 是否使用https上传域名
                .useHttps(true)
                // 服务器响应超时。默认60秒
                .responseTimeout(60)
                .build();

        // 重用uploadManager一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);
        //data = <File对象、或 文件路径、或 字节数组>
        //String key = <指定七牛服务上的文件名，或 null>;
        //String token = <从服务端获取>;
        key = "niaogebiji";
        qiniuToken = qiniuToken.replace("\\s","");
        qiniuToken = qiniuToken.replace("\n","");

        tempList = new ArrayList<>();
        tempList.clear();

        if(null != mediaFiles && !mediaFiles.isEmpty()){
            for (MediaFile fileBean  : mediaFiles) {
                tempList.add(fileBean.getPath());
            }
        }

        if(null != pathList && !pathList.isEmpty()){
            for (MediaFile fileBean  : pathList) {
                tempList.add(fileBean.getPath());
            }
        }

        linkedList = new LinkedList<>();
        linkedList.clear();
        linkedList.addAll(tempList);

        progressBar.setMax(tempList.size() * 100);
        maxSendProgress = tempList.size() * 100;
        tempProgress = 0;
        currentSendProgress = 0;
        uploadPicToQiNiuByOnePic();
        toRetrunBack();
    }

    private void uploadPicToQiNiuByOnePic() {

        //查询并移除第一个元素；
        if(linkedList.size() > 0){
            data = linkedList.poll();

            mExecutorService.submit(() -> {
                KLog.d("tag","本地存储的路径是 "  + data);
                uploadManager.put(data, System.currentTimeMillis() + key , qiniuToken, new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if(info.isOK()) {
                            KLog.i("tag", key + " Upload Success");
                            //恢复默认值
                            data = "";


                        } else {
                            KLog.i("tag", key + " Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                    }
                },new UploadOptions(null,"image/jpeg",true,upProgressHandler,upCancellationSignal));
            });
        }
    }


    UpProgressHandler upProgressHandler = new UpProgressHandler() {
        /**
         * @param key 上传时的upKey；
         * @param percent 上传进度；
         */
        @Override
        public void progress(String key, double percent) {
//            KLog.e("tag","percent11111 " + percent);
            Log.e("1", "run:--------->当前类名: "+ getClass().getSimpleName());
            if(progressBar == null){
                progressBar = findViewById(R.id.progressBar);
            }

            KLog.e("tag","我疯了" + tempProgress);
            KLog.e("tag","percent " + percent);
            progressBar.setMax(maxSendProgress);
            progressBar.setProgress(tempProgress + (int) (percent * 100));
            currentSendProgress = tempProgress + (int) (percent * 100);


            if(percent == 1.0){
                Message message = Message.obtain();
                message.what = QI_NIU_UPLOAD_OK;
                handler.sendMessage(message);
                //上传成功一个，就添加到qiniuPic中去
                qiniuPic.append(key).append(",");
                tempProgress += 100;
                KLog.e("tag", "tempProgress " + tempProgress);
            }

        }
    };




    UpCancellationSignal upCancellationSignal = new UpCancellationSignal() {
        @Override
        public boolean isCancelled() {
            return isCancelled;
        }
    };

    private static final int QI_NIU_UPLOAD_OK = 120;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //七牛上传图片完成计数
                case QI_NIU_UPLOAD_OK:
                    uploadTaskCount++;
                    KLog.e("tag", "上传的个数 " + uploadTaskCount + "");
                    //容器中图片全部上传完成
                    if (uploadTaskCount == tempList.size()) {

                        if(ll_circle_send != null){
                            ll_circle_send.setVisibility(View.GONE);
                        }


                        if(!TextUtils.isEmpty(qiniuPic.toString())){
                            lashPic =  qiniuPic.substring(0,qiniuPic.length()  - 1);
                            KLog.d("tag","构建七牛的图片路径是：" + lashPic);
                        }

                        createBlog();
                        return;
                    }


                    //类似手动点击
                    uploadPicToQiNiuByOnePic();

                    break;
                default:
            }
        }
    };


    private void createBlog(){
        Map<String,String> map = new HashMap<>();
        map.put("blog",blog + "");
        map.put("images",lashPic + "");
        map.put("link",blog_link + "");
        map.put("link_title",blog_link_title + "");
        map.put("type",blog_type + "");
        map.put("pid",blog_pid + "");
        map.put("is_comment",blog_is_comment + "");
        map.put("article_id", article_id + "");
        map.put("article_title", article_title + "");
        map.put("article_image",article_image + "");
        map.put("topic_id",topic_id + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.e("tag","response " + response.getReturn_code());

                        //针对有图片特殊处理
                        if(!TextUtils.isEmpty(resultPic)){
                            if(ll_circle_send != null){
                                ll_circle_send.setVisibility(View.VISIBLE);
                            }

                            if(rl_send_ok != null){
                                rl_send_ok.setVisibility(View.VISIBLE);
                            }


                            Random rand = new Random();
                            int temp = rand.nextInt(5000) + 5000;
                            if(send_num != null){
                                send_num.setText("发布成功！已推荐给 " + temp +"位同行营销圈同行");
                            }

                            new Handler().postDelayed(() -> {
                                hideState();
                            },2000);
                            //发送事件去更新
                            EventBus.getDefault().post(new SendOkCircleEvent());

                            sendStatus =  BaseActivity.isSendOk;

                            //重新恢复状态
                            isAnimPause = false;

                        }else{
                            //① 成功了显示成功进度条
                            setAnimation(progressBar);

                        }


                        removeTempMsg();
                        cleanData();
                    }


                    @Override
                    public void onNetFail(String msg) {

                        if(rl_send_fail != null){
                            rl_send_fail.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public  void removeTempMsg() {
        mTempMsgBean = null;
        SPUtils.getInstance().remove(Constant.TMEP_MSG_INFO);
    }

    private void cleanData() {
        maxSendProgress = 0;
        currentSendProgress = 0;
        uploadTaskCount = 0;
        data = "";
        key = "";
        resultPic = "";
        lashPic = "";
        blog_link  = "";
        blog_link_title = "";
        blog_is_comment = 0;
        blog_type = 0;
    }

    private void hideState() {
        if(ll_circle_send != null){
            ll_circle_send.setVisibility(View.GONE);
            rl_sending.setVisibility(View.VISIBLE);
            rl_send_fail.setVisibility(View.GONE);
            rl_send_ok.setVisibility(View.GONE);
            progressBar.setProgress(0);
            progressBar.setMax(maxSendProgress);
        }

    }


    //是否是纯文本
    public static int  sendStatus;
    public static final int isSending  = 1;
    public static final int isSendOk  = 2;
    public static final int isSendFail  = 3  ;
    public static boolean isTextALl;
    public static boolean isAnimPause;
    public static int currentSendProgress;
    public static int maxSendProgress;
    ValueAnimator animator;




    private void setAnimation(ProgressBar view) {
        animator = ValueAnimator.ofInt(0, 100).setDuration(500);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            view.setProgress((Integer) valueAnimator.getAnimatedValue());
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                KLog.d("tag","取消了");
                isAnimPause = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画正常结束 动画对象存在 界面存储 文本存在
                if(!isAnimPause && animation != null && null != this){
                  KLog.d("tag","动画结束 改变布局");

                    showNumView();

                }

                //重新恢复状态
                isAnimPause = false;

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }
        });
        animator.start();
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
                    if(HomeActivityV2.mMyBinder != null){
                        HomeActivityV2.mMyBinder.pauseMusic();
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
                    HomeActivityV2.mMyBinder.playMusic();
                    HomeActivityV2.mMediaService.setOnProgressListener(progress -> {
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
                    if(HomeActivityV2.mMyBinder != null){
                        HomeActivityV2.mMyBinder.closeMedia();
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
                if(HomeActivityV2.mMyBinder != null){
                    HomeActivityV2.mMyBinder.seekToPositon(dest);
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
        HomeActivityV2.mMyBinder.pauseMusic();

        //有资源时 先关闭之前资源
        HomeActivityV2.mMyBinder.closeMedia();

        //有资源时 重新准备新资源
        HomeActivityV2.mMediaService.prepare(url);


        HomeActivityV2.mMediaService.setOnCloseListener(() -> {
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

        HomeActivityV2.mMediaService.setOnEndListener(() -> {

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
        HomeActivityV2.mMediaService.setOnStartListener(totalLength -> {
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
            HomeActivityV2.mMyBinder.playMusic();
            HomeActivityV2.mMediaService.setOnProgressListener(progress -> {
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

        unregisterReceiver(msgReceiver);


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
        Log.e("tag", "run:--------->当前呈现的类名是: "+ getClass().getSimpleName());


        if(sendStatus == isSending){
            ll_circle_send.setVisibility(View.VISIBLE);
            //显示当前的状态值
            if(progressBar != null){
                progressBar.setProgress(currentSendProgress);
                progressBar.setMax(maxSendProgress);
            }
        }


        //这个是在打开新的界面 同样展示进度的
//        if(sendStatus == isSending){
//            ll_circle_send.setVisibility(View.VISIBLE);
//
//            //动画没有结束 并且是文本
//            if(!isAnimPause && isTextALl){
//                //这里重新设置了，因为progressBar是个新的
//                progressBar.setMax(maxSendProgress);
//                setAnimation(progressBar);
//            }
//
//            if(!isTextALl){
//                //设置最大的进度
//                if(progressBar != null){
//                    KLog.d("最大的进度 " + maxSendProgress);
//                    progressBar.setProgress(currentSendProgress);
//                    progressBar.setMax(maxSendProgress);
//                }
//            }
//
//
//        }else{
//            ll_circle_send.setVisibility(View.GONE);
//        }

        //存在 + view显示
        if(HomeActivityV2.mMyBinder != null && isAudaioShow){
            part_audio.setVisibility(View.VISIBLE);

            if(getClass().getSimpleName().equals("HomeActivityV2")
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
            if( HomeActivityV2.mMyBinder.isPlaying()){
                HomeActivityV2.mMyBinder.playMusic();
                play.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }else{
                play.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }

            HomeActivityV2.mMediaService.setOnProgressListener(progress -> {
                if(seekbar != null){
                    runOnUiThread(() -> {
                        timeParse(time,progress);
                        seekbar.setProgress(progress);
                        currentProgress = progress;
                    });
                }

            });


            HomeActivityV2.mMediaService.setOnEndListener(() -> {
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



    //下方的方法从单独的activity 移动到这里
    public void showProfessionAuthenNo(){
        final ProfessionAutherDialog iosAlertDialog = new ProfessionAutherDialog(this).builder();
        iosAlertDialog.setTitle("完善信息后，被关注几率将提升100%");
        iosAlertDialog.setPositiveButton("让大佬注意你，立即完善", v -> {
            UIHelper.toUserInfoModifyActivity(this);
        }).setNegativeButton("下次再说", v -> {
            UIHelper.toHelloMakeActivity(this);
            overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
        }).setMsg("你还未完善信息！").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    public void showProfessionAuthen(){
        final ProfessionAutherDialog iosAlertDialog = new ProfessionAutherDialog(this).builder();
        iosAlertDialog.setPositiveButton("让大佬注意你，立即认证", v -> {
            //和外面的认证一样

            UIHelper.toWebViewWithLayoutOnlyActivity(this, StringUtil.getLink("certificatecenter"));

        }).setNegativeButton("下次再说", v -> {
            UIHelper.toHelloMakeActivity(this);
            overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
        }).setMsg("你还未职业认证！").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }





}
