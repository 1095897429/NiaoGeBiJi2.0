package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-04
 * 描述:
 */
public class MessageCooperationBean extends BaseBean {

    private String type;
    private CooperationBean params;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public CooperationBean getParams() {
        return params;
    }

    public void setParams(CooperationBean params) {
        this.params = params;
    }

    public static class CooperationBean extends BaseBean {
        private String link;
        private String title;
        private String subTitle;
        private String img;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

}
