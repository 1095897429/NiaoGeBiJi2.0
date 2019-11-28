package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:展示我的收藏文章实体
 */
public class CollectArticleBean extends BaseBean {

    private List<Collect_list> list;

    private String page_no;

    private String num_of_pages;

    public List<Collect_list> getList() {
        return list;
    }

    public void setList(List<Collect_list> list) {
        this.list = list;
    }

    public String getPage_no() {
        return page_no;
    }

    public void setPage_no(String page_no) {
        this.page_no = page_no;
    }

    public String getNum_of_pages() {
        return num_of_pages;
    }

    public void setNum_of_pages(String num_of_pages) {
        this.num_of_pages = num_of_pages;
    }

    // 收藏的相关文章
    public static class Collect_list {

        private String id;
        //文章的id
        private String aid;
        private String title;
        private String author;
        private String author_id;
        private String pic;
        private String published_at;

        private String created_at;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getAid() {
            return aid;
        }

        public void setAid(String aid) {
            this.aid = aid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
