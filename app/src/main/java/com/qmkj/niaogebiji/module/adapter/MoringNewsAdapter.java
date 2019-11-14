package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.MoringNewsBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:
 */
public class MoringNewsAdapter extends BaseQuickAdapter<MoringNewsBean, BaseViewHolder> {

    public MoringNewsAdapter(@Nullable List<MoringNewsBean> data) {
        super(R.layout.activity_moring_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MoringNewsBean item) {



    }
}
