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

        //周榜的影响力
        private String hit_count_weekly;

        //1是已关注  0 是未关注
        private int is_follow;


        //搜索中的部分
        private String aid;
        private String title;
        private String author;
        private String published_at;
        private String pic;

        public String getHit_count_weekly() {
            return hit_count_weekly;
        }

        public void setHit_count_weekly(String hit_count_weekly) {
            this.hit_count_weekly = hit_count_weekly;
        }

        //作者类型
        private String type;

        //作者对应的uid 为0 表示没有关联用户
        private String uid;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public Author() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAid() {
            return aid;
        }

        public void setAid(String aid) {
            this.aid = aid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getPublished_at() {
            return published_at;
        }

        public void setPublished_at(String published_at) {
            this.published_at = published_at;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
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
