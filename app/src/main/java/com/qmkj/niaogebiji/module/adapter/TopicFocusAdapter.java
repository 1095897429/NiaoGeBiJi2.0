package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.qmkj.niaogebiji.module.event.UpdateRecommendTopicFocusListEvent;
import com.qmkj.niaogebiji.module.event.UpdateTopicEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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


        //头像
//        ImageUtil.load(mContext,item.getPic(),helper.getView(R.id.one_img_imgs));


        //关注数 x>=10000，展示1w+
//        if(!TextUtils.isEmpty(item.getHit_count())){
//            long count = Long.parseLong(item.getHit_count());
//            if(count < 10000 ){
//                helper.setText(R.id.tag,"影响力 " + item.getHit_count());
//            }else{
//                double temp = count  ;
//                //1.将数字转换成以万为单位的数字
//                double num = temp / 10000;
//                BigDecimal b = new BigDecimal(num);
//                //2.转换后的数字四舍五入保留小数点后一位;
//                double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
//                helper.setText(R.id.tag,"影响力 " + f1 + " w");
//            }
//        }

        //是否关注 注：1-关注，0-取消关注
        if(0 == item.getIs_select()){
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
            int mPosition = helper.getAdapterPosition();
            //手动修改
            TopicBean bean = mData.get(mPosition);
            if(1 == bean.getIs_select()){
                bean.setIs_select(0);
            }else{
                bean.setIs_select(1);
            }
            notifyItemChanged(mPosition);

            EventBus.getDefault().post(new UpdateRecommendTopicFocusListEvent());
        });

    }
}

