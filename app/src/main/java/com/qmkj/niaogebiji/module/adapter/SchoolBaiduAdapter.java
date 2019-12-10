package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:学院百科
 */
public class SchoolBaiduAdapter extends BaseQuickAdapter<SchoolBean.SchoolBaiDu, BaseViewHolder> {

    public SchoolBaiduAdapter(@Nullable List<SchoolBean.SchoolBaiDu> data) {
        super(R.layout.school_baidu_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper,SchoolBean.SchoolBaiDu item) {

        helper.setImageResource(R.id.baidu_icon,item.getImg());

        helper.setText(R.id.baidu_name,item.getName());
    }
}

