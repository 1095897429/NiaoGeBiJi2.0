package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-25
 * 描述:
 */
public class AuthorArticleBean extends BaseBean {

    private List<RecommendBean.Article_list> list;

    public List<RecommendBean.Article_list> getList() {
        return list;
    }

    public void setList(List<RecommendBean.Article_list> list) {
        this.list = list;
    }
}
