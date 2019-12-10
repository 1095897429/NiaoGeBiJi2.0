package com.qmkj.niaogebiji.module.activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import com.qmkj.niaogebiji.module.bean.VersionBean;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_aboutus;
    }

    @Override
    protected void initView() {
        tv_title.setText("关于我们");
        tv_title.setOnClickListener(view -> {

        });
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @OnClick({R.id.iv_back,R.id.icon,R.id.rl_weixin,
            R.id.rl_version_code,
            R.id.rl_hezuo})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.rl_hezuo:
                copy("niaogibiji");
                getWechatApi();
                break;
            case R.id.rl_weixin:
                copy("shjf");
                getWechatApi();
                break;
            case R.id.rl_version_code:
//                checkupd();
                showUpdateDialog();
                break;
            case R.id.icon:
                String channel = ChannelUtil.getChannel(AboutUsActivity.this);
                if(!TextUtils.isEmpty(channel)){
                    ToastUtils.showShort(channel + "\n" + (BuildConfig.DEBUG ? "测试服":"正式服"));
                }
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
