package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.RewardAlertDialog;
import com.qmkj.niaogebiji.common.dialog.SharePosterDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.NewTaskItemAdapter;
import com.qmkj.niaogebiji.module.adapter.FeatherItemAdapter;
import com.qmkj.niaogebiji.module.bean.FeatherBean;
import com.qmkj.niaogebiji.module.bean.NewPointTaskBean;
import com.qmkj.niaogebiji.module.bean.NewUserTaskBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.FeatherEvent;
import com.qmkj.niaogebiji.module.event.ShowSignRedPointEvent;
import com.qmkj.niaogebiji.module.event.UserFeatherEvent;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:羽毛界面下的第1个Fragment
 */
@SuppressWarnings("ALL")
public class FeatherItemFragment1 extends BaseLazyFragment {

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.newuser_recycler)
    RecyclerView newuser_recycler;

    @BindView(R.id.day_recycler)
    RecyclerView day_recycler;

    @BindView(R.id.part1111)
    LinearLayout part1111;


    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    //适配器2
    FeatherItemAdapter mFeatherItemAdapter;
    //集合1
    List<FeatherBean> mList1 = new ArrayList<>();


    //适配器1
    NewTaskItemAdapter mNewTaskAdapter;
    LinearLayoutManager mLinearLayoutManager1;
    List<NewUserTaskBean.NewTaskBean> mList2 = new ArrayList<>();


    //TODO 2020.1.19 去掉羽毛任务中的快讯评论  R.mipmap.icon_feather_8
    private int[] imgRes = new int[]{R.mipmap.icon_feather_1,R.mipmap.icon_feather_2,R.mipmap.icon_feather_3,
            R.mipmap.icon_feather_4,R.mipmap.icon_feather_5,R.mipmap.icon_feather_6,R.mipmap.icon_feather_7,R.mipmap.icon_feather_9,R.mipmap.icon_feather_10};
    //评论快讯
    private String[] titleRes = new String[]{"学习打卡","邀请好友","阅读文章","文章评分","测一测","文章评论","分享文章","分享快讯","干货投递"};
    //"浏览行业快讯，留下你的看法"
    private String[] tagRes = new String[]{"每日打次卡，总是良好的开始","好友下载-注册-登录APP即可领取","认真阅读干货文章，日积月累总有收获","阅读文章后留下你的评论",
            "掌握文章要点进行自我测试","想说就说，不吐不快","好货不独享，推荐给更多需要的人","成为好友圈的消息通","立即投稿，写出影响力，采纳奖励1000羽毛"};


    private RegisterLoginBean.UserInfo mUserInfo;


    public static FeatherItemFragment1 getInstance(String chainId, String chainName) {
        FeatherItemFragment1 actionItemFragment = new FeatherItemFragment1();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        actionItemFragment.setArguments(args);
        return actionItemFragment;
    }


    @Override
    protected int getLayoutId() {
        //这里使用公用的
        return R.layout.firtst_feather_item_1;
    }

    @Override
    protected void initView() {

        mUserInfo = StringUtil.getUserInfoBean();

        scrollView.setNestedScrollingEnabled(true);

        FeatherBean bean;
        for (int i = 0; i < titleRes.length; i++) {
            bean = new FeatherBean();
            if(0 == i ){
                //显示已签到
                bean.setStatus(1);
            }else if(i== 1 || i == 8 ){
                //其他不显示背景
                bean.setStatus(3);
            }else{
                //显示 + 50
                bean.setStatus(2);
            }
            bean.setImgRes(imgRes[i]);
            bean.setTitle(titleRes[i]);
            bean.setTag(tagRes[i]);
            mList1.add(bean);
        }

        initDayRecyler();

        getUserInfo();
        newUserTasklist();
    }


    @Override
    protected void lazyLoadData() {

    }

    //初始化布局管理器
    private void initDayRecyler() {

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        day_recycler.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFeatherItemAdapter = new FeatherItemAdapter(mList1);
        day_recycler.setAdapter(mFeatherItemAdapter);
        //禁止滑动
        day_recycler.setNestedScrollingEnabled(false);
        day_recycler.setHasFixedSize(true);


        //跳转事件
        mFeatherItemAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (position){
                    case 0:
//                        toSign();
//                        mFeatherItemAdapter.getData().get(0).setStatus(1);
//                        mFeatherItemAdapter.updateData(0);
                        break;
                    case 1:

                        showShareDialog();

                        break;
                    case 2:
//                        getServiceWechatPic();

                        break;
                    case 3:
                        KLog.d("阅读文章");
                        break;
                    case 4:

                        KLog.d("文章评分");

                        break;
                    case 5:
                        KLog.d("文章测一测");

                        break;
                    case 6:
                        KLog.d("文章评论");

                        break;
                    case 7:
                        KLog.d("文章分享");

                        break;
                    case 9:
                        String link = "http://www.niaogebiji.com/article-23344-1.html";
                        UIHelper.toWebViewActivity(getActivity(),link);

                        break;
                    default:
                }
            }
        });

    }

    /** --------------------------------- 新手任务  ---------------------------------*/


    //初始化布局管理器
    private void initNewTaskRecyler() {
        mLinearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        //设置默认垂直布局
        mLinearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        newuser_recycler.setLayoutManager(mLinearLayoutManager1);
        //设置适配器
        mNewTaskAdapter = new NewTaskItemAdapter(mList2);
        newuser_recycler.setAdapter(mNewTaskAdapter);
        //解决数据加载不完
        newuser_recycler.setNestedScrollingEnabled(true);
        newuser_recycler.setHasFixedSize(true);


        //跳转事件
        mNewTaskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                if(0 == position){
                    if(!mList2.isEmpty()){
                        getNewPointTaskAward(mList2.get(position).getType());
                    }
                }else if(1 == position){
                    //TODO 10.15 新逻辑，微信分享
                    showShareDialog();
                }

            }
        });

    }



    private NewUserTaskBean mNewUserTaskBean;
    private List<NewUserTaskBean.NewTaskBean> mNewTaskBeanList;

    private void newUserTasklist() {

        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().newPointTask(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<NewUserTaskBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<NewUserTaskBean> response) {

                        mNewUserTaskBean = response.getReturn_data();

                        if(null != mNewUserTaskBean){

                            //0 显示  1.隐藏
                            if(0 == mNewUserTaskBean.getIs_hide()){
                                mNewTaskBeanList = mNewUserTaskBean.getList();
                                listCommonLogic();
                            }else{
                                part1111.setVisibility(View.GONE);
                            }

                        }

                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);

                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                    }
                });
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void listCommonLogic() {
        if(null == mNewTaskAdapter){
            initNewTaskRecyler();
        }
        mList2.addAll(mNewTaskBeanList);
        mNewTaskAdapter.setNewData(mList2);

    }




    @OnClick({R.id.to_exchange})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.to_exchange:
                UIHelper.toFeatherProductListActivity(getActivity());
                break;
            default:
        }
    }


    /** --------------------------------- 领取新手任务奖励  ---------------------------------*/

    String getPoint ;

    private void getNewPointTaskAward(String type) {
        Map<String,String> map = new HashMap<>();
        map.put("type",type);
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().getNewPointTaskAward(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<NewPointTaskBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<NewPointTaskBean> response) {

                        NewPointTaskBean te = response.getReturn_data();
                        if(null != te){
                            getPoint = te.getPoint() + "";
                            mNewTaskAdapter.updateData();

                            showFeatherNewTaskDialog(getPoint);
                        }



                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);

                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                    }
                });
    }

    private void showFeatherNewTaskDialog(String point) {
        final RewardAlertDialog iosAlertDialog = new RewardAlertDialog(getActivity()).builder();
        iosAlertDialog.setNegativeButton("确定", v -> {

        }).setNegativeButton("知道了", v -> {

            getUserInfoToChangeFeahter();

        }).setMsg("+ " + point).setCanceledOnTouchOutside(false);
        iosAlertDialog.setTitle("领取新手奖励完成 ");
        iosAlertDialog.show();
    }


    /** --------------------------------- 微信分享  ---------------------------------*/

    private void showShareDialog() {
        SharePosterDialog alertDialog = new SharePosterDialog(getActivity()).builder();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setSaveGone(true);
        alertDialog.setTitle("邀请好友");
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    KLog.d("tag", "朋友圈");

                    shareWxCircleByWeb();
                    break;
                case 1:
                    shareWxByWeb();
                    break;

                default:
            }
        });
        alertDialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(getActivity()).release();
    }



    //分享微信（web) 链接
    private void shareWxByWeb() {
        if(null == getActivity()){
            return;
        }

        String sharepic = "";
        String shareurl = mUserInfo.getInvite_url();
        String title = "好友送你运营干货礼包，速来领取！";
        String summary = "造烛求明，学习求理，鸟哥笔记在等你～";
        SHARE_MEDIA platform;
        platform = SHARE_MEDIA.WEIXIN;

        UMImage thumb;
        if (TextUtils.isEmpty(sharepic)) {
            thumb = new UMImage(getActivity(), R.mipmap.icon_fenxiang);
        } else {
            thumb = new UMImage(getActivity(), sharepic);
        }


        UMWeb web = new UMWeb(shareurl);
        //标题
        web.setTitle(title);
        //缩略图
        web.setThumb(thumb);
        //描述
        web.setDescription(summary);

        //传入平台
        new ShareAction(getActivity())
                .setPlatform(platform)
                .withMedia(web)
                .share();
    }


    /**
     * 分享微信（web）
     */
    public void shareWxCircleByWeb() {
        if (getActivity() == null){
            return;
        }

        String sharepic = "";
        String shareurl = mUserInfo.getInvite_url();
        String title = "好友送你运营干货礼包，速来领取！";
        String summary = "造烛求明，学习求理，鸟哥笔记在等你～";
        SHARE_MEDIA platform;
        platform = SHARE_MEDIA.WEIXIN_CIRCLE;

        UMImage thumb;
        if (TextUtils.isEmpty(sharepic)) {
            thumb = new UMImage(getActivity(), R.mipmap.icon_fenxiang);
        } else {
            thumb = new UMImage(getActivity(), sharepic);
        }


        UMWeb web = new UMWeb(shareurl);
        //标题
        web.setTitle(title);
        //缩略图
        web.setThumb(thumb);
        //描述
        web.setDescription(summary);

        //传入平台
        new ShareAction(getActivity())
                .setPlatform(platform)
                .withMedia(web)
                .share();

    }



    /** --------------------------------- 签到  ---------------------------------*/

    private void showFeatherDialog() {
        final RewardAlertDialog iosAlertDialog = new RewardAlertDialog(getActivity()).builder();
        iosAlertDialog.setNegativeButton("确定", v -> {

        }).setNegativeButton("知道了", v -> {
            SPUtils.getInstance().put(Constant.IS_TODAY, System.currentTimeMillis());

            getUserInfoToChangeFeahter();

        }).setMsg("+ 10").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    private void getUserInfoToChangeFeahter() {

        Map<String,String> map = new HashMap<>();

        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().getUserInfo(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<RegisterLoginBean.UserInfo>>() {
                    @Override
                    public void onSuccess(HttpResponse<RegisterLoginBean.UserInfo> response) {
                        mUserInfo = response.getReturn_data();
                        if(null != mUserInfo){
                            EventBus.getDefault().post(new FeatherEvent(mUserInfo.getPoint()));
                        }
                    }

                });
    }


    private void toSign() {

        Map<String,String> map = new HashMap<>();

        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().sign(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.e("tag",response.getReturn_msg());
                        //更新数据集合中的某一个
                        mFeatherItemAdapter.getData().get(0).setStatus(1);
                        mFeatherItemAdapter.notifyItemChanged(0);
                        //签到成功展示弹框
                        showFeatherDialog();

                        //发送事件取消首页的红点
                        EventBus.getDefault().post(new ShowSignRedPointEvent("1"));

                    }

                });
    }



    private void getUserInfo() {

        Map<String,String> map = new HashMap<>();

        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().getUserInfo(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<RegisterLoginBean.UserInfo>>() {
                    @Override
                    public void onSuccess(HttpResponse<RegisterLoginBean.UserInfo> response) {
                        mUserInfo = response.getReturn_data();
                        if(null != mUserInfo){
                            //今天是否签到了：1-已签到，0-未签到
                            if("0".equals(mUserInfo.getSigned_today())){
                                toSign();
                            }
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


    @Override
    protected boolean regEvent() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(UserFeatherEvent event){
        getUserInfo();
        mList2.clear();
        newUserTasklist();
    }




}
