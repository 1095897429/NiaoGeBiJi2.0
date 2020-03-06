package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ProfessionAutherDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.tab.TabLayout;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.AutherCertInitBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.PersonUserInfoBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.event.PeopleFocusEvent;
import com.qmkj.niaogebiji.module.event.ProfessionEvent;
import com.qmkj.niaogebiji.module.event.ShowSearchEvent;
import com.qmkj.niaogebiji.module.event.ShowTopTitleEvent;
import com.qmkj.niaogebiji.module.fragment.CircleRecommendFragmentNew;
import com.qmkj.niaogebiji.module.fragment.FirstItemFragment;
import com.qmkj.niaogebiji.module.fragment.ToolCollectionListFragment;
import com.qmkj.niaogebiji.module.fragment.ToolRecommentListFragment;
import com.qmkj.niaogebiji.module.fragment.UserArticleFragment;
import com.qmkj.niaogebiji.module.fragment.UserCircleFragment;
import com.qmkj.niaogebiji.module.widget.CustomImageSpan;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-15
 * 描述:用户信息版本2
 * 1.实名认证通过展示图标 ，不通过不显示
 */
public class UserInfoV2Activity extends BaseActivity {

    @BindView(R.id.part3333)
    LinearLayout part3333;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.iv_right)
    ImageView iv_right;

    @BindView(R.id.iv_right_1)
    ImageView iv_right_1;


    @BindView(R.id.head_icon)
    CircleImageView head_icon;

    @BindView(R.id.iv_text)
    TextView iv_text;

    @BindView(R.id.name_author_tag)
    TextView name_author_tag;

    @BindView(R.id.sender_name)
    TextView sender_name;

    @BindView(R.id.user_des)
    TextView user_des;

    @BindView(R.id.medal_count)
    TextView medal_count;

    @BindView(R.id.ll_badge)
    LinearLayout ll_badge;


    @BindView(R.id.sender_not_verticity)
    TextView sender_not_verticity;

    @BindView(R.id.noFocus)
    TextView noFocus;

    @BindView(R.id.alreadFocus)
    TextView alreadFocus;

    @BindView(R.id.himcloseme)
    TextView himcloseme;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.small_head_icon)
    ImageView small_head_icon;

    @BindView(R.id.part_small_head)
    LinearLayout part_small_head;

    @BindView(R.id.focus)
    TextView focus;

    @BindView(R.id.already_focus)
    TextView already_focus;


    @BindView(R.id.fans_num)
    TextView fans_num;


    @BindView(R.id.rl_author)
    LinearLayout rl_author;


    @BindView(R.id.head_author_icon)
    CircleImageView head_author_icon;


    @BindView(R.id.author_name)
    TextView author_name;


    @BindView(R.id.author_desc)
    TextView author_desc;


    @BindView(R.id.hint_num)
    TextView hint_num;

    @BindView(R.id.author_type)
    ImageView author_type;

    @BindView(R.id.name_vertify)
    TextView name_vertify;


//    @BindView(R.id.id_auther)
//    ImageView id_auther;
//
//    @BindView(R.id.id_profession)
//    ImageView id_profession;


    private List<ChannelBean> mChannelBeanList;
    private List<String> mTitls = new ArrayList<>();
    private FirstFragmentAdapter mFirstFragmentAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();

    private int page = 1;

    //自己的uid
    private String myUid;
    //传递uid
    private String  otherUid;
    private RegisterLoginBean.UserInfo  mUserInfo;


    //关注状态
    private String follow_status;
    //屏蔽 取消屏蔽
    private String shareContent;


    //是否是作者
    boolean isAuthor;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo_v2;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected boolean regEvent() {
        return true;
    }



    private void initUserInfo() {
        myUid = StringUtil.getMyUid();

        otherUid = getIntent().getStringExtra("uid");
        KLog.d("tag","用户的uid是 " + otherUid );

        //动态h5跳转过来，uid是不带的，那么肯定是自己
        if(TextUtils.isEmpty(otherUid)){
            otherUid = myUid;
        }

        mUserInfo = StringUtil.getUserInfoBean();
        iv_right.setImageResource(R.mipmap.icon_userinfo_other_1);

        getUserInfoV2();
    }


    private void setShowPart(){
        mChannelBeanList = new ArrayList<>();
        ChannelBean bean ;

        if(isAuthor){
            bean = new ChannelBean("0","发布文章");
            //作者是id
            bean.setChaid(temp.getAuthor_info().getId());
            bean.setNum(temp.getAuthor_info().getArticle_count() + "");
            mChannelBeanList.add(bean);
            bean = new ChannelBean("1","发布动态");
            bean.setNum(temp.getBlog_count() + "");
            mChannelBeanList.add(bean);
        }else{
            bean = new ChannelBean("0","发布动态");
            bean.setNum(temp.getBlog_count() + "");
            mChannelBeanList.add(bean);
        }


        if(null != mChannelBeanList){
            setUpAdater();
        }

        setUpTabLayout();
    }

    @Override
    public void initFirstData() {

        initUserInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @OnClick(
            {R.id.iv_back,
            R.id.iv_text,
            R.id.iv_right,
            R.id.iv_right_1,
            R.id.noFocus,
            R.id.focus,
            R.id.alreadFocus,
            R.id.already_focus

    })
    public void clicks(View view){
        if(StringUtil.isFastClick()){
            return;
        }
        switch (view.getId()){
            case R.id.alreadFocus:
            case R.id.already_focus:
                showCancelFocusDialog();
                break;
            case R.id.noFocus:
            case R.id.focus:
                //认证过了直接去打招呼界面
                if("1".equals(mUserInfo.getAuth_email_status()) || "1".equals(mUserInfo.getAuth_card_status())){
                    UIHelper.toHelloMakeActivity(this);
                    overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                }else{
                    showProfessionAuthen();
                }
                break;

            case R.id.iv_text:
                UIHelper.toUserInfoModifyActivity(this);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                showStateByFollow(temp.getFollow_status());
                showPopupWindowReport(iv_right,shareContent);
                setBackgroundAlpha(this, 0.6f);
                break;
            case R.id.iv_right_1:
                showShareDialog();
                break;
            default:
        }
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
                        already_focus.setVisibility(View.GONE);
                        focus.setVisibility(View.VISIBLE);
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



    /** --------------------------------- 分享  ---------------------------------*/
    private String mins;
    ShareBean bean = new ShareBean();
    Bitmap bitmap =  null;
    private ExecutorService mExecutorService;

    //此部分分享数据来自于后台
    private void showShareDialog() {

        //TODO 伪代码
//        if(数据来自后台){
//            显示： 标题 + 文本 + 图片 + （link）
//        }

        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(this).builder();
        alertDialog.setSharelinkView();
        alertDialog.setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position){
                case 0:
                    mExecutorService.submit(() -> {
//                        bitmap = StringUtil.getBitmap(mSchoolTest.getIcon());
                        mHandler.sendEmptyMessage(0x111);
                    });
                    break;
                case 1:
                    mExecutorService.submit(() -> {
//                        bitmap = StringUtil.getBitmap(mSchoolTest.getIcon());
                        mHandler.sendEmptyMessage(0x112);
                    });
                    break;
                case 2:
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("链接复制成功！");
//                    StringUtil.copyLink(mSchoolTest.getTitle() + "\n" +  mSchoolTest.getShare_url());
                    break;
                default:
            }
        });
        alertDialog.show();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            bean.setBitmap(bitmap);
//            bean.setImg(mSchoolTest.getIcon());
//            bean.setLink(mSchoolTest.getShare_url());
//            bean.setTitle("我通过了" + mSchoolTest.getTitle());
//            bean.setContent(conteent);
            if(msg.what == 0x111){
                bean.setShareType("circle_link");
            }else{
                bean.setShareType("weixin_link");
            }
            StringUtil.shareWxByWeb(UserInfoV2Activity.this,bean);

        }
    };



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




    public void showNotSeeEachOther(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("屏蔽", v -> {
            blockUser();

        }).setNegativeButton("再想想", v -> {

        }).setMsg("屏蔽" + temp.getName() + "?").setTitle("屏蔽后，你们不能互相关注且在推荐频道你将看不到他的圈子动态").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
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

    private  PersonUserInfoBean temp;
    private void getUserInfoV2() {
        Map<String,String> map = new HashMap<>();
        map.put("uid",otherUid);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getUserInfoV2(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<PersonUserInfoBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<PersonUserInfoBean> response) {
                        temp = response.getReturn_data();
                        if(temp != null){
                            setHeadData();
                        }
                    }
                });
    }

    private void setHeadData() {
        //设置逻辑
        initDifferLogic();

        head_icon.setOnClickListener(v -> {
            ArrayList<String> pics = new ArrayList<>();
            pics.add(temp.getAvatar());
            UIHelper.toPicPreViewActivity(mContext,  pics,0,false);
        });


        if(temp != null){
            user_des.setText(temp.getPro_summary());


            medal_count.setText(StringUtil.formatPeopleNum(temp.getFans_count() + ""));

            ImageUtil.loadByDefaultHead(this,temp.getAvatar(),head_icon);

            //小头像
            ImageUtil.loadByDefaultHead(this,temp.getAvatar(),small_head_icon);
            //小作者
            tv_title.setText(temp.getName());

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


    private void initDifferLogic() {

        //是否作者信息
        if(temp.isIs_author() && temp.getAuthor_info() != null){
            isAuthor = true;
            AuthorBean.Author bean = temp.getAuthor_info();
            rl_author.setVisibility(View.VISIBLE);
            ImageUtil.loadByDefaultHead(this,bean.getImg(),head_author_icon);
            author_name.setText(bean.getName());
            author_desc.setText(bean.getSummary());


            //影响数
            if(!TextUtils.isEmpty(bean.getHit_count())){
                long count = Long.parseLong(bean.getHit_count());
                if(count < 10000 ){
                    hint_num.setText(bean.getHit_count());
                }else{
                    double temp = count  ;
                    //1.将数字转换成以万为单位的数字
                    double num = temp / 10000;
                    BigDecimal b = new BigDecimal(num);
                    //2.转换后的数字四舍五入保留小数点后一位;
                    double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                    hint_num.setText( f1 + " w");
                }

            }




            //作者类型:1-作者（不显示），2-新手作者，3-新锐作者，4-专栏作者',
            if("1".equals(bean.getType())){
                author_type.setVisibility(View.GONE);
            }else if("2".equals(bean.getType())){
                author_type.setImageResource(R.mipmap.hot_author_newuser);
            }else if("3".equals(bean.getType())){
                author_type.setImageResource(R.mipmap.hot_author_new);
            }else if("4".equals(bean.getType())){
                author_type.setImageResource(R.mipmap.hot_author_professor);
            }
        }

        //设置下方的选择
        setShowPart();

        fans_num.setText(temp.getFollow_count() + "");
        medal_count.setText(temp.getFans_count() + "");

//        if(已实名){ ok
//                显示：已实名状态
//            }

        sender_name.setText(temp.getName());
        //身份证认证状态：1-正常，2-未提交，3-审核中，4-未通过
        if("1".equals(temp.getAuth_idno_status())){
            CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_auther_shenfen,2);
            SpannableString spanString2 = new SpannableString("  icon");
            spanString2.setSpan(imageSpan, 2, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sender_name.append(spanString2);
        }

        //是否认证通过
        if("1".equals(temp.getAuth_email_status()) || "1".equals(temp.getAuth_card_status())){
            //居中对齐imageSpan
            CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_authen_company1,2);
            SpannableString spanString2 = new SpannableString("  icon");
            spanString2.setSpan(imageSpan, 2, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sender_name.append(spanString2);

        }


        //自己
        if(myUid.equals(otherUid)){
            iv_text.setVisibility(View.VISIBLE);

            //TODO 伪代码
//            if(没有认证显示去认证){ ok
//                显示：立即完善信息，建立人脉
//                点击：编辑名片信息
//            }else{
//               if( 认证没通过){
//                   显示：公司 + 职位 + 立即职业认证
//                   点击：认证中心
//               }else{
//                   显示：公司 + 职位
//               }
//            }
//
//            if(用户是作者){ ok
//                显示：作者部分描述 + 文章
//            }

//              if(自己){ ok
//                  显示：编辑信息
//              }


            if(TextUtils.isEmpty(mUserInfo.getCompany_name()) &&
                    TextUtils.isEmpty(mUserInfo.getPosition()) ){
                name_vertify.setVisibility(View.VISIBLE);
                //下划线
                name_vertify.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                //抗锯齿
                name_vertify.getPaint().setAntiAlias(true);
                name_vertify.setOnClickListener(v -> {
                    MobclickAgentUtils.onEvent(UmengEvent.i_auth_2_0_0);
                    if(StringUtil.isFastClick()){
                        return;
                    }
                    UIHelper.toUserInfoModifyActivity(this);
                });
            }else{
                //认证是否通过 -- 这里不用改(不用显示用户名在认证的情况下)
                if("1".equals(mUserInfo.getAuth_email_status()) || "1".equals(mUserInfo.getAuth_card_status())){
                    name_author_tag.setVisibility(View.VISIBLE);
                    name_author_tag.setText( (TextUtils.isEmpty(mUserInfo.getCompany_name())?"":mUserInfo.getCompany_name()) +
                            (TextUtils.isEmpty(mUserInfo.getPosition())?"":mUserInfo.getPosition()));

                    //居中对齐imageSpan
                    CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_authen_company1,2);
                    SpannableString spanString2 = new SpannableString("icon");
                    spanString2.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    name_author_tag.append(spanString2);

                }else{
                    name_author_tag.setVisibility(View.VISIBLE);
                    sender_not_verticity.setVisibility(View.VISIBLE);
                    //下划线
                    sender_not_verticity.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    //抗锯齿
                    sender_not_verticity.getPaint().setAntiAlias(true);
                    name_author_tag.setText( (TextUtils.isEmpty(mUserInfo.getCompany_name())?"":mUserInfo.getCompany_name()) +
                            (TextUtils.isEmpty(mUserInfo.getPosition())?"":mUserInfo.getPosition()));

                    sender_not_verticity.setOnClickListener(v -> ToastUtils.showShort("去认证h5界面"));

                }
            }


        }else{

            //TODO 伪代码
//            if(没完善司职信息 company + position){
//                显示：TA还未完善信息
//            }else{
//                if(认证通过){
//                    显示：已职业认证 + 公司 + 职位
//                }else{
//                    显示：未职业认证
//                }
//            }

//            if(别人){ ok
//                显示：分享图层
//            }



//            if(null != temp){
//                if(!StringUtil.checkNull((temp.getCompany_name()))
//                        && !StringUtil.checkNull((temp.getPosition()))){
//                    sender_not_verticity.setText("未认证");
//                    sender_tag.setVisibility(View.GONE);
//                    sender_not_verticity.setVisibility(View.VISIBLE);
//                }else{
//                    sender_tag.setVisibility(View.VISIBLE);
//                    sender_not_verticity.setVisibility(View.GONE);
//                    sender_tag.setText((StringUtil.checkNull((temp.getCompany_name()))?temp.getCompany_name() + " ":"") +
//                            (TextUtils.isEmpty(temp.getPosition())?"":temp.getPosition()));
//                }
//
//
//                //是否认证通过
//                if("1".equals(temp.getAuth_email_status()) || "1".equals(temp.getAuth_card_status())){
////                    Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
////                    drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
////                    sender_tag.setCompoundDrawables(null,null,drawable,null);
//
//                    //居中对齐imageSpan
////                    CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_authen_company1,2);
////                    SpannableString spanString2 = new SpannableString("icon");
////                    spanString2.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                    sender_tag.append(spanString2);
//                }else{
//                    sender_tag.setCompoundDrawables(null,null,null,null);
//                }
//            }

            //别人
            showStateByFollow(temp.getFollow_status());
            iv_right.setVisibility(View.VISIBLE);
            iv_right_1.setVisibility(View.VISIBLE);
            part3333.setVisibility(View.VISIBLE);
        }
    }



    // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
    private void showStateByFollow(int status) {
        follow_status = status + "";
        shareContent = "屏蔽";
        if("0".equals(follow_status)){
            noFocus.setVisibility(View.VISIBLE);
            focus.setVisibility(View.VISIBLE);
        }else if("1".equals(follow_status)){
            alreadFocus.setVisibility(View.VISIBLE);
            already_focus.setVisibility(View.VISIBLE);
        }else if("3".equals(follow_status)){
            himcloseme.setVisibility(View.VISIBLE);
        }else if("2".equals(follow_status)){
            shareContent = "取消屏蔽";
            part3333.setVisibility(View.GONE);
            focus.setVisibility(View.GONE);
            already_focus.setVisibility(View.GONE);
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
                            UIHelper.toWebViewActivityWithOnLayout(UserInfoV2Activity.this,link,"个人中心");
                        }
                    }
                });
    }




    private void setUpTabLayout() {
        //设置指示器颜色
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.yellow));
        //设置可滑动模式
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        //设置指示器的高度
        mTabLayout.setSelectedTabIndicatorHeight(SizeUtils.dp2px(4f));
        //设置选中状态
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if(null != view){
                    TextView textView = view.findViewById(R.id.tv_header);
                    textView.setTextSize(17);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    textView.setTextColor(getResources().getColor(R.color.text_news_title_color));

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view=tab.getCustomView();
                if(null != view){
                    TextView textView = view.findViewById(R.id.tv_header);
                    textView.setTextSize(16);
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);

        //设置自定义tab,这个需要在setupWithViewPager方法后
        for (int i = 0; i < mTabLayout.getTabCount(); i++){
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.tool_item_tablyout, null);
                TextView textView=view.findViewById(R.id.tv_header);
                TextView num = view.findViewById(R.id.num);
                num.setText(mChannelBeanList.get(i).getNum());
                textView.setText(mChannelBeanList.get(i).getChaname());
                tab.setCustomView(view);
            }
        }
        //设置第二个为选中状态时的tab文字颜色
        View view = mTabLayout.getTabAt(0).getCustomView();
        TextView textView = view.findViewById(R.id.tv_header);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        textView.setSelected(true);
    }

    private void setUpAdater() {
        mFragmentList.clear();
        mTitls.clear();

        //只有一个，那么是圈子
        if(mChannelBeanList.size() == 1){
            for (int i = 0; i < mChannelBeanList.size(); i++) {
                //第一个参数填传递的Uid
                UserCircleFragment fragment1 = UserCircleFragment.getInstance(otherUid,
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(fragment1);

                mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
            }
        }else{
            for (int i = 0; i < mChannelBeanList.size(); i++) {
                if(i== 0){
                    UserArticleFragment fragment1 = UserArticleFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                            mChannelBeanList.get(i).getChaname());
                    mFragmentList.add(fragment1);
                }else if(i == 1){
                    //第一个参数填传递的Uid
                    UserCircleFragment fragment1 = UserCircleFragment.getInstance(otherUid,
                            mChannelBeanList.get(i).getChaname());
                    mFragmentList.add(fragment1);
                }

                mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
            }
        }


        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(this,getSupportFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(0);

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
            getUserInfoV2();
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
                        already_focus.setVisibility(View.VISIBLE);
                        focus.setVisibility(View.GONE);
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




    //滑动显示搜索
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowTopTitleEvent(ShowTopTitleEvent event) {
        if (this != null) {
            String statu = event.getData();
            if("1".equals(statu)){
                part_small_head.setVisibility(View.VISIBLE);
                part3333.setVisibility(View.GONE);
            }else{
                part_small_head.setVisibility(View.GONE);
                part3333.setVisibility(View.VISIBLE);
            }
        }
    }



}
