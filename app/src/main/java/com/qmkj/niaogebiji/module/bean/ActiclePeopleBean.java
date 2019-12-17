package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:人脉 + 文章
 */
public class ActiclePeopleBean extends BaseBean {

        private List<RecommendBean.Article_list> article_list;
        private List<RegisterLoginBean.UserInfo> renmai_list;

        public List<RecommendBean.Article_list> getArticle_list() {
                return article_list;
        }

        public void setArticle_list(List<RecommendBean.Article_list> article_list) {
                this.article_list = article_list;
        }

        public List<RegisterLoginBean.UserInfo> getRenmai_list() {
                return renmai_list;
        }

        public void setRenmai_list(List<RegisterLoginBean.UserInfo> renmai_list) {
                this.renmai_list = renmai_list;
        }
}
