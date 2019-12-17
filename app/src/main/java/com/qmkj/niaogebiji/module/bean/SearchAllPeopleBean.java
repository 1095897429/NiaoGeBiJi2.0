package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:全部搜索中的人脉
 */
public class SearchAllPeopleBean extends BaseBean {

    private String num;

    private List<RegisterLoginBean.UserInfo> list;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public List<RegisterLoginBean.UserInfo> getList() {
        return list;
    }

    public void setList(List<RegisterLoginBean.UserInfo> list) {
        this.list = list;
    }
}
