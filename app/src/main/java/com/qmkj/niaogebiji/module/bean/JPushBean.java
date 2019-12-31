package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-30
 * 描述:后台极光返回的bean
 */
public class JPushBean extends BaseBean {
    private String jump_type;
    private String jump_info;

    public String getJump_type() {
        return jump_type;
    }

    public void setJump_type(String jump_type) {
        this.jump_type = jump_type;
    }

    public String getJump_info() {
        return jump_info;
    }

    public void setJump_info(String jump_info) {
        this.jump_info = jump_info;
    }
}
