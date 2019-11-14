package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:推荐作者
 */
public class AuthorBean extends BaseBean {

    private List<Author> list;
    public void setList(List<Author> list) {
        this.list = list;
    }
    public List<Author> getList() {
        return list;
    }


    public static class Author {

        private String id;
        private String summary;
        private String name;
        private String img;
        private String article_count;
        private String hit_count;
        private int is_follow;

        public Author() {
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
        public String getSummary() {
            return summary;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setImg(String img) {
            this.img = img;
        }
        public String getImg() {
            return img;
        }

        public void setArticle_count(String article_count) {
            this.article_count = article_count;
        }
        public String getArticle_count() {
            return article_count;
        }

        public void setHit_count(String hit_count) {
            this.hit_count = hit_count;
        }
        public String getHit_count() {
            return hit_count;
        }

        public void setIs_follow(int is_follow) {
            this.is_follow = is_follow;
        }
        public int getIs_follow() {
            return is_follow;
        }

    }
}
