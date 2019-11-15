package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:文章详情bean
 */
public class NewsDetailBean extends BaseBean {

    private String aid;
    private String catid;
    private String title;
    private String author_id;
    private String author;
    private String author_flag;
    private String fromsource;
    private String fromurl;
    private String summary;
    private String pic;
    private String search_keywords;
    private String showindex;
    private String status;
    private String viewnum;
    private String commentnum;
    private String favnum;
    private String sharenum;
    private String pointnum;
    private String totalpoint;
    private String pub_type;
    private String published_at;
    private String upnum;
    private List<Relate> relate;
    private String dl_link;
    private String dl_link_code;
    private String dl_point;
    private String que_title;
    private List<Que_answer_json> que_answer_json;
    private String right_answer;
    private String app_content_url;
    private String my_answer;

    private String share_url;
    private String share_title;
    private String share_summary;
    private String share_pic;
    private String is_favorite;
    private String author_avatar;

    //是否下载过：1-下载过，0-未下载
    private String is_dl;

    //是否阅读加积分过 1 加过 0 没有
    private String is_read;

    //文章评分数
    private String article_point;

    //是否关注了作者：1-已关注，0-未关注（可能没登录）
    private String is_follow_author;

    //是否本篇评分过：1-已评分过，0-未评分
    private String is_add_point;


    private int is_show_tip;

    public int getIs_show_tip() {
        return is_show_tip;
    }

    public void setIs_show_tip(int is_show_tip) {
        this.is_show_tip = is_show_tip;
    }

    public String getAuthor_avatar() {
        return author_avatar;
    }

    public void setAuthor_avatar(String author_avatar) {
        this.author_avatar = author_avatar;
    }

    public String getIs_dl() {
        return is_dl;
    }

    public void setIs_dl(String is_dl) {
        this.is_dl = is_dl;
    }

    public String getIs_add_point() {
        return is_add_point;
    }

    public void setIs_add_point(String is_add_point) {
        this.is_add_point = is_add_point;
    }

    public String getIs_follow_author() {
        return is_follow_author;
    }

    public void setIs_follow_author(String is_follow_author) {
        this.is_follow_author = is_follow_author;
    }

    public String getArticle_point() {
        return article_point;
    }

    public void setArticle_point(String article_point) {
        this.article_point = article_point;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }


    public String getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(String is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_summary() {
        return share_summary;
    }

    public void setShare_summary(String share_summary) {
        this.share_summary = share_summary;
    }

    public String getShare_pic() {
        return share_pic;
    }

    public void setShare_pic(String share_pic) {
        this.share_pic = share_pic;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }
    public String getAid() {
        return aid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }
    public String getCatid() {
        return catid;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }
    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor_flag(String author_flag) {
        this.author_flag = author_flag;
    }
    public String getAuthor_flag() {
        return author_flag;
    }

    public void setFromsource(String fromsource) {
        this.fromsource = fromsource;
    }
    public String getFromsource() {
        return fromsource;
    }

    public void setFromurl(String fromurl) {
        this.fromurl = fromurl;
    }
    public String getFromurl() {
        return fromurl;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getSummary() {
        return summary;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
    public String getPic() {
        return pic;
    }

    public void setSearch_keywords(String search_keywords) {
        this.search_keywords = search_keywords;
    }
    public String getSearch_keywords() {
        return search_keywords;
    }

    public void setShowindex(String showindex) {
        this.showindex = showindex;
    }
    public String getShowindex() {
        return showindex;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setViewnum(String viewnum) {
        this.viewnum = viewnum;
    }
    public String getViewnum() {
        return viewnum;
    }

    public void setCommentnum(String commentnum) {
        this.commentnum = commentnum;
    }
    public String getCommentnum() {
        return commentnum;
    }

    public void setFavnum(String favnum) {
        this.favnum = favnum;
    }
    public String getFavnum() {
        return favnum;
    }

    public void setSharenum(String sharenum) {
        this.sharenum = sharenum;
    }
    public String getSharenum() {
        return sharenum;
    }

    public void setPointnum(String pointnum) {
        this.pointnum = pointnum;
    }
    public String getPointnum() {
        return pointnum;
    }

    public void setTotalpoint(String totalpoint) {
        this.totalpoint = totalpoint;
    }
    public String getTotalpoint() {
        return totalpoint;
    }

    public void setPub_type(String pub_type) {
        this.pub_type = pub_type;
    }
    public String getPub_type() {
        return pub_type;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }
    public String getPublished_at() {
        return published_at;
    }

    public void setUpnum(String upnum) {
        this.upnum = upnum;
    }
    public String getUpnum() {
        return upnum;
    }

    public void setRelate(List<Relate> relate) {
        this.relate = relate;
    }
    public List<Relate> getRelate() {
        return relate;
    }

    public void setDl_link(String dl_link) {
        this.dl_link = dl_link;
    }
    public String getDl_link() {
        return dl_link;
    }

    public void setDl_link_code(String dl_link_code) {
        this.dl_link_code = dl_link_code;
    }
    public String getDl_link_code() {
        return dl_link_code;
    }

    public void setDl_point(String dl_point) {
        this.dl_point = dl_point;
    }
    public String getDl_point() {
        return dl_point;
    }

    public void setQue_title(String que_title) {
        this.que_title = que_title;
    }
    public String getQue_title() {
        return que_title;
    }

    public void setQue_answer_json(List<Que_answer_json> que_answer_json) {
        this.que_answer_json = que_answer_json;
    }
    public List<Que_answer_json> getQue_answer_json() {
        return que_answer_json;
    }

    public void setRight_answer(String right_answer) {
        this.right_answer = right_answer;
    }
    public String getRight_answer() {
        return right_answer;
    }

    public void setApp_content_url(String app_content_url) {
        this.app_content_url = app_content_url;
    }
    public String getApp_content_url() {
        return app_content_url;
    }

    public void setMy_answer(String my_answer) {
        this.my_answer = my_answer;
    }
    public String getMy_answer() {
        return my_answer;
    }


    //问答
    public static class Que_answer_json {

        private int answer_id;
        private String answer_title;
        public void setAnswer_id(int answer_id) {
            this.answer_id = answer_id;
        }
        public int getAnswer_id() {
            return answer_id;
        }

        public void setAnswer_title(String answer_title) {
            this.answer_title = answer_title;
        }
        public String getAnswer_title() {
            return answer_title;
        }

    }


    //相关文章
    public static class Relate {

        private String aid;
        private String title;
        private String pic;
        private String author;
        private String created_at;
        private String published_at;
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

        public void setPic(String pic) {
            this.pic = pic;
        }
        public String getPic() {
            return pic;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return author;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
        public String getCreated_at() {
            return created_at;
        }

        public void setPublished_at(String published_at) {
            this.published_at = published_at;
        }
        public String getPublished_at() {
            return published_at;
        }

    }
}
