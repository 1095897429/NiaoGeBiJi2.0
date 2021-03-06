package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:评论实体
 */
public class CommentBean extends BaseBean {

    private List<FirstComment> list;

    public void setList(List<FirstComment> list) {
        this.list = list;
    }
    public List<FirstComment> getList() {
        return list;
    }


    //一级评论
    public static class FirstComment {

        //TODO 2019.11.21 集合内容
        private String cid;
        private String uid;
        private String username;
        private String message;
        private String topcommentid;
        private String replyid;
        private String replyed_uid;
        private String replyed_username;
        private String commented_username;
        private String dateline;
        //是否点赞
        private int is_good;
        private String good_num;
        private String avatar;
        private String company_name;
        private String position;
        //职位认证状态：1-已通过，0-未通过
        private String auth_status;

        //每个textview的行数
        private int lines;

        public int getLines() {
            return lines;
        }

        public void setLines(int lines) {
            this.lines = lines;
        }

        public String getAuth_status() {
            return auth_status;
        }

        public void setAuth_status(String auth_status) {
            this.auth_status = auth_status;
        }

        private String relatedid;
        //二级评论
        private List<FirstComment> commentslist;

        public String getReplyed_uid() {
            return replyed_uid;
        }

        public void setReplyed_uid(String replyed_uid) {
            this.replyed_uid = replyed_uid;
        }

        public String getRelatedid() {
            return relatedid;
        }

        public void setRelatedid(String relatedid) {
            this.relatedid = relatedid;
        }

        public String getReplyed_username() {
            return replyed_username;
        }

        public void setReplyed_username(String replyed_username) {
            this.replyed_username = replyed_username;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }
        public String getCid() {
            return cid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setUsername(String username) {
            this.username = username;
        }
        public String getUsername() {
            return username;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }

        public void setTopcommentid(String topcommentid) {
            this.topcommentid = topcommentid;
        }
        public String getTopcommentid() {
            return topcommentid;
        }

        public void setReplyid(String replyid) {
            this.replyid = replyid;
        }
        public String getReplyid() {
            return replyid;
        }

        public void setCommented_username(String commented_username) {
            this.commented_username = commented_username;
        }
        public String getCommented_username() {
            return commented_username;
        }

        public void setDateline(String dateline) {
            this.dateline = dateline;
        }
        public String getDateline() {
            return dateline;
        }

        public void setIs_good(int is_good) {
            this.is_good = is_good;
        }
        public int getIs_good() {
            return is_good;
        }

        public void setGood_num(String good_num) {
            this.good_num = good_num;
        }
        public String getGood_num() {
            return good_num;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        public String getAvatar() {
            return avatar;
        }

        public List<FirstComment> getCommentslist() {
            return commentslist;
        }

        public void setCommentslist(List<FirstComment> commentslist) {
            this.commentslist = commentslist;
        }
    }

    //二级评论
    public static class SecondComment {
        //TODO 2019.11.21 集合内容
        private String cid;
        private String uid;
        private String username;
        private String message;
        private String topcommentid;
        private String replyid;
        private String replyed_username;
        private String commented_username;
        private String dateline;
        private int is_good;
        private String good_num;
        private String avatar;
        private String company_name;
        private String position;
        private List<String> commentslist;


        public String getReplyed_username() {
            return replyed_username;
        }

        public void setReplyed_username(String replyed_username) {
            this.replyed_username = replyed_username;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }
        public String getCid() {
            return cid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setUsername(String username) {
            this.username = username;
        }
        public String getUsername() {
            return username;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }

        public void setTopcommentid(String topcommentid) {
            this.topcommentid = topcommentid;
        }
        public String getTopcommentid() {
            return topcommentid;
        }

        public void setReplyid(String replyid) {
            this.replyid = replyid;
        }
        public String getReplyid() {
            return replyid;
        }

        public void setCommented_username(String commented_username) {
            this.commented_username = commented_username;
        }
        public String getCommented_username() {
            return commented_username;
        }

        public void setDateline(String dateline) {
            this.dateline = dateline;
        }
        public String getDateline() {
            return dateline;
        }

        public void setIs_good(int is_good) {
            this.is_good = is_good;
        }
        public int getIs_good() {
            return is_good;
        }

        public void setGood_num(String good_num) {
            this.good_num = good_num;
        }
        public String getGood_num() {
            return good_num;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        public String getAvatar() {
            return avatar;
        }

        public void setCommentslist(List<String> commentslist) {
            this.commentslist = commentslist;
        }
        public List<String> getCommentslist() {
            return commentslist;
        }

    }
}
