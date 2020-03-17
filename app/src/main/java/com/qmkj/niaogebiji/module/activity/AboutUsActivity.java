package com.qmkj.niaogebiji.module.activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.AppUpdateUtilNew;
import com.qmkj.niaogebiji.common.utils.ChannelUtil;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.InitDataBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.VersionBean;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:
 */
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.wx_name)
    TextView wx_name;


    @BindView(R.id.hezuo_name)
    TextView hezuo_name;


    @BindView(R.id.version_code)
    TextView version_code;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_aboutus;
    }


    private int mSecretNumber = 0;
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime;

    RegisterLoginBean.UserInfo mRegisterLoginBean;

    @Override
    protected void initView() {
        tv_title.setText("关于我们");

        mRegisterLoginBean =  StringUtil.getUserInfoBean();

        version_code.setText(AppUtils.getAppVersionName() + "");
        tv_title.setOnClickListener(view -> {

        });
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getInitData();


        findViewById(R.id.icon).setOnClickListener(v -> {
            long currentClickTime = SystemClock.uptimeMillis();
            long elapsedTime = currentClickTime - mLastClickTime;
            mLastClickTime = currentClickTime;

            if (elapsedTime < MIN_CLICK_INTERVAL) {
                ++mSecretNumber;
                if (9 == mSecretNumber) {
                    try {
                        // to do 在这处理你想做的事件
                        String id = JPushInterface.getRegistrationID(this);
                        //注册成功后，可能有的时候不会显示id -- 在此处
                        KLog.d("tag","极光推送的id " + id + "");
                        //同时复制到剪贴板上
                        StringUtil.copyLink(id);
                    } catch (Exception e) {
                        Log.i("tag", e.toString());
                    }
                }
            } else {
                mSecretNumber = 0;
                String channel = ChannelUtil.getChannel(AboutUsActivity.this);
                if(!TextUtils.isEmpty(channel)){
                    ToastUtils.showShort(channel + "\n" + (BuildConfig.DEBUG ? "测试服":"正式服"));
                }
            }

        });

    }

    InitDataBean  initDataBean;
    private void getInitData() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getInitData(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<InitDataBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<InitDataBean> response) {

                        initDataBean = response.getReturn_data();
                        if(null != initDataBean){
                            wx_name.setText(initDataBean.getWechat_service_id());
                            hezuo_name.setText(initDataBean.getWechat_business_target_id());
                        }
                    }
                });
    }

    @OnClick({R.id.iv_back,R.id.rl_weixin,
            R.id.rl_version_code,
            R.id.rl_hezuo})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.rl_hezuo:
                copy(initDataBean.getWechat_business_target_id());
                MobclickAgentUtils.onEvent(UmengEvent.i_about_coop_2_0_0);

                ToastUtils.showShort("已复制到剪贴板！请打开微信粘贴搜索   并直接打开微信");

                getWechatApi();
                break;
            case R.id.rl_weixin:
                MobclickAgentUtils.onEvent(UmengEvent.i_about_wx_2_0_0);

                copy(initDataBean.getWechat_service_id());
                getWechatApi();

                ToastUtils.showShort("已复制到剪贴板！请打开微信粘贴搜索   并直接打开微信");
                break;
            case R.id.rl_version_code:
                checkupd();
                MobclickAgentUtils.onEvent(UmengEvent.i_about_ver_2_0_0);



//                copy(mRegisterLoginBean.getAccess_token()); 给测试用的
//
//                ToastUtils.showShort("已复制到剪贴板");
                break;
            case R.id.iv_back:
                finish();
                break;

            default:
        }
    }

    /** --------------------------------- 版本更新  ---------------------------------*/

    //是否强更：1-是,0-提示更
    private VersionBean mVersionBean;

    private String serverVersionCode;

    private String apkUrl;

    private String note;

    private String forceUpdat;

    private void checkupd() {

        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().checkupd(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<VersionBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<VersionBean> response) {
                        mVersionBean = response.getReturn_data();
                        KLog.d("tag",response.getReturn_data());
                        if(null != mVersionBean){
                            if(null != mVersionBean.getList() && !mVersionBean.getList().isEmpty() && null != mVersionBean.getList().get(0)){
                                serverVersionCode = mVersionBean.getList().get(0).getVersion_code();
                                apkUrl = mVersionBean.getList().get(0).getUrl();
                                note = mVersionBean.getList().get(0).getUpdate_desc();
                                forceUpdat = mVersionBean.getList().get(0).getForce_update_flag();
                                //当前的版本小于 后台返回的版本提示更新
                                if(!TextUtils.isEmpty(serverVersionCode)){
                                    if(AppUtils.getAppVersionCode() < Integer.parseInt(serverVersionCode)){
                                        showUpdateDialog();
                                    }else{
                                        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                                        ToastUtils.showShort("已是最新版本");
                                    }
                                }
                            }
                        }
                    }
                });
    }



    private void showUpdateDialog() {
        AppUpdateUtilNew updateUtil = new AppUpdateUtilNew(AboutUsActivity.this,apkUrl);
        updateUtil.showUpdateDialog(note,false);

    }



    private void copy(String content){
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
        ToastUtils.showShort("已复制到剪贴板！请打开微信粘贴搜索");
    }


    // 跳转到微信
    private void getWechatApi(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.showShort("检查到您手机没有安装微信，请安装后使用该功能");
        }
    }





}
