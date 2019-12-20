package com.qmkj.niaogebiji.module.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:圈子动态bean
 *  1.12.20 新增搜索中新增的字段，比如文章
 */
public class CircleBean extends BaseBean {

        private String id;
        private String uid;
        private String blog;
        private List<String> images;
        private String link;
        private String link_title;
        private String type;
        private String pid;
        private String like_num;
        private String show_num;
        private String sort;
        private String created_at;
        //评论数
        private String comment_num;
        private User_info user_info;
        private P_blog p_blog;
        private String share_url;
        //是否已点赞 1是 0否
        private int is_like;
        //内容
        private String comment;

        //兼容搜索
        private String name;
        private String company_name;
        private String position;
        private String is_auth;
        private String article_id;
        private String article_title;
        private String article_image;

        public String getIs_auth() {
            return is_auth;
        }

        public void setIs_auth(String is_auth) {
            this.is_auth = is_auth;
        }

         public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getArticle_id() {
            return article_id;
        }

        public void setArticle_id(String article_id) {
            this.article_id = article_id;
        }

        public String getArticle_title() {
            return article_title;
        }

        public void setArticle_title(String article_title) {
            this.article_title = article_title;
        }

        public String getArticle_image() {
            return article_image;
        }

        public void setArticle_image(String article_image) {
            this.article_image = article_image;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getIs_like() {
            return is_like;
        }

        public void setIs_like(int is_like) {
            this.is_like = is_like;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setBlog(String blog) {
            this.blog = blog;
        }
        public String getBlog() {
            return blog;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
        public List<String> getImages() {
            return images;
        }

        public void setLink(String link) {
            this.link = link;
        }
        public String getLink() {
            return link;
        }

        public void setLink_title(String link_title) {
            this.link_title = link_title;
        }
        public String getLink_title() {
            return link_title;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }
        public String getPid() {
            return pid;
        }

        public void setLike_num(String like_num) {
            this.like_num = like_num;
        }
        public String getLike_num() {
            return like_num;
        }

        public void setShow_num(String show_num) {
            this.show_num = show_num;
        }
        public String getShow_num() {
            return show_num;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }
        public String getSort() {
            return sort;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
        public String getCreated_at() {
            return created_at;
        }

        public void setComment_num(String comment_num) {
            this.comment_num = comment_num;
        }
        public String getComment_num() {
            return comment_num;
        }

        public void setUser_info(User_info user_info) {
            this.user_info = user_info;
        }
        public User_info getUser_info() {
            return user_info;
        }

        public void setP_blog(P_blog p_blog) {
            this.p_blog = p_blog;
        }
        public P_blog getP_blog() {
            return p_blog;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }
        public String getShare_url() {
            return share_url;
        }

   //转发动态详情
   static public class P_blog {
        private String id;
        private String uid;
        private String blog;
        private List<String> images;
        private String link;
        private String link_title;
        private String type;
        private String pid;
        private String like_num;
        private String repost_num;
        private String sort;
        private P_user_info p_user_info;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setBlog(String blog) {
            this.blog = blog;
        }
        public String getBlog() {
            return blog;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
        public List<String> getImages() {
            return images;
        }

        public void setLink(String link) {
            this.link = link;
        }
        public String getLink() {
            return link;
        }

        public void setLink_title(String link_title) {
            this.link_title = link_title;
        }
        public String getLink_title() {
            return link_title;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }
        public String getPid() {
            return pid;
        }

        public void setLike_num(String like_num) {
            this.like_num = like_num;
        }
        public String getLike_num() {
            return like_num;
        }

        public void setRepost_num(String repost_num) {
            this.repost_num = repost_num;
        }
        public String getRepost_num() {
            return repost_num;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }
        public String getSort() {
            return sort;
        }

        public void setP_user_info(P_user_info p_user_info) {
            this.p_user_info = p_user_info;
        }
        public P_user_info getP_user_info() {
            return p_user_info;
        }

    }


   static public class P_user_info {

        private String uid;
        private String name;
        private String avatar;
        private String company_name;
        private String position;
        private String auth_com_status;
        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        public String getAvatar() {
            return avatar;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }
        public String getCompany_name() {
            return company_name;
        }

        public void setPosition(String position) {
            this.position = position;
        }
        public String getPosition() {
            return position;
        }

        public void setAuth_com_status(String auth_com_status) {
            this.auth_com_status = auth_com_status;
        }
        public String getAuth_com_status() {
            return auth_com_status;
        }

    }

}
