package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-18
 * 描述:h5数据 -- 去评论界面
 */
public class H5ToCommentBean extends BaseBean {

    private String type;
    private Params params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public static class Params extends BaseBean {
        private String id;
        private String comment;
        private String created_at;
        private String good_num;
        private String type;
        private String relatedid;
        private String post_title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getGood_num() {
            return good_num;
        }

        public void setGood_num(String good_num) {
            this.good_num = good_num;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRelatedid() {
            return relatedid;
        }

        public void setRelatedid(String relatedid) {
            this.relatedid = relatedid;
        }

        public String getPost_title() {
            return post_title;
        }

        public void setPost_title(String post_title) {
            this.post_title = post_title;
        }
    }
}
