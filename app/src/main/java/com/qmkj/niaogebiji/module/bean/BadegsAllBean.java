package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-25
 * 描述:
 */
public class BadegsAllBean  extends BaseBean {

    private List<BadegBean> show_badges;
    private List<BadegBean> collect_badges;
    private List<BadegBean> all_badges;

    public List<BadegBean> getShow_badges() {
        return show_badges;
    }

    public void setShow_badges(List<BadegBean> show_badges) {
        this.show_badges = show_badges;
    }

    public List<BadegBean> getCollect_badges() {
        return collect_badges;
    }

    public void setCollect_badges(List<BadegBean> collect_badges) {
        this.collect_badges = collect_badges;
    }

    public List<BadegBean> getAll_badges() {
        return all_badges;
    }

    public void setAll_badges(List<BadegBean> all_badges) {
        this.all_badges = all_badges;
    }

    public static class BadegBean{
        private String id;
        private String name;
        private String icon;
        private String desc;
        private int is_get;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getIs_get() {
            return is_get;
        }

        public void setIs_get(int is_get) {
            this.is_get = is_get;
        }
    }
}
