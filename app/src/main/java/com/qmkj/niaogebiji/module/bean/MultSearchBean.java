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

    //干货 ①
    private List<RecommendBean.Article_list> mNewsItemBeanList;
    //人脉 ②
    private List<RegisterLoginBean.UserInfo> userInfos;
    //动态 ③
    private List<CircleBean> mCircleBeanList;
    //百科
    private List<SearchAllBaiduBean.Wiki> wikis;
    //资料
    private List<RecommendBean.Article_list> mThings;
    //作者
    private List<AuthorBean.Author> mAuthorBeanList;



    //快讯
    private List<FlashBulltinBean.BuilltinBean> mFlashBulltinBeanList;

    //工具
    private List<ToolBean> mToolBeanList;
    //活动
    private List<ActionBean.Act_list> mActionBeanList;


    //课程
    private List<CourseBean> mCourseBeanList;
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


    public List<RecommendBean.Article_list> getNewsItemBeanList() {
        return mNewsItemBeanList;
    }

    public void setNewsItemBeanList(List<RecommendBean.Article_list> newsItemBeanList) {
        mNewsItemBeanList = newsItemBeanList;
    }

    public List<FlashBulltinBean.BuilltinBean> getFlashBulltinBeanList() {
        return mFlashBulltinBeanList;
    }

    public void setFlashBulltinBeanList(List<FlashBulltinBean.BuilltinBean> flashBulltinBeanList) {
        mFlashBulltinBeanList = flashBulltinBeanList;
    }

    public List<RecommendBean.Article_list> getThings() {
        return mThings;
    }

    public void setThings(List<RecommendBean.Article_list> things) {
        mThings = things;
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

    public List<AuthorBean.Author> getAuthorBeanList() {
        return mAuthorBeanList;
    }

    public void setAuthorBeanList(List<AuthorBean.Author> authorBeanList) {
        mAuthorBeanList = authorBeanList;
    }

    public List<RegisterLoginBean.UserInfo> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<RegisterLoginBean.UserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    public List<CourseBean> getCourseBeanList() {
        return mCourseBeanList;
    }

    public void setCourseBeanList(List<CourseBean> courseBeanList) {
        mCourseBeanList = courseBeanList;
    }

    public List<SearchAllBaiduBean.Wiki> getWikis() {
        return wikis;
    }

    public void setWikis(List<SearchAllBaiduBean.Wiki> wikis) {
        this.wikis = wikis;
    }

    public List<TestBean> getTestBeanList() {
        return mTestBeanList;
    }

    public void setTestBeanList(List<TestBean> testBeanList) {
        mTestBeanList = testBeanList;
    }
}
