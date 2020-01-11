package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.udesk.UdeskSDKManager;
import cn.udesk.callback.IUdeskFormCallBack;
import cn.udesk.config.UdeskConfig;
import io.reactivex.functions.Consumer;
import udesk.core.UdeskConst;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:输入手机号
 */
public class PhoneInputActivity extends BaseActivity {

    @BindView(R.id.toGetVertifyCode)
    TextView toGetVertifyCode;

    @BindView(R.id.phone_et)
    EditText phone_et;

    String mMobile;

    Typeface typeface;


    //登录方式
    private String loginType;

    private String wechat_token = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phoneinput;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {

        loginType = getIntent().getStringExtra("loginType");

        wechat_token = getIntent().getStringExtra("wechat_token");

        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");

        KeyboardUtils.showSoftInput(phone_et);


        RxTextView
                .textChanges(phone_et)
                .subscribe(charSequence -> {
                    mMobile = charSequence.toString().trim();
                    if(!TextUtils.isEmpty(mMobile)) {
                        toGetVertifyCode.setBackgroundResource(R.drawable.bg_corners_12_yellow);
                        toGetVertifyCode.setEnabled(true);
                        phone_et.setTypeface(typeface);
                        phone_et.setTextSize(28);
                    }else{
                        toGetVertifyCode.setBackgroundResource(R.drawable.bg_corners_12_light_yellow);
                        toGetVertifyCode.setEnabled(false);
                        phone_et.setTypeface(null);
                        phone_et.setTextSize(16);
                    }
                });
    }


    //请求 是否封禁 b-1是 toUDesk b-2 不是 手机号今天是否已经获取超过5次验证码 c-1是 toUDesk c-2不是 发送 成功
    private void envelop(String mobile) {

        //1 是，弹窗提示用户联系客服
//        toUDesk();

        //2 否 手机号今天是否已经获取超过5次验证码 - 显示错误信息
        MobclickAgentUtils.onEvent(UmengEvent.bindphone_bind_2_0_0);
        UIHelper.toVertifyCodeActivity(PhoneInputActivity.this,mobile,wechat_token,loginType);
    }


    @OnClick({R.id.iv_back,R.id.toGetVertifyCode})
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(phone_et);
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.toGetVertifyCode:
                mMobile = phone_et.getText().toString().trim();
//                if(!RegexUtils.isMobileExact(mMobile)){
//                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
//                    ToastUtils.showShort("你输入的好像不是手机号");
//                    return;
//                }

                if(!TextUtils.isEmpty(mMobile) && mMobile.length() == 11){
                    envelop(mMobile);
                }else{
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("你输入的好像不是手机号");
                }


                break;
            default:
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            finish();
        }
    }

    // 点击空白区域 自动隐藏软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super .onTouchEvent(event);
    }


}
