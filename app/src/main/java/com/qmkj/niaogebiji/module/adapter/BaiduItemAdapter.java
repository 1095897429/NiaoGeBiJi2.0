package com.qmkj.niaogebiji.module.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.PeopleBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-2
 * 描述:搜索百科适配器
 */
public class BaiduItemAdapter extends BaseQuickAdapter<PeopleBean, BaseViewHolder> {

    public BaiduItemAdapter(List<PeopleBean> data) {
        super(R.layout.search_baidu_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PeopleBean item) {


    }
}
