package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:热榜的适配器
 */
public class HotNewsAdapter extends BaseQuickAdapter<MultiNewsBean, BaseViewHolder> {
    public HotNewsAdapter(@Nullable List<MultiNewsBean> data) {
        super(R.layout.hot_news_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiNewsBean item) {
        helper.setText(R.id.rank,item.getNewsItemBean().getRank());

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        ((TextView)helper.getView(R.id.rank)).setTypeface(typeface);

        //类型：排名显示颜色
        if("1".equals(item.getNewsItemBean().getRank()) ||
                "2".equals(item.getNewsItemBean().getRank()) ||
                "3".equals(item.getNewsItemBean().getRank()) ){
            helper.setTextColor(R.id.rank, Color.parseColor("#FF5040"));
        }else{
            helper.setTextColor(R.id.rank, Color.parseColor("#818386"));
        }

        //影响数 x>=100000，展示10w+
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


    }
}
