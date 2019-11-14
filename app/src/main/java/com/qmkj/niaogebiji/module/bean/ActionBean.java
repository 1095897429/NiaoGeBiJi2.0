package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:活动bean
 */
public class ActionBean extends BaseBean {


    private List<Adv_list> adv_list;
    private List<Act_list> act_list;
    public void setAdv_list(List<Adv_list> adv_list) {
        this.adv_list = adv_list;
    }
    public List<Adv_list> getAdv_list() {
        return adv_list;
    }

    public void setAct_list(List<Act_list> act_list) {
        this.act_list = act_list;
    }
    public List<Act_list> getAct_list() {
        return act_list;
    }


    //顶部banner广告数组
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



    //活动列表数组
    public static class Act_list {

        private String id;
        private String catid;
        private String title;
        private String money_type;
        private String real_price;
        private String original_price;
        private String pic;
        private String start_time;
        private String end_time;
        private String search_keywords;
        private String apply_number;
        private String district;
        private String location;
        private String summary;
        private String content;
        private String jump_link;
        private String document_link;
        private String notification;
        private String viewnum;
        private String commentnum;
        private String favnum;
        private String sharenum;
        private String type;
        private String showindex;
        private String showcatlist;
        private String status;
        private String porder;
        private String publishuid;
        private String publishuname;
        private String created_at;
        private String updated_at;
        private int act_status;

        //链接类型，1-活动行，2-文章
        private String link_type;

        //dump_link  :活动行链接  ||   文章id
        private String dump_link;


        public String getLink_type() {
            return link_type;
        }

        public void setLink_type(String link_type) {
            this.link_type = link_type;
        }

        public String getDump_link() {
            return dump_link;
        }

        public void setDump_link(String dump_link) {
            this.dump_link = dump_link;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
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

        public void setMoney_type(String money_type) {
            this.money_type = money_type;
        }
        public String getMoney_type() {
            return money_type;
        }

        public void setReal_price(String real_price) {
            this.real_price = real_price;
        }
        public String getReal_price() {
            return real_price;
        }

        public void setOriginal_price(String original_price) {
            this.original_price = original_price;
        }
        public String getOriginal_price() {
            return original_price;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
        public String getPic() {
            return pic;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }
        public String getStart_time() {
            return start_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }
        public String getEnd_time() {
            return end_time;
        }

        public void setSearch_keywords(String search_keywords) {
            this.search_keywords = search_keywords;
        }
        public String getSearch_keywords() {
            return search_keywords;
        }

        public void setApply_number(String apply_number) {
            this.apply_number = apply_number;
        }
        public String getApply_number() {
            return apply_number;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
        public String getDistrict() {
            return district;
        }

        public void setLocation(String location) {
            this.location = location;
        }
        public String getLocation() {
            return location;
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

        public void setJump_link(String jump_link) {
            this.jump_link = jump_link;
        }
        public String getJump_link() {
            return jump_link;
        }

        public void setDocument_link(String document_link) {
            this.document_link = document_link;
        }
        public String getDocument_link() {
            return document_link;
        }

        public void setNotification(String notification) {
            this.notification = notification;
        }
        public String getNotification() {
            return notification;
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

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setShowindex(String showindex) {
            this.showindex = showindex;
        }
        public String getShowindex() {
            return showindex;
        }

        public void setShowcatlist(String showcatlist) {
            this.showcatlist = showcatlist;
        }
        public String getShowcatlist() {
            return showcatlist;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

        public void setPorder(String porder) {
            this.porder = porder;
        }
        public String getPorder() {
            return porder;
        }

        public void setPublishuid(String publishuid) {
            this.publishuid = publishuid;
        }
        public String getPublishuid() {
            return publishuid;
        }

        public void setPublishuname(String publishuname) {
            this.publishuname = publishuname;
        }
        public String getPublishuname() {
            return publishuname;
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

        public void setAct_status(int act_status) {
            this.act_status = act_status;
        }
        public int getAct_status() {
            return act_status;
        }

    }
}
