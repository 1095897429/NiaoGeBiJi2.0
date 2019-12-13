package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

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

        if(!TextUtils.isEmpty(item.getImage_url())){
            ImageUtil.load(mContext,item.getImage_url(),helper.getView(R.id.baidu_icon));
        }

        helper.setText(R.id.content,item.getTitle());
        helper.setText(R.id.money,item.getPrice());
        helper.setText(R.id.tag,item.getNum() + "人学习");

    }
}

