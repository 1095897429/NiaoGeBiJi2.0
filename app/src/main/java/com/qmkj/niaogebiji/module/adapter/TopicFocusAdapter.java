package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
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
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.qmkj.niaogebiji.module.event.UpdapteListTopicEvent;
import com.qmkj.niaogebiji.module.event.UpdateCircleRecommendEvent;
import com.qmkj.niaogebiji.module.event.UpdateRecommendTopicFocusListEvent;
import com.qmkj.niaogebiji.module.event.UpdateTopicEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-2-13
 * 描述:在话题列表中 --- 话题关注适配器
 * 关注 参照作者
 */
public class TopicFocusAdapter extends BaseQuickAdapter<TopicBean, BaseViewHolder> {

    public TopicFocusAdapter(@Nullable List<TopicBean> data) {
        super(R.layout.item_topic_list_focus,data);
    }


    @Override
    protected void convert(BaseViewHolder helper,TopicBean item) {

        TextView chineseTv = helper.getView(R.id.top_title);
        TextPaint paint = chineseTv.getPaint();
        paint.setFakeBoldText(true);
        chineseTv.setText("#" + item.getTitle());

        //头像
        if(!TextUtils.isEmpty(item.getIcon())){
            ImageUtil.load(mContext,item.getIcon(),helper.getView(R.id.one_img_imgs));
        }


        helper.itemView.setOnClickListener(v -> {

            KeyboardUtils.hideSoftInput((Activity) mContext);
            if(StringUtil.isFastClick()){
                return;
            }

            MobclickAgentUtils.onEvent(UmengEvent.quanzi_topiclist_littletopic_2_2_0);


            UIHelper.toTopicDetailActivity(mContext,item.getId() + "");

        });



        TextView textView = helper.getView(R.id.top_focus_num);
        //关注数 x>=100000，展示10w+
        if(!TextUtils.isEmpty(item.getFollow_num())){
            long count = Long.parseLong(item.getFollow_num());
            if(count < 100000 ){
                textView.setText(item.getFollow_num() + "人关注");
            }else{
                double temp = count  ;
                //1.将数字转换成以万为单位的数字
                double num = temp / 10000;
                BigDecimal b = new BigDecimal(num);
                //2.转换后的数字四舍五入保留小数点后一位;
                double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                textView.setText(f1 + "w+" + "人关注");
            }
        }else{
            textView.setText("0人关注");
        };

        //是否关注 注：1-关注，0-取消关注
        if(!item.isIs_follow()){
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

            if(StringUtil.isFastClick()){
                return;
            }

            int mPosition = helper.getAdapterPosition();
            //手动修改
            TopicBean bean = mData.get(mPosition);
            if(bean != null && !bean.isIs_follow()){
                followTopic(mPosition,bean.getId() + "");
            }

        });
        //取关
        helper.getView(R.id.focus_aleady).setOnClickListener(view ->{

            if(StringUtil.isFastClick()){
                return;
            }

            int mPosition = helper.getAdapterPosition();
            //手动修改
            TopicBean bean = mData.get(mPosition);
            if(bean != null && !bean.isIs_follow()){
                followTopic(mPosition,bean.getId() + "");

                MobclickAgentUtils.onEvent(UmengEvent.quanzi_topiclist_follow_2_2_0);

            }else{
                showCancelFocusDialog(mPosition,bean.getId());
            }

        });

    }


    public void showCancelFocusDialog(int position, long topic_id){
        final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(mContext).builder();
        iosAlertDialog.setPositiveButton("取消关注", v -> {
            unfollowTopic(position,topic_id + "");
        }).setNegativeButton("再想想", v -> {}).setMsg("取消关注?").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
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
                        ToastUtils.showShort("关注成功");
                        mData.get(mPosition).setIs_follow(true);

                        //手动添加 后台没有操作
                        String re = mData.get(mPosition).getFollow_num();
                        mData.get(mPosition).setFollow_num((Long.parseLong(re) + 1) + "");

                        notifyItemChanged(mPosition);
                        EventBus.getDefault().post(new UpdateRecommendTopicFocusListEvent());

                        EventBus.getDefault().post(new UpdapteListTopicEvent());

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
                        mData.get(mPosition).setIs_follow(false);

                        //手动添加 后台没有操作
                        String re = mData.get(mPosition).getFollow_num();
                        mData.get(mPosition).setFollow_num((Long.parseLong(re) - 1) + "");

                        notifyItemChanged(mPosition);
                        EventBus.getDefault().post(new UpdateRecommendTopicFocusListEvent());


                        EventBus.getDefault().post(new UpdapteListTopicEvent());
                    }

                    @Override
                    public void onNetFail(String msg) {

                    }
                });
    }
}

