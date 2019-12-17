package com.qmkj.niaogebiji.module.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.PeopleBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-2
 * 描述:职业选择阶段
 */
public class ProfessionItemAdapter extends BaseQuickAdapter<ProBean, BaseViewHolder> {

    public ProfessionItemAdapter(List<ProBean> data) {
        super(R.layout.profession_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProBean item) {

        helper.setText(R.id.pro_name,item.getName());

        ImageUtil.load(mContext,item.getImg(),helper.getView(R.id.pro_img));

        if(!item.isSelect()){
            helper.setBackgroundRes(R.id.part1111,R.color.transparent);
        }else{
            helper.setBackgroundRes(R.id.part1111,R.drawable.bg_corners_12_yellow);
        }

//        helper.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                List<ProBean> mDatas = mData;
//                //① 将所有的selected设置false，当前点击的设为true
//                for (ProBean data : mDatas) {
//                    data.setSelect(false);
//                }
//
//                if(item.isSelect()){
//                    item.setSelect(false);
//                }else{
//                    item.setSelect(true);
//                }
//
//                notifyDataSetChanged();
//            }
//        });
    }


}
