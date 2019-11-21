package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:文章评分返回bean
 */
public class ActiclePointBean extends BaseBean {

    private String result;

    private int is_show_tip;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getIs_show_tip() {
        return is_show_tip;
    }

    public void setIs_show_tip(int is_show_tip) {
        this.is_show_tip = is_show_tip;
    }
}
