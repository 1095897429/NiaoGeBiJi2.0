package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.UserRankBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:排行Item适配器
 */
public class RankItemAdapter extends BaseQuickAdapter<UserRankBean.OtherPoint, BaseViewHolder> {

    public RankItemAdapter(@Nullable List<UserRankBean.OtherPoint> data) {
        super(R.layout.item_rank,data);

    }

    @Override
    protected void convert(BaseViewHolder helper, UserRankBean.OtherPoint item) {


        if(helper.getAdapterPosition() % 2 == 0){
            helper.getView(R.id.part1111).setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }else{
            helper.getView(R.id.part1111).setBackgroundColor(mContext.getResources().getColor(R.color.blue));
        }

        helper.setText(R.id.nickname_rank,item.getName());
        helper.setText(R.id.point_rank,item.getPoint());
        helper.setText(R.id.grade_rank,item.getOrder());

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        ((TextView)helper.getView(R.id.point_rank)).setTypeface(typeface);
        ((TextView)helper.getView(R.id.grade_rank)).setTypeface(typeface);

        //类型：排名显示颜色
        if("1".equals(item.getOrder())){
            helper.setTextColor(R.id.grade_rank, Color.parseColor("#FF5040"));
        }else if("2".equals(item.getOrder())){
            helper.setTextColor(R.id.grade_rank, Color.parseColor("#FA8C16"));
        }else if("3".equals(item.getOrder())){
            helper.setTextColor(R.id.grade_rank, Color.parseColor("#FAAD14"));
        }else{
            helper.setTextColor(R.id.grade_rank, Color.parseColor("#242629"));
        }




    }
}
