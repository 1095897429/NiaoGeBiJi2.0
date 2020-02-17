package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.AuthorListActivity;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.event.UpdateHomeListEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:
 */
public class AuthorAdapter extends BaseQuickAdapter<AuthorBean.Author, BaseViewHolder> {
    public AuthorAdapter(@Nullable List<AuthorBean.Author> data) {
        super(R.layout.author_item,data);
    }

    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    @Override
    protected void convert(BaseViewHolder helper, AuthorBean.Author item) {

        TextView chineseTv = helper.getView(R.id.author_name);
        TextPaint paint = chineseTv.getPaint();
        paint.setFakeBoldText(true);

        helper.setText(R.id.author_name,item.getName());

        //作者简介
        TextView sumary = helper.getView(R.id.author_tag);
        if(!TextUtils.isEmpty(item.getSummary())){
            sumary.setText(item.getSummary());
        }else{
//            sumary.setText("欢迎在鸟哥笔记发布文章。投稿请添加微信（ngbjxym）");
            sumary.setText("");
        }

        //展示作者等级
//        if("1".equals(item.getNewsItemBean().getRank()) ||
//                "2".equals(item.getNewsItemBean().getRank()) ||
//                "3".equals(item.getNewsItemBean().getRank()) ){
//            helper.setImageResource(R.id.author_type,R.mipmap.hot_author_professor);
//        }else{
//            helper.setImageResource(R.id.author_type,R.mipmap.hot_author_new);
//        }

        //图片
        if(!TextUtils.isEmpty(item.getImg())){
            ImageUtil.loadByDefaultHead(mContext,item.getImg() + scaleSize,helper.getView(R.id.head_icon));

        }

        //是否关注：1-关注，0-未关注
        if(0 == item.getIs_follow()){
            helper.setBackgroundRes(R.id.focus,R.drawable.bg_corners_8_yellow);
            helper.setVisible(R.id.focus,true);
            helper.setVisible(R.id.focus_aleady,false);
        }else{
            helper.setBackgroundRes(R.id.focus,R.drawable.bg_corners_8_gray);
            helper.setVisible(R.id.focus,false);
            helper.setVisible(R.id.focus_aleady,true);
        }

        //关注
        helper.getView(R.id.focus).setOnClickListener(view ->{

            MobclickAgentUtils.onEvent("index_flow_authorlist_followbtn"+ (helper.getAdapterPosition()  + 1) +"_2_0_0");

             showCancelFocusDialog(helper.getAdapterPosition());
        });

        helper.getView(R.id.focus_aleady).setOnClickListener(view -> showCancelFocusDialog(helper.getAdapterPosition()));


        helper.itemView.setOnClickListener(view -> {

            MobclickAgentUtils.onEvent("index_flow_authorlist_author"+ (helper.getAdapterPosition()  + 1) +"_2_0_0");

            if(helper.getAdapterPosition() <= 2) {
                MobclickAgentUtils.onEvent("index_search_author_" + (helper.getAdapterPosition() + 1) + "_2_0_0");
            }

            AuthorBean.Author mAuthor = mData.get(helper.getAdapterPosition());
            KLog.d("tag","点击的是 position " + helper.getAdapterPosition() );
            String link =  StringUtil.getLink("authordetail/" + mAuthor.getId());
            UIHelper.toWebViewActivity(mContext,link);
        });

    }


    private String focus_type = "1";
    private String author_id;
    private AuthorBean.Author mAuthor;
    public void showCancelFocusDialog(int position){
        String name ;
        String author ;
        mAuthor = mData.get(position);
        author_id = mAuthor.getId();
        author = mAuthor.getName();
        if(mAuthor.getIs_follow() == 1){
            name = "取消";
            focus_type = "0";
            final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(mContext).builder();
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
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse<IndexFocusBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<IndexFocusBean> response) {
                        if(1 == mAuthor.getIs_follow()){
                            mAuthor.setIs_follow(0);
                        }else{
                            mAuthor.setIs_follow(1);
                        }
                        notifyItemChanged(position);

                        //TODO 11.14 统一发送事件，更新主界面
                        EventBus.getDefault().post(new UpdateHomeListEvent());
                    }

                });
    }



}

