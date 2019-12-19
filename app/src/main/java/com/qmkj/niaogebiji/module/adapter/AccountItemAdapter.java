package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.IncomeBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:账户Item适配器
 */
public class AccountItemAdapter extends BaseQuickAdapter<IncomeBean.InComeDetail, BaseViewHolder> {

    public AccountItemAdapter(@Nullable List<IncomeBean.InComeDetail> data) {
        super(R.layout.item_account_item11,data);

    }

    @Override
    protected void convert(BaseViewHolder helper, IncomeBean.InComeDetail item) {

        helper.setText(R.id.title,item.getOperation());
        //时间
        if(!TextUtils.isEmpty(item.getCreated_at())){
            helper.setText(R.id.title_tag, TimeUtils.millis2String(Long.parseLong(item.getCreated_at())* 1000L,"yyyy/MM/dd"));
        }

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        ((TextView)helper.getView(R.id.account)).setTypeface(typeface);

        //类型：1-增加积分，2-减少积分 显示详情
        if("1".equals(item.getOpe_type())){
            helper.setText(R.id.account," + " + item.getPoint());
            helper.setTextColor(R.id.account, Color.parseColor("#FA541C"));
            helper.setTextColor(R.id.text_feather, Color.parseColor("#FA541C"));
            helper.setVisible(R.id.part3333,false);
        }else if("2".equals(item.getOpe_type())){
            helper.setText(R.id.account," - " + item.getPoint());
            helper.setTextColor(R.id.account, Color.parseColor("#333333"));
            helper.setTextColor(R.id.text_feather, Color.parseColor("#333333"));
            helper.setVisible(R.id.part3333,true);
        }






    }
}
