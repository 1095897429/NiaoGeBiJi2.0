package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.FeatherBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:羽毛任务列表Item适配器
 */
public class FeatherItemAdapter extends BaseQuickAdapter<FeatherBean, BaseViewHolder> {

    public FeatherItemAdapter(@Nullable List<FeatherBean> data) {
        super(R.layout.item_feather_item,data);
    }

    public void updateData(int position){
        notifyDataSetChanged();
    }



    @Override
    protected void convert(BaseViewHolder helper, FeatherBean item) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.donetask).addOnClickListener(R.id.part2222).addOnClickListener(R.id.part3333);

        helper.setText(R.id.task_name,item.getTitle())
                .setText(R.id.task_tag,item.getTag());

        helper.setImageResource(R.id.icon_111,item.getImgRes());

        if(1 == item.getStatus()){
            helper.setVisible(R.id.donetask,true);
            helper.setText(R.id.donetask,"已签到");
            (helper.getView(R.id.donetask)).setBackgroundResource(R.drawable.bg_item_task_signed);
            helper.setVisible(R.id.part2222,false);
            helper.setVisible(R.id.part3333,false);
        }else if(2 == item.getStatus()){
            helper.setVisible(R.id.donetask,false);
            helper.setVisible(R.id.part2222,false);
            helper.setVisible(R.id.part3333,true);
        }else if(3 == item.getStatus()){
            helper.setVisible(R.id.donetask,false);
            helper.setVisible(R.id.donetask,false);
            helper.setVisible(R.id.part2222,true);
        }


        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        ((TextView)helper.getView(R.id.dotask_text)).setTypeface(typeface);

        Typeface typeface1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Black.otf");
        ((TextView)helper.getView(R.id.dotask_text2)).setTypeface(typeface1);



        switch (helper.getAdapterPosition()){
            case 0:
                break;
            case 1:
                helper.setText(R.id.dotask_text,"+100");
                break;
            case 2:
            case 3:
            case 4:

            case 7:
            case 8:
                helper.setText(R.id.dotask_text,"+1000");
                break;
            case 5:
                helper.setText(R.id.dotask_text2,"+10");
                break;
            case 6:
                helper.setText(R.id.dotask_text,"+10");
                break;

            default:
        }
    }
}
