package com.qmkj.niaogebiji.module.activity;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.listener.ToActivityFocusListener;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.AuthorAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.CompleInfoEvent;
import com.qmkj.niaogebiji.module.event.UpdateHomeListEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
 * 创建时间 2019-11-14
 * 描述:作者列表
 */
public class AuthorListActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_author_list;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {
        tv_title.setText("推荐作者");
        initLayout();
        initSamrtLayout();
        authorList();
    }



    private void authorList() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().authorList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<AuthorBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<AuthorBean> response) {
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        AuthorBean mAuthorBean = response.getReturn_data();
                        if(null != mAuthorBean){
                            mAuthors =  mAuthorBean.getList();

                            if(1 == page){
                                mAuthorAdapter.setNewData(mAuthors);
                            }else{
                                //已为加载更多有数据
                                if(mAuthors != null && mAuthors.size() > 0){
                                    mAuthorAdapter.loadMoreComplete();
                                    mAuthorAdapter.addData(mAuthors);
                                }else{
                                    //已为加载更多无更多数据
                                    mAuthorAdapter.loadMoreEnd();
                                }
                            }
                        }

                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }
                });
    }




    /** --------------------------------- 通用的配置  ---------------------------------*/
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    AuthorAdapter mAuthorAdapter;
    //组合集合
    private List<AuthorBean.Author> mAuthors;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    private int page = 1;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mAuthorAdapter = new AuthorAdapter(mAuthors);
        mRecyclerView.setAdapter(mAuthorAdapter);
        //禁止动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        initEvent();

    }

    private void initEvent() {

        mAuthorAdapter.setOnLoadMoreListener(() -> {
            ++page;
            authorList();
        },mRecyclerView);

        //点击事件
//        mAuthorAdapter.setOnItemClickListener((adapter, view, position) -> {
//            AuthorBean.Author mAuthor = mAuthorAdapter.getData().get(position);
//            KLog.d("tag","点击的是 position " + position );
////            String link =  StringUtil.getLink("authordetail/" + mAuthor.getId());
//
//            String test = "http://192.168.14.103:8081/" + "authordetail/" + mAuthor.getId();
//            UIHelper.toWebViewActivity(AuthorListActivity.this,test);
//        });


        //关注事件 -- 获取uid
        mAuthorAdapter.setToActivityFocusListener(position -> {

            mPosition = position;
            uid = mAuthorAdapter.getData().get(position).getUid();

            //用户的个人信息
            RegisterLoginBean.UserInfo user = StringUtil.getUserInfoBean();
            if(TextUtils.isEmpty(user.getCompany_name()) &&
                    TextUtils.isEmpty(user.getPosition()) ){
                showProfessionAuthenNo();
                return;
            }

            //认证过了直接去打招呼界面
            if("1".equals(user.getAuth_email_status()) || "1".equals(user.getAuth_card_status())){
                UIHelper.toHelloMakeActivity(AuthorListActivity.this);
                overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
            }else{
                showProfessionAuthen();
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

    //打招呼返回的字段
    private String message = "";
    private String uid;
    private int mPosition;
    private void followUser() {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",uid);
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

                        mAuthorAdapter.notifyItemChanged(mPosition);
                    }
                });
    }






    private void initSamrtLayout() {
        smartRefreshLayout.setEnableLoadMore(false);
        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAuthors.clear();
            page = 1;
            authorList();
        });
    }



    /** --------------------------------- 关注 取关  ---------------------------------*/
    //1-去关注，0-取消关注
    private String focus_type = "1";
    private String author_id;
    private AuthorBean.Author mAuthor;

    public void showCancelFocusDialog(int position){
        String name ;
        String author ;
        mAuthor = mAuthorAdapter.getData().get(position);
        author_id = mAuthor.getId();
        author = mAuthor.getName();
        if(mAuthor.getIs_follow() == 1){
            name = "取消";
            focus_type = "0";
            final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(AuthorListActivity.this).builder();
            iosAlertDialog.setPositiveButton("取消关注", v -> {
                followAuthor(position);
            }).setNegativeButton("再想想", v -> {}).setMsg("取消关注?").setCanceledOnTouchOutside(false);
            iosAlertDialog.show();
        }else{
            focus_type = "1";
            followAuthor(position);
        }
    }



    private void followAuthor(int position) {
        Map<String,String> map = new HashMap<>();
        map.put("type",focus_type);
        map.put("id",author_id);
        String result = RetrofitHelper.commonParam(map);


        RetrofitHelper.getApiService().followAuthor(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<IndexFocusBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<IndexFocusBean> response) {
                        if(1 == mAuthor.getIs_follow()){
                            mAuthor.setIs_follow(0);
                        }else{
                            mAuthor.setIs_follow(1);
                        }
                        mAuthorAdapter.notifyItemChanged(position);

                        //TODO 11.14 统一发送事件，更新主界面
                        EventBus.getDefault().post(new UpdateHomeListEvent());
                    }

                });
    }


    @OnClick({R.id.iv_back})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;

            default:
        }
    }



    /** 点击关注，取关回调此方法 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(UpdateHomeListEvent event){
        //更新某条数据 在集合中找到
        if(null != this){
            int position;
            int is_follow = event.getIs_follow();
            String authorId = event.getAuthorId();
            for (AuthorBean.Author temp : mAuthorAdapter.getData()) {
                if(authorId.equals(temp.getId())){
                    position = mAuthorAdapter.getData().indexOf(temp);
                    temp.setIs_follow(is_follow);
                    mAuthorAdapter.notifyItemChanged(position);
                    break;
                }
            }
        }

    }


}
