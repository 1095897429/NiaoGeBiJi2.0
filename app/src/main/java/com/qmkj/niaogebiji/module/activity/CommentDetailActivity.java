package com.qmkj.niaogebiji.module.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.TalkAlertDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.CommentAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
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
 * 创建时间 2019-11-21
 * 描述:一级评论 (帖子 文章 快讯)
 *
 * 脉脉是所有评论中回复一条之后 二级评论大于1 显示 共2条回复>> !! 擦，2条也不显示显示更多
 */
public class CommentDetailActivity extends BaseActivity {


    @BindView(R.id.ll_have_comment)
    LinearLayout ll_have_comment;

    @BindView(R.id.first_comment_num)
    TextView first_comment_num;

    @BindView(R.id.more_comment_recycler)
    RecyclerView mRecyclerView;

    //1级 适配器
    CommentAdapter mCommentAdapter;
    //组合集合
    List<CommentBean.FirstComment> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private int page = 1;

    private String blog_id = "5";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_detail;
    }

    @Override
    protected void initView() {
        initLayout();
        getData();
        blogDetail();
        getBlogComment();
    }


    private void blogDetail() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",blog_id + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().blogDetail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<User_info>>() {
                    @Override
                    public void onSuccess(HttpResponse<User_info> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                    }

                });
    }


    private void getBlogComment() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",blog_id + "");
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getBlogComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<CommentBeanNew>>() {
                    @Override
                    public void onSuccess(HttpResponse<CommentBeanNew> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                    }

                });
    }

    private void getData() {
        //模拟评论数据
        CommentBean.FirstComment  bean1 ;
        List<CommentBean.SecondComment> secondCommentList = new ArrayList<>();
        List<CommentBean.SecondComment> secondCommentList2 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            bean1 = new CommentBean.FirstComment();
            //模拟一级评论下有一条评论
            if(i == 0 ){
                CommentBean.SecondComment secondComment = new CommentBean.SecondComment();
                secondCommentList.add(secondComment);
                bean1.setCommentslist(secondCommentList);
            }else if(i == 1){
                //模拟一级评论下有来两条评论
                CommentBean.SecondComment secondComment = new CommentBean.SecondComment();
                secondCommentList2.add(secondComment);
                CommentBean.SecondComment secondComment2 = new CommentBean.SecondComment();
                secondCommentList2.add(secondComment2);
                bean1.setCommentslist(secondCommentList2);
            }else{

            }

            bean1.setMessage("10月31日，格力电器公告拟修订公司章程，其中，经营范围新增了「研发、制造、销售新能源发电产品、储能系统及充电桩」的内容。");
            mAllList.add(bean1);
        }

        mCommentAdapter.setNewData(mAllList);

    }


    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCommentAdapter = new CommentAdapter(mAllList);
        mRecyclerView.setAdapter(mCommentAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
        mCommentAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击一级评论去二级评论");
            showSheetDialog();
            getCommentData();
        });

        mCommentAdapter.setOnLoadMoreListener(() -> {
            ++page;
            KLog.d("tag","加载更多");
        },mRecyclerView);

        setEmpty(mCommentAdapter);


        mCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.comment_delete:
                    KLog.d("tag","删除自己的帖子");
                    break;
                case R.id.toSecondComment:
                case R.id.ll_has_second_comment:
                    KLog.d("tag","点击一级评论去二级评论");
                    showSheetDialog();
                    getCommentData();
                    break;
                case R.id.comment_priase:
                    KLog.d("tag","帖子点赞");
                    break;

                    default:
            }
        });
    }



    @OnClick({R.id.toComment,R.id.circle_comment,
            R.id.iv_back

    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.circle_comment:
            case R.id.toComment:
                showTalkDialog(-1,"","first");
                break;
            default:
        }
    }



    //参数一 用于数据更新      参数三 评论一级 还是 评论二级
    private void showTalkDialog(int position,String talkCid,String from) {
        final TalkAlertDialog talkAlertDialog = new TalkAlertDialog(this).builder();
        talkAlertDialog.setMyPosition(position);
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            KLog.d("tag","接受到的文字是 " + words);
            commentBulletin(position,words,talkCid,from);
        });
        talkAlertDialog.show();
    }



    //追加一条数据，在之前的集合上
    private void commentBulletin(int position,String content,String talkCid,String from) {
        if("first".equals(from)){
            //本地测试
            CommentBean.FirstComment firstComment = new CommentBean.FirstComment();
            firstComment.setMessage(content);
            mAllList.add(firstComment);
            mCommentAdapter.notifyItemChanged(position);
        }else if("second".equals(from)){
            MulSecondCommentBean bean;
            bean = new MulSecondCommentBean();

            CommentBean.FirstComment  bean1 = new CommentBean.FirstComment();
            //TODO 这里是相当于二级评论中的一级评论
            bean1.setMessage(content);
            bean.setFirstComment(bean1);

            bean.setItemType(1);
            list.add(bean);
            bottomSheetAdapter.notifyItemChanged(position);
        }
    }


    private void updateListDataStatus(int position,CommentOkBean bean) {
//            if(null == bean){
//                bean = new CommentOkBean();
//                bean.setCid("1");
//                bean.setContent("我是帅哥");
//                bean.setUsername("周六");
//            }

//             CommentBean.FirstComment bean1 =  new CommentBean.FirstComment();
//
//             mAllList.add(bean1);
//
//            mCommentAdapter.notifyItemChanged(position);
    }


    /** --------------------------------- 二级弹框 评论  ---------------------------------*/
    int secondPage = 1;
    RelativeLayout totalk;
    RecyclerView mSecondRV;
    BottomSheetDialog bottomSheetDialog;
    CommentSecondAdapter bottomSheetAdapter;
    BottomSheetBehavior mDialogBehavior;

    List<MulSecondCommentBean> list = new ArrayList<>();

    private void getCommentData() {

        list.clear();

        MulSecondCommentBean bean;
        bean = new MulSecondCommentBean();
        bean.setItemType(2);
        list.add(bean);

        //模拟评论数据
        CommentBean.FirstComment  bean1 ;
        for (int i = 0; i < 4; i++) {
            bean = new MulSecondCommentBean();
            bean1 = new CommentBean.FirstComment();
            bean1.setMessage("我是二级评论 " + i);
            bean.setFirstComment(bean1);
            bean.setItemType(1);
            list.add(bean);

        }

        bottomSheetAdapter.setNewData(list);

    }

    private void showSheetDialog() {
        View view = View.inflate(this, R.layout.dialog_bottom_comment, null);
        mSecondRV = view.findViewById(R.id.recycler);
        totalk = view.findViewById(R.id.totalk);
        totalk.setOnClickListener(view1 -> showTalkDialog(-1,"111","second"));

        bottomSheetAdapter = new CommentSecondAdapter(list);
        mSecondRV.setHasFixedSize(true);
        mSecondRV.setLayoutManager(new LinearLayoutManager(this));
        mSecondRV.setItemAnimator(new DefaultItemAnimator());
        mSecondRV.setAdapter(bottomSheetAdapter);

        bottomSheetDialog = new BottomSheetDialog(this, R.style.MyCommentDialog);
        bottomSheetDialog.setContentView(view);

        mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(ScreenUtils.getScreenHeight());
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDialog.dismiss();
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                if(newState == BottomSheetBehavior.STATE_DRAGGING){
                    KLog.d("tag",bottomSheet.getTop() + "");
                }

                //手指移动布局的高度
                if(newState == BottomSheetBehavior.STATE_SETTLING){

                    KLog.d("tag11","屏幕的高度减去状态栏高度是 : " +  (ScreenUtils.getScreenHeight() - SizeUtils.dp2px(25)) +  "   " + bottomSheet.getTop() + "");

                    if(bottomSheet.getTop() >= 200){
                        bottomSheetDialog.dismiss();
                        mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


        bottomSheetDialog.show();

        initScondEvent();
    }

    private void initScondEvent() {

        bottomSheetAdapter.setOnLoadMoreListener(() -> {
            ++secondPage;
            KLog.d("tag","加载更多");
        },mSecondRV);

        setEmpty(bottomSheetAdapter);

        bottomSheetAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showTalkDialog(position,"1111","second");
            }
        });
    }


}



