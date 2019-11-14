package com.qmkj.niaogebiji.common.net.api;


import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;

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



}
