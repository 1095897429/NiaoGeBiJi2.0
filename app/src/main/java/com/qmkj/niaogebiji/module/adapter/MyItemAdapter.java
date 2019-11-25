package com.qmkj.niaogebiji.module.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.MyBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:
 */
public class MyItemAdapter extends BaseQuickAdapter<MyBean, BaseViewHolder> {

    public MyItemAdapter(List<MyBean> data) {
        super(R.layout.my_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyBean item) {

        helper.setImageResource(R.id.pic,item.getResId());
        helper.setText(R.id.name,item.getName());

    }
}
