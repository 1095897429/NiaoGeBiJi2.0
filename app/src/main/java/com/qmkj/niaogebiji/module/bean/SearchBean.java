package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class SearchBean extends BaseBean {

    private List<Hot_search> hot_search;
    private List<Adv_list> adv_list;

    public List<Adv_list> getAdv_list() {
        return adv_list;
    }

    public void setAdv_list(List<Adv_list> adv_list) {
        this.adv_list = adv_list;
    }

    public void setHot_search(List<Hot_search> hot_search) {
        this.hot_search = hot_search;
    }
    public List<Hot_search> getHot_search() {
        return hot_search;
    }


    //热搜
    public static class Hot_search {
        private String id;
        private String search_string;
        private String separated_keywords;
        private String article_num;
        private String search_times;
        private String porder;
        private String created_at;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setSearch_string(String search_string) {
            this.search_string = search_string;
        }
        public String getSearch_string() {
            return search_string;
        }

        public void setSeparated_keywords(String separated_keywords) {
            this.separated_keywords = separated_keywords;
        }
        public String getSeparated_keywords() {
            return separated_keywords;
        }

        public void setArticle_num(String article_num) {
            this.article_num = article_num;
        }
        public String getArticle_num() {
            return article_num;
        }

        public void setSearch_times(String search_times) {
            this.search_times = search_times;
        }
        public String getSearch_times() {
            return search_times;
        }

        public void setPorder(String porder) {
            this.porder = porder;
        }
        public String getPorder() {
            return porder;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
        public String getCreated_at() {
            return created_at;
        }

    }


    //广告
    public static class Adv_list {

        private String id;
        private String title;
        private String platform;
        private String position;
        private String porder;
        private String show_time;
        private String end_time;
        private String pic;
        private String type;
        private String relatedid;
        private String link;
        private String offline;
        private String pv;
        private String uv;
        private String author;
        private String show_title;
        private String show_author;
        private String created_at;
        private String updated_at;
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

        public void setPlatform(String platform) {
            this.platform = platform;
        }
        public String getPlatform() {
            return platform;
        }

        public void setPosition(String position) {
            this.position = position;
        }
        public String getPosition() {
            return position;
        }

        public void setPorder(String porder) {
            this.porder = porder;
        }
        public String getPorder() {
            return porder;
        }

        public void setShow_time(String show_time) {
            this.show_time = show_time;
        }
        public String getShow_time() {
            return show_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }
        public String getEnd_time() {
            return end_time;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
        public String getPic() {
            return pic;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setRelatedid(String relatedid) {
            this.relatedid = relatedid;
        }
        public String getRelatedid() {
            return relatedid;
        }

        public void setLink(String link) {
            this.link = link;
        }
        public String getLink() {
            return link;
        }

        public void setOffline(String offline) {
            this.offline = offline;
        }
        public String getOffline() {
            return offline;
        }

        public void setPv(String pv) {
            this.pv = pv;
        }
        public String getPv() {
            return pv;
        }

        public void setUv(String uv) {
            this.uv = uv;
        }
        public String getUv() {
            return uv;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return author;
        }

        public void setShow_title(String show_title) {
            this.show_title = show_title;
        }
        public String getShow_title() {
            return show_title;
        }

        public void setShow_author(String show_author) {
            this.show_author = show_author;
        }
        public String getShow_author() {
            return show_author;
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
