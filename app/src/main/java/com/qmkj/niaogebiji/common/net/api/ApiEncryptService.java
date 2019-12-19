package com.qmkj.niaogebiji.common.net.api;


import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.ActicleAllBean;
import com.qmkj.niaogebiji.module.bean.ActiclePeopleBean;
import com.qmkj.niaogebiji.module.bean.ActiclePointBean;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.AppointmentBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CateAllBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CollectArticleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.ExchageDetailBean;
import com.qmkj.niaogebiji.module.bean.FeatherProductBean;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.FlashOkBean;
import com.qmkj.niaogebiji.module.bean.IncomeBean;
import com.qmkj.niaogebiji.module.bean.IndexBulltin;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.IsPhoneBindBean;
import com.qmkj.niaogebiji.module.bean.MoringAllBean;
import com.qmkj.niaogebiji.module.bean.MoringIndexBean;
import com.qmkj.niaogebiji.module.bean.NewPointTaskBean;
import com.qmkj.niaogebiji.module.bean.NewUserTaskBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.OfficialBean;
import com.qmkj.niaogebiji.module.bean.PersonUserInfoBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.QINiuTokenBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.SearchAllActicleBean;
import com.qmkj.niaogebiji.module.bean.SearchAllAuthorBean;
import com.qmkj.niaogebiji.module.bean.SearchAllBaiduBean;
import com.qmkj.niaogebiji.module.bean.SearchAllCircleBean;
import com.qmkj.niaogebiji.module.bean.SearchAllPeopleBean;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.qmkj.niaogebiji.module.bean.SearchResultBean;
import com.qmkj.niaogebiji.module.bean.TestNewBean;
import com.qmkj.niaogebiji.module.bean.TestOkBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;
import com.qmkj.niaogebiji.module.bean.UserRankBean;
import com.qmkj.niaogebiji.module.bean.VersionBean;

import java.util.List;

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


    /* 2019.12.09 微信登录接口 判断此微信已把绑定*/
    @FormUrlEncoded
    @POST("app/auth/wechatlogin")
    Observable<HttpResponse<RegisterLoginBean.UserInfo>> wechatlogin(@Field("params") String param);


    /* 2019.12.9 推荐动态 */
    @FormUrlEncoded
    @POST("app/blog/recommendBlogList")
    Observable<HttpResponse<List<CircleBean>>> recommendBlogList(@Field("params") String param);


    /* 2019.12.9 关注动态 */
    @FormUrlEncoded
    @POST("app/blog/followBlogList")
    Observable<HttpResponse<List<CircleBean>>> followBlogList(@Field("params") String param);


    /* 2019.12.9 发布动态  -- ok*/
    @FormUrlEncoded
    @POST("app/blog/createBlog")
    Observable<HttpResponse> createBlog(@Field("params") String param);




    /* 2019.12.9 动态详情 上面部分 */
    @FormUrlEncoded
    @POST("app/blog/blogDetail")
    Observable<HttpResponse<CircleBean>> blogDetail(@Field("params") String param);


    /* 2019.12.9 动态详情 一级评论列表 */
    @FormUrlEncoded
    @POST("app/blog/getBlogComment")
    Observable<HttpResponse<List<CommentBeanNew>>> getBlogComment(@Field("params") String param);

    /* 2019.12.9 获取七牛云上传token ok */
    @FormUrlEncoded
    @POST("app/blog/getUploadToken")
    Observable<HttpResponse<QINiuTokenBean>> getUploadToken(@Field("params") String param);


    /* 2019.12.12 动态举报 ok */
    @FormUrlEncoded
    @POST("app/blog/reportBlog")
    Observable<HttpResponse> reportBlog(@Field("params") String param);



    /* 2019.12.12 删除评论 */
    @FormUrlEncoded
    @POST("app/blog/deleteBlog")
    Observable<HttpResponse> deleteBlog(@Field("params") String param);

    /* 2019.12.12 点赞 取消点赞 - 圈子 */
    @FormUrlEncoded
    @POST("app/blog/likeBlog")
    Observable<HttpResponse> likeBlog(@Field("params") String param);

    /* 2019.12.12 点赞 取消点赞 - 评论 */
    @FormUrlEncoded
    @POST("app/blog/likeComment")
    Observable<HttpResponse> likeComment(@Field("params") String param);






    /* 2019.12.12 学院首页 */
    @FormUrlEncoded
    @POST("app/academy/index")
    Observable<HttpResponse<SchoolBean>> index(@Field("params") String param);


    /* 2019.12.13 学院测试列表 */
    @FormUrlEncoded
    @POST("app/academy/testCateList")
    Observable<HttpResponse<List<SchoolBean.SchoolTest>>> testCateList(@Field("params") String param);

    /* 2019.12.13 学院测试题目 */
    @FormUrlEncoded
    @POST("app/academy/getTestQuestions")
    Observable<HttpResponse<List<TestNewBean>>> getTestQuestions(@Field("params") String param);


    /* 2019.12.13 学院测试交卷 */
    @FormUrlEncoded
    @POST("app/academy/recordTest")
    Observable<HttpResponse> recordTest(@Field("params") String param);

    /* 2019.12.13 学院预约考试 */
    @FormUrlEncoded
    @POST("app/academy/reserveTest")
    Observable<HttpResponse<AppointmentBean>> reserveTest(@Field("params") String param);

    /* 2019.12.13 首页推荐工具 */
    @FormUrlEncoded
    @POST("app/tool/index")
    Observable<HttpResponse<List<ToollndexBean>>> toolindex(@Field("params") String param);

    /* 2019.12.13 推荐工具列表 */
    @FormUrlEncoded
    @POST("app/tool/list")
    Observable<HttpResponse<List<ToolBean>>> toollist(@Field("params") String param);

    /* 2019.12.13 收藏工具 */
    @FormUrlEncoded
    @POST("app/tool/collect")
    Observable<HttpResponse> collect(@Field("params") String param);

    /* 2019.12.13 取消收藏工具 */
    @FormUrlEncoded
    @POST("app/tool/cancelCollect")
    Observable<HttpResponse> cancelCollect(@Field("params") String param);


    /* 2019.12.13 收藏工具列表 */
    @FormUrlEncoded
    @POST("app/tool/collectionList")
    Observable<HttpResponse<List<ToolBean>>> collectionList(@Field("params") String param);


    /* 2019.12.13 工具分类 */
    @FormUrlEncoded
    @POST("app/tool/getToolCate")
    Observable<HttpResponse<List<ToollndexBean>>> getToolCate(@Field("params") String param);


    /* 2019.12.13 搜索工具列表 */
    @FormUrlEncoded
    @POST("app/tool/searchTool")
    Observable<HttpResponse<List<ToolBean>>> searchTool(@Field("params") String param);



    /* 2019.12.14 获取客服微信图接口  - ok */
    @FormUrlEncoded
    @POST("app/init/getServiceWechatPic")
    Observable<HttpResponse<OfficialBean>> getServiceWechatPic(@Field("params") String param);


    /* 2019.12.14 获取首页顶部早报（只拿今天的，否则为空） - ok*/
    @FormUrlEncoded
    @POST("app/article/getTopPost")
    Observable<HttpResponse<MoringIndexBean>> getTopPost(@Field("params") String param);



    /* 2019.12.14 早报列表接口 - ok */
    @FormUrlEncoded
    @POST("app/article/mplist")
    Observable<HttpResponse<MoringAllBean>> mplist(@Field("params") String param);



    /* 2019.12.14 文章详情页接口 */
    @FormUrlEncoded
    @POST("app/article/detail")
    Observable<HttpResponse<NewsDetailBean>> detail(@Field("params") String param);



    /* 2019.12.15  首页快讯滚动列表  */
    @FormUrlEncoded
    @POST("app/item/getIndexBulltin")
    Observable<HttpResponse<IndexBulltin>> getIndexBulltin(@Field("params") String param);


    /* 2019.12.15 获取用户信息*/
    @FormUrlEncoded
    @POST("app/my/getUserInfo")
    Observable<HttpResponse<RegisterLoginBean.UserInfo>> getUserInfo(@Field("params") String param);


    /* 2019.12.16 动态评论详情*/
    @FormUrlEncoded
    @POST("app/blog/blogCommentDetail")
    Observable<HttpResponse<CommentBeanNew>> blogCommentDetail(@Field("params") String param);


    /* 2019.12.16 二级评论列表 */
    @FormUrlEncoded
    @POST("app/blog/getCommentComment")
    Observable<HttpResponse<List<CommentBeanNew>>> getCommentComment(@Field("params") String param);

    /* 2019.12.16 猜你喜欢 */
    @FormUrlEncoded
    @POST("app/item/recommendAuthorArticleList")
    Observable<HttpResponse<IndexFocusBean>> recommendAuthorArticleList(@Field("params") String param);



    /* 2019.12.16 首页干货   */
    @FormUrlEncoded
    @POST("app/article/recommendlist")
    Observable<HttpResponse<RecommendBean>> recommendlist(@Field("params") String param);



    /* 2019.12.17 文章评论列表接口 */
    @FormUrlEncoded
    @POST("app/article/commentList")
    Observable<HttpResponse<CommentBean>> commentList(@Field("params") String param);

    /* 2019.12.17 文章评论回复*/
    @FormUrlEncoded
    @POST("app/my/createComment")
    Observable<HttpResponse<CommentOkBean>> createComment(@Field("params") String param);


    /* 2019.12.17 微信授权后，设置新的手机号之前，检验手机号是否注册过*/
    @FormUrlEncoded
    @POST("app/auth/isMobileReg")
    Observable<HttpResponse<IsPhoneBindBean>> isMobileReg(@Field("params") String param);


    /* 2019.12.17 发送验证码*/
    @FormUrlEncoded
    @POST("app/auth/sendverifycode")
    Observable<HttpResponse> sendverifycode(@Field("params") String param);


    /* 2019.12.17 使用手机验证码登录  */
    @FormUrlEncoded
    @POST("app/auth/loginViaCode")
    Observable<HttpResponse<RegisterLoginBean.UserInfo>> loginViaCode(@Field("params") String param);

    /* 2019.12.17 动态单独评论 */
    @FormUrlEncoded
    @POST("app/blog/createBlogComment")
    Observable<HttpResponse> createBlogComment(@Field("params") String param);

    /* 2019.12.17 评论评论 */
    @FormUrlEncoded
    @POST("app/blog/createCommentComment")
    Observable<HttpResponse> createCommentComment(@Field("params") String param);

    /* 2019.12.17 搜索－文章&人脉 */
    @FormUrlEncoded
    @POST("app/item/searchArticlePeople")
    Observable<HttpResponse<ActiclePeopleBean>> searchArticlePeople(@Field("params") String param);

    /* 2019.12.17 搜索－文章 */
    @FormUrlEncoded
    @POST("app/item/searchArticle")
    Observable<HttpResponse<SearchAllActicleBean>> searchArticle(@Field("params") String param);

    /* 2019.12.17 搜索－人脉 */
    @FormUrlEncoded
    @POST("app/item/searchPeople")
    Observable<HttpResponse<SearchAllPeopleBean>> searchPeople(@Field("params") String param);

    /* 2019.12.17 搜索－动态 */
    @FormUrlEncoded
    @POST("app/item/searchBlog")
    Observable<HttpResponse<SearchAllCircleBean>> searchBlog(@Field("params") String param);

    /* 2019.12.17 搜索－百科 */
    @FormUrlEncoded
    @POST("app/item/searchWiki")
    Observable<HttpResponse<SearchAllBaiduBean>> searchWiki(@Field("params") String param);

    /* 2019.12.17 搜索－资料 */
    @FormUrlEncoded
    @POST("app/item/searchMaterial")
    Observable<HttpResponse<SearchAllActicleBean>> searchMaterial(@Field("params") String param);

    /* 2019.12.17 搜索－作者 */
    @FormUrlEncoded
    @POST("app/item/searchAuthor")
    Observable<HttpResponse<SearchAllAuthorBean>> searchAuthor(@Field("params") String param);

    /* 2019.12.17 个人详情 */
    @FormUrlEncoded
    @POST("app/center/getUserInfo")
    Observable<HttpResponse<PersonUserInfoBean>> getPersonInfo(@Field("params") String param);

    /* 2019.12.17 更懂你职位信息 */
    @FormUrlEncoded
    @POST("app/my/getProfession")
    Observable<HttpResponse<List<ProBean>>> getProfession(@Field("params") String param);

    /* 2019.12.17 职业工作年限记录 */
    @FormUrlEncoded
    @POST("/app/my/personal")
    Observable<HttpResponse> personal(@Field("params") String param);


    /* 2019.12.17 微信登录绑定帐号 */
    @FormUrlEncoded
    @POST("/app/auth/WechatBindAccountViaCode")
    Observable<HttpResponse<RegisterLoginBean.UserInfo>> WechatBindAccountViaCode(@Field("params") String param);


    /* 2019.12.18 所有文章分类接口 */
    @FormUrlEncoded
    @POST("/app/article/allCategory")
    Observable<HttpResponse<CateAllBean>> allCategory(@Field("params") String param);



    /* 2019.12.18 分类文章列表接口 */
    @FormUrlEncoded
    @POST("app/article/catlist")
    Observable<HttpResponse<ActicleAllBean>> catlist(@Field("params") String param);

    /* 2019.12.18 文章子评论列表接口 */
    @FormUrlEncoded
    @POST("app/article/subCommentList")
    Observable<HttpResponse<CommentBean>> subCommentList(@Field("params") String param);


    /* 2019.12.18 文章评论点赞接口 */
    @FormUrlEncoded
    @POST("app/my/goodArticle")
    Observable<HttpResponse> goodArticle(@Field("params") String param);


    /* 2019.12.18 文章评论取消点赞接口 */
    @FormUrlEncoded
    @POST("app/my/cancelGoodArticle")
    Observable<HttpResponse> cancelGoodArticle(@Field("params") String param);


    /* 2019.12.19 羽毛任务-新手任务*/
    @FormUrlEncoded
    @POST("app/my/newPointTask")
    Observable<HttpResponse<NewUserTaskBean>> newPointTask(@Field("params") String param);


    /* 2019.12.19 积分商品列表接口  */
    @FormUrlEncoded
    @POST("app/my/getMallList")
    Observable<HttpResponse<FeatherProductBean>> getMallList(@Field("params") String param);


    /* 2019.12.19 收支明细*/
    @FormUrlEncoded
    @POST("app/my/pointlist")
    Observable<HttpResponse<IncomeBean>> pointlist(@Field("params") String param);


    /* 2019.12.19   积分兑换明细接口 */
    @FormUrlEncoded
    @POST("app/my/exchDetail")
    Observable<HttpResponse<ExchageDetailBean>> exchDetail(@Field("params") String param);


    /* 2019.12.19  羽毛排行榜*/
    @FormUrlEncoded
    @POST("app/my/userRank")
    Observable<HttpResponse<UserRankBean>> userRank(@Field("params") String param);


    /* 2019.12.19 积分商品详情接口  */
    @FormUrlEncoded
    @POST("app/my/getItmeDetailt")
    Observable<HttpResponse<FeatherProductBean>> getItmeDetailt(@Field("params") String param);




    /* 2019.12.19 商品兑换接口*/
    @FormUrlEncoded
    @POST("app/my/exchangePoint")
    Observable<HttpResponse<ExchageDetailBean>> exchangePoint(@Field("params") String param);



    /* 2019.12.19 获取单个分类商品列表  */
    @FormUrlEncoded
    @POST("app/my/getCatList")
    Observable<HttpResponse<FeatherProductBean>> getCatList(@Field("params") String param);


    /* 2019.12.19 领取新手任务奖励*/
    @FormUrlEncoded
    @POST("app/my/getNewPointTaskAward")
    Observable<HttpResponse<NewPointTaskBean>> getNewPointTaskAward(@Field("params") String param);


    /* 2019.12.19 签到 */
    @FormUrlEncoded
    @POST("app/my/sign")
    Observable<HttpResponse> sign(@Field("params") String param);



    /* 2019.12.19 关注用户 */
    @FormUrlEncoded
    @POST("app/center/followUser")
    Observable<HttpResponse> followUser(@Field("params") String param);


    /* 2019.12.19 取消关注用户 */
    @FormUrlEncoded
    @POST("app/center/unfollowUser")
    Observable<HttpResponse> unfollowUser(@Field("params") String param);



    /* 2019.12.19 举报用户 */
    @FormUrlEncoded
    @POST("app/center/reportUser")
    Observable<HttpResponse> reportUser(@Field("params") String param);


    /* 2019.12.19 屏蔽用户 */
    @FormUrlEncoded
    @POST("app/center/blockUser")
    Observable<HttpResponse> blockUser(@Field("params") String param);

    /* 2019.12.19 取消屏蔽用户 */
    @FormUrlEncoded
    @POST("app/center/unblockUser")
    Observable<HttpResponse> unblockUser(@Field("params") String param);

    /* 2019.12.19 重置工作信息 */
    @FormUrlEncoded
    @POST("app/my/resetPersonal")
    Observable<HttpResponse> resetPersonal(@Field("params") String param);

    /* 2019.12.19 判断首页是否展示更懂你 */
    @FormUrlEncoded
    @POST("app/my/isPersonal")
    Observable<HttpResponse> isPersonal(@Field("params") String param);







}
