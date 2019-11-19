package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
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


    @Override
    protected int getLayoutId() {
        return R.layout.activity_phoneinput;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {

        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");

        KeyboardUtils.showSoftInput(phone_et);

        toGetVertifyCode.setOnClickListener(view -> {
            mMobile = phone_et.getText().toString().trim();
            if(!RegexUtils.isMobileExact(mMobile)){
                ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                ToastUtils.showShort("你输入的好像不是手机号");
                return;
            }
            envelop(mMobile);
        });


        RxTextView
                .textChanges(phone_et)
                .subscribe(charSequence -> {
                    KLog.d("tag", "accept: " + charSequence.toString());
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


    //是否封禁
    private void envelop(String mobile) {

        //1 是，弹窗提示用户联系客服
//        toUDesk();

        //2 否 手机号今天是否已经获取超过5次验证码 - 显示错误信息

        UIHelper.toVertifyCodeActivity(PhoneInputActivity.this);
    }


    @OnClick({R.id.iv_back})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }



    /** --------------------------------- 联系客服  ---------------------------------*/
    //没有登录的时候，userinfo不传，登录，就传
    private void toUDesk(){

        UdeskConfig.Builder builder = new UdeskConfig.Builder();

        //token为随机获取的，如 UUID.randomUUID().toString()
        String sdktoken = UUID.randomUUID().toString();
        KLog.d("tag",sdktoken + "");
        Map<String, String> info = new HashMap<>();
        info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN, sdktoken);
        //以下信息是可选

//        if(null != mUserInfo){
//            info.put(UdeskConst.UdeskUserInfo.NICK_NAME,mUserInfo.getNickname());
//            info.put(UdeskConst.UdeskUserInfo.CELLPHONE,mUserInfo.getMobile());
//            builder.setCustomerUrl(mUserInfo.getAvatar());
//        }
        info.put(UdeskConst.UdeskUserInfo.DESCRIPTION,"描述信息");


        builder.setUsephoto(true);
        builder.setUseEmotion(true);
        builder.setUseMore(true);
        builder.setUserForm(true);
        builder.setUserSDkPush(true);
        builder.setFormCallBack(new IUdeskFormCallBack() {
            @Override
            public void toLuachForm(Context context) {
                KLog.d("tag","jkkkk");
            }
        });
        builder.setDefualtUserInfo(info);
        UdeskSDKManager.getInstance().entryChat(BaseApp.getApplication(), builder.build(), sdktoken);
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
