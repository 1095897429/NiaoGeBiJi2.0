package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-09
 * 描述:评论新实体
 */
public class CommentBeanNew extends BaseBean {

    private String puid;
    private User_info p_user_info;

    private String id;
    private String uid;
    private String comment;
    private String like_num;
    private String created_at;
    private User_info user_info;
    private String comment_num;
    private String comment_class;
    //是否已点赞 1是 0否
    private int is_like;
    private List<SecondComment> comment_comment;

    public int getIs_like() {
        return is_like;
    }

    public void setIs_like(int is_like) {
        this.is_like = is_like;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public User_info getP_user_info() {
        return p_user_info;
    }

    public void setP_user_info(User_info p_user_info) {
        this.p_user_info = p_user_info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLike_num() {
        return like_num;
    }

    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public User_info getUser_info() {
        return user_info;
    }

    public void setUser_info(User_info user_info) {
        this.user_info = user_info;
    }

    public String getComment_num() {
        return comment_num;
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }

    public String getComment_class() {
        return comment_class;
    }

    public void setComment_class(String comment_class) {
        this.comment_class = comment_class;
    }

    public List<SecondComment> getComment_comment() {
        return comment_comment;
    }

    public void setComment_comment(List<SecondComment> comment_comment) {
        this.comment_comment = comment_comment;
    }

    //二级评论
    public static class SecondComment {
        private String id;
        private String uid;
        private String puid;
        private String comment;
        private String like_num;
        private String create_at;
        private String comment_class;
        private User_info user_info;
        private User_info p_user_info;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPuid() {
            return puid;
        }

        public void setPuid(String puid) {
            this.puid = puid;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getLike_num() {
            return like_num;
        }

        public void setLike_num(String like_num) {
            this.like_num = like_num;
        }

        public String getCreate_at() {
            return create_at;
        }

        public void setCreate_at(String create_at) {
            this.create_at = create_at;
        }

        public String getComment_class() {
            return comment_class;
        }

        public void setComment_class(String comment_class) {
            this.comment_class = comment_class;
        }

        public User_info getUser_info() {
            return user_info;
        }

        public void setUser_info(User_info user_info) {
            this.user_info = user_info;
        }

        public User_info getP_user_info() {
            return p_user_info;
        }

        public void setP_user_info(User_info p_user_info) {
            this.p_user_info = p_user_info;
        }
    }
}
