package com.qmkj.niaogebiji.module.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qmkj.niaogebiji.common.base.BaseBean;
import com.qmkj.niaogebiji.module.activity.DataInfomationActivity;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:搜索结果的多实体
 */
public class MultSearchBean extends BaseBean implements MultiItemEntity {

    //干货
    private List<NewsItemBean> mNewsItemBeanList;
    //快讯
    private List<FlashBulltinBean.BuilltinBean> mFlashBulltinBeanList;
    //资料
    private List<NewsItemBean> mDataItemBeanList;
    //工具
    private List<ToolBean> mToolBeanList;
    //活动
    private List<ActionBean.Act_list> mActionBeanList;
    //动态
    private List<CircleBean> mCircleBeanList;
    //作者
    private List<AuthorBean> mAuthorBeanList;
    //人脉
    private List<PersonConnectionBean> mPersonConnectionBeanList;
    //课程
    private List<CourseBean> mCourseBeanList;
    //百科
    private List<BaiduBean> mBaiduBeanList;
    //测试
    private List<TestBean> mTestBeanList;
    //类型
    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }


    public List<NewsItemBean> getNewsItemBeanList() {
        return mNewsItemBeanList;
    }

    public void setNewsItemBeanList(List<NewsItemBean> newsItemBeanList) {
        mNewsItemBeanList = newsItemBeanList;
    }

    public List<FlashBulltinBean.BuilltinBean> getFlashBulltinBeanList() {
        return mFlashBulltinBeanList;
    }

    public void setFlashBulltinBeanList(List<FlashBulltinBean.BuilltinBean> flashBulltinBeanList) {
        mFlashBulltinBeanList = flashBulltinBeanList;
    }

    public List<NewsItemBean> getDataItemBeanList() {
        return mDataItemBeanList;
    }

    public void setDataItemBeanList(List<NewsItemBean> dataItemBeanList) {
        mDataItemBeanList = dataItemBeanList;
    }

    public List<ToolBean> getToolBeanList() {
        return mToolBeanList;
    }

    public void setToolBeanList(List<ToolBean> toolBeanList) {
        mToolBeanList = toolBeanList;
    }

    public List<ActionBean.Act_list> getActionBeanList() {
        return mActionBeanList;
    }

    public void setActionBeanList(List<ActionBean.Act_list> actionBeanList) {
        mActionBeanList = actionBeanList;
    }

    public List<CircleBean> getCircleBeanList() {
        return mCircleBeanList;
    }

    public void setCircleBeanList(List<CircleBean> circleBeanList) {
        mCircleBeanList = circleBeanList;
    }

    public List<AuthorBean> getAuthorBeanList() {
        return mAuthorBeanList;
    }

    public void setAuthorBeanList(List<AuthorBean> authorBeanList) {
        mAuthorBeanList = authorBeanList;
    }

    public List<PersonConnectionBean> getPersonConnectionBeanList() {
        return mPersonConnectionBeanList;
    }

    public void setPersonConnectionBeanList(List<PersonConnectionBean> personConnectionBeanList) {
        mPersonConnectionBeanList = personConnectionBeanList;
    }

    public List<CourseBean> getCourseBeanList() {
        return mCourseBeanList;
    }

    public void setCourseBeanList(List<CourseBean> courseBeanList) {
        mCourseBeanList = courseBeanList;
    }

    public List<BaiduBean> getBaiduBeanList() {
        return mBaiduBeanList;
    }

    public void setBaiduBeanList(List<BaiduBean> baiduBeanList) {
        mBaiduBeanList = baiduBeanList;
    }

    public List<TestBean> getTestBeanList() {
        return mTestBeanList;
    }

    public void setTestBeanList(List<TestBean> testBeanList) {
        mTestBeanList = testBeanList;
    }
}
