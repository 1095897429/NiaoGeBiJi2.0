package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-16
 * 描述:干货就是之前的首页推荐列表
 */
public class RecommendBean extends BaseBean {


    private List<Article_list> article_list;
    private Adv_list adv_list;
    //是否有新文章
    private String desc_notice;

    public String getDesc_notice() {
        return desc_notice;
    }

    public void setDesc_notice(String desc_notice) {
        this.desc_notice = desc_notice;
    }

    public void setArticle_list(List<Article_list> article_list) {
        this.article_list = article_list;
    }
    public List<Article_list> getArticle_list() {
        return article_list;
    }

    public void setAdv_list(Adv_list adv_list) {
        this.adv_list = adv_list;
    }
    public Adv_list getAdv_list() {
        return adv_list;
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

    //文章列表
    public static class Article_list {

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

        //文章主图2（主要APP使用）
        private String pic2;
        private String pic3;
        //1-单图小，2-单图大，3-三图
        private String pic_type;

        public String getPic2() {
            return pic2;
        }

        public void setPic2(String pic2) {
            this.pic2 = pic2;
        }

        public String getPic3() {
            return pic3;
        }

        public void setPic3(String pic3) {
            this.pic3 = pic3;
        }

        public String getPic_type() {
            return pic_type;
        }

        public void setPic_type(String pic_type) {
            this.pic_type = pic_type;
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

    }

    public class Que_answer_json {

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

    public class Relate {

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
