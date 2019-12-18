package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CateAllBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:
 */
public class CategoryAdapter extends BaseQuickAdapter<CateAllBean.CateBean, BaseViewHolder> {
    public CategoryAdapter(@Nullable List<CateAllBean.CateBean> data) {
        super(R.layout.item_category,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CateAllBean.CateBean item) {
            helper.setText(R.id.tag_name,item.getCat());
    }
}
