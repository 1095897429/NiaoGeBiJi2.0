package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorArticleBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.AuthorDetailBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.event.ShowTopAuthorEvent;
import com.qmkj.niaogebiji.module.event.ShowTopTitleEvent;
import com.qmkj.niaogebiji.module.event.UpdateHomeListEvent;
import com.qmkj.niaogebiji.module.event.toRefreshMoringEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.RecyclerViewNoBugLinearLayoutManager;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-08-19
 * 描述:作者详情页
 * list布局参考 文章分类列表
 */
public class AuthorDetailActivity extends BaseActivity {

    //作者
    private String focus_type = "1";
    private String authorId;

    @BindView(R.id.focus)
    TextView focus;

    @BindView(R.id.focus_aleady)
    TextView focus_aleady;

    @BindView(R.id.part_small_focus)
    TextView part_small_focus;

    @BindView(R.id.part_small_already_focus)
    TextView part_small_already_focus;

    @BindView(R.id.id_auhtor_img)
    ImageView id_auhtor_img;

    @BindView(R.id.bg_img)
    ImageView bg_img;

    @BindView(R.id.small_head_icon)
    CircleImageView small_head_icon;

    @BindView(R.id.tv_title)
    TextView tv_title;


    @BindView(R.id.author_type)
    ImageView author_type;

    @BindView(R.id.author_desc)
    TextView author_desc;

    @BindView(R.id.acticle_count)
    TextView acticle_count;

    @BindView(R.id.hint_num)
    TextView hint_num;


    @BindView(R.id.author_name)
    TextView author_name;


    @BindView(R.id.part_small_head)
    LinearLayout part_small_head;



    //列表数据
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;



    private int page = 1;
    LinearLayoutManager mLinearLayoutManager;
    FirstItemNewAdapter mFirstItemNewAdapter;
    List<MultiNewsBean> mAllList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_author_detail;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }



    @Override
    protected void initView() {
        mExecutorService = Executors.newFixedThreadPool(2);
    }


    int scrollY = 0;

    @Override
    public void initFirstData() {

        authorId = getIntent().getStringExtra("authorId");

        authorView();

        initLayout();

        getAuthorArticle();

//        initFdddData();



//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//              @Override
//              public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                  super.onScrollStateChanged(recyclerView, newState);
//              }
//
//              @Override
//              public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                  super.onScrolled(recyclerView, dx, dy);
//
//                  scrollY += dy;
//
//                  if(scrollY > SizeUtils.dp2px(211f)){
//                      setStatusBarColor(AuthorDetailActivity.this,getResources().getColor(R.color.white));
//                      rl_title.setBackgroundColor(getResources().getColor(R.color.white));
//                      part_small_head.setVisibility(View.VISIBLE);
//                  }else{
//                      setStatusBarColor(AuthorDetailActivity.this,Color.TRANSPARENT);
//                      rl_title.setBackgroundColor(Color.TRANSPARENT);
//                      part_small_head.setVisibility(View.GONE);
//
//                  }
//
//
//              }
//
//          });
    }

    private void getAuthorArticle() {
        KLog.e("tag","page " + page);
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        map.put("author_id",authorId);
        map.put("page_size",10 + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getAuthorArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<AuthorArticleBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<AuthorArticleBean> response) {
                        AuthorArticleBean bean = response.getReturn_data();
                        if(bean != null){
                            List<RecommendBean.Article_list> mList= bean.getList();
                            setArticleData(mList);
                        }
                    }
                });
    }

    private void setArticleData(List<RecommendBean.Article_list> articles) {
        if(1 == page){
            if(null != articles && !articles.isEmpty()){
                setActicleData(articles);
                mFirstItemNewAdapter.setNewData(mAllList);
                //如果第一次返回的数据不满10条，则显示无更多数据
                if(articles.size() < Constant.SEERVER_NUM){
                    mFirstItemNewAdapter.loadMoreComplete();
                    mFirstItemNewAdapter.loadMoreEnd();
                }
            }else{
                //第一次加载无数据
                ll_empty.setVisibility(View.VISIBLE);
                ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("暂无内容");
                ((ImageView)ll_empty.findViewById(R.id.iv_empty)).setImageResource(R.mipmap.icon_empty_article);
                mRecyclerView.setVisibility(View.GONE);
            }
        }else{
            //已为加载更多有数据
            if(articles != null && articles.size() > 0){
                setActicleData(articles);
                mFirstItemNewAdapter.addData(tempList);
                mFirstItemNewAdapter.loadMoreComplete();
            }else{
                //已为加载更多无更多数据
                mFirstItemNewAdapter.loadMoreEnd();
            }
        }
    }



    List<MultiNewsBean> tempList = new ArrayList<>();
    private void setActicleData(List<RecommendBean.Article_list> article_lists) {

        tempList.clear();
        RecommendBean.Article_list itemBean;
        MultiNewsBean bean1 ;

        String pic_type;
        for (int i = 0; i < article_lists.size(); i++) {
            itemBean = article_lists.get(i);
            bean1 = new MultiNewsBean();
            pic_type = article_lists.get(i).getPic_type();
            if("1".equals(pic_type)){
                bean1.setItemType(1);
            }else if("2".equals(pic_type)){
                bean1.setItemType(3);
            }else if("3".equals(pic_type)){
                bean1.setItemType(2);
            }else{
                bean1.setItemType(1);
            }
            bean1.setNewsActicleList(itemBean);
            tempList.add(bean1);
        }

        if(page == 1){
            mAllList.addAll(tempList);
        }
    }









    private AuthorDetailBean.AuthorDetail mAuthorDetail;
    private void authorView() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        map.put("id",authorId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().authorView(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<AuthorDetailBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<AuthorDetailBean> response) {
                        AuthorDetailBean bean = response.getReturn_data();
                        if(bean != null){
                            mAuthorDetail = bean.getDetail();
                            setHeadData(mAuthorDetail);
                        }
                    }
                });
    }

    private void setHeadData(AuthorDetailBean.AuthorDetail bean) {

        Glide.with(this).load(bean.getImg())
                .apply(bitmapTransform(new BlurTransformation(25)))
                .into(bg_img);
        ImageUtil.loadByDefaultHead(this,bean.getImg(),id_auhtor_img);


        ImageUtil.loadByDefaultHead(this,bean.getImg(),small_head_icon);

        author_name.setText(bean.getName());
        tv_title.setText(bean.getName());


        author_desc.setText(bean.getSummary());

        //发表文章
        acticle_count.setText(bean.getArticle_count());


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

        //影响数 -- 后台返回的就是带有w
        if(!TextUtils.isEmpty(bean.getHit_count())){
            hint_num.setText( bean.getHit_count());
//            long count = Long.parseLong(bean.getHit_count());
//            if(count < 10000 ){
//               hint_num.setText( bean.getHit_count());
//            }else{
//                double temp = count  ;
//                //1.将数字转换成以万为单位的数字
//                double num = temp / 10000;
//                BigDecimal b = new BigDecimal(num);
//                //2.转换后的数字四舍五入保留小数点后一位;
//                double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
//                hint_num.setText( f1 + " w");
//            }
        }

       if(1 == bean.getIs_follow()){
           focus_aleady.setVisibility(View.VISIBLE);
           part_small_already_focus.setVisibility(View.VISIBLE);
       }else {
           focus.setVisibility(View.VISIBLE);
           part_small_focus.setVisibility(View.VISIBLE);
       }
    }


    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorId);
        }
    }


    private void initFdddData() {
        KLog.d("tag","页数 page = " + page);
        MultiNewsBean bean1 ;
        RecommendBean.Article_list itemBean;
        if(page == 1){
            mAllList.clear();
            for (int i = 0; i < 10; i++) {
                itemBean = new RecommendBean.Article_list();
                itemBean.setAuthor("亮" + i);
                bean1 = new MultiNewsBean();
                bean1.setItemType(1);
                bean1.setNewsActicleList(itemBean);
                mAllList.add(bean1);
            }

            mFirstItemNewAdapter.setNewData(mAllList);
        }else if(page == 2){
            List<MultiNewsBean> temps = new ArrayList<>();
            temps.clear();
            for (int i = 0; i < 8; i++) {
                itemBean = new RecommendBean.Article_list();
                itemBean.setAuthor("周" + i);
                bean1 = new MultiNewsBean();
                bean1.setItemType(1);
                bean1.setNewsActicleList(itemBean);
                temps.add(bean1);
            }
            mFirstItemNewAdapter.addData(temps);
            mFirstItemNewAdapter.loadMoreComplete();
        }else{
            mFirstItemNewAdapter.loadMoreEnd();
        }

    }


    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFirstItemNewAdapter = new FirstItemNewAdapter(mAllList);
        mRecyclerView.setAdapter(mFirstItemNewAdapter);
        //false 这个方法主要是设置RecyclerView不处理滚动事件(主要用于嵌套中)
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        mFirstItemNewAdapter.setOnLoadMoreListener(() -> {
            ++page;
            getAuthorArticle();
        },mRecyclerView);


        //点击事件
        mFirstItemNewAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的是 position " + position );

            if(StringUtil.isFastClick()){
                return;
            }
            String aid = mFirstItemNewAdapter.getData().get(position).getNewsActicleList().getAid();
            if (!TextUtils.isEmpty(aid)) {
                UIHelper.toNewsDetailActivity(this, aid);
            }

        });


//        headView = LayoutInflater.from(this).inflate(R.layout.item_head_author_detail,null);
//
//        mFirstItemAdapter.setHeaderView(headView);
    }



    @OnClick({
            R.id.focus,R.id.part_small_focus,
            R.id.focus_aleady,R.id.part_small_already_focus,
            R.id.iv_back, R.id.iv_right_1
    })
    public void clicks(View view){
        switch (view.getId()){

            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right_1:
                showShareDialog();
                break;
            case R.id.focus:
            case R.id.focus_aleady:
            case R.id.part_small_focus:
            case R.id.part_small_already_focus:
                showCancelFocusDialog();
                break;

            default:
        }
    }


    /** --------------------------------- 分享  ---------------------------------*/
    private String mins;
    ShareBean bean = new ShareBean();
    Bitmap bitmap =  null;
    private ExecutorService mExecutorService;

    //此部分分享数据来自于后台
    private void showShareDialog() {

        //TODO 伪代码
//        if(数据来自后台){ ok
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
                    StringUtil.copyLink(mAuthorDetail.getShare_title() + "\n" +  mAuthorDetail.getShare_url());
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
            bean.setImg(mAuthorDetail.getShare_pic());
            bean.setLink(mAuthorDetail.getShare_url());
            bean.setTitle(mAuthorDetail.getShare_title());
            bean.setContent(mAuthorDetail.getShare_summary());
            if(msg.what == 0x111){
                bean.setShareType("circle_link");
            }else{
                bean.setShareType("weixin_link");
            }
            StringUtil.shareWxByWeb(AuthorDetailActivity.this,bean);

        }
    };



    private void showCancelFocusDialog() {
        if(mAuthorDetail.getIs_follow() == 1){
            focus_type = "0";
            final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(mContext).builder();
            iosAlertDialog.setPositiveButton("取消关注", v -> {
                followAuthor();
            }).setNegativeButton("再想想", v -> {}).setMsg("取消关注?").setCanceledOnTouchOutside(false);
            iosAlertDialog.show();
        }else{
            focus_type = "1";
            followAuthor();
        }
    }


    private String uid;



    private void followAuthor() {
        Map<String,String> map = new HashMap<>();
        map.put("type",focus_type);
        map.put("id",authorId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followAuthor(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse<IndexFocusBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<IndexFocusBean> response) {
                        if(1 == mAuthorDetail.getIs_follow()){
                            mAuthorDetail.setIs_follow(0);

                            focus.setVisibility(View.VISIBLE);
                            part_small_focus.setVisibility(View.VISIBLE);
                            focus_aleady.setVisibility(View.GONE);
                            part_small_already_focus.setVisibility(View.GONE);
                        }else{
                            mAuthorDetail.setIs_follow(1);
                            focus.setVisibility(View.GONE);
                            part_small_focus.setVisibility(View.GONE);
                            focus_aleady.setVisibility(View.VISIBLE);
                            part_small_already_focus.setVisibility(View.VISIBLE);

                            ToastUtils.showShort("关注成功，关注作者的文章已加入关注列表");

                        }

                        //带有authorId的事件 -- 没有调用接口时，手动的判断可以
                        UpdateHomeListEvent event = new UpdateHomeListEvent();
                        event.setAuthorId(authorId);
                        event.setIs_follow(mAuthorDetail.getIs_follow());
                        EventBus.getDefault().post(event);
                    }

                });
    }


    //滑动显示搜索
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowTopAuthorEvent(ShowTopAuthorEvent event) {
        if (this != null) {
            String statu = event.getData();
            if("1".equals(statu)){
                part_small_head.setVisibility(View.VISIBLE);
            }else{
                part_small_head.setVisibility(View.GONE);
            }
        }
    }



}
