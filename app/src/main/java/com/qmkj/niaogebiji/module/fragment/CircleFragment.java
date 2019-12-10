package com.qmkj.niaogebiji.module.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.CircleMakeActivity;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.QINiuTokenBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.event.SendingCircleEvent;
import com.qmkj.niaogebiji.module.widget.tab1.ViewPagerTitle;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.xzh.imagepicker.bean.MediaFile;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        String [] titile = new String[]{"关注","推荐"};

        pager_title.initData(titile,mViewPager,1);
        mExecutorService = Executors.newFixedThreadPool(2);
    }


    //点击切换fragement会调用
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause

        }else{
            //resume
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
            if(i == 0){
                CircleFocusFragment focusFragment = CircleFocusFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(focusFragment);
            }else if(i == 1){
                CircleRecommendFragment actionFragment = CircleRecommendFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(actionFragment);
            }
            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
        }

        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(getActivity(),getChildFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(1);

        initEvent();
    }

    private void initEvent() {
        //设置事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                KLog.d(TAG, "选中的位置 ：" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    @OnClick({R.id.icon_send_msg,R.id.rl_newmsg,
        R.id.search_part,
        R.id.toReSend
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.toReSend:
                changeData();
                break;
            case R.id.search_part:
                UIHelper.toSearchActivity(getActivity());
                break;
            case R.id.rl_newmsg:
                KLog.d("tag","去消息界面");

                break;
            case R.id.icon_send_msg:
                if(StringUtil.isFastClick()){
                    return;
                }

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
        part11.setVisibility(View.VISIBLE);
        part22.setVisibility(View.GONE);
        part33.setVisibility(View.GONE);
        ll_circle_send.setVisibility(View.VISIBLE);
        mediaFiles = mTempMsgBean.getImgPath();


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
    private  List<MediaFile> mediaFiles = new ArrayList<>();
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
                            if(null != mediaFiles && !mediaFiles.isEmpty()){
                                uploadPicToQiNiu();
                            }else{
                                createBlog();
                            }
                        }
                    }
                });
    }


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
        UploadManager uploadManager = new UploadManager(config);
        //data = <File对象、或 文件路径、或 字节数组>
        //String key = <指定七牛服务上的文件名，或 null>;
        //String token = <从服务端获取>;
        key = null;
        qiniuToken = qiniuToken.replace("\\s","");
        qiniuToken = qiniuToken.replace("\n","");

        for (int i = 0; i < mediaFiles.size(); i++) {
            data = mediaFiles.get(i).getPath();
            uploadManager.put(data, key, qiniuToken,
                    (key, info, res) -> {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if(info.isOK()) {
                            KLog.i("qiniu", "Upload Success");
                        } else {
                            KLog.i("qiniu", "Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        KLog.i("qiniu", key + ",  " + info + ", " + res);
                    }, new UploadOptions(null, null, false, (key, percent) -> {
                        //上传进度
                        KLog.i("qiniu", key + ": " + percent);
                        if(1.0 == percent){
                            Message message = Message.obtain();
                            message.what = QI_NIU_UPLOAD_OK;
                            handler.sendMessage(message);
                        }

                    }, new UpCancellationSignal() {
                        @Override
                        public boolean isCancelled() {
                            return isCancelled;
                        }
                    }));
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //七牛上传图片完成计数
                case QI_NIU_UPLOAD_OK:
                    uploadTaskCount++;
                    KLog.e("uploadTaskCount", uploadTaskCount + "");
                    float per = (float) (uploadTaskCount * 1.0 / mediaFiles.size());
                    int pro = (int) (per * 100);
                    progressBar.setProgress(pro);
                    //容器中图片全部上传完成
                    if (uploadTaskCount == mediaFiles.size()) {
                        part11.setVisibility(View.GONE);
                        createBlog();
                    }
                    break;
                case CHECK_NET_OK:
                    getUploadToken();
                    break;
                case CHECK_NET_FALSE:
                    ToastUtils.showShort("网络不可用");
                    showErrorState();
                    break;
                default:
            }
        }
    };



    // 点击取消按钮，让UpCancellationSignal##isCancelled()方法返回true，以停止上传
    private void cancell() {
        isCancelled = true;
    }



    String blog = "动态内容";
    String blog_images = "";
    String blog_link = "";
    String blog_link_title = "";
    //0原创 1转发
    int blog_type = 0;
    //被转发动态ID，原创为0
    int blog_pid = 0;
    //转发时是否同时评论动态，1是 0否
    int blog_is_comment = 0;

    private void createBlog(){
        Map<String,String> map = new HashMap<>();
        map.put("blog",blog + "");
        map.put("images",blog_images + "");
        map.put("link",blog_link + "");
        map.put("link_title",blog_link_title + "");
        map.put("type",blog_type + "");
        map.put("pid",blog_pid + "");
        map.put("is_comment",blog_is_comment + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        part33.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> {
//                            initAnim();
                             hideState();
                        },2000);
                    }
                });
    }

    private void hideState() {
        ll_circle_send.setVisibility(View.GONE);
        part11.setVisibility(View.VISIBLE);
        part22.setVisibility(View.GONE);
        part33.setVisibility(View.GONE);
        progressBar.setProgress(0);
        uploadTaskCount = 0;
    }

    //消失动画
    private void initAnim() {
        KLog.d("tag","----以当前控件为原点，向下为正方向---");
        ObjectAnimator translationY = ObjectAnimator.ofFloat(ll_circle_send, "translationY", 0f, -SizeUtils.dp2px(60f));
        ObjectAnimator translationY1 = ObjectAnimator.ofFloat(mViewPager, "translationY", 0f, -SizeUtils.dp2px(60f));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationY,translationY1);
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


}
