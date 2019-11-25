package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:版本信息
 */
public class VersionBean extends BaseBean {

    private List<Version> list;

    public List<Version> getList() {
        return list;
    }

    public void setList(List<Version> list) {
        this.list = list;
    }

    public static class Version{
        private String version_number;
        private String status;
        private String version_code;
        private String url;
        private String force_update_flag;
        private String update_desc;
        private String name;

        public String getVersion_number() {
            return version_number;
        }

        public void setVersion_number(String version_number) {
            this.version_number = version_number;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getVersion_code() {
            return version_code;
        }

        public void setVersion_code(String version_code) {
            this.version_code = version_code;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getForce_update_flag() {
            return force_update_flag;
        }

        public void setForce_update_flag(String force_update_flag) {
            this.force_update_flag = force_update_flag;
        }

        public String getUpdate_desc() {
            return update_desc;
        }

        public void setUpdate_desc(String update_desc) {
            this.update_desc = update_desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
