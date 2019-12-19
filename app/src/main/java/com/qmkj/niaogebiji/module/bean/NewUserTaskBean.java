package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:羽毛任务新手任务
 */
public class NewUserTaskBean extends BaseBean {

    private int is_hide;
    private List<NewTaskBean> list;

    public int getIs_hide() {
        return is_hide;
    }

    public void setIs_hide(int is_hide) {
        this.is_hide = is_hide;
    }

    public List<NewTaskBean> getList() {
        return list;
    }

    public void setList(List<NewTaskBean> list) {
        this.list = list;
    }

    public static class NewTaskBean extends BaseBean{
        private String id;
        private String title;
        private String desc;
        private String point;
        private String type;
        // 0-去完成，1-去领取，2-已领取
        private String status;
        private String icon;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
