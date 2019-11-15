package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:
 */
public class FirstAuthorAdapter extends BaseQuickAdapter<IndexFocusBean.Auther_list, BaseViewHolder> {

    public FirstAuthorAdapter(@Nullable List<IndexFocusBean.Auther_list> data) {
        super(R.layout.frist_focus_author_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, IndexFocusBean.Auther_list item) {

        //设置子View点击事件
        helper.addOnClickListener(R.id.focus).addOnClickListener(R.id.focus_aleady);

        ImageUtil.load(mContext,item.getImg(),helper.getView(R.id.head_icon));

        helper.setText(R.id.name,item.getName());

        //影响数
        if(!TextUtils.isEmpty(item.getHit_count())){
            long count = Long.parseLong(item.getHit_count());
            if(count < 10000 ){
                helper.setText(R.id.tag,"影响力 " + item.getHit_count());
            }else{
                double temp = count  ;
                //1.将数字转换成以万为单位的数字
                double num = temp / 10000;
                BigDecimal b = new BigDecimal(num);
                //2.转换后的数字四舍五入保留小数点后一位;
                double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                helper.setText(R.id.tag,"影响力 " + f1 + " w");
            }

        }


        if(0 == item.getIs_follow()){
            helper.setVisible(R.id.focus,true);
            helper.setVisible(R.id.focus_aleady,false);
        }else{
            helper.setVisible(R.id.focus,false);
            helper.setVisible(R.id.focus_aleady,true);
        }
    }
}
