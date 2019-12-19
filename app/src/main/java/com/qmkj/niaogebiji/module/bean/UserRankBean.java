package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:排行榜bean
 */
public class UserRankBean extends BaseBean {

    private User_info user_info;
    private List<OtherPoint> list;
    public void setUser_info(User_info user_info) {
        this.user_info = user_info;
    }
    public User_info getUser_info() {
        return user_info;
    }

    public List<OtherPoint> getList() {
        return list;
    }

    public void setList(List<OtherPoint> list) {
        this.list = list;
    }

    public class User_info {

        private String uid;
        private String point;
        private String avater;
        private String rank;
        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setPoint(String point) {
            this.point = point;
        }
        public String getPoint() {
            return point;
        }

        public void setAvater(String avater) {
            this.avater = avater;
        }
        public String getAvater() {
            return avater;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }
        public String getRank() {
            return rank;
        }

    }

    public class OtherPoint {

        private String order;
        private String uid;
        private String point;
        private String name;

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setPoint(String point) {
            this.point = point;
        }
        public String getPoint() {
            return point;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

    }


}
