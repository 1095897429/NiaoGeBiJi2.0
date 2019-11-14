package com.qmkj.niaogebiji.module.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:首页新闻的多实体
 */
public class MultiNewsBean extends BaseBean implements MultiItemEntity {

    //首页新闻
    private NewsItemBean mNewsItemBean;
    //首页快讯
    private FirstItemBean mFirstItemBean;
    //首页关注作者
//    private IndexFocusBean.Auther_list mAutherList;
    private FouBBBB mFouBBBB;
    //首页关注作者文章
    private IndexFocusBean.Article_list mArticleList;

    //类型
    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }


    public NewsItemBean getNewsItemBean() {
        return mNewsItemBean;
    }

    public void setNewsItemBean(NewsItemBean newsItemBean) {
        mNewsItemBean = newsItemBean;
    }

    public FirstItemBean getFirstItemBean() {
        return mFirstItemBean;
    }

    public void setFirstItemBean(FirstItemBean firstItemBean) {
        mFirstItemBean = firstItemBean;
    }


//    public IndexFocusBean.Auther_list getAutherList() {
//        return mAutherList;
//    }
//
//    public void setAutherList(IndexFocusBean.Auther_list autherList) {
//        mAutherList = autherList;
//    }


    public FouBBBB getFouBBBB() {
        return mFouBBBB;
    }

    public void setFouBBBB(FouBBBB fouBBBB) {
        mFouBBBB = fouBBBB;
    }

    public IndexFocusBean.Article_list getArticleList() {
        return mArticleList;
    }

    public void setArticleList(IndexFocusBean.Article_list articleList) {
        mArticleList = articleList;
    }
}
