package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.TestBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:
 */
public class TestItemAdapter extends BaseQuickAdapter<TestBean, BaseViewHolder> {

    public TestItemAdapter(@Nullable List<TestBean> data) {
        super(R.layout.activity_test_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestBean item) {

        if(helper.getAdapterPosition() % 2 == 0){
            helper.setVisible(R.id.is_test_ok,false);
        }else{
            helper.setVisible(R.id.is_test_ok,true);
        }


    }
}
