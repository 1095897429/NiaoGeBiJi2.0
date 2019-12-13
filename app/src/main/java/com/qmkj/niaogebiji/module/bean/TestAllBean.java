package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:一个课时下所有答案的题目
 */
public class TestAllBean extends BaseBean {

    private List<TestNewBean> mList;

    public List<TestNewBean> getList() {
        return mList;
    }

    public void setList(List<TestNewBean> list) {
        mList = list;
    }
}
