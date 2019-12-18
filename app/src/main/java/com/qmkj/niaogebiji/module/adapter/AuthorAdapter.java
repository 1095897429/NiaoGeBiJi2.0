package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
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

    @Override
    protected void convert(BaseViewHolder helper, AuthorBean.Author item) {
        //设置子View点击事件
//        helper.addOnClickListener(R.id.focus).addOnClickListener(R.id.focus_aleady);


        TextView chineseTv = helper.getView(R.id.author_name);
        TextPaint paint = chineseTv.getPaint();
        paint.setFakeBoldText(true);

        helper.setText(R.id.author_name,item.getName()).setText(R.id.author_tag,item.getSummary());

        //图片
        ImageUtil.load(mContext,item.getImg(),helper.getView(R.id.head_icon));

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
        helper.getView(R.id.focus).setOnClickListener(view -> showCancelFocusDialog(helper.getAdapterPosition()));

        helper.getView(R.id.focus_aleady).setOnClickListener(view -> showCancelFocusDialog(helper.getAdapterPosition()));


        helper.itemView.setOnClickListener(view -> {


            AuthorBean.Author mAuthor = mData.get(helper.getAdapterPosition());
            KLog.d("tag","点击的是 position " + helper.getAdapterPosition() );
//            String link =  StringUtil.getLink("authordetail/" + mAuthor.getId());
            String test = "http://192.168.14.103:8080/" + "authordetail/" + mAuthor.getId();
            UIHelper.toWebViewActivity(mContext,test);
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
            iosAlertDialog.setPositiveButton("确定", v -> {
                followAuthor(position);
            }).setNegativeButton("取消", v -> {}).setMsg("确定要 " + name +"关注「" + author  +"」").setCanceledOnTouchOutside(false);
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

