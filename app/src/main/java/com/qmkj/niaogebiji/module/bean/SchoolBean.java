package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:学院bean
 */
public class SchoolBean extends BaseBean {

    private List<SchoolBaiDu> wiki;

    private List<SchoolTest> cates;

    private List<SchoolBook> course;


    public List<SchoolBaiDu> getWiki() {
        return wiki;
    }

    public void setWiki(List<SchoolBaiDu> wiki) {
        this.wiki = wiki;
    }

    public List<SchoolTest> getCates() {
        return cates;
    }

    public void setCates(List<SchoolTest> cates) {
        this.cates = cates;
    }

    public List<SchoolBook> getCourse() {
        return course;
    }

    public void setCourse(List<SchoolBook> course) {
        this.course = course;
    }

    //百科
    public static class SchoolBaiDu extends BaseBean{

        private String title;
        private String icon;
        private int cate_id;
        private String desc;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getCate_id() {
            return cate_id;
        }

        public void setCate_id(int cate_id) {
            this.cate_id = cate_id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    //测一测
    public static class SchoolTest extends BaseBean{
        private String id;
        private String title;
        // 是否展示限时标志 1是 0否
        private String is_limit_time;
        private String num;
        private String badge_id;
        private String icon;
        private String desc;
        private String time;
        private String question_num;
        private String per_score;
        private String pass_score;
        private String comment;
        private Record record;
        private int is_show_badge;
        private String share_url;
        //自己加的分数 -- 用于测试完判断
        private String myScore;


        //分享数据
        private String share_title;
        private String share_content;
        private String share_icon;
        //朋友圈分享内容
        private String moments_share_title;


        public String getShare_title() {
            return share_title;
        }

        public void setShare_title(String share_title) {
            this.share_title = share_title;
        }

        public String getShare_content() {
            return share_content;
        }

        public void setShare_content(String share_content) {
            this.share_content = share_content;
        }

        public String getShare_icon() {
            return share_icon;
        }

        public void setShare_icon(String share_icon) {
            this.share_icon = share_icon;
        }

        public String getMoments_share_title() {
            return moments_share_title;
        }

        public void setMoments_share_title(String moments_share_title) {
            this.moments_share_title = moments_share_title;
        }

        public String getMyScore() {
            return myScore;
        }

        public void setMyScore(String myScore) {
            this.myScore = myScore;
        }

        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public String getPass_score() {
            return pass_score;
        }

        public void setPass_score(String pass_score) {
            this.pass_score = pass_score;
        }

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

        public void setIs_limit_time(String is_limit_time) {
            this.is_limit_time = is_limit_time;
        }
        public String getIs_limit_time() {
            return is_limit_time;
        }

        public void setNum(String num) {
            this.num = num;
        }
        public String getNum() {
            return num;
        }

        public void setBadge_id(String badge_id) {
            this.badge_id = badge_id;
        }
        public String getBadge_id() {
            return badge_id;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }

        public void setTime(String time) {
            this.time = time;
        }
        public String getTime() {
            return time;
        }

        public void setQuestion_num(String question_num) {
            this.question_num = question_num;
        }
        public String getQuestion_num() {
            return question_num;
        }

        public void setPer_score(String per_score) {
            this.per_score = per_score;
        }
        public String getPer_score() {
            return per_score;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
        public String getComment() {
            return comment;
        }

        public void setRecord(Record record) {
            this.record = record;
        }
        public Record getRecord() {
            return record;
        }

        public void setIs_show_badge(int is_show_badge) {
            this.is_show_badge = is_show_badge;
        }
        public int getIs_show_badge() {
            return is_show_badge;
        }
    }


    public class Record extends BaseBean{
        // 是否已参加过测试 1是 0否
        private int is_tested;
        private String score;

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public void setIs_tested(int is_tested) {
            this.is_tested = is_tested;
        }
        public int getIs_tested() {
            return is_tested;
        }



    }

    //课程
    public static class SchoolBook extends BaseBean{
        private String id;
        private String image_url;
        private String title;
        private String price;
        private String num;
        private String link;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
