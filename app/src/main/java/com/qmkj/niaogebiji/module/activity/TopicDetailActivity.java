package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.qmkj.niaogebiji.module.event.UpdateRecommendTopicFocusListEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.RecyclerViewNoBugLinearLayoutManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-14
 * 描述:话题明细页
 */
public class TopicDetailActivity extends BaseActivity {

    @BindView(R.id.bg_img)
    ImageView bg_img;

    @BindView(R.id.one_img)
    ImageView one_img;

    @BindView(R.id.send_choose)
    TextView send_choose;

    @BindView(R.id.topic_titile)
    TextView topic_titile;

    @BindView(R.id.topic_desc)
    TextView topic_desc;

    @BindView(R.id.focus_num)
    TextView focus_num;

    @BindView(R.id.focus)
    TextView focus;

    @BindView(R.id.alreadFocus)
    TextView alreadFocus;


    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;


    private String topicId;
    //排序方式 0默认，按排序值 1按时间
    private String sort_type = "0";

    //页数
    private int page = 1;
    //适配器
    CircleRecommentAdapterNew mCircleRecommentAdapterNew;
    //组合集合
    List<CircleBean> mAllList = new ArrayList<>();
    //布局管理器
    RecyclerViewNoBugLinearLayoutManager mLinearLayoutManager;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_detail;
    }

    @Override
    public void initFirstData() {

        topicId = getIntent().getStringExtra("topicId");


        initLayout();


        getTopicDetail();

        getListByTopicId();
    }

    private void getListByTopicId() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        map.put("sort_type",sort_type);
        map.put("topic_id",topicId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getListByTopicId(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CircleBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CircleBean>> response) {
                        hideWaitingDialog();
                        List<CircleBean> serverData = response.getReturn_data();
                        if(1 == page){
                            if(serverData != null && !serverData.isEmpty()){
                                setData2(serverData);
                                mCircleRecommentAdapterNew.setNewData(mAllList);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(serverData.size() < Constant.SEERVER_NUM){
                                    mCircleRecommentAdapterNew.loadMoreEnd();
                                }
                                ll_empty.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }else{
                                KLog.d("tag","设置空布局");
                                //第一次加载无数据
                                ll_empty.setVisibility(View.VISIBLE);
                                ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("暂无推荐内容");
                                ((ImageView)ll_empty.findViewById(R.id.iv_empty)).setImageResource(R.mipmap.icon_empty_article);
                                mRecyclerView.setVisibility(View.GONE);
                            }
                        }else{
                            //已为加载更多有数据
                            if(serverData != null && serverData.size() > 0){
                                setData2(serverData);
                                mCircleRecommentAdapterNew.loadMoreComplete();
                                mCircleRecommentAdapterNew.addData(teList);
                            }else{
                                //已为加载更多无更多数据
                                mCircleRecommentAdapterNew.loadMoreEnd();
                            }
                        }
                    }
                });
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
                mAllList.clear();
                mAllList.addAll(teList);
            }
        }
    }


    //临时变量，用于关注 、 取关
    private TopicBean mTopicBean;
    private void getTopicDetail() {
        Map<String,String> map = new HashMap<>();
        map.put("topic_id",topicId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getTopicDetail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<TopicBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<TopicBean> response) {
                        mTopicBean = response.getReturn_data();
                        setHeadData(response.getReturn_data());
                    }
                });
    }

    private void setHeadData(TopicBean bean) {

        //图片背景
        if(!TextUtils.isEmpty(bean.getIcon())){
            Glide.with(this).load(bean.getIcon())
                    .apply(bitmapTransform(new BlurTransformation(25)))
                    .into(bg_img);
            ImageUtil.loadByDefaultHead(this,bean.getIcon(),one_img);
        }else{
            String url = "https://desk-fd.zol-img.com.cn/t_s2560x1440c5/g2/M00/05/09/ChMlWl1BAz-IcV0oADKEXBJ0ncgAAMP0gAAAAAAMoR0279.jpg";
            Glide.with(this).load(url)
                    .apply(bitmapTransform(new BlurTransformation(25)))
                    .into(bg_img);
            ImageUtil.loadByDefaultHead(this,url,one_img);
        }


        topic_titile.setText("#" + bean.getTitle());

        topic_desc.setText(bean.getDesc());

        //关注数 x>=10000，展示1w+
        if(!TextUtils.isEmpty(bean.getFollow_num())){
            long count = Long.parseLong(bean.getFollow_num());
            if(count < 10000 ){
                focus_num.setText(bean.getFollow_num() + "人 关注");
            }else{
                double temp = count  ;
                //1.将数字转换成以万为单位的数字
                double num = temp / 10000;
                BigDecimal b = new BigDecimal(num);
                //2.转换后的数字四舍五入保留小数点后一位;
                double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                focus_num.setText(f1 + " w" + "人 关注");
            }
        }

        //是否关注 注：1-关注，0-取消关注
        if(!bean.isIs_follow()){
           focus.setVisibility(View.VISIBLE);
        }else{
            alreadFocus.setVisibility(View.VISIBLE);
        }

    }


    private void initLayout() {
        mLinearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCircleRecommentAdapterNew = new CircleRecommentAdapterNew(mAllList);
        mRecyclerView.setAdapter(mCircleRecommentAdapterNew);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    @SuppressLint("CheckResult")
    private void initEvent() {
        mCircleRecommentAdapterNew.setOnLoadMoreListener(() -> {
            ++page;
//            recommendBlogList();
        }, mRecyclerView);

    }


    @Override
    protected void initView() {
        //测试数据
//        for (int i = 0; i < 10; i++) {
//            mAllList.add(new CircleBean());
//        }
//        mCircleRecommentAdapterNew.setNewData(mAllList);
    }


    @OnClick({R.id.iv_back,
            R.id.send_choose,
            R.id.focus,R.id.alreadFocus,
            R.id.create_blog
           })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.send_choose:
                CircleBean item = new CircleBean();
                showPopupWindow(item,send_choose);
                StringUtil.setBackgroundAlpha((Activity) mContext, 0.6f);
                break;
            case R.id.create_blog:
                UIHelper.toCircleMakeActivityV2(this,mTopicBean);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.focus:
            case R.id.alreadFocus:
                if(!mTopicBean.isIs_follow()){
                    followTopic(0,mTopicBean.getId() + "");
                }else{
                    unfollowTopic(0,mTopicBean.getId() + "");
                }

                break;

            default:
        }
    }



    private void followTopic(int mPosition,String topic_id) {
        Map<String,String> map = new HashMap<>();
        map.put("topic_id",topic_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followTopic(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse<String>>() {
                    @Override
                    public void onSuccess(HttpResponse<String> response) {
                        KLog.d("tag","关注成功，改变状态");
                        mTopicBean.setIs_follow(true);
                        alreadFocus.setVisibility(View.VISIBLE);
                        focus.setVisibility(View.GONE);
                        EventBus.getDefault().post(new UpdateRecommendTopicFocusListEvent());
                    }

                    @Override
                    public void onNetFail(String msg) {

                    }
                });
    }


    private void unfollowTopic(int mPosition,String topic_id) {
        Map<String,String> map = new HashMap<>();
        map.put("topic_id",topic_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().unfollowTopic(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse<String>>() {
                    @Override
                    public void onSuccess(HttpResponse<String> response) {
                        KLog.d("tag","取关成功，改变状态");

                        mTopicBean.setIs_follow(false);
                        focus.setVisibility(View.VISIBLE);
                        alreadFocus.setVisibility(View.GONE);
                        EventBus.getDefault().post(new UpdateRecommendTopicFocusListEvent());
                    }

                    @Override
                    public void onNetFail(String msg) {

                    }
                });
    }



    /**- ------------------------------- 浮层  --------------------------------- */

    private void showPopupWindow(CircleBean circleBean,View view) {
        //加载布局
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_topic, null);
        PopupWindow mPopupWindow = new PopupWindow(inflate);
        TextView report = inflate.findViewById(R.id.report);
        TextView share = inflate.findViewById(R.id.share);
        //必须设置宽和高
        mPopupWindow.setWidth(SizeUtils.dp2px(134f));
        mPopupWindow.setHeight(SizeUtils.dp2px(88f));
        //点击其他地方隐藏,false为无反应
        mPopupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //以view的左下角为原点，xoff为正表示向x轴正方向偏移像素
            mPopupWindow.showAsDropDown(view, ScreenUtils.getScreenWidth() -SizeUtils.dp2px(16f * 2 + 64f + 134f), SizeUtils.dp2px(0f));
        }
        //对popupWindow进行显示
        mPopupWindow.update();
        //消失时将透明度设置回来
        mPopupWindow.setOnDismissListener(() -> {
            if (null != mContext) {
                StringUtil.setBackgroundAlpha((Activity) mContext, 1f);
            }
        });

        //时间 1
        report.setOnClickListener(view1 -> {
            sort_type = "1";
            showWaitingDialog();
            getListByTopicId();
            send_choose.setText(report.getText().toString());
            mPopupWindow.dismiss();
        });

        //热度 0
        share.setOnClickListener(view1 -> {
            sort_type = "0";
            showWaitingDialog();
            getListByTopicId();
            send_choose.setText(share.getText().toString());
            mPopupWindow.dismiss();
        });
    }


    @BindView(R.id.loading_dialog)
    LinearLayout loading_dialog;

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    public void showWaitingDialog() {
        loading_dialog.setVisibility(View.VISIBLE);
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("images/loading.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
    }

    /**
     * 隐藏等待提示框
     */
    public void hideWaitingDialog() {
        if(null != lottieAnimationView){
            loading_dialog.setVisibility(View.GONE);
            lottieAnimationView.cancelAnimation();
        }
    }





}
