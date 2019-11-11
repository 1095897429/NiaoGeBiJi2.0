package com.qmkj.niaogebiji.module.activity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.wxapi.WXEntryActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述: 登录
 */
public class LoginActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {

    }


    @OnClick({R.id.weixinLogin,R.id.phoneLogin})
    public void login(View view) {
        switch (view.getId()) {
            case R.id.weixinLogin:
                weChatAuth();

                break;
            case R.id.phoneLogin:
                UIHelper.toPhoneInputActivity(LoginActivity.this);
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
