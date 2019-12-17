package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
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

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ProfessionAutherDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CircleRecommendAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.ActiclePeopleBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.PersonUserInfoBean;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.fragment.CircleRecommendFragment;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

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
 */
public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.iv_text)
    TextView iv_text;

    TextView send_article_num;
    TextView medal_count;
    TextView sender_name;
    TextView user_des;
    CircleImageView head_icon;
    LinearLayout ll_badge;
    TextView sender_not_verticity;

    @BindView(R.id.iv_right)
    ImageView iv_right;

    @BindView(R.id.part3333)
    LinearLayout part3333;


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private int page = 1;
    //适配器
    CircleRecommendAdapter mCircleRecommendAdapter;
    //组合集合
    List<MultiCircleNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    //先用这个bean代替
    NewsDetailBean mNewsDetailBean;

    private int myPosition;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo;
    }

    @Override
    protected void initView() {

        myUid = getIntent().getStringExtra("uid");

        iv_text.setVisibility(View.GONE);
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.mipmap.icon_userinfo_other_1);
        initLayout();

        getPersonInfo();

    }




    private String  myUid;
    private  PersonUserInfoBean temp;
    private void getPersonInfo() {
        Map<String,String> map = new HashMap<>();
        map.put("uid",myUid);
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
                        temp = response.getReturn_data();
                        if(temp != null){
                            setData1();
                        }
                    }
                });
    }

    private void setData1() {
        //头部信息
        send_article_num = headView.findViewById(R.id.send_article_num);
        medal_count = headView.findViewById(R.id.medal_count);
        sender_name = headView.findViewById(R.id.sender_name);
        user_des = headView.findViewById(R.id.user_des);
        head_icon = headView.findViewById(R.id.head_icon);
        ll_badge = headView.findViewById(R.id.ll_badge);
        sender_not_verticity = headView.findViewById(R.id.ll_badge);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        medal_count.setTypeface(typeface);
        send_article_num.setTypeface(typeface);

        sender_not_verticity.setOnClickListener((v)->{
            showProfessionAuthen();
        });

        head_icon.setOnClickListener((v)->{
            toPicPrewView();

        });


        sender_name.setText(temp.getName());
        send_article_num.setText(temp.getBlog_count() + "");
        medal_count.setText(temp.getFans_count() + "");

        ImageUtil.load(this,temp.getAvatar(),head_icon);

        if(temp.getBadges() != null && !temp.getBadges().isEmpty()){
               ll_badge.removeAllViews();
                for (int i = 0; i < temp.getBadges().size(); i++) {
                    ImageView imageView = new ImageView(mContext);
                    String icon = temp.getBadges().get(i).getIcon();
                    if(!TextUtils.isEmpty(icon)){
                        ImageUtil.load(mContext,icon,imageView);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.width = SizeUtils.dp2px(22);
                    lp.height = SizeUtils.dp2px(22);
                    lp.gravity = Gravity.CENTER;
                    lp.setMargins(0,0,SizeUtils.dp2px(8),0);
                    imageView.setLayoutParams(lp);
                    ll_badge.addView(imageView);
                }
            }
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
        mCircleRecommendAdapter = new CircleRecommendAdapter(mAllList);
        mRecyclerView.setAdapter(mCircleRecommendAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        //添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        initEvent();
    }



    private void getData() {

//        NewsItemBean itemBean;
//        FirstItemBean firstItemBean;
//        MultiCircleNewsBean bean1;
//        for (int i = 0; i < 10; i++) {
//            if (i == 2) {
//                firstItemBean = new FirstItemBean();
//                bean1 = new MultiCircleNewsBean();
//                bean1.setItemType(4);
//                bean1.setFirstItemBean(firstItemBean);
//            } else {
//                itemBean = new NewsItemBean();
//                itemBean.setTitle("名称 " + i);
//                bean1 = new MultiCircleNewsBean();
//                if (i == 4) {
//                    bean1.setItemType(2);
//                } else if (i == 5) {
//                    bean1.setItemType(3);
//                } else {
//                    bean1.setItemType(1);
//                }
//
//                bean1.setNewsItemBean(itemBean);
//            }
//            mAllList.add(bean1);
//        }

        mCircleRecommendAdapter.setNewData(mAllList);

    }


    View headView;
    @SuppressLint("CheckResult")
    private void initEvent() {

        mCircleRecommendAdapter.setOnLoadMoreListener(() -> {
            ++page;
            getData();
        }, mRecyclerView);

        headView = LayoutInflater.from(this).inflate(R.layout.person_head_view,null);




        mCircleRecommendAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            myPosition = position;
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
                    UIHelper.toCommentDetailActivity(this,"5","1");
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


        mCircleRecommendAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (StringUtil.isFastClick()) {
                return;
            }
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FirstItemNewAdapter.RIGHT_IMG_TYPE:
                    UIHelper.toCommentDetailActivity(this,"5","1");
                    break;
                default:
            }

        });
    }

    @OnClick({R.id.iv_back,
            R.id.iv_text,R.id.iv_right,
            R.id.noFocus,
            R.id.alreadFocus
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.alreadFocus:
                showCancelFocusDialog();
                break;
            case R.id.noFocus:
//                ToastUtils.showShort("关注成功");
                showProfessionAuthen();
//                UIHelper.toHelloMakeActivity(this);
//                overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                break;
            case R.id.iv_right:
                showPopupWindow2(iv_right,"屏蔽");
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



    public void showProfessionAuthen(){
        final ProfessionAutherDialog iosAlertDialog = new ProfessionAutherDialog(this).builder();
        iosAlertDialog.setPositiveButton("让大佬注意你，立即认证", v -> {
            KLog.d("tag","h5 去验证 ");
        }).setNegativeButton("下次再说", v -> {

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
            mPopupWindow.dismiss();
        });

        share.setOnClickListener(view1 -> {
            mPopupWindow.dismiss();
        });
    }


    private void showPopupWindow2(View view,String content) {
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
            ToastUtils.showShort("发请求，举报成功");
            mPopupWindow.dismiss();
        });

        share.setOnClickListener(view1 -> {
            mPopupWindow.dismiss();
            showNotSeeEachOther();
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
                    UIHelper.toTranspondActivity(this);
                    //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
                    overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                    break;
                default:
            }
        });
        alertDialog.show();
    }


    public void showNotSeeEachOther(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("屏蔽", v -> {
            ToastUtils.showShort("您已屏蔽对方");
            part3333.setVisibility(View.GONE);
        }).setNegativeButton("再想想", v -> {

        }).setMsg("屏蔽小羽毛?").setTitle("屏蔽后，你们不能互相关注且在推荐频道你将看不到他的圈子动态").setCanceledOnTouchOutside(false);
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

        }).setNegativeButton("再想想", v -> {}).setMsg("是否"  +"取消关注「" + author  +"」").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
//        if(mAuthor.getIs_follow() == 1){
//            name = "取消";
//            focus_type = "0";
//            final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(this).builder();
//            iosAlertDialog.setPositiveButton("取消关注", v -> {
//
//            }).setNegativeButton("再想想", v -> {}).setMsg("确定要 " + name +"关注「" + author  +"」").setCanceledOnTouchOutside(false);
//            iosAlertDialog.show();
//        }else{
//            focus_type = "1";
//
//        }
    }
}
