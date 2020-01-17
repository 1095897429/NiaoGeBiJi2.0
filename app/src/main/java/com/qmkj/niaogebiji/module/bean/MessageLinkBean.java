package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-01-13
 * 描述:
 */
public class MessageLinkBean {
    private String type;
    private MessageLink params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MessageLink getParams() {
        return params;
    }

    public void setParams(MessageLink params) {
        this.params = params;
    }

    public static class   MessageLink extends BaseBean {
        private String link;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
