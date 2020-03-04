package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-04
 * 描述:我的界面中推荐商品
 */
public class MyProductBean extends BaseBean {

    private List<FeatherProductBean.Productean.ProductItemBean> list;

    public List<FeatherProductBean.Productean.ProductItemBean> getList() {
        return list;
    }

    public void setList(List<FeatherProductBean.Productean.ProductItemBean> list) {
        this.list = list;
    }
}
