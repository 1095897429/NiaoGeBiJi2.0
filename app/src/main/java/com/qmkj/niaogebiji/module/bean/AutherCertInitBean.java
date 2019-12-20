package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-20
 * 描述:职业认证
 */
public class AutherCertInitBean extends BaseBean {

        private int flow_step;
        private String msg;
        private String email;
        private String company_name;
        private String pos;
        private String position;
        private String name;
        public void setFlow_step(int flow_step) {
            this.flow_step = flow_step;
        }
        public int getFlow_step() {
            return flow_step;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
        public String getMsg() {
            return msg;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public String getEmail() {
            return email;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }
        public String getCompany_name() {
            return company_name;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }
        public String getPos() {
            return pos;
        }

        public void setPosition(String position) {
            this.position = position;
        }
        public String getPosition() {
            return position;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }


}
