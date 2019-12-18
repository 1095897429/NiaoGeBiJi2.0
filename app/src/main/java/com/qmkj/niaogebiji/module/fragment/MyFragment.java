package com.qmkj.niaogebiji.module.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.common.utils.TimeAppUtils;
import com.qmkj.niaogebiji.module.activity.AuthorListActivity;
import com.qmkj.niaogebiji.module.adapter.MyItemAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.MyBean;
import com.qmkj.niaogebiji.module.bean.OfficialBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.udesk.UdeskSDKManager;
import cn.udesk.callback.IUdeskFormCallBack;
import cn.udesk.config.UdeskConfig;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import udesk.core.UdeskConst;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class MyFragment extends BaseLazyFragment {

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.read_time)
    TextView read_time;


    @BindView(R.id.medal_count)
    TextView medal_count;

    @BindView(R.id.feather_count)
    TextView feather_count;


    @BindView(R.id.name_tag)
    TextView name_tag;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.head_icon)
    CircleImageView head_icon;


    @BindView(R.id.vip_time)
    TextView vip_time;


    //适配器
    MyItemAdapter mMyItemAdapter;
    //组合集合
    List<MyBean> mAllList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    private RegisterLoginBean.UserInfo mUserInfo;


    private int [] images = new int[]{R.mipmap.icon_my_feather,R.mipmap.icon_my_invite,R.mipmap.icon_my_medal,
            R.mipmap.icon_my_dynamic,R.mipmap.icon_my_focus,R.mipmap.icon_my_circle};

    private String [] names = new String[]{"羽毛任务","邀请好友","徽章中心","我的动态","干货收藏","圈子关注"};

    public static MyFragment getInstance() {
        return new MyFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }




    @Override
    protected void initView() {

        getUserInfo();

        initLayout();
        getData();
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        name.setTypeface(typeface);
        read_time.setTypeface(typeface);
        medal_count.setTypeface(typeface);
        feather_count.setTypeface(typeface);
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
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = StringUtil.getUserInfoBean();
        if(null != mUserInfo){
            getUserInfo();
        }
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
                            setUserInfo();
                        }
                    }
                });

    }

    private void setUserInfo() {
        if(null != mUserInfo){

            //vip时间 20191216
            String vipstart = mUserInfo.getVip_start_date();
            vipstart = "2019-12-16";
            String vipend = mUserInfo.getVip_end_date();
            vipend = "2019-12-18";

            //TODO 计算时间差
            int space =  TimeAppUtils.stringDaysBetween(vipstart,vipend);
            vip_time.setText("鸟哥笔记VIP剩余"+ space + "天");

            TextPaint paint = name.getPaint();
            paint.setFakeBoldText(true);

            if(!TextUtils.isEmpty(mUserInfo.getNickname())){
                name.setText(mUserInfo.getNickname());
            }else{
                name.setText(mUserInfo.getName());
            }

            if(!TextUtils.isEmpty(mUserInfo.getPro_summary())){
                name_tag.setText(mUserInfo.getPro_summary());
            }else{
                name_tag.setText("写一句话介绍一下自己");
            }

            ImageUtil.load(mContext,mUserInfo.getAvatar(),head_icon);

            //个人中心消息通知：1-有新消息，0-无新消息
            if("1".equals(mUserInfo.getIs_red())){

            }else{

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
        }

    }


    private void getData() {

        MyBean bean1 ;
        for (int i = 0; i < 6; i++) {
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
            switch (position){
                case 1:
                    UIHelper.toInviteActivity(getActivity());
                    break;
                case 3:
                    KLog.d("tag","去 h5的我的动态");
                    String link = Constant.TEST_URL + "myactivity/";
                    UIHelper.toWebViewActivity(getActivity(),link);

                    break;
                case 4:
                    UIHelper.toMyCollectionListActivity(getActivity());
                    break;
                    default:
            }
        });
    }


    @Override
    public void initData() {
        //认证与未认证
        name_tag.setText("立即职业认证，建立人脉");
    }


    @OnClick({R.id.toSet,R.id.toMsg,R.id.about_ll,
                R.id.name_tag,
                R.id.part2222_2,
                R.id.part3333_3,
                R.id.toExchange,
                R.id.rl_vip_time,
                R.id.toVip,
                R.id.toQue,R.id.advice_ll,
                R.id.toUserInfo})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.toUserInfo:

                UIHelper.toUserInfoActivity(getActivity(),mUserInfo.getUid());

                break;
            case R.id.advice_ll:
                toUDesk();
                break;
            case R.id.toQue:
                ToastUtils.showShort("h5 页面");
                break;
            case R.id.about_ll:
                UIHelper.toAboutUsActivity(getActivity());
                break;
            case R.id.toVip:
            case R.id.rl_vip_time:
                KLog.d("tag","h5 VIP页面");
                break;
            case R.id.toExchange:
                KLog.d("tag","去羽毛商城");
                break;
            case R.id.part3333_3:
                KLog.d("tag","去羽毛任务");
                break;
            case R.id.part2222_2:
                KLog.d("tag","去徽章详情页");
                break;
            case R.id.name_tag:
                KLog.d("tag","h5 职业认证");
                break;
            case R.id.toMsg:

                UIHelper.toLoginActivity(getActivity());
                break;
            case R.id.toSet:
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
                            UIHelper.toWebViewActivity(getActivity(),advice_url);
                        }
                    }

                });
    }


}
