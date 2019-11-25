package com.qmkj.niaogebiji.module.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.TalkAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CommentAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
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
 * 描述:评论详情 (帖子 文章 快讯)
 */
public class CommentDetailActivity extends BaseActivity {

    @BindView(R.id.ll_have_comment)
    LinearLayout ll_have_comment;

    @BindView(R.id.ll_nocomment)
    LinearLayout ll_nocomment;

    @BindView(R.id.more_comment_recycler)
    RecyclerView mRecyclerView;

    //适配器
    CommentAdapter mCommentAdapter;
    //组合集合
    List<CommentBean.FirstComment> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_detail;
    }

    @Override
    protected void initView() {
        initLayout();
        getData();
    }

    private void getData() {

        CommentBean.FirstComment  bean1 ;
        for (int i = 0; i < 2; i++) {
            bean1 = new CommentBean.FirstComment();
            mAllList.add(bean1);
        }

        mCommentAdapter.setNewData(mAllList);

        if(mAllList.isEmpty()){
            ll_nocomment.setVisibility(View.VISIBLE);
        }else{
            ll_have_comment.setVisibility(View.VISIBLE);
        }
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
        mCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.comment_delete:
                    KLog.d("tag","删除自己的帖子");
                    break;
                case R.id.toSecondComment:
                case R.id.ll_has_second_comment:
                    KLog.d("tag","去二级评论");
                    break;
                case R.id.comment_priase:
                    KLog.d("tag","帖子点赞");
                    break;

                    default:
            }
        });
    }


    @OnClick({R.id.toComment,R.id.iv_back})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.toComment:
                showTalkDialog(-1,"");
                break;
            default:
        }
    }



    private void showTalkDialog(int position,String talkCid) {
        final TalkAlertDialog talkAlertDialog = new TalkAlertDialog(this).builder();
        talkAlertDialog.setMyPosition(position);
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            KLog.d("tag","接受到的文字是 " + words);
            commentBulletin(position,words,talkCid);

        });
        talkAlertDialog.show();
    }



    private void commentBulletin(int position,String content,String c_id) {

        //网络测试
//        Map<String,String> map = new HashMap<>();
//        map.put("target_id","");
//        map.put("content",content);
//        map.put("reply_id",c_id);
//        String result = RetrofitHelper.commonParam(map);
//        RetrofitHelper.getApiService().createComment(result)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                .subscribe(new BaseObserver<HttpResponse<CommentOkBean>>() {
//                    @Override
//                    public void onSuccess(HttpResponse<CommentOkBean> response) {
//
//                        //TODO 10.12 评论成功不给与展示
//                        CommentOkBean bean = response.getReturn_data();
//                        updateListDataStatus(position,bean);
//                    }
//
//                });

        //本地测试
        updateListDataStatus(position,null);
    }


    private void updateListDataStatus(int position,CommentOkBean bean) {
//            if(null == bean){
//                bean = new CommentOkBean();
//                bean.setCid("1");
//                bean.setContent("我是帅哥");
//                bean.setUsername("周六");
//            }

             CommentBean.FirstComment bean1 =  new CommentBean.FirstComment();

              mAllList.add(bean1);

            mCommentAdapter.notifyItemChanged(position);
    }


}



