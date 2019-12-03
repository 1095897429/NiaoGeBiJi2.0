package com.qmkj.niaogebiji.module.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.PeopleBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-2
 * 描述:搜索人脉适配器
 */
public class PeopleItemAdapter extends BaseQuickAdapter<PeopleBean, BaseViewHolder> {

    public PeopleItemAdapter(List<PeopleBean> data) {
        super(R.layout.search_people_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PeopleBean item) {

    }
}
