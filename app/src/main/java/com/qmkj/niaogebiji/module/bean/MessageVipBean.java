package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-26
 * 描述:h5消息 用户 bean
 */
public class MessageVipBean extends BaseBean {

        private String type;
        private VipBean params;
        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

    public VipBean getParams() {
        return params;
    }

    public void setParams(VipBean params) {
        this.params = params;
    }

    public static class  VipBean extends BaseBean{
            private String active;

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }
    }




}
