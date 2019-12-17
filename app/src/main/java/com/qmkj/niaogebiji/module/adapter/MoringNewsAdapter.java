package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.MoringAllBean;
import com.qmkj.niaogebiji.module.bean.MoringNewsBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:
 */
public class MoringNewsAdapter extends BaseQuickAdapter<MoringAllBean.MoringBean, BaseViewHolder> {

    public MoringNewsAdapter(@Nullable List<MoringAllBean.MoringBean> data) {
        super(R.layout.activity_moring_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MoringAllBean.MoringBean item) {

        helper.addOnClickListener(R.id.toListen);

        helper.setText(R.id.summary,item.getTitle());

        if(null != item.getCreated_at()){
            helper.setText(R.id.toMoreMoring, TimeUtils.millis2String(Long.parseLong(item.getCreated_at())* 1000L,"yyyy/MM/dd"));

        }

    }
}
