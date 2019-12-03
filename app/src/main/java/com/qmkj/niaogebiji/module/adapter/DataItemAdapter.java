package com.qmkj.niaogebiji.module.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ToolBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-2
 * 描述:搜索全部中的资料
 */
public class DataItemAdapter extends BaseQuickAdapter<ToolBean, BaseViewHolder> {

    public DataItemAdapter(List<ToolBean> data) {
        super(R.layout.search_data_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ToolBean item) {


    }
}
