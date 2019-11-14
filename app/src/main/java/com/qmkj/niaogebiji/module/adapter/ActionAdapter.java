package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ActionBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:
 */
public class ActionAdapter extends BaseQuickAdapter<ActionBean, BaseViewHolder> {
    public ActionAdapter(@Nullable List<ActionBean> data) {
        super(R.layout.item_action,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActionBean item) {
        helper.addOnClickListener(R.id.relate_data);
    }
}

