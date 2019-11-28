package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.TestBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:开始测试适配器
 */
public class TestLaunchItemAdapter extends BaseQuickAdapter<TestBean, BaseViewHolder> {

    public TestLaunchItemAdapter(@Nullable List<TestBean> data) {
        super(R.layout.test_launch_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper,TestBean item) {
        helper.setText(R.id.option,item.getAnswer());
        if(helper.getAdapterPosition() == 0){
            helper.setText(R.id.tag,"A.");
        }else if(helper.getAdapterPosition() == 1){
            helper.setText(R.id.tag,"B.");
        }else if(helper.getAdapterPosition() == 2){
            helper.setText(R.id.tag,"C.");
        }else if(helper.getAdapterPosition() == 3){
            helper.setText(R.id.tag,"D.");
        }

        if(!item.isSelect()){
            helper.setImageResource(R.id.img,R.mipmap.icon_answer_default);
        }else{
            helper.setImageResource(R.id.img,R.mipmap.icon_answer_select);
        }

        //默认为false,提交了答案之后变为true
        if(item.isError()){
            helper.setBackgroundRes(R.id.ll_part,R.drawable.bg_corners_12_light_green);
            helper.setVisible(R.id.right_icon,true);
        }else{
            helper.setBackgroundColor(R.id.ll_part,0);
            helper.setVisible(R.id.right_icon,false);
        }


        if(item.isClick()){
            helper.getView(R.id.ll_part).setEnabled(false);
        }else{
            helper.getView(R.id.ll_part).setEnabled(true);
        }
    }


}

