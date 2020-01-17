package com.qmkj.niaogebiji.module.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.CircleMakeActivity;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.QINiuTokenBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.qmkj.niaogebiji.module.event.ShowRedPointEvent;
import com.qmkj.niaogebiji.module.widget.tab1.ViewPagerTitle;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.xzh.imagepicker.bean.MediaFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class CircleFragment extends BaseLazyFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.pager_title)
    ViewPagerTitle pager_title;

    @BindView(R.id.ll_circle_send)
    LinearLayout ll_circle_send;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.part11)
    RelativeLayout part11;

    @BindView(R.id.part22)
    RelativeLayout part22;

    @BindView(R.id.part33)
    RelativeLayout part33;


    @BindView(R.id.red_point)
    FrameLayout red_point;


    @BindView(R.id.search_first)
    TextView search_first;

    @BindView(R.id.send_num)
    TextView send_num;


    //Fragment 集合
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();
    //存储频道集合
    private List<ChannelBean> mChannelBeanList;
    //适配器
    private FirstFragmentAdapter mFirstFragmentAdapter;


    public static CircleFragment getInstance() {
        return new CircleFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle;
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {
        RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();;
        if(userInfo != null){
            if("1".equals(userInfo.getIs_red())){
                red_point.setVisibility(View.VISIBLE);
            }else if("0".equals(userInfo.getIs_red())){
                red_point.setVisibility(View.GONE);
            }
        }

        String [] titile = new String[]{"关注","推荐"};
        pager_title.initData(titile,mViewPager,1);
        mExecutorService = Executors.newFixedThreadPool(2);

        if(!TextUtils.isEmpty(Constant.firstSearchName)){
            search_first.setHint(Constant.firstSearchName);
        }
    }


    @Override
    public void initData() {
        mChannelBeanList = new ArrayList<>();
        ChannelBean bean ;
        bean = new ChannelBean("0","关注");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("1","推荐");
        mChannelBeanList.add(bean);

        if(null != mChannelBeanList){
            setUpAdater();
        }

    }


    private void setUpAdater() {
        mFragmentList.clear();
        mTitls.clear();
        for (int i = 0; i < mChannelBeanList.size(); i++) {
            if (i == 0) {
                CircleFocusFragment focusFragment = CircleFocusFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(focusFragment);
            } else if (i == 1) {
                CircleRecommendFragmentNew actionFragment = CircleRecommendFragmentNew.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(actionFragment);
            }
            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
        }

        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(getActivity(), getChildFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(1);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    MobclickAgentUtils.onEvent(UmengEvent.quanzi_followtab_2_0_0);
                }else if(position == 1){
                    MobclickAgentUtils.onEvent(UmengEvent.quanzi_recommendtab_2_0_0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }




    @OnClick({R.id.icon_send_msg,
            R.id.rl_newmsg,
        R.id.search_part,
        R.id.toReSend,
        R.id.icon_send_cancel,

    })
    public void clicks(View view){

        if(StringUtil.isFastClick()){
            return;
        }

        switch (view.getId()){
            case R.id.icon_send_cancel:
                showDeleteCircle();
                break;
            case R.id.toReSend:
                changeData();
                break;
            case R.id.search_part:

                MobclickAgentUtils.onEvent(UmengEvent.quanzi_searchbar_2_0_0);

                UIHelper.toSearchActivity(getActivity());
                break;
            case R.id.rl_newmsg:
                //TODO 测试布局移动

//                Animation translateAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.trans);
//                ll_circle_send.setAnimation(translateAnimation);
//                ll_circle_send.startAnimation(translateAnimation);

//                initAnim();
                MobclickAgentUtils.onEvent(UmengEvent.quanzi_message_2_0_0);
                UIHelper.toWebViewActivityWithOnLayout(getActivity(),StringUtil.getLink("messagecenter"),"显示一键已读消息");
                break;
            case R.id.icon_send_msg:

                MobclickAgentUtils.onEvent(UmengEvent.quanzi_publish_2_0_0);


                //① 用getActivity方法发起调用，只有父Activity的onActivityResult会调用，Fragment中的onActivityResult不会被调用
                Intent intent = new Intent(getActivity(), CircleMakeActivity.class);
                startActivityForResult(intent,100);

                //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
                getActivity().overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                break;

            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 200){
            Bundle bundle = data.getExtras();
            mTempMsgBean = (TempMsgBean) bundle.getSerializable("key");

            changeData();
        }
    }

    private ExecutorService mExecutorService;

    private void changeData() {

        mediaFiles = mTempMsgBean.getImgPath();
        pathList = mTempMsgBean.getImgPath2();
        blog_link  = mTempMsgBean.getLinkurl();
        blog_link_title =  mTempMsgBean.getLinkTitle();


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

        //判断网络是否链接
        if(NetworkUtils.isConnected()){
            //判断网络是否可用
            mExecutorService.submit(() -> {
                Message message = Message.obtain();
                if(NetworkUtils.isAvailable()){
                    message.what = CHECK_NET_OK;
                }else{
                    message.what = CHECK_NET_FALSE;
                }
                handler.sendMessage(message);
            });
        }else{
            showErrorState();
        }

    }

    /** --------------------------------- 发布帖子中  ---------------------------------v*/

   private TempMsgBean mTempMsgBean;
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
    private static final int QI_NIU_UPLOAD_OK = 120;
    private static final int CHECK_NET_OK = 110;
    private static final int CHECK_NET_FALSE= 111;

    private void showErrorState(){
        part11.setVisibility(View.GONE);
        part22.setVisibility(View.VISIBLE);
        part33.setVisibility(View.GONE);
    }

    String qiniuToken;
    private void getUploadToken() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getUploadToken(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<QINiuTokenBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<QINiuTokenBean> response) {
                        KLog.d("tag","response " + response.getReturn_data().getToken());
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
    private void uploadPicToQiNiu() {

        Configuration config = new Configuration.Builder()
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
        tempProgress = 0;
        uploadPicToQiNiuByOnePic();

    }

    private void uploadPicToQiNiuByOnePic() {

        //查询并移除第一个元素；
        if(linkedList.size() > 0){
            data = linkedList.poll();

            mExecutorService.submit(() -> {
                KLog.d("tag","data "  + data);
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


    private int tempProgress;
    UpProgressHandler upProgressHandler = new UpProgressHandler() {
    /**
     * @param key 上传时的upKey；
     * @param percent 上传进度；
     */
        @Override
        public void progress(String key, double percent) {
//            progressDialog.setProgress((int) (upLoadData.length * percent));
//            KLog.d("tag","key " + key + "  percent  " + percent);

            progressBar.setProgress(tempProgress + (int) (percent * 100));
            if(1.0 == percent){
                Message message = Message.obtain();
                message.what = QI_NIU_UPLOAD_OK;
                handler.sendMessage(message);
                //上传成功一个，就添加到qiniuPic中去
                qiniuPic.append(key).append(",");
                tempProgress += 100;
            }
        }
    };


    UpCancellationSignal upCancellationSignal = new UpCancellationSignal() {
        @Override
        public boolean isCancelled() {
            return isCancelled;
        }
    };


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //七牛上传图片完成计数
                case QI_NIU_UPLOAD_OK:
                    uploadTaskCount++;
                    KLog.e("tag", "上传的个数 " + uploadTaskCount + "");

//                    float per = (float) (uploadTaskCount * 1.0 / mediaFiles.size());
//                    int pro = (int) (per * 100);
//                    progressBar.setProgress(pro);
                    //容器中图片全部上传完成
                    if (uploadTaskCount == tempList.size()) {
                        part11.setVisibility(View.GONE);

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
                case CHECK_NET_OK:
                    //网络正常，显示进度
                    ll_circle_send.setVisibility(View.VISIBLE);
                    part11.setVisibility(View.VISIBLE);
                    //① 如果没有图片，显示属性动画
                    if(TextUtils.isEmpty(resultPic)){
                        setAnimation(progressBar);
                        createBlog();
                    }else{
                        getUploadToken();
                    }
                    break;
                case CHECK_NET_FALSE:
                    ToastUtils.showShort("网络不可用");
                    showErrorState();
                    break;
                default:
            }
        }
    };

    boolean isAnimPause;
    ValueAnimator animator;

    private void setAnimation(ProgressBar view) {
        animator = ValueAnimator.ofInt(0, 100).setDuration(1000);
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
                    KLog.d("tag","动画结束");
                    part11.setVisibility(View.GONE);
                    part22.setVisibility(View.GONE);
                    part33.setVisibility(View.VISIBLE);
                    Random rand = new Random();
                    int temp = rand.nextInt(5000) + 5000;
                    send_num.setText("发布成功！已推荐给 " + temp +"位同行营销圈同行");
                    //发送事件去更新
                    EventBus.getDefault().post(new SendOkCircleEvent());
                    removeTempMsg();
                    cleanData();
                    new Handler().postDelayed(() -> {
                        hideState();
                    },2000);
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




    // 点击取消按钮，让UpCancellationSignal##isCancelled()方法返回true，以停止上传
    private void cancell() {
        isCancelled = true;
    }


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
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.d("tag","response " + response.getReturn_code());

                        //针对有图片特殊处理
                        if(!TextUtils.isEmpty(resultPic)){
                            ll_circle_send.setVisibility(View.VISIBLE);
                            part33.setVisibility(View.VISIBLE);
                            Random rand = new Random();
                            int temp = rand.nextInt(5000) + 5000;
                            send_num.setText("发布成功！已推荐给 " + temp +"位同行营销圈同行");
                             new Handler().postDelayed(() -> {
                                hideState();
                            },2000);
                            //发送事件去更新
                            EventBus.getDefault().post(new SendOkCircleEvent());
                        }


                        removeTempMsg();
                        cleanData();


                    }
                });
    }

    private void cleanData() {
        uploadTaskCount = 0;
        data = "";
        key = "";
        resultPic = "";
        lashPic = "";
        blog_link  = "";
        blog_link_title = "";
    }


    public  void removeTempMsg() {
        mTempMsgBean = null;
        SPUtils.getInstance().remove(Constant.TMEP_MSG_INFO);
    }

    private void hideState() {
        ll_circle_send.setVisibility(View.GONE);
        part11.setVisibility(View.GONE);
        part22.setVisibility(View.GONE);
        part33.setVisibility(View.GONE);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        uploadTaskCount = 0;
    }

    //消失动画
    private void initAnim() {
        KLog.d("tag","----以当前控件为原点，向下为正方向---");
        ObjectAnimator translationY = ObjectAnimator.ofFloat(ll_circle_send, "translationY", 0f, -SizeUtils.dp2px(60f));
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ll_circle_send, "alpha", 1f, 0);
        ObjectAnimator translationY1 = ObjectAnimator.ofFloat(mViewPager, "translationY", 0f, -SizeUtils.dp2px(60f));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationY,translationY1,alpha);
        animatorSet.setDuration(1000);
        animatorSet.start();
        //动画的监听
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                KLog.d("tag","动画开始");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                KLog.d("tag","动画结束");
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                KLog.d("tag","动画取消");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                KLog.d("tag","动画重复");
            }
        });

    }


    public void showDeleteCircle(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(getActivity()).builder();
        iosAlertDialog.setPositiveButton("取消", v -> {
            hideState();
        }).setNegativeButton("再想想", v -> {}).setMsg("取消发布动态").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowRedPointEvent(ShowRedPointEvent event){
        if("1".equals( event.getIs_red())){
            red_point.setVisibility(View.VISIBLE);
        }else if("0".equals(event.getIs_red())){
            red_point.setVisibility(View.GONE);
        }
    }


}
