package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:手机是否注册
 */
public class IsPhoneBindBean extends BaseBean {
    //0 未注册
    private String is_reg;

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIs_reg() {
        return is_reg;
    }

    public void setIs_reg(String is_reg) {
        this.is_reg = is_reg;
    }
}
