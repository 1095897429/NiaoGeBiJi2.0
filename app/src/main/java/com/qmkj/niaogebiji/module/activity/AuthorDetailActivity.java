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
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
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

import butterknife.BindView;
import butterknife.OnClick;
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
    private AuthorBean.Author mAuthor;

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


    @BindView(R.id.part_small_head)
    LinearLayout part_small_head;



    //列表数据
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;


    private int page = 1;
    LinearLayoutManager mLinearLayoutManager;
    FirstItemNewAdapter mFirstItemAdapter;
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

    }


    int scrollY = 0;

    @Override
    public void initFirstData() {

        authorId = getIntent().getStringExtra("authorId");

        //伪代码
//        if(未关注/关注){
//            显示：图标
//        }

        String url = "https://desk-fd.zol-img.com.cn/t_s2560x1440c5/g2/M00/05/09/ChMlWl1BAz-IcV0oADKEXBJ0ncgAAMP0gAAAAAAMoR0279.jpg";
        Glide.with(this).load(url)
                .apply(bitmapTransform(new BlurTransformation(25)))
                .into(bg_img);
//
//        ImageUtil.loadByDefaultHead(this,url,id_auhtor_img);



        initLayout();

        initFdddData();


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

            mFirstItemAdapter.setNewData(mAllList);
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
            mFirstItemAdapter.addData(temps);
            mFirstItemAdapter.loadMoreComplete();
        }else{
            mFirstItemAdapter.loadMoreEnd();
        }

    }


    View headView;
    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFirstItemAdapter = new FirstItemNewAdapter(mAllList);
        mRecyclerView.setAdapter(mFirstItemAdapter);
        //false 这个方法主要是设置RecyclerView不处理滚动事件(主要用于嵌套中)
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        mFirstItemAdapter.setOnLoadMoreListener(() -> {
            ++page;
            initFdddData();
        },mRecyclerView);


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
            //分享参考学院答题 / 用户中心
            case R.id.iv_right_1:
                showShareDialog();
                break;
            case R.id.focus:
            case R.id.focus_aleady:
            case R.id.part_small_focus:
            case R.id.part_small_already_focus:
                ToastUtils.showShort("点击乐乐乐了");
                //带有authorId的事件
                UpdateHomeListEvent event = new UpdateHomeListEvent();
                event.setAuthorId(authorId);
                EventBus.getDefault().post(event);
//                showCancelFocusDialog();
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
            StringUtil.shareWxByWeb(AuthorDetailActivity.this,bean);

        }
    };



    private void showCancelFocusDialog() {
        if(mAuthor.getIs_follow() == 1){
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
                        if(1 == mAuthor.getIs_follow()){
                            mAuthor.setIs_follow(0);

                            focus.setVisibility(View.VISIBLE);
                            part_small_focus.setVisibility(View.VISIBLE);
                            focus_aleady.setVisibility(View.GONE);
                            part_small_already_focus.setVisibility(View.GONE);
                        }else{
                            mAuthor.setIs_follow(1);

                            focus.setVisibility(View.GONE);
                            part_small_focus.setVisibility(View.GONE);
                            focus_aleady.setVisibility(View.VISIBLE);
                            part_small_already_focus.setVisibility(View.VISIBLE);
                        }

                        //带有authorId的事件 -- 没有调用接口时，手动的判断可以
                        UpdateHomeListEvent event = new UpdateHomeListEvent();
                        event.setAuthorId(authorId);
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
