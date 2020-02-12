package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
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
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.OneKeyLoginListener;
import com.chuanglan.shanyan_sdk.listener.OpenLoginAuthListener;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.OnMultiClickUtils;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020.2.9
 * 描述:输入手机号
 * 版本2 :  新增闪验 一键登录使用场景
 */
public class PhoneInputV2Activity extends BaseActivity {

    @BindView(R.id.toGetVertifyCode)
    TextView toGetVertifyCode;

    @BindView(R.id.phone_et)
    EditText phone_et;

    @BindView(R.id.phone_tv)
    TextView phone_tv;

    @BindView(R.id.changePhone)
    TextView changePhone;


    String mMobile;

    Typeface typeface;


    //登录方式
    private String loginType;

    private String wechat_token = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phoneinput_v2;
    }




    private void openLoginActivity() {
        //拉授权页方法
        OneKeyLoginManager.getInstance().openLoginAuth(false, new OpenLoginAuthListener() {
            @Override
            public void getOpenLoginAuthStatus(int code, String result) {
                if (1000 == code) {
                    //拉起授权页成功
                    KLog.e("tag", "拉起授权页成功： _code==" + code + "   _result==" + result);
                } else {
                    //拉起授权页失败
                    KLog.e("tag",  "拉起授权页失败： _code==" + code + "   _result==" + result);
                    ToastUtils.showShort(result);
                }
            }
        }, new OneKeyLoginListener() {
            @Override
            public void getOneKeyLoginStatus(int code, String result) {
                if (1011 == code) {
                    KLog.e("tag",  "用户点击授权页返回： _code==" + code + "   _result==" + result);
                    return;
                } else if (1000 == code) {
                    KLog.e("tag",  "用户点击登录获取token成功： _code==" + code + "   _result==" + result);
                    //OneKeyLoginManager.getInstance().setLoadingVisibility(false);
                    //AbScreenUtils.showToast(getApplicationContext(), "用户点击登录获取token成功");
                } else {
                    KLog.e("tag",  "用户点击登录获取token失败： _code==" + code + "   _result==" + result);
                }
//                startTime = System.currentTimeMillis();
//                startResultActivity(code, result);
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {

        loginType = getIntent().getStringExtra("loginType");

        wechat_token = getIntent().getStringExtra("wechat_token");

        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");

        phone_tv.setTypeface(typeface);

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
        UIHelper.toVertifyCodeActivity(PhoneInputV2Activity.this,mobile,wechat_token,loginType);
    }


    @OnClick({R.id.iv_back,R.id.toGetVertifyCode,
            R.id.changePhone})
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(phone_et);
        switch (view.getId()){
            case R.id.changePhone:

                break;
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
