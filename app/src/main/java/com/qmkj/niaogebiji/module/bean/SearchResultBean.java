package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:搜索结果页
 */
public class SearchResultBean extends BaseBean {

    private String num;
    private List<Article_list> list;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public List<Article_list> getList() {
        return list;
    }

    public void setList(List<Article_list> list) {
        this.list = list;
    }

    //作者的相关文章
    public static class Article_list {
        private String aid;
        private String title;
        private String author;
        private String author_id;
        private String pic;
        private String published_at;
        //1-关注，0-未关注
        private int is_follow;

        public int getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(int is_follow) {
            this.is_follow = is_follow;
        }

        public void setAid(String aid) {
            this.aid = aid;
        }
        public String getAid() {
            return aid;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return author;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }
        public String getAuthor_id() {
            return author_id;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
        public String getPic() {
            return pic;
        }

        public void setPublished_at(String published_at) {
            this.published_at = published_at;
        }
        public String getPublished_at() {
            return published_at;
        }

    }


}
