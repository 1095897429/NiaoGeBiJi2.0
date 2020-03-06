package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:封禁用户  , token失效用户 --- 都给与token
 */
public class LoginSyEvent {
    public String  wxToken;

    public String getWxToken() {
        return wxToken;
    }

    public void setWxToken(String wxToken) {
        this.wxToken = wxToken;
    }

    public LoginSyEvent(String wxToken) {
        this.wxToken = wxToken;
    }
}
