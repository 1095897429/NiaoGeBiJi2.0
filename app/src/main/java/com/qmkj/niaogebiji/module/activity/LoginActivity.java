package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.SecretAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.event.LoginErrEvent;
import com.qmkj.niaogebiji.module.event.LoginGoodEvent;
import com.socks.library.KLog;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.udesk.UdeskSDKManager;
import cn.udesk.config.UdeskConfig;
import udesk.core.UdeskConst;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:
 * 1.手机验证码 登录 -- loginViaCode
 * 2.微信登录 -- wechatlogin
 *      1.新用户 -- 在验证码输入完成后 -- WechatBindAccountViaCode
 *      2.老用户 -- 直接登录了
 * 3.获取验证码 -- sendverifycode
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.checkbox)
    CheckBox mCheckBox;

    @BindView(R.id.user_text)
    TextView user_text;

    @BindView(R.id.phoneLogin)
    LinearLayout phoneLogin;


    //登录方式
    private String loginType;

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }


    @Override
    public void initData() {
        // click logo button 10 times continuously to open com.android.launcher3
        findViewById(R.id.part1111).setOnClickListener(v -> {
            long currentClickTime = SystemClock.uptimeMillis();
            long elapsedTime = currentClickTime - mLastClickTime;
            mLastClickTime = currentClickTime;

            if (elapsedTime < MIN_CLICK_INTERVAL) {
                ++mSecretNumber;
                if (9 == mSecretNumber) {
                    try {
                        // to do 在这处理你想做的事件
                        phoneLogin.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Log.i("tag", e.toString());
                    }
                }
            } else {
                mSecretNumber = 0;
            }

        });

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {

        initEvent();

        RxView.clicks(phoneLogin)
                //每1秒中只处理第一个元素
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    if (!mCheckBox.isChecked()) {
                        Toast.makeText(this, "请先同意用户协议与隐私政策", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    loginType = "phone";

                    boolean isAgree = SPUtils.getInstance().getBoolean("isAgree");
                    if (!isAgree) {
                        showSecretDialog(this);
                    } else {
                        MobclickAgentUtils.onEvent(UmengEvent.wxlogin_phone_2_0_0);
                        UIHelper.toPhoneInputActivity(LoginActivity.this, "", loginType);
                    }

                });
    }

    private void initEvent() {
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.login_text));
        ForegroundColorSpan fCs1 = new ForegroundColorSpan(Color.parseColor("#82A9C1"));
        ForegroundColorSpan fCs2 = new ForegroundColorSpan(Color.parseColor("#82A9C1"));

        spannableString.setSpan(fCs1, 9, 19, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(fCs2, 20, 30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


        ClickableSpan user_ll = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ((TextView) view).setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));
                UIHelper.toUserAgreeActivity(mContext);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan secret_ll = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ((TextView) view).setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));
                UIHelper.toSecretActivity(mContext);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //设置颜色
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(user_ll, 9, 19, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(secret_ll, 20, 30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        //必须设置才能响应点击事件
        user_text.setMovementMethod(LinkMovementMethod.getInstance());
        user_text.setText(spannableString);

    }


    @OnClick({R.id.weixinLogin, R.id.iv_back})
    public void login(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.weixinLogin:

                if(StringUtil.isFastClick()){
                    return;
                }

                if (isWxAppInstalledAndSupported(this)) {
                    loginType = "weixin";
                    if (!mCheckBox.isChecked()) {
                        ToastUtils.setGravity(Gravity.BOTTOM, 0, SizeUtils.dp2px(40));
                        ToastUtils.showShort("请先同意用户协议与隐私政策");
                        return;
                    }

                    boolean isAgree = SPUtils.getInstance().getBoolean("isAgree");
                    if (!isAgree) {
                        showSecretDialog(this);
                    } else {
                        MobclickAgentUtils.onEvent(UmengEvent.wxlogin_wx_2_0_0);
                        weChatAuth();
                    }

                } else {
                    ToastUtils.setGravity(Gravity.BOTTOM, 0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("您还未安装微信");
                }



                break;
            default:
        }
    }


    /**
     * --------------------------------- 微信原生登录  ---------------------------------
     */
    private IWXAPI api;

    private void weChatAuth() {
        if (api == null) {
            api = WXAPIFactory.createWXAPI(LoginActivity.this, Constant.WXAPPKEY, true);
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wx_login_duzun";
        api.sendReq(req);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toLoginErrEvent(LoginErrEvent event) {
        showFobbidUserDialog();
    }


    public void showFobbidUserDialog() {
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("联系客服", v -> {
            toUDesk();
        }).setNegativeButton("取消", v -> {
        }).setMsg("你的账户已被封禁\n" +
                "请联系客服处理").setBold().setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    /**
     * --------------------------------- 联系客服  ---------------------------------
     */
    //没有登录的时候，userinfo不传，登录，就传
    private void toUDesk() {
        UdeskConfig.Builder builder = new UdeskConfig.Builder();
        //token为随机获取的，如 UUID.randomUUID().toString()
        String sdktoken = UUID.randomUUID().toString();
        KLog.d("tag", sdktoken + "");
        Map<String, String> info = new HashMap<>();
        info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN, sdktoken);
        info.put(UdeskConst.UdeskUserInfo.DESCRIPTION, "描述信息");
        builder.setUsephoto(true);
        builder.setUseEmotion(true);
        builder.setUseMore(true);
        builder.setUserForm(true);
        builder.setUserSDkPush(true);
        builder.setFormCallBack(context -> KLog.d("tag", "jkkkk"));
        builder.setDefualtUserInfo(info);
        UdeskSDKManager.getInstance().entryChat(BaseApp.getApplication(), builder.build(), sdktoken);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginGoodEvent(LoginGoodEvent event) {
        if (this != null) {
            this.finish();
        }
    }


    /**
     * 判断手机是否安装微信
     * 判断微信客户端是否安装及安装的版本是否支持微信开放平台(https://blog.csdn.net/aa1165768113/article/details/82109089?utm_source=distribute.pc_relevant.none-task)
     * @param context 上下文
     * @return
     */
    public static boolean isWxAppInstalledAndSupported(Context context) {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(context, Constant.WXAPPKEY);

        boolean bIsWXAppInstalledAndSupported = wxApi.isWXAppInstalled();
        if (!bIsWXAppInstalledAndSupported) {
            final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if (pn.equals("com.tencent.mm")) {
                        return true;
                    }
                }
            }
            return false;
        }

        return true;
    }


    //判断是否安装了微信 -- 单一方法并不能适配所有机型 （https://www.jianshu.com/p/8589e9d82a65）
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }


    private void showSecretDialog(Context ctx) {
        final SecretAlertDialog iosAlertDialog = new SecretAlertDialog(ctx).builder();
        iosAlertDialog.setMsg(ctx.getResources().getString(R.string.secret_hint))
                .setPositiveButton("同意", v -> {
                    SPUtils.getInstance().put("isAgree", true);
                    KLog.d("tag", "同意");
                    MobclickAgentUtils.onEvent(UmengEvent.agreement_agree_2_0_0);
                    if("phone".equals(loginType)){
                        MobclickAgentUtils.onEvent(UmengEvent.wxlogin_phone_2_0_0);
                        UIHelper.toPhoneInputActivity(LoginActivity.this, "", loginType);
                    }else if("weixin".equals(loginType)){
                        MobclickAgentUtils.onEvent(UmengEvent.wxlogin_wx_2_0_0);
                        weChatAuth();
                    }


                })
                .setNegativeButton("不同意", v -> {
                    MobclickAgentUtils.onEvent(UmengEvent.agreement_disagree_2_0_0);
                    KLog.d("tag","弹框内部做了二次弹框的操作");
                }).setCanceledOnTouchOutside(false);
        iosAlertDialog.setCancelable(false);
        iosAlertDialog.show();

    }




    private int mSecretNumber = 0;
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime;

    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity

        KLog.d("tag","按下了back键   onBackPressed()");
    }


}



