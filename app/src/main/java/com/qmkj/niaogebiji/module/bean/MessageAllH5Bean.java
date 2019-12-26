package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-26
 * 描述:h5消息bean
 */
public class MessageAllH5Bean extends BaseBean {

        private String type;
        private MessageH5Bean params;
        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

    public MessageH5Bean getParams() {
        return params;
    }

    public void setParams(MessageH5Bean params) {
        this.params = params;
    }

    public static class  MessageH5Bean extends BaseBean{
            private String id;
            private String read;
            private String note;
            private String type;
            private String relatedid;
            private String relatedtitle;
            private String authorid;
            private String author;
            private int created_at;


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getRead() {
                return read;
            }

            public void setRead(String read) {
                this.read = read;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
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

            public String getRelatedtitle() {
                return relatedtitle;
            }

            public void setRelatedtitle(String relatedtitle) {
                this.relatedtitle = relatedtitle;
            }

            public String getAuthorid() {
                return authorid;
            }

            public void setAuthorid(String authorid) {
                this.authorid = authorid;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public int getCreated_at() {
                return created_at;
            }

            public void setCreated_at(int created_at) {
                this.created_at = created_at;
            }
        }




}
