package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-09
 * 描述:用户信息带徽章部分
 */
 public class User_info extends BaseBean {

    private String uid;
    private String name;
    private String avatar;
    private String company_name;
    private String position;
    private String auth_com_status;
    private List<Badge> badge;
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public void setBadge(List<Badge> badge) {
        this.badge = badge;
    }
    public List<Badge> getBadge() {
        return badge;
    }


    static public class Badge {


        private String id;
        private String name;
        private String icon;
        private String desc;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
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

    }

}
