package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-27
 * 描述:邀请好友 - 海报
 */
public class InvitePosterBean extends BaseBean {

    private List<String> pic_list;

    public List<String> getPic_list() {
        return pic_list;
    }

    public void setPic_list(List<String> pic_list) {
        this.pic_list = pic_list;
    }
}
