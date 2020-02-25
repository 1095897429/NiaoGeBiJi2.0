package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

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
        RecommendBean.Article_list bean = item.getNewsActicleList();

        ImageUtil.load(mContext,bean.getPic(),helper.getView(R.id.one_img_imgs));
        //标题
        helper.setText(R.id.one_img_title,bean.getTitle());

        //作者
        helper.setText(R.id.one_img_auth,bean.getAuthor());

        //阅读
        if(!TextUtils.isEmpty(bean.getViewnum())){
            long count = Long.parseLong(bean.getViewnum());
            if(count < 10000 ){
                helper.setText(R.id.reading_num,"阅读 " + bean.getViewnum());
            }else{
                double temp = count  ;
                //1.将数字转换成以万为单位的数字
                double num = temp / 10000;
                BigDecimal b = new BigDecimal(num);
                //2.转换后的数字四舍五入保留小数点后一位;
                double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                helper.setText(R.id.reading_num,"阅读 " + f1 + " w");
            }

        }



        helper.setText(R.id.rank,helper.getAdapterPosition() + 1 + "");

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        ((TextView)helper.getView(R.id.rank)).setTypeface(typeface);

        //类型：排名显示颜色
        if(0 ==  helper.getAdapterPosition() ||
               1 ==  helper.getAdapterPosition()||
                2 == helper.getAdapterPosition() ){
            helper.setTextColor(R.id.rank, Color.parseColor("#FF5040"));
        }else{
            helper.setTextColor(R.id.rank, Color.parseColor("#818386"));
        }


        helper.itemView.setOnClickListener(v -> {
            if(StringUtil.isFastClick()){
                return;
            }

            String aid = bean.getAid();
            if (!TextUtils.isEmpty(aid)) {
                UIHelper.toNewsDetailActivity(mContext, aid);
            }
        });

    }
}
