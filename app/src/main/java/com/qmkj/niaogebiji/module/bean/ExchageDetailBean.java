package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.io.Serializable;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:商品兑换详情
 */
public class ExchageDetailBean extends BaseBean {


    private ExchageDetail list;
    public void setList(ExchageDetail list) {
        this.list = list;
    }
    public ExchageDetail getList() {
        return list;
    }



    public static class ExchageDetail implements Serializable {

        private String id;
        private String title;
        //2 实体商品
        private String cat;
        private String point;
        private String point_memo;
        private String img_list;
        private String img_detail;
        private String summary;
        private String content;
        private String link;
        private String status;
        private String started_at;
        private String expired_at;
        private String created_at;
        private String updated_at;
        private String name;
        private String mobile;
        private String address;

        //提取码
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }
        public String getCat() {
            return cat;
        }

        public void setPoint(String point) {
            this.point = point;
        }
        public String getPoint() {
            return point;
        }

        public void setPoint_memo(String point_memo) {
            this.point_memo = point_memo;
        }
        public String getPoint_memo() {
            return point_memo;
        }

        public void setImg_list(String img_list) {
            this.img_list = img_list;
        }
        public String getImg_list() {
            return img_list;
        }

        public void setImg_detail(String img_detail) {
            this.img_detail = img_detail;
        }
        public String getImg_detail() {
            return img_detail;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
        public String getSummary() {
            return summary;
        }

        public void setContent(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
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

        public void setStarted_at(String started_at) {
            this.started_at = started_at;
        }
        public String getStarted_at() {
            return started_at;
        }

        public void setExpired_at(String expired_at) {
            this.expired_at = expired_at;
        }
        public String getExpired_at() {
            return expired_at;
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

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
        public String getMobile() {
            return mobile;
        }

        public void setAddress(String address) {
            this.address = address;
        }
        public String getAddress() {
            return address;
        }

    }
}
