package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:邀请好友
 */
public class InviteBean extends BaseBean {

    private List<String> pic_links;

    public List<String> getPic_links() {
        return pic_links;
    }

    public void setPic_links(List<String> pic_links) {
        this.pic_links = pic_links;
    }
}
