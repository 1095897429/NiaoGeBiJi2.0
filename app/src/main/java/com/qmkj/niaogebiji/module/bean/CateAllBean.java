package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:所有文章分类
 */
public class CateAllBean extends BaseBean {

    private List<CateBean> list;

    public List<CateBean> getList() {
        return list;
    }

    public void setList(List<CateBean> list) {
        this.list = list;
    }

    public static class CateBean{
        private int catid;
        private String cat;


        public int getCatid() {
            return catid;
        }

        public void setCatid(int catid) {
            this.catid = catid;
        }

        public String getCat() {
            return cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }
    }


}
