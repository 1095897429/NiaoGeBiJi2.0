package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:学院bean
 */
public class SchoolBean extends BaseBean {

    //百科
    public static class SchoolBaiDu{

        private int img;
        private String name;

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    //测一测
    public static class SchoolTest{
    }

    //课程
    public static class SchoolBook{
    }
}
