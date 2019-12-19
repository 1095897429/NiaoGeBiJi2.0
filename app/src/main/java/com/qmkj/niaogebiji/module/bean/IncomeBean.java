package com.qmkj.niaogebiji.module.bean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:
 */
public class IncomeBean {


    private String page_no;
    private String num_of_pages;
    private List<InComeDetail> list;
    public void setPage_no(String page_no) {
        this.page_no = page_no;
    }
    public String getPage_no() {
        return page_no;
    }

    public void setNum_of_pages(String num_of_pages) {
        this.num_of_pages = num_of_pages;
    }
    public String getNum_of_pages() {
        return num_of_pages;
    }

    public void setList(List<InComeDetail> list) {
        this.list = list;
    }
    public List<InComeDetail> getList() {
        return list;
    }


    public static class InComeDetail {

        private String id;
        private String point;
        private String cat;
        private String img;
        private String operation;
        private String created_at;
        //类型：1-增加积分，2-减少积分，默认所有
        private String ope_type;
        //明细关联数据的类型：1-渠道，2-代理，3-用户，4-评论，5-兑换，6-文章，7-快讯
        private String related_type;

        private String related_id;

        public String getRelated_id() {
            return related_id;
        }

        public void setRelated_id(String related_id) {
            this.related_id = related_id;
        }

        public String getRelated_type() {
            return related_type;
        }

        public void setRelated_type(String related_type) {
            this.related_type = related_type;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setPoint(String point) {
            this.point = point;
        }
        public String getPoint() {
            return point;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }
        public String getCat() {
            return cat;
        }

        public void setImg(String img) {
            this.img = img;
        }
        public String getImg() {
            return img;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
        public String getOperation() {
            return operation;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
        public String getCreated_at() {
            return created_at;
        }

        public void setOpe_type(String ope_type) {
            this.ope_type = ope_type;
        }
        public String getOpe_type() {
            return ope_type;
        }

    }
}
