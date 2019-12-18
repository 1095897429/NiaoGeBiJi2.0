package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.socks.library.KLog;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

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
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {

        KLog.d("tag",mCheckBox.isChecked() + "");
        initEvent();

        RxView.clicks(phoneLogin)
                            //每1秒中只处理第一个元素
                            .throttleFirst(500, TimeUnit.MILLISECONDS)
                            .subscribe(object -> {
                                if(!mCheckBox.isChecked()){
                                    Toast.makeText(this,"请先同意用户协议与隐私政策",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                loginType = "phone";

                                UIHelper.toPhoneInputActivity(LoginActivity.this,"",loginType);
                            });
    }

    private void initEvent() {
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.login_text));
        ForegroundColorSpan fCs1 = new ForegroundColorSpan(Color.parseColor("#82A9C1"));
        ForegroundColorSpan fCs2 = new ForegroundColorSpan(Color.parseColor("#82A9C1"));

        spannableString.setSpan(fCs1,9,19, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(fCs2,20,30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);



        ClickableSpan user_ll = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ((TextView)view).setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));
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
                ((TextView)view).setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));
                UIHelper.toSecretActivity(mContext);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                //设置颜色
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(user_ll,9,19, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(secret_ll,20,30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        //必须设置才能响应点击事件
        user_text.setMovementMethod(LinkMovementMethod.getInstance());
        user_text.setText(spannableString);

    }


    @OnClick({R.id.weixinLogin,R.id.iv_back})
    public void login(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.weixinLogin:
                loginType  = "weixin";
                if(!mCheckBox.isChecked()){
                    Toast.makeText(this,"请先同意用户协议与隐私政策",Toast.LENGTH_SHORT).show();
                    return;
                }
                weChatAuth();
                break;
            default:
        }
    }



    /** --------------------------------- 微信原生登录  ---------------------------------*/
    private IWXAPI api;

    private void weChatAuth(){
        if (api == null) {
            api = WXAPIFactory.createWXAPI(LoginActivity.this, Constant.WXAPPKEY, true);
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wx_login_duzun";
        api.sendReq(req);
    }


}
