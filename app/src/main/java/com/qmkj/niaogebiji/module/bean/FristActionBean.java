package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-23
 * 描述:首页item中的活动
 */
public class FristActionBean extends BaseBean {

    private ActionBean.Act_list activity;

    public ActionBean.Act_list getActivity() {
        return activity;
    }

    public void setActivity(ActionBean.Act_list activity) {
        this.activity = activity;
    }
}
