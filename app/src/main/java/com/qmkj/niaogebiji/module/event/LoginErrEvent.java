package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:封禁用户
 */
public class LoginErrEvent {
    public String  url;

    public LoginErrEvent(String  url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
