package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.FeatherProductBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:cat商品Item适配器
 */
public class NewsFeatherCatItemAdapter extends BaseQuickAdapter<FeatherProductBean.Productean, BaseViewHolder> {

    public NewsFeatherCatItemAdapter(@Nullable List<FeatherProductBean.Productean> data) {
        super(R.layout.item_new_feather_cat_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeatherProductBean.Productean bean) {



        ImageUtil.load(mContext,bean.getImg_list(),helper.getView(R.id.img_1));

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        ((TextView)helper.getView(R.id.money)).setTypeface(typeface);

        TextView chineseTv = helper.getView(R.id.content);
        TextPaint paint = chineseTv.getPaint();
        paint.setFakeBoldText(true);


        helper.setText(R.id.money,bean.getPoint());


        helper.setText(R.id.content,bean.getTitle());


    }
}
