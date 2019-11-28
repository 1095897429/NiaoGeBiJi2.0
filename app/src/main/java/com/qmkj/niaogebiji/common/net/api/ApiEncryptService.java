package com.qmkj.niaogebiji.common.net.api;


import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.ActiclePointBean;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CollectArticleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.FlashOkBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.qmkj.niaogebiji.module.bean.SearchResultBean;
import com.qmkj.niaogebiji.module.bean.TestOkBean;
import com.qmkj.niaogebiji.module.bean.VersionBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-09-12
 * 描述:restful格式
 * aes 加密  post 请求方式params = 加密字符串
 *
 */
public interface ApiEncryptService{

    /* 2019.8.23 快讯模块列表接口  */
    @FormUrlEncoded
    @POST("app/item/getBulletinList")
    Observable<HttpResponse<FlashBulltinBean>> getBulletinList(@Field("params") String param);


    /* 快讯 1 || 评论 2 点赞接口  */
    @FormUrlEncoded
    @POST("app/my/goodBulletin")
    Observable<HttpResponse> goodBulletin(@Field("params") String param);


    /* 快讯 1 || 评论 2 取消点赞接口  */
    @FormUrlEncoded
    @POST("app/my/cancleGoodBulletin")
    Observable<HttpResponse> cancleGoodBulletin(@Field("params") String param);


    /* 2019.8.27   快讯分享接口   */
    @FormUrlEncoded
    @POST("app/item/bulletinShare")
    Observable<HttpResponse<FlashBulltinBean>> bulletinShare(@Field("params") String param);


    /* 2019.8.25 首页【关注】接口 */
    @FormUrlEncoded
    @POST("app/item/getIndexArticle")
    Observable<HttpResponse<IndexFocusBean>> getIndexArticle(@Field("params") String param);



    /* 2019.8.24 作者列表接口  */
    @FormUrlEncoded
    @POST("app/item/authorList")
    Observable<HttpResponse<AuthorBean>> authorList(@Field("params") String param);


    /* 2019.8.26 关注 || 取消关注作者 接口  需登录  已解决*/
    @FormUrlEncoded
    @POST("app/my/followAuthor")
    Observable<HttpResponse<IndexFocusBean>> followAuthor(@Field("params") String param);

    /* 2019.8.29 文章详情页接口 */
    @FormUrlEncoded
    @POST("app/article/detail")
    Observable<HttpResponse<NewsDetailBean>> detail(@Field("params") String param);



    /* 2019.8.29 文章收藏 */
    @FormUrlEncoded
    @POST("app/my/favorite")
    Observable<HttpResponse> acticleFavorite(@Field("params") String param);

    /* 2019.8.29 取消收藏 */
    @FormUrlEncoded
    @POST("app/my/unfavorite")
    Observable<HttpResponse> acticleUnfavorite(@Field("params") String param);


    /* 2019.8.23 活动列表接口  */
    @FormUrlEncoded
    @POST("app/item/activitiesList")
    Observable<HttpResponse<ActionBean>> activitiesList(@Field("params") String param);


    /* 2019.8.26 热搜接口 */
    @FormUrlEncoded
    @POST("app/item/searchIndex")
    Observable<HttpResponse<SearchBean>> searchIndex(@Field("params") String param);


    /* 2019.8.25 搜索结果返回接口 */
    @FormUrlEncoded
    @POST("app/item/searchContent")
    Observable<HttpResponse<SearchResultBean>> searchContent(@Field("params") String param);


    /* 2019.8.29 文章评论列表接口 */
    @FormUrlEncoded
    @POST("app/article/commentList")
    Observable<HttpResponse<CommentBean>> commentList(@Field("params") String param);


    /* 2019.8.29 文章测一测回答*/
    @FormUrlEncoded
    @POST("app/my/answerArticleQa")
    Observable<HttpResponse<TestOkBean>> answerArticleQa(@Field("params") String param);



    /* 2019.8.29 文章资料下载*/
    @FormUrlEncoded
    @POST("app/my/dlArticle")
    Observable<HttpResponse> dlArticle(@Field("params") String param);



    /* 2019.9.23 快讯分享加羽毛*/
    @FormUrlEncoded
    @POST("app/my/addBulletinSharePoint")
    Observable<HttpResponse<FlashOkBean>> addBulletinSharePoint(@Field("params") String param);



    /* 2019.9.2 文章评分*/
    @FormUrlEncoded
    @POST("app/my/addArticlePoint")
    Observable<HttpResponse<ActiclePointBean>> addArticlePoint(@Field("params") String param);


    /* 2019.8.29 评论回复*/
    @FormUrlEncoded
    @POST("app/my/createComment")
    Observable<HttpResponse<CommentOkBean>> createComment(@Field("params") String param);



    /* 2019.8.24 版本更新接口  */
    @FormUrlEncoded
    @POST("app/init/checkupd")
    Observable<HttpResponse<VersionBean>> checkupd(@Field("params") String param);


    /* 2019.9.6 更新用户信息*/
    @FormUrlEncoded
    @POST("app/my/alterinfo")
    Observable<HttpResponse<RegisterLoginBean.UserInfo>> alterinfo(@Field("params") String param);


    /* 2019.11.26 我的关注列表接口  接口有问题  已解决 */
    @FormUrlEncoded
    @POST("app/my/myFollowList")
    Observable<HttpResponse<AuthorBean>> myFollowList(@Field("params") String param);


    /* 2019.11.27 我的收藏列表  有问题*/
    @FormUrlEncoded
    @POST("app/my/favoriteList")
    Observable<HttpResponse<CollectArticleBean>> favoriteList(@Field("params") String param);


    /* 2019.11.27 快讯评论列表接口  */
    @FormUrlEncoded
    @POST("app/item/getCommentList")
    Observable<HttpResponse<CommentBean>> getCommentLis(@Field("params") String param);


    /* 2019.11.28 文章阅读加积分*/
    @FormUrlEncoded
    @POST("app/my/readArticle")
    Observable<HttpResponse<TestOkBean>> readArticle(@Field("params") String param);


}
