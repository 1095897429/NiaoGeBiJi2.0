package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:全部搜索中的百科
 */
public class SearchAllBaiduBean extends BaseBean {

    private String num;

    private List<Wiki> list;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }


    public List<Wiki> getList() {
        return list;
    }

    public void setList(List<Wiki> list) {
        this.list = list;
    }

    public static class Wiki{

        private String id;
        private String cate_id;
        private String title;
        private String content;
        private String word_id;
        private String first_char;
        private String detail_id;
        private String created_at;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getWord_id() {
            return word_id;
        }

        public void setWord_id(String word_id) {
            this.word_id = word_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCate_id() {
            return cate_id;
        }

        public void setCate_id(String cate_id) {
            this.cate_id = cate_id;
        }


        public String getFirst_char() {
            return first_char;
        }

        public void setFirst_char(String first_char) {
            this.first_char = first_char;
        }

        public String getDetail_id() {
            return detail_id;
        }

        public void setDetail_id(String detail_id) {
            this.detail_id = detail_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
