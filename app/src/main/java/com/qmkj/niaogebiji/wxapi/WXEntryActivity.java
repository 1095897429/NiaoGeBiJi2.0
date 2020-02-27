package com.qmkj.niaogebiji.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.VertifyCodeActivity;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.ActicleShareEvent;
import com.qmkj.niaogebiji.module.event.FlashShareEvent;
import com.qmkj.niaogebiji.module.event.LoginErrEvent;
import com.qmkj.niaogebiji.module.event.LoginGoodEvent;
import com.qmkj.niaogebiji.module.fragment.FlashFragment;
import com.socks.library.KLog;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-8
 * 描述: code been used -- 什么原因导致用过了，Activity 可以，换成 WXCallbackActivity 就不行
 *       WXCallbackActivity 是友盟的，他已经消耗过了
 *
 * 版本2：微信登录成功之后，判断是否能拿到手机号，拿不到就跳转到让他自己输入手机号的页面
 */
public class WXEntryActivity extends  Activity implements IWXAPIEventHandler {

    private IWXAPI mIWXAPI;
    //登录
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    //分享
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIWXAPI = WXAPIFactory.createWXAPI(this, Constant.WXAPPKEY, false);
        //将你收到的intent和实现IWXAPIEventHandler接口的对象传递给handleIntent方法
        mIWXAPI.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mIWXAPI.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    //第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp baseResp) {
        String result ;
        //类型：分享还是登录
        int type = baseResp.getType();
        KLog.d("tag", "type:------> 1 是登录 :  2是分享       " + type);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //用户同意
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    //用户换取access_token的code，仅在ErrCode为0时有效
                    String tempCode = ((SendAuth.Resp) baseResp).code;
//                    KLog.d("tag", "code:------>" + tempCode);

                    SendAuth.Resp sendResp = (SendAuth.Resp) baseResp;
                    if (sendResp != null) {
                        String code = sendResp.code;
                        KLog.d("tag","获取code,请求开始");
                        wechatlogin(code);
                        KLog.d("tag","获取code,请求结束");
                    }

                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    KLog.d("tag","微信分享成功");
                    //通过EventBus发送
//                    if(FlashFragment.isFlashShare){
//                        EventBus.getDefault().post(new FlashShareEvent("快讯分享"));
//                    }

                    //文章分享成功
                    if(Constant.isActicleShare){
                        EventBus.getDefault().post(new ActicleShareEvent("文章分享"));
                    }

                }

                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "拒绝授权微信登录";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                result = "发送返回";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();

                finish();
                break;
        }
    }


    private RegisterLoginBean.UserInfo mWxResultBean;

    private void wechatlogin(String code) {
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().wechatlogin(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<HttpResponse<RegisterLoginBean.UserInfo>>() {
                    @Override
                    public void onSuccess(HttpResponse<RegisterLoginBean.UserInfo> response) {
                        KLog.e("tag",response.getReturn_msg());
                        KLog.d("tag","用户状态 " + response.getReturn_data().getStatus());
                        mWxResultBean = response.getReturn_data();
                        if(null != mWxResultBean){
                            KLog.e("tag","wechatoken 是 " + response.getReturn_data().getWechat_token());
                            //wechat_token（不为空) 是 新用户
                            if(!TextUtils.isEmpty(mWxResultBean.getWechat_token())){
                                UIHelper.toPhoneInputActivity(WXEntryActivity.this,mWxResultBean.getWechat_token(),"weixin");
                                //TODO 新逻辑，进入闪验界面
//                                UIHelper.toPhoneShanYanActivity(WXEntryActivity.this,mWxResultBean.getWechat_token(),"weixin");
//                                EventBus.getDefault().post(new LoginGoodEvent("微信登录 成功，销毁登录界面"));
                            }else{
                                RegisterLoginBean.UserInfo mUserInfo = response.getReturn_data();
                                StringUtil.setUserInfoBean(mUserInfo);
                                SPUtils.getInstance().put(Constant.IS_LOGIN,true);
                                UIHelper.toHomeActivity(WXEntryActivity.this,0);
                                EventBus.getDefault().post(new LoginGoodEvent("微信获取数据成功，并在此发送事件请求用户数据"));
                            }

//                            RegisterLoginBean.UserInfo mUserInfo = response.getReturn_data();
//                            StringUtil.setUserInfoBean(mUserInfo);
//                            SPUtils.getInstance().put(Constant.IS_LOGIN,true);
//                            UIHelper.toHomeActivity(WXEntryActivity.this,0);
//                            //在FristFragment中有，
//                            EventBus.getDefault().post(new LoginGoodEvent("微信登录 成功，销毁登录界面"));

                            finish();
                        }
                    }


                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        if("20058".equals(return_code)){
                            KLog.d("tag","不用管");
                        }else if("30001".equals(return_code)){
                            EventBus.getDefault().post(new LoginErrEvent("封禁用户"));
                        }
                    }


                    @Override
                    public void onNetFail(String mes) {

                        finish();
                    }
                });
    }
}
