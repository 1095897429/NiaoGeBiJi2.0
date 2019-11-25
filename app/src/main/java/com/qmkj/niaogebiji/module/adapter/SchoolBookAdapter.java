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
 * 描述:学院课程
 */
public class SchoolBookAdapter extends BaseQuickAdapter<SchoolBean.SchoolBook, BaseViewHolder> {

    public SchoolBookAdapter(@Nullable List<SchoolBean.SchoolBook> data) {
        super(R.layout.school_book_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper,SchoolBean.SchoolBook item) {

    }
}

