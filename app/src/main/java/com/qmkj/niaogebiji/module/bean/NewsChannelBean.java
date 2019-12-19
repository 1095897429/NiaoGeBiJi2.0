package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:
 */
public class NewsChannelBean extends BaseBean {

    //所有频道的集合
    private List<AllChannelBean> all_channel;

    public List<AllChannelBean> getAll_channel() {
        return all_channel;
    }

    public void setAll_channel(List<AllChannelBean> all_channel) {
        this.all_channel = all_channel;
    }

    public  static class AllChannelBean extends BaseBean {
        /**
         * chaid : 2
         * chaname : 娱乐
         */
        private String chaid;
        private String chaname;
        private String status;

        public AllChannelBean(String chaid, String chaname) {
            this.chaid = chaid;
            this.chaname = chaname;
        }

        public String getChaid() {
            return chaid;
        }

        public void setChaid(String chaid) {
            this.chaid = chaid;
        }

        public String getChaname() {
            return chaname;
        }

        public void setChaname(String chaname) {
            this.chaname = chaname;
        }


    }
}
