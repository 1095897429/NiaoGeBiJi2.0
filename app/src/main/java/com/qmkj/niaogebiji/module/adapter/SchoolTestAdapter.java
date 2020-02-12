package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

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
        super(R.layout.school_test_item_new,data);
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

        //TODO 不及格不显示已完成 2020.2.11?????
        //是否已完成 是否已参加过测试 1是 0否
        if(item.getRecord() != null ){
            if(1 == item.getRecord().getIs_tested()){
                helper.setVisible(R.id.test_done_img,true);
            }else{
                helper.setVisible(R.id.test_done_img,false);
            }
        }


        if(!TextUtils.isEmpty(item.getIcon())){
            ImageUtil.load(mContext,item.getIcon(),helper.getView(R.id.test_img));
            ImageView imageView= helper.getView(R.id.test_img);
            imageView.setAlpha(64);
        }
    }
}

