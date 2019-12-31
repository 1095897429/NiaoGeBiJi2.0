package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-26
 * 描述:h5消息 用户 bean
 */
public class MessageUserBean extends BaseBean {

        private String type;
        private H5UserBean params;
        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

    public H5UserBean getParams() {
        return params;
    }

    public void setParams(H5UserBean params) {
        this.params = params;
    }

    public static class  H5UserBean extends BaseBean{
            private String uid;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }




}
