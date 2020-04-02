package com.qmkj.niaogebiji.module.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-04-02
 * 描述:多布局的实体
 */
public class CourseMultiBean extends BaseBean implements MultiItemEntity {

    private BannerBean mBannerBean;

    private CourserRecommendBean mCoursrRecommendBean;

    private TestInfoBean mTestInfoBean;

    private CourserCategoryBean mCourserCategoryBean;

    private CourserNormalBean mCourserNormalBean;


    public BannerBean getBannerBean() {
        return mBannerBean;
    }

    public void setBannerBean(BannerBean bannerBean) {
        mBannerBean = bannerBean;
    }

    public CourserRecommendBean getCoursrRecommendBean() {
        return mCoursrRecommendBean;
    }

    public void setCoursrRecommendBean(CourserRecommendBean coursrRecommendBean) {
        mCoursrRecommendBean = coursrRecommendBean;
    }

    public TestInfoBean getTestInfoBean() {
        return mTestInfoBean;
    }

    public void setTestInfoBean(TestInfoBean testInfoBean) {
        mTestInfoBean = testInfoBean;
    }

    public CourserCategoryBean getCourserCategoryBean() {
        return mCourserCategoryBean;
    }

    public void setCourserCategoryBean(CourserCategoryBean courserCategoryBean) {
        mCourserCategoryBean = courserCategoryBean;
    }

    public CourserNormalBean getCourserNormalBean() {
        return mCourserNormalBean;
    }

    public void setCourserNormalBean(CourserNormalBean courserNormalBean) {
        mCourserNormalBean = courserNormalBean;
    }

    private int type;

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return type;
    }


    //banner对象
    public static class BannerBean extends BaseBean{

    }

    //推荐对象
    public static class CourserRecommendBean extends BaseBean{

    }

    //测一测 资料下载 名词解析
    public static class TestInfoBean extends BaseBean{

    }
    //课程分类
    public static class CourserCategoryBean extends BaseBean{

    }


    //课程列表
    public static class CourserNormalBean extends BaseBean{

    }

}
