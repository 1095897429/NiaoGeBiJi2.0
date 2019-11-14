package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;

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

    }
}
