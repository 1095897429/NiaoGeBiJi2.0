package com.qmkj.niaogebiji.module.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:搜索结果的多实体
 */
public class MultSearchBean extends BaseBean implements MultiItemEntity {

    //新闻
    private NewsItemBean mNewsItemBean;
    //快讯
    private FirstItemBean mFirstItemBean;
    //关注作者文章
    private IndexFocusBean.Article_list mArticleList;

    //新闻1
    private SearchNewBean mSearchNewBean;

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

    public SearchNewBean getSearchNewBean() {
        return mSearchNewBean;
    }

    public void setSearchNewBean(SearchNewBean searchNewBean) {
        mSearchNewBean = searchNewBean;
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


    public IndexFocusBean.Article_list getArticleList() {
        return mArticleList;
    }

    public void setArticleList(IndexFocusBean.Article_list articleList) {
        mArticleList = articleList;
    }




    public static class SearchNewBean{

        private List<NewsItemBean> mNewsItemBeans;

        public List<NewsItemBean> getNewsItemBeans() {
            return mNewsItemBeans;
        }

        public void setNewsItemBeans(List<NewsItemBean> newsItemBeans) {
            mNewsItemBeans = newsItemBeans;
        }
    }
}
