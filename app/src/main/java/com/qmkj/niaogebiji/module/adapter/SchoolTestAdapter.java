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
 * 描述:学院测试
 */
public class SchoolTestAdapter extends BaseQuickAdapter<SchoolBean.SchoolTest, BaseViewHolder> {

    public SchoolTestAdapter(@Nullable List<SchoolBean.SchoolTest> data) {
        super(R.layout.school_test_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper,SchoolBean.SchoolTest item) {

        helper.setText(R.id.test_title,item.getTitle());
        //是否展示限时标志 1是 0否
        if("0".equals(item.getIs_limit_time())){
            helper.setVisible(R.id.test_time,false);
        }else if("1".equals(item.getIs_limit_time())){
            helper.setVisible(R.id.test_time,true);
        }

        if(!TextUtils.isEmpty(item.getIcon())){
            ImageUtil.load(mContext,item.getIcon(),helper.getView(R.id.test_img));
        }
    }
}

