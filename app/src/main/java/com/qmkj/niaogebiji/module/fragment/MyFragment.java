package com.qmkj.niaogebiji.module.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivityV2;
import com.qmkj.niaogebiji.module.activity.UserInfoV2Activity;
import com.qmkj.niaogebiji.module.adapter.FeatherItemAdapterMy;
import com.qmkj.niaogebiji.module.adapter.FeatherItemAdapterNew;
import com.qmkj.niaogebiji.module.adapter.MyItemAdapter;
import com.qmkj.niaogebiji.module.bean.AutherCertInitBean;
import com.qmkj.niaogebiji.module.bean.BadegsAllBean;
import com.qmkj.niaogebiji.module.bean.ExchageDetailBean;
import com.qmkj.niaogebiji.module.bean.FeatherProductBean;
import com.qmkj.niaogebiji.module.bean.MyBean;
import com.qmkj.niaogebiji.module.bean.MyProductBean;
import com.qmkj.niaogebiji.module.bean.OfficialBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.ShowRedPointEvent;
import com.qmkj.niaogebiji.module.widget.CustomImageSpan;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.NoLineCllikcSpan;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.apache.commons.lang3.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.udesk.UdeskSDKManager;
import cn.udesk.config.UdeskConfig;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import udesk.core.UdeskConst;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:1.用户  + 认证(公司 ) + 徽章
 */
public class MyFragment extends BaseLazyFragment {

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.red_point)
    FrameLayout red_point;


    @BindView(R.id.read_time)
    TextView read_time;

    @BindView(R.id.acticle_count)
    TextView acticle_count;

    @BindView(R.id.medal_count)
    TextView medal_count;

    @BindView(R.id.feather_count)
    TextView feather_count;


    @BindView(R.id.name_vertify)
    TextView name_vertify;

    @BindView(R.id.name_author_tag)
    TextView name_author_tag;

    @BindView(R.id.name_vertify_no)
    TextView name_vertify_no;

    @BindView(R.id.part_ll_acticle)
    LinearLayout part_ll_acticle;


    @BindView(R.id.toVip)
    ImageView toVip;


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.head_icon)
    CircleImageView head_icon;


    @BindView(R.id.vip_time)
    TextView vip_time;

    @BindView(R.id.ll_badge)
    LinearLayout ll_badge;

    @BindView(R.id.recycler1111)
    RecyclerView recycler1111;

    @BindView(R.id.ll_product)
    LinearLayout ll_product;



    //商品适配器
    GridLayoutManager mProductGridLayoutManager;
    FeatherItemAdapterMy mProductItemNewAdapter;
    //集合1
    List<FeatherProductBean.Productean.ProductItemBean> temps;


    //适配器
    MyItemAdapter mMyItemAdapter;
    //组合集合
    List<MyBean> mAllList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    private RegisterLoginBean.UserInfo mUserInfo;


    private int [] images = new int[]{ R.mipmap.icon_my_dynamic,R.mipmap.icon_my_medal,
            R.mipmap.icon_my_focus,R.mipmap.icon_my_circle,
            R.mipmap.icon_my_feather,R.mipmap.icon_my_vertify,R.mipmap.icon_my_invite
           };

    private String [] names = new String[]{"我的发布","我的徽章","我的收藏","我的关注","我的羽毛","认证中心","邀请好友"};

    public static MyFragment getInstance() {
        return new MyFragment();
    }


    //初始化布局管理器
    private void initVertical() {
        mProductGridLayoutManager = new GridLayoutManager(getActivity(),4);
        //设置布局管理器
        recycler1111.setLayoutManager(mProductGridLayoutManager);
        //禁用change动画
        ((SimpleItemAnimator)recycler1111.getItemAnimator()).setSupportsChangeAnimations(false);
        //设置适配器
        mProductItemNewAdapter = new FeatherItemAdapterMy(temps);
        recycler1111.setAdapter(mProductItemNewAdapter);
        //解决数据加载不完
        recycler1111.setNestedScrollingEnabled(false);
        recycler1111.setHasFixedSize(true);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }


    @Override
    protected void initView() {


//        getRecommendMallList();

        initVertical();

//        getUserInfo();

        initLayout();
        getData();
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        name.setTypeface(typeface);
        read_time.setTypeface(typeface);
        medal_count.setTypeface(typeface);
        acticle_count.setTypeface(typeface);
        feather_count.setTypeface(typeface);
    }



    private void getProducts(List<FeatherProductBean.Productean.ProductItemBean> temp) {
        //测试数据
//        for (int i = 0; i < 4; i++) {
//            temps.add(new FeatherProductBean.Productean.ProductItemBean());
//        }

        mProductItemNewAdapter.setNewData(temp);
    }

    List<BadegsAllBean.BadegBean> listall = new ArrayList<>();
    private void getBadgeList() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getBadgeList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<BadegsAllBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<BadegsAllBean> response) {
                        BadegsAllBean temp = response.getReturn_data();
                        if(null != temp){
                            List<BadegsAllBean.BadegBean> list1 = temp.getShow_badges();
                            List<BadegsAllBean.BadegBean> list2 = temp.getCollect_badges();

                            listall.clear();
                            listall.addAll(list1);
                            listall.addAll(list2);
                            setBadege();
                        }

                    }
                });
    }

    //设置个人中心徽章
    private void setBadege() {
        if(!listall.isEmpty()){
            ll_badge.removeAllViews();
            for (int i = 0; i < listall.size(); i++) {
                ImageView imageView = new ImageView(mContext);
                String icon = listall.get(i).getIcon();
                if(!TextUtils.isEmpty(icon)){
                    ImageUtil.load(mContext,icon,imageView);
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.width = SizeUtils.dp2px(18);
                lp.height = SizeUtils.dp2px(18);
                lp.gravity = Gravity.CENTER;
                lp.setMargins(0,0,SizeUtils.dp2px(8),0);
                imageView.setLayoutParams(lp);
                ll_badge.addView(imageView);
            }
        }
    }

    //点击切换fragement会调用
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause

        }else{
            //resume
            mUserInfo = StringUtil.getUserInfoBean();
            if(null != mUserInfo){
                getUserInfo();
                getRecommendMallList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = StringUtil.getUserInfoBean();
        if(null != mUserInfo){
            getUserInfo();
            getRecommendMallList();
        }
    }



    private void getRecommendMallList() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getRecommendMallList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<MyProductBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<MyProductBean> response) {
                        MyProductBean tem = response.getReturn_data();
                        temps = tem.getList();
                        if(temps != null && !temps.isEmpty()){
                            getProducts(temps);
                        }else{
                            ll_product.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNetFail(String msg) {
                        //其他错误也将其隐藏
                        ll_product.setVisibility(View.GONE);
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
//                        KLog.d("tag","邀请链接是 " + mUserInfo.getInvite_url());
                        if(null != mUserInfo){
                            StringUtil.setUserInfoBean(mUserInfo);
                            setUserInfo();
                            getBadgeList();
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

//    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    private void setUserInfo() {
        if(null != mUserInfo){
            //是否显示红点  个人中心消息通知：1-有新消息，0-无新消息
           if(!TextUtils.isEmpty( mUserInfo.getIs_red())){
               if("1".equals( mUserInfo.getIs_red())){
                   red_point.setVisibility(View.VISIBLE);
               }else if("0".equals( mUserInfo.getIs_red())){
                   red_point.setVisibility(View.GONE);
               }
               EventBus.getDefault().post(new ShowRedPointEvent(mUserInfo.getIs_red()));
           }

            if(!TextUtils.isEmpty(mUserInfo.getVip_last_time())){
                vip_time.setText("鸟哥笔记VIP剩余"+ mUserInfo.getVip_last_time() + "天");
            }

            TextPaint paint = name.getPaint();
            paint.setFakeBoldText(true);

            if(!TextUtils.isEmpty(mUserInfo.getNickname())){
                name.setText(mUserInfo.getNickname());

                // 解密
//                name.setText(StringEscapeUtils.unescapeJava(mUserInfo.getNickname().replace("\\\\u","\\u")));


            }else{
                name.setText(mUserInfo.getName());
            }

//            if(没有认证显示去认证 -- 名字是否为空判断){ ok
//                显示：立即完善信息，建立人脉(下划线)
//                点击：编辑名片信息
//            }else{
//                if( 认证没通过){
//                    显示：公司 + 职位 + 立即职业认证
//                    点击：认证中心
//                }else{
//                    显示：公司 + 职位 + 认证图标
//                }
//            }

//            if(关联作者){ ok
//                显示：发布文章数
//            }else{
//                显示：隐藏布局
//            }



            KLog.d("tag","author_uid " + mUserInfo.getUid());
            if("1".equals(mUserInfo.getIs_author())){
                //关联
                part_ll_acticle.setVisibility(View.VISIBLE);
                acticle_count.setText(mUserInfo.getAuthor_article_num());
            }


            //name_vertify 完善信息
            //name_author_tag 公司 + 职位
            //name_vertify_no 立即认证
            if(TextUtils.isEmpty(mUserInfo.getCompany_name()) &&
                    TextUtils.isEmpty(mUserInfo.getPosition()) ){
                name_vertify.setVisibility(View.VISIBLE);
                name_vertify_no.setVisibility(View.GONE);
                name_author_tag.setVisibility(View.GONE);
                name_vertify.setText("立即完善信息，建立人脉");
                //下划线
                name_vertify.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                //抗锯齿
                name_vertify.getPaint().setAntiAlias(true);
                name_vertify.setOnClickListener(v -> {
                    MobclickAgentUtils.onEvent(UmengEvent.i_auth_2_0_0);
                    if(StringUtil.isFastClick()){
                        return;
                    }

                    MobclickAgentUtils.onEvent(UmengEvent.i_complete_2_2_0);


                    UIHelper.toUserInfoModifyActivity(getActivity());

                });
            }else{
                //认证是否通过 -- 这里不用改(不用显示用户名在认证的情况下)
                if("1".equals(mUserInfo.getAuth_email_status()) || "1".equals(mUserInfo.getAuth_card_status())){
                    name_author_tag.setVisibility(View.VISIBLE);
                    name_vertify.setVisibility(View.GONE);
                    name_vertify_no.setVisibility(View.GONE);
                    name_author_tag.setText( (TextUtils.isEmpty(mUserInfo.getCompany_name())?"":mUserInfo.getCompany_name()) + " " +
                            (TextUtils.isEmpty(mUserInfo.getPosition())?"":mUserInfo.getPosition()));

                    //居中对齐imageSpan
                    CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_authen_company1,2);
                    SpannableString spanString2 = new SpannableString("icon");
                    spanString2.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    name_author_tag.append(spanString2);


                }else{
//                    name_author_tag.setVisibility(View.VISIBLE);
//                    name_vertify_no.setVisibility(View.VISIBLE);
//                    //下划线
//                    name_vertify_no.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//                    //抗锯齿
//                    name_vertify_no.getPaint().setAntiAlias(true);
//                    name_vertify.setVisibility(View.GONE);
//                    name_author_tag.setText( (TextUtils.isEmpty(mUserInfo.getCompany_name())?"":mUserInfo.getCompany_name()) + " " +
//                            (TextUtils.isEmpty(mUserInfo.getPosition())?"":mUserInfo.getPosition()));
//
//                    name_vertify_no.setOnClickListener(v ->{
//
//                        MobclickAgentUtils.onEvent(UmengEvent.i_auth_2_2_0);
//
//                        UIHelper.toNewWebView(getActivity(),StringUtil.getLink("certificatecenter"));
//                    });


                    //立即认证放在文字后面
                    name_author_tag.setVisibility(View.VISIBLE);
                    name_vertify.setVisibility(View.GONE);
                    name_vertify_no.setVisibility(View.GONE);
                    name_author_tag.setText( (TextUtils.isEmpty(mUserInfo.getCompany_name())?"":mUserInfo.getCompany_name()) + " " +
                            (TextUtils.isEmpty(mUserInfo.getPosition())?"":mUserInfo.getPosition()));

                    NoLineCllikcSpan clickableSpan = new NoLineCllikcSpan() {
                        @Override
                        public void onClick(View widget) {
                            if(StringUtil.isFastClick()){
                                return;
                            }
                            UIHelper.toNewWebView(getActivity(),StringUtil.getLink("certificatecenter"));
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {  //设置样式
                            super.updateDrawState(ds);
                            ds.setColor(mContext.getResources().getColor(R.color.zan_select));  //字体颜色
                            ds.setUnderlineText(true);      //下划线
                        }
                    };

                    ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.zan_select));
                    //居中对齐imageSpan
                    SpannableString spanString2 = new SpannableString("  立即认证");
                    spanString2.setSpan(fCs2, 2, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanString2.setSpan(clickableSpan, 2, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    name_author_tag.append(spanString2);
                    //能点击相应事件
                    name_author_tag.setMovementMethod(LinkMovementMethod.getInstance());

                }
            }




            //是否领过vip 1-领取过，0-未领取过 180天字段
            if("1".equals(mUserInfo.getIs_180_vip())){
                toVip.setVisibility(View.GONE);
            }else{
                toVip.setVisibility(View.VISIBLE);
            }

            ImageUtil.loadByDefaultHead(mContext,mUserInfo.getAvatar(),head_icon);

//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.bg_flash);
//            Bitmap res = ImageUtils.addTextWatermark(bitmap,"靓仔",16,getResources().getColor(R.color.text_first_color),10,10);
//            head_icon.setImageBitmap(res);

            //个人中心消息通知：1-有新消息，0-无新消息
            if("1".equals(mUserInfo.getIs_red())){
                red_point.setVisibility(View.VISIBLE);
            }else{
                red_point.setVisibility(View.GONE);
            }

            //更新本地用户信息
            StringUtil.setUserInfoBean(mUserInfo);

            if(!TextUtils.isEmpty(mUserInfo.getPoint())){
                feather_count.setText(mUserInfo.getPoint());
            }

            TextPaint paint2 = feather_count.getPaint();
            paint2.setFakeBoldText(true);

            TextPaint paint3 = medal_count.getPaint();
            paint3.setFakeBoldText(true);
            if(!TextUtils.isEmpty(mUserInfo.getBadge_num())){
                medal_count.setText(mUserInfo.getBadge_num());
            }

            if(!TextUtils.isEmpty(mUserInfo.getRead_article_num())){
                read_time.setText(mUserInfo.getRead_article_num());
            }

        }

    }


    private void getData() {

        MyBean bean1 ;
        for (int i = 0; i < names.length; i++) {
            bean1 = new MyBean();
            bean1.setResId(images[i]);
            bean1.setName(names[i]);
            mAllList.add(bean1);
        }

        mMyItemAdapter.setNewData(mAllList);
    }




    private void initLayout() {
        mGridLayoutManager = new GridLayoutManager(getActivity(),4);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mMyItemAdapter = new MyItemAdapter(mAllList);
        mRecyclerView.setAdapter(mMyItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
        mMyItemAdapter.setOnItemClickListener((adapter, view, position) -> {

            if(StringUtil.isFastClick()){
                return;
            }
            switch (position){
                case 0:

                    MobclickAgentUtils.onEvent(UmengEvent.i_dynamic_2_0_0);
                    //  后面添加标题
                    UIHelper.toNewWebView(getActivity(),StringUtil.getLink("myactivity"),"我的发布");

                    break;
                case 1:

                    MobclickAgentUtils.onEvent(UmengEvent.i_badge_2_0_0);

                    UIHelper.toWebViewBadgeActivity(getActivity(),StringUtil.getLink("mybadge"),"webview_badges");


                    break;
                case 2:
                    MobclickAgentUtils.onEvent(UmengEvent.i_indexcollect_2_0_0);

                    UIHelper.toMyCollectionListActivity(getActivity());

                    break;
                case 3:
                    MobclickAgentUtils.onEvent(UmengEvent.i_quanzifollow_2_0_0);

                    UIHelper.toNewWebView(getActivity(),StringUtil.getLink("myconcern"),"我的关注");


                    break;
                case 4:
                    MobclickAgentUtils.onEvent(UmengEvent.i_task_2_0_0);

                    UIHelper.toFeatherctivity(getActivity());
                    break;
                case 5:

                    MobclickAgentUtils.onEvent(UmengEvent.i_certcenter_2_2_0);

                    UIHelper.toNewWebView(getActivity(),StringUtil.getLink("certificatecenter"));

                    break;
                case 6:
                    MobclickAgentUtils.onEvent(UmengEvent.i_invite_2_0_0);

                    UIHelper.toInviteActivity(getActivity());
                    break;

                    default:
            }
        });
    }


    @Override
    public void initData() {
    }


    @OnClick({
                R.id.all_part1111,
                R.id.toSet,
                R.id.about_ll,
                R.id.ll_badge,
                R.id.part3333_3,
                R.id.toExchange,
                R.id.rl_vip_time,
                R.id.toVip,
                R.id.toQue,R.id.advice_ll,
                R.id.head_icon,
                R.id.rl_newmsg,
                R.id.part2222_2,
                 R.id.toUserInfo
    })
    public void clicks(View view){
        if(StringUtil.isFastClick()){
            return;
        }

        switch (view.getId()){
            case R.id.part2222_2:
                //获得徽章
                MobclickAgentUtils.onEvent(UmengEvent.i_mybadge_2_0_0);
                UIHelper.toWebViewBadgeActivity(getActivity(),StringUtil.getLink("mybadge"),"webview_badges");

                break;
            case R.id.rl_newmsg:
                MobclickAgentUtils.onEvent(UmengEvent.i_message_2_0_0);

                UIHelper.toWebViewActivityWithOnLayout(getActivity(),StringUtil.getLink("messagecenter"),"显示一键已读消息");


                break;
            case R.id.toUserInfo:
                MobclickAgentUtils.onEvent(UmengEvent.i_icon_2_0_0);

                UIHelper.toUserInfoV2Activity(getActivity(),mUserInfo.getUid());

                break;
            case R.id.head_icon:
                ArrayList<String> pics = new ArrayList<>();
                pics.add(mUserInfo.getAvatar());
                //手动跳转
                Intent intent = new Intent(mContext, PicPreviewActivityV2.class);
                Bundle bundle = new Bundle ();
                bundle.putStringArrayList ("imageList", pics);
                bundle.putBoolean("fromNet",true);
                bundle.putInt("index",0);
                bundle.putString("from","userinfo");
                bundle.putBoolean("isShowDown",true);
                intent.putExtras(bundle);
                mContext.startActivity(intent);

                break;
            case R.id.advice_ll:
                MobclickAgentUtils.onEvent(UmengEvent.i_feedback_2_0_0);

                //TODO 改为链接
                UIHelper.toNewWebView(getActivity(),Constant.ADVICE);
                break;
            case R.id.toQue:
                MobclickAgentUtils.onEvent(UmengEvent.i_question_2_0_0);

                UIHelper.toNewWebView(getActivity(),StringUtil.getLink("questions"));
//                UIHelper.toWebViewActivityWithOnLayout(getActivity(),StringUtil.getLink("questions"),"questions");
                break;
            case R.id.about_ll:
                MobclickAgentUtils.onEvent(UmengEvent.i_about_2_0_0);

                UIHelper.toAboutUsActivity(getActivity());
                break;
            case R.id.toVip:
                MobclickAgentUtils.onEvent(UmengEvent.i_vip_ad_2_0_0);
                //TODO 2020.1.14 领取vip返回不了
                UIHelper.toWebViewAllActivity(getActivity(),StringUtil.getLink("vipmember"),"vipmember");

                break;
            case R.id.rl_vip_time:
                MobclickAgentUtils.onEvent(UmengEvent.i_vip_2_0_0);
                MobclickAgentUtils.onEvent(UmengEvent.i_renewal_2_0_0);

                UIHelper.toWebViewAllActivity(getActivity(),StringUtil.getLink("vipmember"),"vipmember");

                break;
            case R.id.toExchange:
                MobclickAgentUtils.onEvent(UmengEvent.i_exchange_2_0_0);
                KLog.d("tag","去羽毛商城");
                UIHelper.toFeatherProductListActivity(getActivity());
                break;
            case R.id.all_part1111:
                MobclickAgentUtils.onEvent(UmengEvent.i_exchange_more_2_2_0);
                KLog.d("tag","更多好礼");
                UIHelper.toFeatherProductListActivity(getActivity());
                break;
            case R.id.part3333_3:
                MobclickAgentUtils.onEvent(UmengEvent.i_myfeather_2_0_0);

                KLog.d("tag","去羽毛任务");
                UIHelper.toFeatherctivity(getActivity());
                break;
            case R.id.ll_badge:
                KLog.d("tag","去徽章详情页");
                UIHelper.toWebViewBadgeActivity(getActivity(),StringUtil.getLink("mybadge"),"mybadge");
                break;
            case R.id.toSet:
                MobclickAgentUtils.onEvent(UmengEvent.i_setting_2_0_0);

                UIHelper.toSettingActivity(getActivity());
                break;

            default:
        }
    }



    /** --------------------------------- 意见反馈  ---------------------------------*/
    private void toUDesk(){
        UdeskConfig.Builder builder = new UdeskConfig.Builder();
        //token为随机获取的，如 UUID.randomUUID().toString()
        String sdktoken = UUID.randomUUID().toString();
        KLog.d("tag",sdktoken + "");
        Map<String, String> info = new HashMap<>();
        info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN, sdktoken);
        //以下信息是可选
        if(null != mUserInfo){
            info.put(UdeskConst.UdeskUserInfo.NICK_NAME,mUserInfo.getNickname());
            info.put(UdeskConst.UdeskUserInfo.CELLPHONE,mUserInfo.getMobile());
            builder.setCustomerUrl(mUserInfo.getAvatar());
        }
        info.put(UdeskConst.UdeskUserInfo.DESCRIPTION,"描述信息");
        builder.setUsephoto(true);
        builder.setUseEmotion(true);
        builder.setUseMore(true);
        builder.setUserForm(true);
        builder.setUserSDkPush(true);
        builder.setFormCallBack(context -> {
            getServiceWechatPic();
        });
        builder.setDefualtUserInfo(info);
        UdeskSDKManager.getInstance().entryChat(BaseApp.getApplication(), builder.build(), sdktoken);
    }


    /** --------------------------------- 意见反馈  ---------------------------------*/
    //临时存储变量
    private OfficialBean mOfficialBean;
    private String advice_url;

    private void getServiceWechatPic() {
        Map<String,String> map = new HashMap<>();

        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().getServiceWechatPic(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<OfficialBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<OfficialBean> response) {
                        KLog.e("tag",response.getReturn_code());
                        mOfficialBean = response.getReturn_data();
                        if(null != mOfficialBean){
                            advice_url = mOfficialBean.getQuestion_url();
//                            UIHelper.toWebViewActivity(getActivity(),advice_url);
                        }
                    }

                });
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
                            UIHelper.toWebViewActivityWithOnLayout(getActivity(),link,"个人中心");
                        }
                    }
                });
    }


}
