package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.SchoolBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:学院测试
 */
public class SchoolTestAdapter extends BaseQuickAdapter<SchoolBean.SchoolTest, BaseViewHolder> {

    public SchoolTestAdapter(@Nullable List<SchoolBean.SchoolTest> data) {
        super(R.layout.school_test_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper,SchoolBean.SchoolTest item) {

    }
}

