package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:商品实体
 */
public class FeatherProductBean extends BaseBean {

    private List<Productean> list;
    public void setList(List<Productean> list) {
        this.list = list;
    }
    public List<Productean> getList() {
        return list;
    }


    public static class Productean extends BaseBean{
        //以id为明细基准
        private String id;
        private String title;
        //1线上资料 2是实体  3 优惠券 4.书籍
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
        //是否已兑换该商品：1-是，0-否
        private int has_exch;

        private List<ProductItemBean> point_list;

        public List<ProductItemBean> getPoint_list() {
            return point_list;
        }

        public void setPoint_list(List<ProductItemBean> point_list) {
            this.point_list = point_list;
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

        public void setHas_exch(int has_exch) {
            this.has_exch = has_exch;
        }
        public int getHas_exch() {
            return has_exch;
        }


        public static class ProductItemBean extends BaseBean {
            private String id;
            private String title;
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
            private int has_exch;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCat() {
                return cat;
            }

            public void setCat(String cat) {
                this.cat = cat;
            }

            public String getPoint() {
                return point;
            }

            public void setPoint(String point) {
                this.point = point;
            }

            public String getPoint_memo() {
                return point_memo;
            }

            public void setPoint_memo(String point_memo) {
                this.point_memo = point_memo;
            }

            public String getImg_list() {
                return img_list;
            }

            public void setImg_list(String img_list) {
                this.img_list = img_list;
            }

            public String getImg_detail() {
                return img_detail;
            }

            public void setImg_detail(String img_detail) {
                this.img_detail = img_detail;
            }

            public String getSummary() {
                return summary;
            }

            public void setSummary(String summary) {
                this.summary = summary;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStarted_at() {
                return started_at;
            }

            public void setStarted_at(String started_at) {
                this.started_at = started_at;
            }

            public String getExpired_at() {
                return expired_at;
            }

            public void setExpired_at(String expired_at) {
                this.expired_at = expired_at;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }

            public int getHas_exch() {
                return has_exch;
            }

            public void setHas_exch(int has_exch) {
                this.has_exch = has_exch;
            }
        }

    }




}
