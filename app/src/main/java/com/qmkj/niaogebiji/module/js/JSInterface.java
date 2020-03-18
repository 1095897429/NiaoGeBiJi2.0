package com.qmkj.niaogebiji.module.js;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.HomeActivityV2;
import com.qmkj.niaogebiji.module.activity.WebViewAllActivity;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.H5ShareBean;
import com.qmkj.niaogebiji.module.bean.H5ToCommentBean;
import com.qmkj.niaogebiji.module.bean.MessageAllH5Bean;
import com.qmkj.niaogebiji.module.bean.MessageCooperationBean;
import com.qmkj.niaogebiji.module.bean.MessageLinkBean;
import com.qmkj.niaogebiji.module.bean.MessageUserBean;
import com.qmkj.niaogebiji.module.bean.MessageVipBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.event.ProfessionEvent;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-18
 * 描述:所有js交互的操作
 */

@SuppressLint("JavascriptInterface")
public class JSInterface {

    protected Activity act;
    protected WebView webView;

    public JSInterface(Activity act, WebView webView) {
        this.webView = webView;
        this.act = act;
    }


    //类型
    //{"type":"toCommentDetail","params":{"id":"11970","comment":"好好","created_at":"1584444420","good_num":"0","type":"1","relatedid":"24843","post_title":"文章标题"}}
    @JavascriptInterface
    public void sendMessage(String param) {
        KLog.d("tag","接受到的参数 是 param " + param);
        if(!TextUtils.isEmpty(param)){
            try {
                JSONObject b= new JSONObject(param);
                //获取类型
                String result = b.optString("type");
                //测试列表
                 if("toTestList".equals(result)){
                    Constant.isReLoad = true;
                    UIHelper.toTestListActivity(act);
                }else if("toArticleDetail".equals(result)){
                    //去文章详情
                    JSONObject object  = b.getJSONObject("params");
                    String id = object.optString("id");
                    UIHelper.toNewsDetailActivity(act,id);
                }else if("toConfirmOk".equals(result)){
                     //提交成功的好的按钮
                    act.finish();
                }else if("toHome".equals(result)){
                    //去文章首页干货
                    Constant.isReLoad = true;
                    UIHelper.toHomeActivity(act, HomeActivityV2.H5_TO_ACTICLE);
                }else if("toKnow".equals(result)){
                    //去更懂你
                    Constant.isReLoad = true;
                    act.runOnUiThread(() -> getProfession());
                }else if("shareVip".equals(result)){
                    MessageCooperationBean javaBean = JSON.parseObject(param, MessageCooperationBean.class);
                    MessageCooperationBean.CooperationBean bean = javaBean.getParams();
                    if(bean != null){
                        showShareVipDialog(bean);
                    }
                }else if("toVipMember".equals(result)){
                    UIHelper.toWebViewAllActivity(act,StringUtil.getLink("vipmember"),"vipmember");
                }else if("toUserDetail".equals(result)){
                    MessageUserBean javaBean = JSON.parseObject(param, MessageUserBean.class);
                    MessageUserBean.H5UserBean bean = javaBean.getParams();
                    String uid =  bean.getUid();
                    UIHelper.toUserInfoActivity(act,uid);
                }else if("toQuesPage".equals(result)){
                    MessageVipBean javaBean = JSON.parseObject(param, MessageVipBean.class);
                    MessageVipBean.VipBean bean = javaBean.getParams();
                    String active =  bean.getActive();
                    UIHelper.toWebViewActivityWithOnLayout(act,StringUtil.getLink("questions/" + active),"questions");
                }else if("tolink".equals(result)){
                    //工具中的跳转
                    MessageLinkBean javaBean = JSON.parseObject(param, MessageLinkBean.class);
                    MessageLinkBean.MessageLink bean = javaBean.getParams();
                    String link =  bean.getLink();
                    UIHelper.toWebViewActivityWithOnStep(act,link);
                }else if("toSubmitInfo".equals(result)){
                    //去编辑界面
                    UIHelper.toUserInfoModifyActivity(act);
                }else if("resetAuthInfo".equals(result)){
                    //重置成功，请求用户信息数据
                    getUserInfo();
                }else if("toCommentDetail".equals(result)){
                    //我的动态 评论 - 去详情
                     H5ToCommentBean javaBean = JSON.parseObject(param, H5ToCommentBean.class);
                     H5ToCommentBean.Params bean = javaBean.getParams();
                     if(bean != null){
                         if(!TextUtils.isEmpty(bean.getType())){
                             if("1".equals(bean.getType())){
                                 //去文章详情页
                                 UIHelper.toNewsDetailActivity(act,bean.getRelatedid());
                             }else if("2".equals(bean.getType())){
                                 UIHelper.toCommentDetailActivity(act,bean.getRelatedid());
                                 //圈子一级评论
                             }else if("3".equals(bean.getType())){
                                 //圈子二级评论
                                 UIHelper.toCommentDetailActivity(act,bean.getRelatedid());
                             }
                         }
                     }
                }else if("toActivityDetail".equals(result)){
                     //发布 去圈子明细
                    JSONObject object  = b.getJSONObject("params");
                    String id = object.optString("id");
                    UIHelper.toCommentDetailActivity(act,id);
                }else if("shareActivity".equals(result)){
                     H5ShareBean javaBean = JSON.parseObject(param, H5ShareBean.class);
                     H5ShareBean.Params bean = javaBean.getParams();
                     if(bean != null){
                         showShareDialog(bean);
                     }
                }else if("toSubmitInfo".equals(result)){
                     //去编辑界面 认证界面
                     UIHelper.toUserInfoModifyActivity(act);
                 }else if("resetAuthInfo".equals(result)){
                     //重置成功，请求用户信息数据 认证界面
                     getUserInfo();
                 }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



    private void showShareDialog( H5ShareBean.Params item) {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(act).builder();
        alertDialog.setShareDynamicView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    ShareBean bean1 = new ShareBean();
                    bean1.setShareType("circle_link");
                    bean1.setLink(item.getShare_url());
                    bean1.setTitle(item.getMoments_share_title());
                    bean1.setContent(item.getBlog());
                    bean1.setImg(item.getShare_icon());
                    StringUtil.shareWxByWeb( act,bean1);
                    break;
                case 1:
                    KLog.d("tag","朋友 是链接");
                    ShareBean bean = new ShareBean();
                    bean.setShareType("weixin_link");
                    bean.setLink(item.getShare_url());
                    bean.setTitle(item.getShare_title());
                    bean.setContent(item.getBlog());
                    bean.setImg(item.getShare_icon());
                    StringUtil.shareWxByWeb(act,bean);
                    break;
                case 4:
                    KLog.d("tag", "转发到动态");
                    UIHelper.toTranspondActivity(act, toBuildCircleBean(item));
                    //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
                    act.overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                    break;
                default:
            }
        });
        alertDialog.show();
    }


    private CircleBean toBuildCircleBean(H5ShareBean.Params params){

        ArrayList<String> ins = new ArrayList<>();
        int size = params.getImages().size();
        if(size > 0){
            ins.add(params.getImages().get(0));
        }

        CircleBean item = new CircleBean();
        item.setId(params.getId());
        item.setImages(ins);
        item.setLink(params.getLink());
        item.setLink_title(params.getLink_title());
        item.setType(params.getType());
        item.setArticle_id(params.getArticle_id());
        item.setArticle_image(params.getArticle_image());
        item.setArticle_title(params.getArticle_title());
        item.setIs_like(params.getIs_like());
        item.setComment(params.getBlog());
        item.setBlog(params.getBlog());
        item.setShare_url(params.getShare_url());
        return item;
    }

    //TODO 2.19 数据来自后台，h5从后台获取，传递给我们 -- link + 头像 取用户字段 title + content 取 h5返回
    private void showShareVipDialog(MessageCooperationBean.CooperationBean item) {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(act).builder();
        alertDialog.setSharelinkView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    RegisterLoginBean.UserInfo mUserInfo = StringUtil.getUserInfoBean();
                    ShareBean bean1 = new ShareBean();
                    bean1.setShareType("circle_link");
                    bean1.setLink(mUserInfo.getInvite_url());
                    bean1.setResId(R.mipmap.icon_fenxiang);

                    bean1.setTitle(item.getTitle());
                    bean1.setContent(item.getSubTitle());
                    StringUtil.shareWxByWeb( act,bean1);
                    break;
                case 1:
                    KLog.d("tag","朋友 是链接");
                    RegisterLoginBean.UserInfo mUserInfo2 = StringUtil.getUserInfoBean();

                    ShareBean bean = new ShareBean();
                    bean.setResId(R.mipmap.icon_fenxiang);
                    bean.setShareType("weixin_link");
                    bean.setLink(mUserInfo2.getInvite_url());
                    bean.setTitle(item.getTitle());
                    bean.setContent(item.getSubTitle());
                    StringUtil.shareWxByWeb( act,bean);
                    break;
                case 2:
                    RegisterLoginBean.UserInfo mUserInf= StringUtil.getUserInfoBean();

                    StringUtil.copyLink(item.getTitle() + "\n" + mUserInf.getInvite_url());
                    break;
                default:
            }
        });
        alertDialog.show();
    }




    private void getUserInfo() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getUserInfo(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<HttpResponse<RegisterLoginBean.UserInfo>>() {
                    @Override
                    public void onSuccess(HttpResponse<RegisterLoginBean.UserInfo> response) {
                        RegisterLoginBean.UserInfo mUserInfo = response.getReturn_data();
                        if(null != mUserInfo){
                            StringUtil.setUserInfoBean(mUserInfo);
                        }
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        if("2003".equals(return_code) || "1008".equals(return_code)){
                            UIHelper.toLoginActivity(BaseApp.getApplication());
                        }
                    }
                });
    }




    private ArrayList<ProBean> temp1;
    private void getProfession() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getProfession(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<HttpResponse<List<ProBean>>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<List<ProBean>> response) {
                        temp1 = (ArrayList<ProBean>) response.getReturn_data();
                        if(temp1 != null && !temp1.isEmpty()){
                            UIHelper.toMoreKnowYouActivity(act,temp1);
                        }
                    }
                });
    }


}
