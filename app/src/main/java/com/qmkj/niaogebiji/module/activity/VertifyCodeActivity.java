package com.qmkj.niaogebiji.module.activity;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.common.utils.SystemUtil;
import com.qmkj.niaogebiji.module.bean.IsPhoneBindBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.widget.SecurityCodeView;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.udesk.UdeskSDKManager;
import cn.udesk.config.UdeskConfig;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import udesk.core.UdeskConst;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:验证码
 */
public class VertifyCodeActivity extends BaseActivity {

    @BindView(R.id.et)
    EditText et;

    @BindView(R.id.noget_code)
    TextView noget_code;

    @BindView(R.id.reget_code)
    TextView reget_code;

    @BindView(R.id.phone_text)
    TextView phone_text;

    @BindView(R.id.edit_security_code)
    SecurityCodeView editText;

    String inputContent;

    Disposable disposable;

    //倒计时60秒
    public static int COUNT = 2;

    private String phone;

    private String loginType;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_vertifycode;
    }

    @Override
    protected void initView() {

        loginType = getIntent().getStringExtra("loginType");

        KeyboardUtils.showSoftInput(et);


        phone = getIntent().getStringExtra("phone");
        phone_text.setText("已向" + phone +" 发送验证码");

        toGetCode();

        editText.setInputCompleteListener(new SecurityCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                inputContent = editText.getEditContent();
                KLog.d("tag","请输入验证码 : " + inputContent);
                if("weixin".equals(loginType)){
                    WechatBindAccountViaCode();
                }else if("phone".equals(loginType)){
                    loginViaCode();
                }
            }

            @Override
            public void deleteContent(boolean isDelete) {
                KLog.d("tag","请输入验证码");
            }
        });

        reget_code.setOnClickListener(view -> {
            toGetCode();
        });
    }

    private void WechatBindAccountViaCode() {
        Map<String,String> map = new HashMap<>();
        map.put("mobile",phone);
        map.put("verify_code",inputContent);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().WechatBindAccountViaCode(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {

                    }

                    @Override
                    public void onHintError(String errorMes) {
                        ToastUtils.setGravity(Gravity.CENTER,0,0);
                        ToastUtils.showShort(errorMes);
                        ToastUtils.setGravity(Gravity.BOTTOM,0,0);
                    }

                });
    }

    private void loginViaCode() {
            Map<String,String> map = new HashMap<>();
            map.put("mobile",phone);
            map.put("verify_code",inputContent);
            //设备厂商
            map.put("device_manufact", SystemUtil.getDeviceManufactuerer());
            //设备机型
            map.put("device_model",SystemUtil.getSystemModel());
            String result = RetrofitHelper.commonParam(map);
            RetrofitHelper.getApiService().loginViaCode(result)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new BaseObserver<HttpResponse<RegisterLoginBean.UserInfo>>() {
                        @Override
                        public void onSuccess(HttpResponse<RegisterLoginBean.UserInfo> response) {

                            RegisterLoginBean.UserInfo mUserInfo = response.getReturn_data();
                            UIHelper.toHomeActivity(VertifyCodeActivity.this,0);
                            //保存一个对象
                            StringUtil.setUserInfoBean(mUserInfo);
                            finish();
                        }

                        @Override
                        public void onHintError(String errorMes) {
                            ToastUtils.setGravity(Gravity.CENTER,0,0);
                            ToastUtils.showShort(errorMes);
                            ToastUtils.setGravity(Gravity.BOTTOM,0,0);
                        }

                    });

    }


    private void toGetCode(){
        sendverifycode();
    }



    //验证码类型：1-短信（默认），2-语音
    private String mType = "1";
    //操作类型： 1-注册 2-绑定微信账号 3-微信绑定已有账号 4-密码重置 5-极速登录 6-更换手机之验证旧手机 7-更换手机之验证新手机
    private String mOpeType = "1";

    private void sendverifycode() {
        Map<String,String> map = new HashMap<>();
        map.put("mobile",phone);
        map.put("type", mType);
        map.put("ope_type",mOpeType);

        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().sendverifycode(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.e("tag",response.getReturn_msg());
                        //设置倒计时
                        initRxTime();
                    }
                });
    }



    private void initRxTime() {
        int time = SPUtils.getInstance().getInt("lastTime");
        int count;
        if(time == -1){
            //第一次进入
            count = COUNT;
            reget_code.setEnabled(true);
        }else if(time == 0){
            //倒计时完成了
            count = COUNT;
            reget_code.setEnabled(true);
        }else{
            //倒计时没结束进入
            count = time;
            reget_code.setEnabled(false);
        }

        //参数依次为：从0开始，发送次数是9次 ，0秒延时,每隔1秒发射,主线程中
        disposable = Observable.intervalRange(0,count + 1,0,1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                    //接收到消息，这里需要判空，因为3秒倒计时中间如果页面结束了，会造成找不到 tvAdCountDown
                    if (reget_code != null) {
                        reget_code.setText("重新发送 " + (count - aLong) +  " 秒");
                        reget_code.setTextColor(Color.parseColor("#AAAEB3"));
                        SPUtils.getInstance().put("lastTime",(int) (count - aLong));
                        reget_code.setEnabled(false);
                    }
                }).doOnComplete(() -> {
                    reget_code.setEnabled(true);
//                    KLog.d("tag","完成之后跳转到主页面");
                    SPUtils.getInstance().put("lastTime",COUNT);
                    reget_code.setText("获取验证码");
                    reget_code.setTextColor(Color.parseColor("#5675A7"));
                })
                .subscribe();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != disposable){
            disposable.dispose();
        }
    }


    @OnClick({R.id.iv_back,R.id.noget_code})
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(et);
        switch (view.getId()){
            case R.id.noget_code:

                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }



    public void showFobbidUserDialog(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("联系客服", v -> {
            toUDesk();
        }).setNegativeButton("取消", v -> {}).setMsg("你的账户已被封禁\n" +
                "请联系客服处理").setBold().setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    public void showMesCountOverDialog(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("联系客服", v -> {

        }).setNegativeButton("取消", v -> {}).setMsg("验证码发送次数今日已达上限\n" +
                "请联系客服处理").setBold().setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
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


    /** --------------------------------- 联系客服  ---------------------------------*/
    //没有登录的时候，userinfo不传，登录，就传
    private void toUDesk(){
        UdeskConfig.Builder builder = new UdeskConfig.Builder();
        //token为随机获取的，如 UUID.randomUUID().toString()
        String sdktoken = UUID.randomUUID().toString();
        KLog.d("tag",sdktoken + "");
        Map<String, String> info = new HashMap<>();
        info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN, sdktoken);
        info.put(UdeskConst.UdeskUserInfo.DESCRIPTION,"描述信息");
        builder.setUsephoto(true);
        builder.setUseEmotion(true);
        builder.setUseMore(true);
        builder.setUserForm(true);
        builder.setUserSDkPush(true);
        builder.setFormCallBack(context -> KLog.d("tag","jkkkk"));
        builder.setDefualtUserInfo(info);
        UdeskSDKManager.getInstance().entryChat(BaseApp.getApplication(), builder.build(), sdktoken);
    }



}
