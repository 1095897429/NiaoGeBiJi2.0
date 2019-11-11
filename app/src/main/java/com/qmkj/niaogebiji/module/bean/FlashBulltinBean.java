package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:快讯bean
 */
public class FlashBulltinBean extends BaseBean {

    private List<BuilltinBean> list;
    public void setList(List<BuilltinBean> list) {
        this.list = list;
    }
    public List<BuilltinBean> getList() {
        return list;
    }


    public static class BuilltinBean implements Serializable {

        private String id;
        private String pub_time;
        private String note;
        private String title;
        private String content;
        private String pic;
        private String link;
        private String status;
        private String comment_num;
        //文章url
        private String url;
        //点赞数
        private String good_num;
        private String created_at;
        private String updated_at;
        //用户是否点赞：1-点赞
        private int is_good;
        private String qrcode_url;
        private String day_dec;
        private String show_time;
        //显示 头条 icon  top = 1 显示
        private String top;

        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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

        public void setIs_good(int is_good) {
            this.is_good = is_good;
        }
        public int getIs_good() {
            return is_good;
        }

        public void setQrcode_url(String qrcode_url) {
            this.qrcode_url = qrcode_url;
        }
        public String getQrcode_url() {
            return qrcode_url;
        }

        public void setDay_dec(String day_dec) {
            this.day_dec = day_dec;
        }
        public String getDay_dec() {
            return day_dec;
        }

        public void setShow_time(String show_time) {
            this.show_time = show_time;
        }
        public String getShow_time() {
            return show_time;
        }

    }

}
