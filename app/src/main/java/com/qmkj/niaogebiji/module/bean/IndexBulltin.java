package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-15
 * 描述:首页快讯bean
 */
public class IndexBulltin  extends BaseBean {

    private List<Bulletn_list> bulletn_list;
    public void setBulletn_list(List<Bulletn_list> bulletn_list) {
        this.bulletn_list = bulletn_list;
    }
    public List<Bulletn_list> getBulletn_list() {
        return bulletn_list;
    }


    public static class Bulletn_list {

        private String id;
        private String pub_time;
        private String note;
        private String title;
        private String content;
        private String pic;
        private String link;
        private String status;
        private String comment_num;
        private String good_num;
        private String created_at;
        private String updated_at;

        //显示 头条 icon  top = 1 显示
        private String top;


        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setPub_time(String pub_time) {
            this.pub_time = pub_time;
        }
        public String getPub_time() {
            return pub_time;
        }

        public void setNote(String note) {
            this.note = note;
        }
        public String getNote() {
            return note;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setContent(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
        public String getPic() {
            return pic;
        }

        public void setLink(String link) {
            this.link = link;
        }
        public String getLink() {
            return link;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

        public void setComment_num(String comment_num) {
            this.comment_num = comment_num;
        }
        public String getComment_num() {
            return comment_num;
        }

        public void setGood_num(String good_num) {
            this.good_num = good_num;
        }
        public String getGood_num() {
            return good_num;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
        public String getCreated_at() {
            return created_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
        public String getUpdated_at() {
            return updated_at;
        }

    }


}
