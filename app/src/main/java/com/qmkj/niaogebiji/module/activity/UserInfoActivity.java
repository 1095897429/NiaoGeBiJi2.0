package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ProfessionAutherDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.AutherCertInitBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.PersonUserInfoBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.qmkj.niaogebiji.module.event.PeopleFocusEvent;
import com.qmkj.niaogebiji.module.event.ProfessionEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-25
 * 描述:个人信息界面(个人 + 别人)
 * 1.自己的信息 -- 底部都不显示，顶部显示编辑，并且显示认证的一些信息 -- 直接去认证
 * 2.别人的信息 -- 根据状态 显示底部[我屏蔽他，底部不显示]
 *
 * 3.uid判断显示不同的布局
 * 4.取消屏蔽 -- 提示屏蔽成功，下方布局消失，同时改变popup中的屏蔽为取消屏蔽
 *
 *
 * 1.空布局是  activity_empty
 *
 * 1.我 ：用户 + 立即认证(公司 + 职位) + 徽章 + 个人简介
 * 2.别人：用户  + 显示 未认证 或者(公司 + 职位) +  徽章 + 个人简介
 */
public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.iv_text)
    TextView iv_text;

    LinearLayout part2222_2;
    TextView send_article_num;
    TextView medal_count;
    TextView sender_name;
    TextView user_des;
    CircleImageView head_icon;
    LinearLayout ll_badge;
    TextView senderverticity;
    TextView sender_tag;

    @BindView(R.id.iv_right)
    ImageView iv_right;

    @BindView(R.id.part3333)
    LinearLayout part3333;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;


    @BindView(R.id.noFocus)
    TextView noFocus;

    @BindView(R.id.alreadFocus)
    TextView alreadFocus;


    @BindView(R.id.himcloseme)
    TextView himcloseme;


    private int page = 1;

    CircleRecommentAdapterNew mCircleRecommentAdapterNew;
    List<CircleBean> mAllList = new ArrayList<>();

    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    //关注状态
    private String follow_status;
    //自己的uid
    private String myUid;
    private RegisterLoginBean.UserInfo  mUserInfo;
    //传递uid
    private String  otherUid;
    //屏蔽 取消屏蔽
    private String shareContent;

    private  PersonUserInfoBean temp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo;
    }


    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(this);
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            getPersonInfo();
        });
    }

    @Override
    protected void initView() {
        initSamrtLayout();

        myUid = StringUtil.getMyUid();

        otherUid = getIntent().getStringExtra("uid");

        //动态h5跳转过来，uid是不带的，那么肯定是自己
        if(TextUtils.isEmpty(otherUid)){
            otherUid = myUid;
        }

        mUserInfo = StringUtil.getUserInfoBean();
        iv_right.setImageResource(R.mipmap.icon_userinfo_other_1);
        initLayout();

        getPersonInfo();

    }
    private void initDifferLogic() {

        //自己
        if(myUid.equals(otherUid)){
            iv_text.setVisibility(View.VISIBLE);
            //认证
            if("1".equals(temp.getAuth_email_status()) || "1".equals(temp.getAuth_card_status())){
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
                drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                sender_tag.setCompoundDrawables(null,null,drawable,null);
                sender_tag.setVisibility(View.VISIBLE);
                senderverticity.setVisibility(View.GONE);
                sender_tag.setText( (TextUtils.isEmpty(temp.getCompany_name())?"":temp.getCompany_name()) +
                        (TextUtils.isEmpty(temp.getPosition())?"":temp.getPosition()));
            }else{
                sender_tag.setVisibility(View.GONE);
                senderverticity.setText("立即职业认证，建立人脉");
                senderverticity.setVisibility(View.VISIBLE);
                senderverticity.setOnClickListener((v)->{
                    if(StringUtil.isFastClick()){
                        return;
                    }
                    posCertInit();
                });
            }
        }else{
            if("1".equals(temp.getAuth_email_status()) || "1".equals(temp.getAuth_card_status())){
                sender_tag.setVisibility(View.VISIBLE);
                senderverticity.setVisibility(View.GONE);
                sender_tag.setText( (TextUtils.isEmpty(temp.getCompany_name())?"":temp.getCompany_name()) +
                        (TextUtils.isEmpty(temp.getPosition())?"":temp.getPosition()));
            }else{
                sender_tag.setVisibility(View.GONE);
                senderverticity.setText("未认证");
                senderverticity.setVisibility(View.VISIBLE);
            }
            //别人
            showStateByFollow(temp.getFollow_status());
            iv_right.setVisibility(View.VISIBLE);
            part3333.setVisibility(View.VISIBLE);

        }
    }

    private void getPersonInfo() {
        Map<String,String> map = new HashMap<>();
        map.put("uid",otherUid);
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getPersonInfo(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<PersonUserInfoBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<PersonUserInfoBean> response) {

                        if(smartRefreshLayout != null){
                            smartRefreshLayout.finishRefresh();
                        }
                        temp = response.getReturn_data();
                        if(temp != null){
                            setHeadData();
                            setBlogList(temp.getBlog_list());
                        }
                    }
                });
    }

    private void setBlogList(List<CircleBean> blog_list) {
        if(page == 1){
            if(!blog_list.isEmpty()){
                setData2(blog_list);
                mCircleRecommentAdapterNew.setNewData(mAllList);
                //如果第一次返回的数据不满10条，则显示无更多数据
                if(blog_list.size() < Constant.SEERVER_NUM){
                    mCircleRecommentAdapterNew.loadMoreEnd();
                }
            }else{
                ll_empty.setVisibility(View.VISIBLE);

                ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("您还没有关注的圈子用户哦，快去推荐看看吧！");

            }
        }else{
            //已为加载更多有数据
            if(blog_list != null && blog_list.size() > 0){
                setData2(blog_list);
                mCircleRecommentAdapterNew.loadMoreComplete();
                mCircleRecommentAdapterNew.addData(teList);
            }else{
                //已为加载更多无更多数据
                mCircleRecommentAdapterNew.loadMoreComplete();
                mCircleRecommentAdapterNew.loadMoreEnd();
            }
        }
    }


    List<CircleBean> teList = new ArrayList<>();
    private void setData2(List<CircleBean> list) {
        teList.clear();
        if(list != null){
            int type ;
            CircleBean temp;
            for (int i = 0; i < list.size(); i++) {
                temp  = list.get(i);
                //获取类型
                type = StringUtil.getCircleType(temp);
                //检查links同时添加原创文本
                StringUtil.addLinksData(temp);

                if(type == CircleRecommentAdapterNew.ZF_TEXT ||
                        type == CircleRecommentAdapterNew.ZF_PIC ||
                        type == CircleRecommentAdapterNew.ZF_ACTICLE ||
                        type == CircleRecommentAdapterNew.ZF_LINK){
                    //检查links同时添加到转发文本
                    StringUtil.addTransLinksData(temp);
                }

                //如果判断有空数据，则遍历下一个数据
                if(100 == type){
                    continue;
                }
                temp.setCircleType(type);
                teList.add(temp);
            }
            if(page == 1){
                mAllList.addAll(teList);
            }
        }
    }



    // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
    private void showStateByFollow(int status) {
        follow_status = status + "";
        shareContent = "屏蔽";
        if("0".equals(follow_status)){
            noFocus.setVisibility(View.VISIBLE);
        }else if("1".equals(follow_status)){
            alreadFocus.setVisibility(View.VISIBLE);
        }else if("3".equals(follow_status)){
            himcloseme.setVisibility(View.VISIBLE);
        }else if("2".equals(follow_status)){
            shareContent = "取消屏蔽";
            part3333.setVisibility(View.GONE);
        }
    }


    private void setHeadData() {
        //头部信息
        part2222_2 = headView.findViewById(R.id.part2222_2);
        send_article_num = headView.findViewById(R.id.send_article_num);
        medal_count = headView.findViewById(R.id.medal_count);
        sender_name = headView.findViewById(R.id.sender_name);
        user_des = headView.findViewById(R.id.user_des);
        head_icon = headView.findViewById(R.id.head_icon);
        head_icon.setOnClickListener(v -> {
            ArrayList<String> pics = new ArrayList<>();
            pics.add(temp.getAvatar());
            UIHelper.toPicPreViewActivity(mContext,  pics,0,false);

        });
        ll_badge = headView.findViewById(R.id.ll_badge);
        sender_tag = headView.findViewById(R.id.sender_tag);
        senderverticity = headView.findViewById(R.id.sender_not_verticity);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        medal_count.setTypeface(typeface);
        send_article_num.setTypeface(typeface);

        //去关注列表
        part2222_2.setOnClickListener((v)->{
            if(StringUtil.isFastClick()){
                return;
            }
            if(myUid.equals(otherUid)){
                UIHelper.toWebViewActivityWithOnLayout(this,StringUtil.getLink("myconcern"),"");
            }else{
                UIHelper.toWebViewActivityWithOnLayout(this,StringUtil.getLink("hisconcern/"  + otherUid),"");
            }
        });


        //设置逻辑
        initDifferLogic();

        if(temp != null){
            user_des.setText(temp.getPro_summary());
            sender_name.setText(temp.getName());
            send_article_num.setText(temp.getBlog_count() + "");

            medal_count.setText(StringUtil.formatPeopleNum(temp.getFans_count() + ""));

            ImageUtil.loadByDefaultHead(this,temp.getAvatar(),head_icon);

            //徽章
            if(temp.getBadge() != null && !temp.getBadge().isEmpty()){
                ll_badge.setVisibility(View.VISIBLE);
                ll_badge.removeAllViews();
                for (int i = 0; i < temp.getBadge().size(); i++) {
                    ImageView imageView = new ImageView(mContext);
                    String icon = temp.getBadge().get(i).getIcon();
                    if(!TextUtils.isEmpty(icon)){
                        ImageUtil.load(mContext,icon,imageView);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.width = SizeUtils.dp2px(14);
                    lp.height = SizeUtils.dp2px(14);
                    lp.gravity = Gravity.CENTER;
                    lp.setMargins(0,0,SizeUtils.dp2px(7),0);
                    imageView.setLayoutParams(lp);
                    ll_badge.addView(imageView);
                }
            }else {
                ll_badge.setVisibility(View.GONE);
            }
        }

    }

    private void posCertInit() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().posCertInit(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<AutherCertInitBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<AutherCertInitBean> response) {
                        AutherCertInitBean temp = response.getReturn_data();
                        if(temp != null){
                            //1-身份证认证页面，2-邮箱提交页面，3-邮箱已提交页面，4-人工审核提交成功页面，5-人工审核失败页面，6-人工审核通过页面
                            int step = temp.getFlow_step();
                            String link = "";

                            if(1 == step){
                                link = StringUtil.getLink("professionidone");
                            }else if(2 == step){
                                link = StringUtil.getLink("professionidthree");
                            }else if(3 == step){
                                link = StringUtil.getLink("professionidfour");
                            }else if(4 == step){
                                link = StringUtil.getLink("manexaminesuc");
                            }else if(5 == step){
                                link = StringUtil.getLink("manexaminefail");
                            }else if(6 == step){
                                link = StringUtil.getLink("professionidsuc");
                            }
                            UIHelper.toWebViewActivityWithOnLayout(UserInfoActivity.this,link,"个人中心");
                        }
                    }
                });
    }


    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mLinearLayoutManager.setSmoothScrollbarEnabled(true);
        mLinearLayoutManager.setAutoMeasureEnabled(true);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCircleRecommentAdapterNew = new CircleRecommentAdapterNew(mAllList);
        mCircleRecommentAdapterNew.setFromUserInfo(true);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setAdapter(mCircleRecommentAdapterNew);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        //添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        initEvent();
    }


    View headView;
    @SuppressLint("CheckResult")
    private void initEvent() {

        mCircleRecommentAdapterNew.setOnLoadMoreListener(() -> {
            ++page;
            getPersonInfo();
        }, mRecyclerView);

        headView = LayoutInflater.from(this).inflate(R.layout.person_head_view,null);

        mCircleRecommentAdapterNew.setHeaderView(headView);


        mCircleRecommentAdapterNew.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.circle_remove:
                    break;
                case R.id.circle_priase:
                    goodBulletin("111");
                    break;
                case R.id.ll_report:
                    showPopupWindow(adapter.getViewByPosition(position, R.id.ll_report));
                    setBackgroundAlpha(this, 0.6f);
                    break;
                case R.id.circle_comment:
                    KLog.d("tag", "评论去圈子详情");
//                    UIHelper.toCommentDetailActivity(this,"5","1",position);
                    break;
                case R.id.circle_share:
                    KLog.d("tag", "圈子分享");
                    showShareDialog();
                    break;
                case R.id.part2222:
                    KLog.d("tag", "图片预览");
                    toPicPrewView();
                    break;
                case R.id.part1111:
                    KLog.d("tag", "去个人界面");
                    break;
                case R.id.toMoreActivity:
                    EventBus.getDefault().post(new toActionEvent("去活动界面"));
                    break;
                case R.id.toMoreFlash:
                    EventBus.getDefault().post(new toActionEvent("去快讯信息流"));
                    break;

                default:
            }
        });


        mCircleRecommentAdapterNew.setOnItemClickListener((adapter, view, position) -> {
            if (StringUtil.isFastClick()) {
                return;
            }
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FirstItemNewAdapter.RIGHT_IMG_TYPE:
//                    UIHelper.toCommentDetailActivity(this,"5","1",position);
                    break;
                default:
            }

        });
    }

    @OnClick({R.id.iv_back,
            R.id.iv_text,R.id.iv_right,
            R.id.noFocus,
            R.id.alreadFocus,
    })
    public void clicks(View view){
        if(StringUtil.isFastClick()){
            return;
        }
        switch (view.getId()){
            case R.id.alreadFocus:
                showCancelFocusDialog();
                break;
            case R.id.noFocus:
                //认证过了直接去打招呼界面
                if("1".equals(mUserInfo.getAuth_email_status()) || "1".equals(mUserInfo.getAuth_card_status())){
                    UIHelper.toHelloMakeActivity(this);
                    overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                }else{
                    showProfessionAuthen();
                }

                break;
            case R.id.iv_right:

                showStateByFollow(temp.getFollow_status());

                showPopupWindowReport(iv_right,shareContent);
                setBackgroundAlpha(this, 0.6f);
                break;
            case R.id.iv_text:
                UIHelper.toSettingActivity(this);
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }

    private String message = "";
    private void followUser() {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",otherUid);
        map.put("message",message + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        alreadFocus.setVisibility(View.VISIBLE);
                        noFocus.setVisibility(View.GONE);
                        ToastUtils.showShort("关注成功");
                        // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
                        temp.setFollow_status(1);
                        EventBus.getDefault().post(new PeopleFocusEvent(otherUid,1));

                        //重新设置关注数 + 1
                        temp.setFans_count(Integer.parseInt(StringUtil.formatPeopleNum((temp.getFans_count() + 1) + "")));
                        medal_count.setText(temp.getFans_count() + "");


                    }
                });
    }


    public void showProfessionAuthen(){
        final ProfessionAutherDialog iosAlertDialog = new ProfessionAutherDialog(this).builder();
        iosAlertDialog.setPositiveButton("让大佬注意你，立即认证", v -> {
            //和外面的认证一样
            posCertInit();
        }).setNegativeButton("下次再说", v -> {
            UIHelper.toHelloMakeActivity(this);
            overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
        }).setMsg("你还未职业认证！").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


   //--------------------------------- 图片预览  ---------------------------------
    private void toPicPrewView() {
        ArrayList<String> photos = new ArrayList<>();
        photos.add("https://desk-fd.zol-img.com.cn/t_s4096x2160c5/g2/M00/02/06/ChMlWl03wq6IbWwqAA-IxrPijHEAAMDAwJ0cR8AD4je242.jpg");
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imageList", photos);
        bundle.putBoolean("fromNet", true);
        bundle.putInt("index", 0);
        Intent intent = new Intent(this, PicPreviewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    //点赞
    protected void goodBulletin(String flash_id) {
        Map<String,String> map = new HashMap<>();
        map.put("type",1 +"");
        map.put("id",flash_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().goodBulletin(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        changePriaseStatus();
                    }
                });
    }

    //取赞
    protected void cancleGoodBulletin(String flash_id) {
        Map<String,String> map = new HashMap<>();
        map.put("type",1 +"");
        map.put("id",flash_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().cancleGoodBulletin(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        changePriaseStatus();
                    }
                });
    }


    //--------------------------------- 浮层  ---------------------------------
    private void showPopupWindow(View view) {
        //加载布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.popupwindow_report, null);
        PopupWindow mPopupWindow = new PopupWindow(inflate);
        TextView report = inflate.findViewById(R.id.report);
        TextView share = inflate.findViewById(R.id.share);
        //必须设置宽和高
        mPopupWindow.setWidth(SizeUtils.dp2px(80f));
        mPopupWindow.setHeight(SizeUtils.dp2px(88f));
        //点击其他地方隐藏,false为无反应
        mPopupWindow.setFocusable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //以view的左下角为原点，xoff为正表示向x轴正方向偏移像素
            mPopupWindow.showAsDropDown(view, -SizeUtils.dp2px(10f), SizeUtils.dp2px(10f));
        }


        //对popupWindow进行显示
        mPopupWindow.update();
        //消失时将透明度设置回来
        mPopupWindow.setOnDismissListener(() -> {
            if (null != this) {
                setBackgroundAlpha(this, 1f);
            }
        });

        report.setOnClickListener(view1 -> {
            ToastUtils.showShort("发请求，举报成功");
             reportUser();
            mPopupWindow.dismiss();
        });

        share.setOnClickListener(view1 -> {
            blockUser();
            mPopupWindow.dismiss();
        });
    }

    private void blockUser() {
        Map<String,String> map = new HashMap<>();
        map.put("block_uid",otherUid);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().blockUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.showShort("您已屏蔽对方");
                        part3333.setVisibility(View.GONE);

                        // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
                        temp.setFollow_status(2);
                        EventBus.getDefault().post(new PeopleFocusEvent(otherUid,2));
                    }
                });
    }


    private void unblockUser() {
        Map<String,String> map = new HashMap<>();
        map.put("block_uid",otherUid);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().unblockUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //屏蔽用户成功
                        part3333.setVisibility(View.VISIBLE);
                        noFocus.setVisibility(View.VISIBLE);
                        alreadFocus.setVisibility(View.GONE);
                        himcloseme.setVisibility(View.GONE);
                        ToastUtils.showShort("取消屏蔽成功");

                        // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
                        temp.setFollow_status(0);
                        EventBus.getDefault().post(new PeopleFocusEvent(otherUid,0));
                    }
                });
    }


    private void showPopupWindowReport(View view,String content) {
        //加载布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.popupwindow_report_userinfo, null);
        PopupWindow mPopupWindow = new PopupWindow(inflate);
        TextView report = inflate.findViewById(R.id.report);
        TextView share = inflate.findViewById(R.id.share);
        //必须设置宽和高
        mPopupWindow.setWidth(SizeUtils.dp2px(80f));
        mPopupWindow.setHeight(SizeUtils.dp2px(88f));
        //点击其他地方隐藏,false为无反应
        mPopupWindow.setFocusable(true);
        //设置文本
        share.setText(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //以view的左下角为原点，xoff为正表示向x轴正方向偏移像素
            mPopupWindow.showAsDropDown(view, -SizeUtils.dp2px(52f), SizeUtils.dp2px(8f));
        }

        //对popupWindow进行显示
        mPopupWindow.update();
        //消失时将透明度设置回来
        mPopupWindow.setOnDismissListener(() -> {
            if (null != this) {
                setBackgroundAlpha(this, 1f);
            }
        });

        report.setOnClickListener(view1 -> {
            reportUser();
            mPopupWindow.dismiss();
        });

        share.setOnClickListener(view1 -> {
            mPopupWindow.dismiss();

            if(shareContent.equals("屏蔽")){
                showNotSeeEachOther();
            }else if(shareContent.equals("取消屏蔽")){
                unblockUser();
            }

        });
    }

    private void reportUser() {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",otherUid);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().reportUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.showShort("举报成功");
                    }
                });
    }


    //设置页面的透明度   1表示不透明
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            //此行代码主要是解决在华为手机上半透明效果无效的bug
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }



    private void showShareDialog() {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(this).builder();
        alertDialog.setShareDynamicView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    KLog.d("tag", "朋友圈 是张图片");
                    WxShareBean bean = new WxShareBean();
                    shareWxCircleByWeb(bean);
                    break;
                case 1:
                    KLog.d("tag", "朋友 是链接");
                    WxShareBean bean2 = new WxShareBean();
                    shareWxByWeb(bean2);
                    break;
                case 4:
                    KLog.d("tag", "转发到动态");
//                    UIHelper.toTranspondActivity(this);
//                    //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
//                    overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                    break;
                default:
            }
        });
        alertDialog.show();
    }


    public void showNotSeeEachOther(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("屏蔽", v -> {
            blockUser();

        }).setNegativeButton("再想想", v -> {

        }).setMsg("屏蔽" + temp.getName() + "?").setTitle("屏蔽后，你们不能互相关注且在推荐频道你将看不到他的圈子动态").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    /** --------------------------------- 关注 取关  ---------------------------------*/
    //1-去关注，0-取消关注
    private String focus_type = "1";
    private String author_id;

    public void showCancelFocusDialog(){
        String author ;
        author_id = "1";
        author = "小羽毛";
        final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(this).builder();
        iosAlertDialog.setPositiveButton("取消关注", v -> {

            unfollowUser();

        }).setNegativeButton("再想想", v -> {}).setMsg("取消关注？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();

    }

    private void unfollowUser() {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",otherUid);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().unfollowUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        noFocus.setVisibility(View.VISIBLE);
                        alreadFocus.setVisibility(View.GONE);
                        ToastUtils.showShort("取消关注成功");
                        // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
                        temp.setFollow_status(0);
                        EventBus.getDefault().post(new PeopleFocusEvent(otherUid, 0));

                        // - 1
                        temp.setFans_count(Integer.parseInt(StringUtil.formatPeopleNum((temp.getFans_count() - 1) + "")));
                        medal_count.setText(temp.getFans_count() + "");
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(data != null){
                message = data.getExtras().getString("message");
                KLog.d("tqg","接收到的文字是 " + message);
            }
        }

        followUser();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfessionEvent(ProfessionEvent event){
       if("2".equals( event.getType())){
           getPersonInfo();
       }

    }


}
