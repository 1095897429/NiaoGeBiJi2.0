package com.qmkj.niaogebiji.common.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.AboutUsActivity;
import com.qmkj.niaogebiji.module.activity.AuthorDetailActivity;
import com.qmkj.niaogebiji.module.activity.AuthorListActivity;
import com.qmkj.niaogebiji.module.activity.CategoryActivity;
import com.qmkj.niaogebiji.module.activity.CategoryListActivity;
import com.qmkj.niaogebiji.module.activity.CircleMakeActivity;
import com.qmkj.niaogebiji.module.activity.CircleMakeActivityV2;
import com.qmkj.niaogebiji.module.activity.CircleMakeAddLinkActivity;
import com.qmkj.niaogebiji.module.activity.CommentDetailActivityV2;
import com.qmkj.niaogebiji.module.activity.CooperationActivity;
import com.qmkj.niaogebiji.module.activity.DataInfomationActivity;
import com.qmkj.niaogebiji.module.activity.ExchangeAllListActivity;
import com.qmkj.niaogebiji.module.activity.ExchangeDetailActivity2;
import com.qmkj.niaogebiji.module.activity.FeatherActivity;
import com.qmkj.niaogebiji.module.activity.FeatherCatListActivity;
import com.qmkj.niaogebiji.module.activity.FeatherListActivity;
import com.qmkj.niaogebiji.module.activity.FeatherListDetailActivity;
import com.qmkj.niaogebiji.module.activity.HelloMakeActivity;
import com.qmkj.niaogebiji.module.activity.HomeActivityV2;
import com.qmkj.niaogebiji.module.activity.InviteActivity;
import com.qmkj.niaogebiji.module.activity.LoginActivity;
import com.qmkj.niaogebiji.module.activity.MessageDetailActivity;
import com.qmkj.niaogebiji.module.activity.ModifyUserInfoActivity;
import com.qmkj.niaogebiji.module.activity.MoreKnowYouActivity;
import com.qmkj.niaogebiji.module.activity.MoringNewsListActivity;
import com.qmkj.niaogebiji.module.activity.MyCollectionListActivity;
import com.qmkj.niaogebiji.module.activity.MyWebViewOnBackActivity;
import com.qmkj.niaogebiji.module.activity.NewsDetailActivity;
import com.qmkj.niaogebiji.module.activity.NewsThingDetailActivity;
import com.qmkj.niaogebiji.module.activity.PhoneInputActivity;
import com.qmkj.niaogebiji.module.activity.PhoneInputV2Activity;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivity;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivityV2;
import com.qmkj.niaogebiji.module.activity.SearchActivity;
import com.qmkj.niaogebiji.module.activity.SecretActivity;
import com.qmkj.niaogebiji.module.activity.SettingActivity;
import com.qmkj.niaogebiji.module.activity.TestDetailActivity;
import com.qmkj.niaogebiji.module.activity.TestLauchActivity;
import com.qmkj.niaogebiji.module.activity.TestListActivity;
import com.qmkj.niaogebiji.module.activity.TestResultActivity;
import com.qmkj.niaogebiji.module.activity.TestResultFailActivity;
import com.qmkj.niaogebiji.module.activity.ToolEditActivity;
import com.qmkj.niaogebiji.module.activity.ToolSearchActivity;
import com.qmkj.niaogebiji.module.activity.TopicDetailActivityV2;
import com.qmkj.niaogebiji.module.activity.TopicListActivity;
import com.qmkj.niaogebiji.module.activity.TopSelectActivity;
import com.qmkj.niaogebiji.module.activity.TranspondActivity;
import com.qmkj.niaogebiji.module.activity.UserAgreeActivity;
import com.qmkj.niaogebiji.module.activity.UserInfoModifyActivity;
import com.qmkj.niaogebiji.module.activity.UserInfoV2Activity;
import com.qmkj.niaogebiji.module.activity.VertifyCodeActivity;
import com.qmkj.niaogebiji.module.activity.WebViewActivity;
import com.qmkj.niaogebiji.module.activity.WebViewActivityWithLayout;
import com.qmkj.niaogebiji.module.activity.WebViewActivityWithStep;
import com.qmkj.niaogebiji.module.activity.WebViewAllActivity;
import com.qmkj.niaogebiji.module.activity.WebViewBadgeActivity;
import com.qmkj.niaogebiji.module.activity.WebViewEditBadgeActivity;
import com.qmkj.niaogebiji.module.activity.WebViewWithLayoutOnlyActivity;
import com.qmkj.niaogebiji.module.activity.WebViewWithLayoutOnlyActivityV2;
import com.qmkj.niaogebiji.module.activity.WelcomeActivity;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.ExchageDetailBean;
import com.qmkj.niaogebiji.module.bean.MessageAllH5Bean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.TestNewBean;
import com.qmkj.niaogebiji.module.bean.TopicBean;

import java.util.ArrayList;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:
 */
public class UIHelper {

    /** 打开主界面 */
    public static void toHomeActivity(Context ctx, int type) {
        Intent intent = new Intent(ctx, HomeActivityV2.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    /** 打开欢迎界面 */
    public static void toWelcomeActivity(Context ctx) {
        Intent intent = new Intent(ctx, WelcomeActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开登录界面 */
    public static void toLoginActivity(Context ctx) {
        Intent intent = new Intent(ctx, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }

    /** 打开输入手机界面 */
    public static void toPhoneInputActivity(Context ctx,String wechat_token,String loginType) {
        Intent intent = new Intent(ctx, PhoneInputActivity.class);
        intent.putExtra("loginType",loginType);
        intent.putExtra("wechat_token",wechat_token);
        ctx.startActivity(intent);
    }

    /** 打开输入手机闪验界面 */
    public static void toPhoneShanYanActivity(Context ctx,String wechat_token,String loginType) {
        Intent intent = new Intent(ctx, PhoneInputV2Activity.class);
        intent.putExtra("loginType",loginType);
        intent.putExtra("wechat_token",wechat_token);
        ctx.startActivity(intent);
    }

    /** 打开验证码界面 */
    public static void toVertifyCodeActivity(Activity ctx,String phone,String wechat_token,String loginType) {
        Intent intent = new Intent(ctx, VertifyCodeActivity.class);
        intent.putExtra("loginType",loginType);
        intent.putExtra("wechat_token",wechat_token);
        intent.putExtra("phone",phone);
        ctx.startActivityForResult(intent,100);
    }

    /** 打开搜索结果界面 */
//    public static void toSearchResultActivity(Context ctx,String keyword) {
//        Intent intent = new Intent(ctx, SearchResultActivity.class);
//        intent.putExtra("keyword",keyword);
//        ctx.startActivity(intent);
//    }

      /** 打开搜索界面 */
    public static void toSearchActivity(Context ctx) {
        Intent intent = new Intent(ctx, SearchActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开早报界面 */
    public static void toMoringActivity(Context ctx) {
        Intent intent = new Intent(ctx, MoringNewsListActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开分类界面 */
    public static void toCategoryActivity(Context ctx) {
        Intent intent = new Intent(ctx, CategoryActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开资料详情界面 */
    public static void toDataInfoActivity(Context ctx,String newsId) {
        Intent intent = new Intent(ctx, DataInfomationActivity.class);
        intent.putExtra("newsId",newsId);
        ctx.startActivity(intent);
    }


    /** 打开图片预览界面 */
    public static void toPicPreViewActivity(Context ctx,ArrayList<String> photos,int position,boolean isShowDown) {
        Intent intent = new Intent(ctx, PicPreviewActivityV2.class);
        Bundle bundle = new Bundle ();
        bundle.putStringArrayList ("imageList", photos);
        bundle.putBoolean("fromNet",true);
        bundle.putInt("index",position);
        bundle.putBoolean("isShowDown",isShowDown);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    public static void toPicPreViewActivityOld(Context ctx,ArrayList<String> photos,int position,boolean isShowDown) {
        Intent intent = new Intent(ctx, PicPreviewActivity.class);
        Bundle bundle = new Bundle ();
        bundle.putStringArrayList ("imageList", photos);
        bundle.putBoolean("fromNet",true);
        bundle.putInt("index",position);
        bundle.putBoolean("isShowDown",isShowDown);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }

    /** 打开关注/推荐 作者列表界面 */
    public static void toAuthorListActivity(Context ctx) {
        Intent intent = new Intent(ctx, AuthorListActivity.class);
        ctx.startActivity(intent);
    }


    /** 打开分类列表 界面 */
    public static void toCategoryListActivity(Context ctx,  String catid,String title) {
        Intent intent = new Intent(ctx, CategoryListActivity.class);
        intent.putExtra("catid",catid);
        intent.putExtra("title",title);
        ctx.startActivity(intent);
    }

    /** 打开新闻界面 */
    public static void toNewsDetailActivity(Context ctx,String newsId) {
        Intent intent = new Intent(ctx, NewsDetailActivity.class);
        intent.putExtra("newsId",newsId);
        ctx.startActivity(intent);
    }


    /** 打开圈子发布界面 */
    public static void toCircleMakeActivity(Context ctx) {
        Intent intent = new Intent(ctx, CircleMakeActivityV2.class);
        ctx.startActivity(intent);
    }


    /** 打开圈子发布界面  -- 两个参数的*/
    public static void toCircleMakeActivityV2(Context ctx, TopicBean topicBean) {
        Intent intent = new Intent(ctx, CircleMakeActivityV2.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("topicBean",topicBean);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    /** 打开圈子发布界面  -- 三个参数的，从文章处过来的*/
    public static void toCircleMakeActivityV2(Context ctx, TopicBean topicBean, NewsDetailBean mNewsDetailBean) {
        Intent intent = new Intent(ctx, CircleMakeActivityV2.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("topicBean",topicBean);
        bundle.putSerializable("articleBean",mNewsDetailBean);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    /** 打开圈子发布2界面 */
    public static void toCircleMakeAddLinkActivity(Activity ctx,int reqCode) {
        Intent intent = new Intent(ctx, CircleMakeAddLinkActivity.class);
        ctx.startActivityForResult(intent,reqCode);
    }

    /** 打开隐私界面 */
    public static void toSecretActivity(Context ctx) {
        Intent intent = new Intent(ctx, SecretActivity.class);
        ctx.startActivity(intent);
    }


    /** 打开用户协议界面 */
    public static void toUserAgreeActivity(Context ctx) {
        Intent intent = new Intent(ctx, UserAgreeActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开网页界面 */
    public static void toWebViewActivity(Context ctx,String link) {
        Intent intent = new Intent(ctx, WebViewActivity.class);
        intent.putExtra("link",link);
        ctx.startActivity(intent);
    }

    /** 打开网页界面  返回网页的上一页*/
    public static void toWebViewActivityWithOnStep(Context ctx,String link) {
        Intent intent = new Intent(ctx, WebViewActivityWithStep.class);
        intent.putExtra("link",link);
        ctx.startActivity(intent);
    }


    public static void toWebViewWithLayoutOnlyActivity(Context ctx,String link) {
        Intent intent = new Intent(ctx, WebViewWithLayoutOnlyActivity.class);
        intent.putExtra("link",link);
        ctx.startActivity(intent);
    }

    public static void toWebViewBadgeActivity(Context ctx, String link,String fromWhere) {
        Intent intent = new Intent(ctx, WebViewBadgeActivity.class);
        intent.putExtra("link",link);
        intent.putExtra("fromWhere",fromWhere);
        ctx.startActivity(intent);
    }


    public static void toWebViewEditBadgeActivity(Context ctx, String link,String fromWhere) {
        Intent intent = new Intent(ctx, WebViewEditBadgeActivity.class);
        intent.putExtra("link",link);
        intent.putExtra("fromWhere",fromWhere);
        ctx.startActivity(intent);
    }

    public static void toWebViewAllActivity(Context ctx, String link,String fromWhere) {
        Intent intent = new Intent(ctx, WebViewAllActivity.class);
        intent.putExtra("link",link);
        intent.putExtra("fromWhere",fromWhere);
        ctx.startActivity(intent);
    }


    public static void toWebViewActivityWithOnLayout(Context ctx, String link,String fromWhere) {
        Intent intent = new Intent(ctx, WebViewActivityWithLayout.class);
        intent.putExtra("link",link);
        intent.putExtra("fromWhere",fromWhere);
        ctx.startActivity(intent);
    }

    /** 打开评论详情界面  position 放在set方法中传入*/
    public static void toCommentDetailActivity(Context ctx,String blog_id) {
        Intent intent = new Intent(ctx, CommentDetailActivityV2.class);
        Bundle  bundle = new Bundle();
        bundle.putString("blog_id",blog_id);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    /** 打开转发界面 */
    public static void toTranspondActivity(Context ctx, CircleBean item) {
        Intent intent = new Intent(ctx, TranspondActivity.class);
        Bundle  bundle = new Bundle();
        bundle.putSerializable("circle",item);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }

    /** 打开设置界面 */
    public static void toSettingActivity(Context ctx) {
        Intent intent = new Intent(ctx, SettingActivity.class);
        ctx.startActivity(intent);
    }


    /** 打开关注界面 */
    public static void toAboutUsActivity(Context ctx) {
        Intent intent = new Intent(ctx, AboutUsActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开测一测列表界面 */
    public static void toTestListActivity(Context ctx) {
        Intent intent = new Intent(ctx, TestListActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开测一测详情界面 */
    public static void toTestDetailActivity(Context ctx, SchoolBean.SchoolTest test) {
        Intent intent = new Intent(ctx, TestDetailActivity.class);
        Bundle bundle  = new Bundle();
        bundle.putSerializable("bean",test);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }

    /** 打开测一测开始界面 */
    public static void toTestLauchActivity(Activity ctx, ArrayList<TestNewBean> list, SchoolBean.SchoolTest testBean) {
        Intent intent = new Intent(ctx, TestLauchActivity.class);
        Bundle bundle  = new Bundle();
        //序列化,要注意转化(Serializable)
        bundle.putSerializable("list",list);
        bundle.putSerializable("bean",testBean);
        intent.putExtras(bundle);
        ctx.startActivityForResult(intent,100);
    }

    /** 打开测一测结果界面 */
    public static void toTestResultActivity(Context ctx,SchoolBean.SchoolTest test) {
        Intent intent = new Intent(ctx, TestResultActivity.class);
        Bundle bundle  = new Bundle();
        bundle.putSerializable("bean",test);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }

    /** 打开测一测结果失败界面 */
    public static void toTestResultFailActivity(Context ctx,SchoolBean.SchoolTest test) {
        Intent intent = new Intent(ctx, TestResultFailActivity.class);
        Bundle bundle  = new Bundle();
        bundle.putSerializable("bean",test);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }

    /** 打开个人信息界面 */
    public static void toUserInfoActivity(Context ctx,String uid) {
        Intent intent = new Intent(ctx, UserInfoV2Activity.class);
        intent.putExtra("uid",uid);
        ctx.startActivity(intent);
    }

    /** 打开个人信息界面 新版本 */
    public static void toUserInfoV2Activity(Context ctx,String uid) {
        Intent intent = new Intent(ctx, UserInfoV2Activity.class);
        intent.putExtra("uid",uid);
        ctx.startActivity(intent);
    }

    /** 打开打招呼界面 */
    public static void toHelloMakeActivity(Activity ctx) {
        Intent intent = new Intent(ctx, HelloMakeActivity.class);
        ctx.startActivityForResult(intent,100);
    }


    /** 打开收藏界面 */
    public static void toMyCollectionListActivity(Context ctx) {
        Intent intent = new Intent(ctx, MyCollectionListActivity.class);
        ctx.startActivity(intent);
    }


    /** 打开邀请界面 */
    public static void toInviteActivity(Context ctx) {
        Intent intent = new Intent(ctx, InviteActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开工具编辑界面 */
    public static void toToolEditActivity(Context ctx) {
        Intent intent = new Intent(ctx, ToolEditActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开工具搜索界面 */
    public static void toToolSearchActivity(Context ctx) {
        Intent intent = new Intent(ctx, ToolSearchActivity.class);
        ctx.startActivity(intent);
    }


    /** 打开更懂你界面 */
    public static void toMoreKnowYouActivity(Activity ctx, ArrayList<ProBean> list) {
        Intent intent = new Intent(ctx, MoreKnowYouActivity.class);
        Bundle bundle  = new Bundle();
        //序列化,要注意转化(Serializable)
        bundle.putSerializable("list",list);
        intent.putExtras(bundle);
        ctx.startActivityForResult(intent,100);
    }


    /** 打开羽毛商品列表界面 */
    public static void toFeatherProductListActivity(Context ctx) {
        Intent intent = new Intent(ctx, FeatherListActivity.class);
        ctx.startActivity(intent);
    }



    /** 打开兑换记录界面 */
    public static void toExchangeAllListActivity(Context ctx) {
        Intent intent = new Intent(ctx, ExchangeAllListActivity.class);
        ctx.startActivity(intent);
    }



    /** 打开商品cat列表界面 */
    public static void toFeatherCatListActivity(Context ctx,String id,String name) {
        Intent intent = new Intent(ctx, FeatherCatListActivity.class);
        intent.putExtra("cat_id",id);
        intent.putExtra("name",name);
        ctx.startActivity(intent);
    }


    /** 打开商品兑换结果界面 */
    public static void toExchangeDetailActivity2(Context ctx, ExchageDetailBean bean, String from, String catid) {
        Intent intent = new Intent(ctx, ExchangeDetailActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putString("from",from);
        bundle.putString("catid",catid);
        bundle.putSerializable("bean",bean);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    /** 打开干货明细界面 */
    public static void toNewsThingDetailActivity(Context ctx,String newsId) {
        Intent intent = new Intent(ctx, NewsThingDetailActivity.class);
        intent.putExtra("newsId",newsId);
        ctx.startActivity(intent);
    }



    /** 打开商品详情界面 */
    public static void toFeatherListDetailActivity(Context ctx,String id) {
        Intent intent = new Intent(ctx, FeatherListDetailActivity.class);
        intent.putExtra("product_id",id);
        ctx.startActivity(intent);
    }

    /** 打开羽毛界面*/
    public static void toFeatherctivity(Context ctx) {
        Intent intent = new Intent(ctx, FeatherActivity.class);
        ctx.startActivity(intent);
    }


    /** 打开商品详情界面 */
    public static void toExchangeDetailActivity(Context ctx, ExchageDetailBean bean, String from, String catid) {
        Intent intent = new Intent(ctx, ExchangeDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("from",from);
        bundle.putString("catid",catid);
        bundle.putSerializable("bean",bean);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    /** 打开用户修改界面 */
    public static void toModifyUserInfo(Activity ctx, String type, String content) {
        Intent intent = new Intent(ctx, ModifyUserInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        bundle.putString("content",content);
        intent.putExtras(bundle);
        ctx.startActivityForResult(intent,SettingActivity.COMMON_MODIFY);
    }


    /** 打开消息中心明细 */
    public static void toMsgDetailActivity(Context ctx, MessageAllH5Bean.MessageH5Bean bean) {
        Intent intent = new Intent(ctx, MessageDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean",bean);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    //TODO 2020.1.16 统一的vip界面 -- 这样做的好处是改一处即可
    public static void toVipActivity(Context ctx) {
        Intent intent = new Intent(ctx, WebViewAllActivity.class);
        intent.putExtra("link", StringUtil.getLink("vipmember"));
        intent.putExtra("fromWhere","vipmember");
        ctx.startActivity(intent);
    }



    /** 打开话题界面 -- 从发布圈子处点击进入*/
    public static void toTopicSelectivity(Activity ctx,String topicId) {
        Intent intent = new Intent(ctx, TopSelectActivity.class);
        intent.putExtra("topicId", topicId);
        ctx.startActivityForResult(intent,CircleMakeActivity.REQUEST_SELECT_TOPIC_CODE);
    }

    /** 打开话题界面 -- 从圈子的查看更多处点击进入*/
    public static void toTopListActivity(Context ctx) {
        Intent intent = new Intent(ctx, TopicListActivity.class);
        ctx.startActivity(intent);
    }



    /** 打开用户编辑界面*/
    public static void toUserInfoModifyActivity(Activity ctx) {
        Intent intent = new Intent(ctx, UserInfoModifyActivity.class);
        ctx.startActivity(intent);
    }


    /** 打开话题明细界面*/
    public static void toTopicDetailActivity(Context ctx,String topicId) {
        Intent intent = new Intent(ctx, TopicDetailActivityV2.class);
        intent.putExtra("topicId",topicId);
        ctx.startActivity(intent);
    }

    /** 打开作者详情页界面*/
    public static void toAuthorDetailActivity(Context ctx,String authorId) {
        Intent intent = new Intent(ctx, AuthorDetailActivity.class);
        intent.putExtra("authorId",authorId);
        ctx.startActivity(intent);
    }


    /** */
    public static void toCooperationActivity(Activity ctx,String url) {
        Intent intent = new Intent(ctx, CooperationActivity.class);
        intent.putExtra("url",url);
        ctx.startActivity(intent);
        //参数一：Activity2进入动画  参数二：Activity1退出动画
        ctx.overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
    }


    public static void toWebViewNomal(Context ctx,String link) {
        Intent intent = new Intent(ctx, WebViewWithLayoutOnlyActivityV2.class);
        intent.putExtra("link",link);
        ctx.startActivity(intent);
    }


    public static void toNewWebView(Context ctx, String link) {
        Intent intent = new Intent(ctx, MyWebViewOnBackActivity.class);
        intent.putExtra("link",link);
        ctx.startActivity(intent);
    }

    public static void toNewWebView(Context ctx, String link,String title) {
        Intent intent = new Intent(ctx, MyWebViewOnBackActivity.class);
        intent.putExtra("link",link);
        intent.putExtra("title",title);
        ctx.startActivity(intent);
    }


}
