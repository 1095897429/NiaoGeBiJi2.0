package com.qmkj.niaogebiji.module.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ScaleXSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.dialog.ShareFlashDialog;
import com.qmkj.niaogebiji.common.dialog.ShareFlashDialogV2;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.common.utils.ZXingUtils;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivity;
import com.qmkj.niaogebiji.module.adapter.FlashItemAdapter;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.FlashOkBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.event.FlashShareEvent;
import com.qmkj.niaogebiji.module.event.FlashSpecificEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.widget.ItemHeaderDecoration;
import com.qmkj.niaogebiji.module.widget.MyLoadMoreView;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述：快讯的Fragment
 * 版本2：调整了UI展示
 */
public class FlashFragmentV2 extends BaseLazyFragment  {

    @BindView(R.id.sticky_header)
    LinearLayout sticky_header;

    @BindView(R.id.header_textview)
    TextView header_textview;

    @BindView(R.id.header_textview_weekend)
    TextView header_textview_weekend;

    private Typeface typeface;

    /** --------------------------------- 快讯列表  ---------------------------------*/
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;


    private LinearLayoutManager mLinearLayoutManager;
    private int page = 1;
    //索引 点赞改变状态
    private int myPosition;
    private FlashItemAdapter mFlashItemAdapter;
    private List<FlashBulltinBean.BuilltinBean> mBuilltinBeans = new ArrayList<>();
    private FlashBulltinBean mFlashBulltinBean;
    private FlashBulltinBean.BuilltinBean mTempBuilltinBean;


    public static FlashFragmentV2 getInstance() {
        FlashFragmentV2 newsItemFragment = new FlashFragmentV2();
        Bundle args = new Bundle();
        newsItemFragment.setArguments(args);
        return newsItemFragment;
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


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_flash_v2;
    }

    @Override
    protected void initView() {
        showWaitingDialog();
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Black.otf");
        initSamrtLayout();
        initLayout();
        initShareLayout();
    }

    private View view;
    private void initShareLayout() {
        //初始化分享内容
        view =  LayoutInflater.from(getActivity()).inflate(R.layout.activity_poster,null,false);
        title = view.findViewById(R.id.title);
        tag = view.findViewById(R.id.tag);
        today_time = view.findViewById(R.id.today_time);
        time_txt = view.findViewById(R.id.time_txt);
        image33 = view.findViewById(R.id.image33);
        flash_ll = view.findViewById(R.id.ll_poster);
        today_time.setTypeface(typeface);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mBuilltinBeans.clear();
            page = 1;
            getBulletinList();
        });

    }


    @Override
    protected void initData() {
        getBulletinList();
    }

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFlashItemAdapter = new FlashItemAdapter(mBuilltinBeans);
        //禁用change动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        //设置Adapter
        mRecyclerView.setAdapter(mFlashItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        mFlashItemAdapter.setLoadMoreView(new MyLoadMoreView());

        initEvent();
    }

    private void initEvent() {
        mFlashItemAdapter.setOnLoadMoreListener(() -> {
            ++page;
            getBulletinList();
        },mRecyclerView);

        //监听RecyclerView滚动，实现粘性头部
        mRecyclerView.addOnScrollListener(new RvScrollListener());

        mFlashItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {

            myPosition = position;
            mTempBuilltinBean = mFlashItemAdapter.getData().get(position);

            switch (view.getId()){
                case R.id.flash_priase:
                    if(position <= 9){
                        MobclickAgentUtils.onEvent("news_share" + (position + 1) + "_2_0_0");
                    }

                    if(mTempBuilltinBean.getIs_good() == 0){
                        goodBulletin(mTempBuilltinBean.getId());
                    }else{
                        cancleGoodBulletin(mTempBuilltinBean.getId());
                    }
                    break;
                case R.id.flash_share:
                    if(position <= 9){
                        MobclickAgentUtils.onEvent("news_laud" + (position + 1) + "_2_0_0");
                    }

                    if(StringUtil.isFastClick()){
                        return;
                    }

                    ShareFlashDialogV2();
                    break;
                case R.id.part1111:
                    ArrayList<String> photos = new ArrayList<>();

                    String pic = mTempBuilltinBean.getPic();
                    if(!TextUtils.isEmpty(pic)){
                        photos.add(pic);
                    }

                    if(!photos.isEmpty()){
                        Bundle bundle = new Bundle ();
                        bundle.putStringArrayList ("imageList", photos);
                        bundle.putBoolean("fromNet",true);
                        bundle.putInt("index",0);
                        Intent intent = new Intent(getActivity(), PicPreviewActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    break;
                default:
            }
        });
    }

    private void getBulletinList() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getBulletinList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<FlashBulltinBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<FlashBulltinBean> response) {

                        hideWaitingDialog();
                        mFlashBulltinBean = response.getReturn_data();
                        if(null != mFlashBulltinBean){

                            mBuilltinBeans = mFlashBulltinBean.getList();
                            if(null != smartRefreshLayout){
                                smartRefreshLayout.finishRefresh();
                            }
                            if(1 == page){
                                mFlashItemAdapter.setNewData(mBuilltinBeans);

//                                mRecyclerView.addItemDecoration(new ItemHeaderDecoration(getActivity(),mBuilltinBeans));


                                //旧展示
//                                header_textview.setText( mBuilltinBeans.get(0).getDay_dec() + "" + mBuilltinBeans.get(0).getShow_time());

                                header_textview.setText(TimeUtils.millis2String(Long.parseLong(mBuilltinBeans.get(0).getPub_time())* 1000L,"MM月dd日"));
                                String temp = TimeUtils.millis2String(Long.parseLong(mBuilltinBeans.get(0).getPub_time())* 1000L,"yyyy-MM-dd HH:mm:ss");
                                String string = TimeUtils.getChineseWeek(temp);

//                                boolean isToday = TimeUtils.isToday(temp);
//                                if(isToday){
//                                    string = string + "~ 今天";
//                                    //创建一个SpannableString对象
//                                    SpannableString sStr = new SpannableString(string);
//                                    //设置字体大小（相对值,单位：像素） 参数表示为默认字体宽度的多少倍 ,2.0f表示默认字体宽度的两倍，即X轴方向放大为默认字体的两倍，而高度不变
//                                    sStr.setSpan(new ScaleXSpan(0.75f), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                }

                                header_textview_weekend.setText(string);

                            }else{
                                //已为加载更多有数据
                                if(mBuilltinBeans != null && mBuilltinBeans.size() > 0){
                                    mFlashItemAdapter.loadMoreComplete();
                                    mFlashItemAdapter.addData(mBuilltinBeans);
                                }else{
                                    //已为加载更多无更多数据
                                    mFlashItemAdapter.loadMoreEnd();
                                }
                            }
                        }
                    }

                    @Override
                    public void onNetFail(String msg) {
                        hideWaitingDialog();
                    }
                });
    }


    private class RvScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                //获取第一个可见view的位置
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();

                if(null != mBuilltinBeans){
                    FlashBulltinBean.BuilltinBean temmp = mFlashItemAdapter.getData().get(firstItemPosition);
                    if(null != temmp){
                        if(header_textview.getText().toString()
                                .equals(TimeUtils.millis2String(Long.parseLong(temmp.getPub_time())* 1000L,"MM月dd日"))){
                              //旧的展示
//                            header_textview.setText(temmp.getDay_dec() + "" + temmp.getShow_time());

                            //新的展示
//                            header_textview.setText(TimeUtils.millis2String(Long.parseLong(temmp.getCreated_at())* 1000L,"MM月dd日"));
//                            String temp = TimeUtils.millis2String(Long.parseLong(temmp.getCreated_at())* 1000L,"yyyy-MM-dd HH:mm:ss");
//                            String string = TimeUtils.getChineseWeek(temp);
//                            header_textview_weekend.setText(string);

                        }else{
//                            header_textview.setText( temmp.getShow_time());

                            header_textview.setText(TimeUtils.millis2String(Long.parseLong(temmp.getPub_time())* 1000L,"MM月dd日"));
                            String temp = TimeUtils.millis2String(Long.parseLong(temmp.getPub_time())* 1000L,"yyyy-MM-dd HH:mm:ss");
                            String string = TimeUtils.getChineseWeek(temp);
                            header_textview_weekend.setText(string);
                        }
                    }
                }
            }

        }
    }



    @Override
    protected void changePriaseStatus() {
        if (1 != mTempBuilltinBean.getIs_good()) {
            mTempBuilltinBean.setIs_good(1);
            mTempBuilltinBean.setGood_num((Integer.parseInt(mTempBuilltinBean.getGood_num()) + 1 )+ "");
        } else {
            mTempBuilltinBean.setIs_good(0);
            mTempBuilltinBean.setGood_num((Integer.parseInt(mTempBuilltinBean.getGood_num()) - 1) + "");
        }
        mFlashItemAdapter.notifyItemChanged(myPosition);
    }

    private void ShareFlashDialogV2() {
        ShareFlashDialogV2 alertDialog = new ShareFlashDialogV2(getActivity()).builder();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position){
                case 0:
                    KLog.d("tag","海报 朋友 1");

                    sharePositon = 1;
                    flash_ll.removeAllViews();
                    initShareLayout();
                    bulletinShare();

                    break;
                case 1:
                    KLog.d("tag","海报 朋友圈 0 ");
                    sharePositon = 0;
                    flash_ll.removeAllViews();
                    initShareLayout();
                    bulletinShare();
                    break;
                case 2:
                    KLog.d("tag","vx朋友");
                    sharePositon = 1;
                    bulletinShareByLink();
                    MobclickAgentUtils.onEvent(UmengEvent.index_news_sharelinks_weixin_2_2_0);
                    break;
                case 3:
                    KLog.d("tag","vx朋友圈");
                    sharePositon = 0;
                    bulletinShareByLink();
                    MobclickAgentUtils.onEvent(UmengEvent.index_news_sharelinks_moments_2_2_0);
                    break;
                default:
            }
        });
        alertDialog.show();
    }




    /** --------------------------------- 快讯分享  ---------------------------------*/
    //分享时的bean数据 -- 来自后台
    private FlashBulltinBean mFlashBulltinBeanShare;
    private FlashBulltinBean.BuilltinBean mBuilltinBean;

    private void bulletinShare() {
        Map<String,String> map = new HashMap<>();
        map.put("id",mTempBuilltinBean.getId() + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().bulletinShare(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<FlashBulltinBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<FlashBulltinBean> response) {
                        mFlashBulltinBeanShare = response.getReturn_data();
                        if(null != mFlashBulltinBeanShare){
                            mBuilltinBean = mFlashBulltinBeanShare.getList().get(0);

                            if(null != mBuilltinBean){
                                String url = mBuilltinBean.getQrcode_url();
                                Bitmap bitmap = ZXingUtils.createQRImage(url, SizeUtils.dp2px(48), SizeUtils.dp2px(48));
                                image33.setImageBitmap(bitmap);

                                setData();
                            }

                        }

                    }
                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }
                    }
                });
    }

    private void bulletinShareByLink() {
        Map<String,String> map = new HashMap<>();
        map.put("id",mTempBuilltinBean.getId() + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().bulletinShare(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<FlashBulltinBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<FlashBulltinBean> response) {
                        mFlashBulltinBeanShare = response.getReturn_data();
                        if(null != mFlashBulltinBeanShare){
                            mBuilltinBean = mFlashBulltinBeanShare.getList().get(0);

                            if(null != mBuilltinBean){
                                if(0 == sharePositon){
                                    shareWxCircleByLink(mBuilltinBean);
                                }else if(1 == sharePositon){
                                    shareWxByLink(mBuilltinBean);
                                }
                            }
                        }

                    }
                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }
                    }
                });
    }


    /** --------------------------------- 微信分享  ---------------------------------*/
    private TextView title;
    private TextView tag;
    private TextView today_time;
    private TextView time_txt;
    private ImageView image33;
    private LinearLayout flash_ll;
    private Bitmap bitmap;
    private int sharePositon;

    private void setData() {
        TextPaint paint = title.getPaint();
        paint.setFakeBoldText(true);
        title.setText(mTempBuilltinBean.getTitle());
        tag.setText(mTempBuilltinBean.getContent());

        String time = "";
        String time2 = "";
        if(!TextUtils.isEmpty(mTempBuilltinBean.getPub_time())){
            time =  TimeUtils.millis2String(Long.parseLong(mTempBuilltinBean.getPub_time())* 1000L,"yyyy/MM/dd");
            time_txt.setText(time);
            time2 =  TimeUtils.millis2String(Long.parseLong(mTempBuilltinBean.getPub_time())* 1000L,"HH:mm");
            today_time.setText(time2);
        }

        KLog.d("tag","日期是：" + time + " " + " 今天时间是 " + time2);
        //TODO 海报的时间生成的不对，验证是布局生成的问题
        new Handler().postDelayed(() -> getShareImg(),500);
    }

    // 生成海报
    private void getShareImg() {
        flash_ll.refreshDrawableState();
        flash_ll.setDrawingCacheEnabled(true);
        flash_ll.buildDrawingCache();
        new Handler().postDelayed(() -> {
            // 要在运行在子线程中
            // 获取图片
            bitmap = flash_ll.getDrawingCache();
            if (bitmap == null) {//处理华为meta9等手机出现的问题

                if(flash_ll.getWidth() == 0){
                    int width = ScreenUtils.getScreenWidth();
                    int height = ScreenUtils.getScreenHeight();
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    if (Build.VERSION.SDK_INT >= 11) {
                        flash_ll.measure(View.MeasureSpec.makeMeasureSpec(width,
                                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
                        flash_ll.layout((int) flash_ll.getX(),
                                (int) flash_ll.getY(),
                                (int) flash_ll.getX() + width,
                                (int) flash_ll.getY() + height);
                    } else {
                        flash_ll.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        flash_ll.layout(0, 0, width,
                                height);
                    }
                    flash_ll.draw(canvas);


                }else{

                    bitmap = Bitmap.createBitmap(flash_ll.getWidth(),
                            flash_ll.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    if (Build.VERSION.SDK_INT >= 11) {
                        flash_ll.measure(View.MeasureSpec.makeMeasureSpec(flash_ll.getWidth(),
                                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(flash_ll.getHeight(), View.MeasureSpec.EXACTLY));
                        flash_ll.layout((int) flash_ll.getX(),
                                (int) flash_ll.getY(),
                                (int) flash_ll.getX() + flash_ll.getMeasuredWidth(),
                                (int) flash_ll.getY() + flash_ll.getMeasuredHeight());
                    } else {
                        flash_ll.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        flash_ll.layout(0, 0, flash_ll.getMeasuredWidth(),
                                flash_ll.getMeasuredHeight());
                    }
                    flash_ll.draw(canvas);
                }
            }

            if(0 == sharePositon){
                shareWxCircleByPic();
            }else if(1 == sharePositon){
                shareWxByPic();
            }

        }, 0);
    }


    // 分享微信（link)
    public void shareWxByLink(FlashBulltinBean.BuilltinBean builltinBean) {
        if (this == null){
            return;
        }

        ShareBean bean1 = new ShareBean();
        bean1.setShareType("weixin_link");
        bean1.setImg(builltinBean.getPic());
        bean1.setLink(builltinBean.getUrl());
        bean1.setTitle(builltinBean.getTitle());
        bean1.setContent(builltinBean.getContent());
        StringUtil.shareWxByWeb(getActivity(), bean1);

        isFlashShare = true;
    }


    // 分享微信圈（link)
    public void shareWxCircleByLink(FlashBulltinBean.BuilltinBean builltinBean) {

        if (this == null){
            return;
        }

        ShareBean bean1 = new ShareBean();
        bean1.setShareType("circle_link");
        bean1.setImg(builltinBean.getPic());
        bean1.setLink(builltinBean.getUrl());
        bean1.setTitle(builltinBean.getTitle());
        bean1.setContent(builltinBean.getContent());
        StringUtil.shareWxByWeb(getActivity(), bean1);

        isFlashShare = true;

    }


    // 分享微信（pic)
    public void shareWxByPic() {
        if (this == null){
            return;
        }

        SHARE_MEDIA platform;
        platform = SHARE_MEDIA.WEIXIN;
        UMImage image = new UMImage(getActivity(), bitmap);
        UMImage  thumb = new UMImage(getActivity(), bitmap);
        image.setThumb(thumb);

        //传入平台
        new ShareAction(getActivity())
                .withText("哈哈")
                .setPlatform(platform)
                .withMedia(image)
                .share();
        isFlashShare = true;
    }


    // 分享微信圈（pic)
    public void shareWxCircleByPic() {
        if (this == null){
            return;
        }

        SHARE_MEDIA platform;
        platform = SHARE_MEDIA.WEIXIN_CIRCLE;
        UMImage  image = new UMImage(getActivity(), bitmap);

        //传入平台
        new ShareAction(getActivity())
                .withText("哈哈")
                .setPlatform(platform)
                .withMedia(image)
                .share();

        isFlashShare = true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBus(toRefreshEvent event){
        if(getUserVisibleHint()){
            KLog.d("tag","我是快讯界面，我刷新了");
            mRecyclerView.scrollToPosition(0);
            smartRefreshLayout.autoRefresh();
        }
    }


    /** --------------------------------- 快讯微信请求分享  ---------------------------------*/

    public  boolean isFlashShare = false;

    private void addBulletinSharePoint() {

        Map<String,String> map = new HashMap<>();
        map.put("kid",mTempBuilltinBean.getId() + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().addBulletinSharePoint(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<FlashOkBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<FlashOkBean>response) {
                        FlashOkBean tem = response.getReturn_data();
                        if("1".equals(tem.getIs_award())){
                            ToastUtils.setGravity(Gravity.CENTER,0,0);
                            ToastUtils.showShort("分享成功，获得5羽毛");
                        }else{
                            ToastUtils.setGravity(Gravity.CENTER,0,0);
                            ToastUtils.showShort("分享成功");
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isFlashShare){
            addBulletinSharePoint();
            isFlashShare = false;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toFlashSpecificEvent(FlashSpecificEvent event){
        String po =  event.getTag();
        KLog.d("tag","定位到具体flash 的位置 ,快讯索引是 " + po);
        toFixLoaction(po);
    }


    //跳转到固定的位置
    public void toFixLoaction(String flashid) {
        int mPosition = 0;
        if (mFlashItemAdapter != null) {
            for (int i = 0; i < mFlashItemAdapter.getData().size(); i++) {
                if (mFlashItemAdapter.getData().get(i).getId().equals(flashid)) {
                    mPosition = i;
                    break;
                }
            }

            LinearLayoutManager mLayoutManager =
                    (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(mPosition, 0);
        }
    }




}
